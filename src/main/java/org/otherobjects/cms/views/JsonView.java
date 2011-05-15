package org.otherobjects.cms.views;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.PagedList;
import org.otherobjects.cms.util.StringUtils;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import flexjson.JSONSerializer;

@Component
public class JsonView implements View, ViewResolver {
    
    public static final String JSON_DATA_KEY = "jsonData";
    public static final String JSON_INCLUDES_KEY = "jsonIncludes";
    public static final String JSON_DEEP_SERIALIZE = "jsonDeep";
    public static final String JSON_MIME_OVERRIDE = "mimeOverride";

    // Content type to set in response. Defaults to 'application/json'. Override via 
    // 'otherobjects.view.json.contentType' property
    private String contentType = MediaType.APPLICATION_JSON.toString();
    
    @Resource
    private MessageSource messageSource;

    //TODO Make this configurable?
    private static final String ENCODING = "UTF-8";

    @Resource
    private OtherObjectsConfigurator otherObjectsConfigurator;
    
    // exclude patterns as defined in property 'otherobjects.view.json.exclude.patterns'
    private String[] excludePatterns = new String[0];
    
    @PostConstruct
    public void init() {
        contentType = otherObjectsConfigurator.getProperty("otherobjects.view.json.contentType", contentType);
        
        // Comma separated list of exclude patterns for the JSON serialiser
        String excludePatterns = otherObjectsConfigurator.getProperty("otherobjects.view.json.exclude.patterns");
        if(StringUtils.isNotBlank(excludePatterns)) {
            this.excludePatterns = StringUtils.splitByWholeSeparator(excludePatterns, ",");
            for(int i = 0; i < this.excludePatterns.length; ++i) {
                this.excludePatterns[i] = StringUtils.strip(this.excludePatterns[i]); 
            }
        }
        
    }
    
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        Writer responseWriter = response.getWriter();
        
        StringWriter writer = new StringWriter();
        JSONSerializer serializer = new JSONSerializer();

        // exclude patterns as defined in property 'otherobjects.view.json.exclude.patterns'
        for(String pattern : excludePatterns) {
            serializer.exclude(pattern);
        }

        if (model.containsKey(JSON_MIME_OVERRIDE)) {
            this.contentType = (String) model.get(JSON_MIME_OVERRIDE);
        }
        
        boolean deep = false;
        if (model.containsKey(JSON_DEEP_SERIALIZE))
            deep = (Boolean) model.get(JSON_DEEP_SERIALIZE);

        if (model.containsKey(JSON_DATA_KEY)) {
            if (model.containsKey(JSON_INCLUDES_KEY))
                serializer = serializer.include((String[]) model.get(JSON_INCLUDES_KEY));

            writer.write(serializeData(serializer, model.get(JSON_DATA_KEY), deep));
        }
        else {
            Object item = model.get("item");
            writer.write(serializeData(serializer, item, deep));

        }
        String json = writer.toString();

        Locale locale = LocaleContextHolder.getLocale();
        MessageSource messageSource = getMessageSource();

        // FIXME Is this a good idea or even a good place?
        json = localiseString(json, messageSource, locale);

        // FIXME Is there a more performant way of doing this
        //logger.debug(json);
        response.setCharacterEncoding(ENCODING);
        response.setContentType(contentType);
        responseWriter.write(json);
    }
    
    private String serializeData(JSONSerializer serializer, Object data, boolean deep) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", data);
        Map<String, Object> metaData = new HashMap<String, Object>();
        
        if(data instanceof PagedList<?>) {
            metaData.put("pageData", new JSONPagedListMetaData((PagedList<?>) data));
        }
        
        map.put("metaData", metaData); 

        if(deep)
            return serializer.deepSerialize(map);
        else
            return serializer.serialize(map);
    }

    protected String localiseString(String text, MessageSource messageSource, Locale locale) {
        Pattern p = Pattern.compile("\\$\\{([\\w\\.]*)\\}");
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String msg = messageSource.getMessage(m.group(1), null, locale);
            m.appendReplacement(sb, msg);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Responds to 'application/json'
     *
     * <hr/>
     * Date: 13 May 2011
     *
     * @see org.springframework.web.servlet.View#getContentType()
     * @return
     * @author greg
     */
    public String getContentType() {
        return MediaType.APPLICATION_JSON.toString();
    }

    public class JSONPagedListMetaData {
        public JSONPagedListMetaData(PagedList<?> pagedList) {
            currentPage = pagedList.getCurrentPage();
            itemTotal = pagedList.getItemTotal();
            pageCount = pagedList.getPageCount();
            pageSize = pagedList.getPageSize();
        }
    
        public int currentPage;
        public int itemTotal;
        public int pageCount;
        public int pageSize;
        
    }

    /**
     * @param otherObjectsConfigurator the otherObjectsConfigurator to set
     */
    public void setOtherObjectsConfigurator(
            OtherObjectsConfigurator otherObjectsConfigurator) {
        this.otherObjectsConfigurator = otherObjectsConfigurator;
    }

    @Override
    public View resolveViewName(String viewName, Locale locale)
            throws Exception {
        
        if("json".equals(viewName)) {
            return this;
        } 
        
        if(StringUtils.endsWithIgnoreCase(viewName, ".json")) {
            return this;
        }
        
        return null;
    };
    
}
