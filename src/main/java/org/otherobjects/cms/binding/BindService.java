package org.otherobjects.cms.binding;

import javax.servlet.http.HttpServletRequest;

import org.otherobjects.cms.types.TypeDef;
import org.springframework.validation.BindingResult;

/**
 * BindService is a very simple interface that allows one to bind http request parameters to DynaNode properties. 
 * Implementation choice and configuration happens through spring context. 
 * @author joerg
 *
 */
public interface BindService
{
    public BindingResult bind(Object item, TypeDef typeDef, HttpServletRequest request);
}
