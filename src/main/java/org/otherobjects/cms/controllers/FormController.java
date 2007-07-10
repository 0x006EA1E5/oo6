package org.otherobjects.cms.controllers;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import org.otherobjects.cms.dao.DynaNodeDao;import org.otherobjects.cms.model.DynaNode;import org.otherobjects.cms.types.PropertyDef;import org.otherobjects.cms.types.TypeDef;import org.otherobjects.cms.types.TypeService;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.util.Assert;import org.springframework.web.servlet.ModelAndView;import org.springframework.web.servlet.mvc.Controller;/** * Controller to process form submission. Only data for types registered in the TypeService is supported. * * @author rich */public class FormController implements Controller{    private Logger logger = LoggerFactory.getLogger(FormController.class);        private DynaNodeDao dynaNodeDao;    private TypeService typeService;    @SuppressWarnings("unchecked")    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception    {        try        {            // Get form info            String id = request.getParameter("id");            String typeName = request.getParameter("ooType");            Assert.notNull(typeName, "Type must be provided in form data.");            TypeDef type = typeService.getType(typeName);            Assert.notNull(type, "Type not found:" + typeName);                        // Load existing object            DynaNode dynaNode;            if(id != null)            {                logger.info("Populating existing object: {} ({})", id, typeName);                dynaNode = dynaNodeDao.get(id);            }            else            {                logger.info("Creating new object: " + typeName);                dynaNode = new DynaNode(typeName);            }                            // Set dynaNode properties from parameters            for (PropertyDef pd : type.getProperties())            {                String name = pd.getName();                String value = request.getParameter(name);                if( value != null)                    dynaNode.set(name, value);            }                        // Save new object            dynaNodeDao.save(dynaNode);            //                String name = (String) parameterNames.nextElement();//                String value = request.getParameter(name);//                name = name.replaceAll("data\\.", "");////                PropertyDef property = typeDef.getProperty(name);//                if (name.endsWith(":BOOLEAN"))//                {//                    name = name.substring(0, name.length() - 8);//                    if (!data.containsKey(name))//                        data.put(name, false);//                }//                else if (property != null)//                {//                    if (value == null || value.length() == 0)//                        data.put(name, null);//                    else//                    {//                        Object convert = ConvertUtils.convert(value, property.getDataClass());//                        data.put(name, convert);//                    }//                }//            }////            // Validate//            // TODO add in commons validator too?//            Errors errors = new MapBindingResult(new HashMap(), "data");////            if (typeDef.getValidators() != null)//            {//                ValangValidator v = new ValangValidator();//                v.setValang(typeDef.getValidators());//                v.afterPropertiesSet();////                for (Iterator iter = v.getRules().iterator(); iter.hasNext();)//                {//                    ValidationRule rule = (ValidationRule) iter.next();//                    rule.validate(data, errors);//                }//            }////            // We have errors so return error messages//            ModelAndView view = new ModelAndView("jsonView");//            if (errors.getErrorCount() > 0)//            {//                List<Object> jsonErrors = new ArrayList<Object>();//                for (FieldError e : (List<FieldError>) errors.getFieldErrors())//                {//                    Map<String, String> error = new HashMap<String, String>();//                    error.put("id", e.getField());//                    error.put("msg", e.getDefaultMessage());//                    jsonErrors.add(error);//                }//                view.getModel().put("success", false);//                view.getModel().put("errors", jsonErrors);//                return view;//            }////            // All good so save the data        //            getJcrService().getTemplate().execute(new JcrCallback()//            {//                public Object doInJcr(Session session) throws RepositoryException//                {//                    Node node = session.getNodeByUUID(id);////                    for (String name : data.keySet())//                    {//                        if (data.get(name) != null)//                            node.setProperty(name, JcrService.createValue(typeDef.getProperty(name).getType(), data.get(name)));//                    }////                    if (data.containsKey("name"))//                    {//                        String oldPath = node.getPath();//                        String newPath = node.getParent().getPath() + "/" + data.get("name");//                        if (!newPath.equals(oldPath))//                            session.move(oldPath, newPath);//                    }//                    else if (data.containsKey("title"))//                    {//                        String oldPath = node.getPath();//                        String newPath = node.getParent().getPath() + "/" + StringUtils.generateUrlCode("" + data.get("title")) + ".html";//                        if (!newPath.equals(oldPath))//                            session.move(oldPath, newPath);//                    }//                    session.save();//                    return null;//                }//            });////            view.getModel().put("success", true);////            // If type has changed re-register types//            if (type.equals("PropertyDef"))//                getJcrService().registerAllowedTypes("");////            if (type.equals("Script"))//                getJcrService().registerEvents("");            return null;        }        catch (Exception e)        {            ModelAndView view = new ModelAndView("jsonView");            view.getModel().put("success", false);            view.getModel().put("message", e.getMessage());            logger.error("Error saving form data.", e);            return view;        }    }    public DynaNodeDao getDynaNodeDao()    {        return dynaNodeDao;    }    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)    {        this.dynaNodeDao = dynaNodeDao;    }    public TypeService getTypeService()    {        return typeService;    }    public void setTypeService(TypeService typeService)    {        this.typeService = typeService;    }}