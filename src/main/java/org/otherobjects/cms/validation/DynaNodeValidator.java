package org.otherobjects.cms.validation;

import java.util.Iterator;

import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class DynaNodeValidator implements Validator {
	
	private TypeService typeService;
	
	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	public boolean supports(Class clazz) {
		return DynaNode.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Assert.isInstanceOf(DynaNode.class, target, "Can only validate DynaNodes");
		
		DynaNode valObj = (DynaNode) target;
		TypeDef typeDef = typeService.getType(valObj.getOoType());
		
		for(Iterator<PropertyDef> it = typeDef.getProperties().iterator(); it.hasNext();)
		{
			PropertyDef propertyDef = it.next();
			Object value = errors.getFieldValue(propertyDef.getName());
			String fieldName = propertyDef.getName();
			
			if(propertyDef.isRequired())
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "field.required");
			
			if(propertyDef.getMaxLength() > -1)
			{
				if(value != null && value.toString().length() > propertyDef.getMaxLength())
					errors.rejectValue(fieldName, "field.valueTooLong");
			}
		}
		
	}

}
