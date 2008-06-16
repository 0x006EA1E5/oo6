package org.otherobjects.cms.binding;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface HttpServletRequestModifier extends HttpServletRequest
{
    public void rewriteParameter(String originalParameterName, String newParameterName);

    public Map<String, String> getRewrittenPaths();
}
