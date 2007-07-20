package org.otherobjects.cms.binding;

import javax.servlet.http.HttpServletRequest;

import org.otherobjects.cms.model.DynaNode;
import org.springframework.validation.BindingResult;

/**
 * BindService is a very simple interface that allows one to bind http request parameters to DynaNode properties. 
 * Implementation choice and configuration happens through spring context. 
 * @author joerg
 *
 */
public interface BindService {
	public BindingResult bind(DynaNode dynaNode, HttpServletRequest request);
}
