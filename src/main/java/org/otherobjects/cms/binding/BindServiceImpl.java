package org.otherobjects.cms.binding;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.otherobjects.cms.beans.JcrBeanService;
import org.otherobjects.cms.model.DynaNode;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * Very thin wrapper around springs ServletRequestDataBinder with our default CustomEditor(s) configured.
 * Call the bind method after you have beanified your DynaNode using {@link JcrBeanService} (This might change)
 * 
 * @author joerg
 *
 */
public class BindServiceImpl implements BindService {
	
	private String dateFormat;
	
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public BindingResult bind(DynaNode dynaNode, 
			HttpServletRequest request) {
		
		ServletRequestDataBinder binder = new ServletRequestDataBinder(dynaNode);
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat(dateFormat), true));
        binder.bind(request);
        
		return binder.getBindingResult();
	}

}
