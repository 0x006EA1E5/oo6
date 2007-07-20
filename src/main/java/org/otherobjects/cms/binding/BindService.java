package org.otherobjects.cms.binding;

import javax.servlet.http.HttpServletRequest;

import org.otherobjects.cms.model.DynaNode;
import org.springframework.validation.BindingResult;

/**
 * 
 * @author joerg
 *
 */
public interface BindService {
	public BindingResult bind(DynaNode dynaNode, HttpServletRequest request);
}
