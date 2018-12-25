package com.x.organization.assemble.control.jaxrs.personattribute;

import com.x.base.core.project.exception.PromptException;
import com.x.organization.core.entity.Person;

class ExceptionNameExistWithPerson extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNameExistWithPerson(Person person, String unique) {
		super("{} 已有名为: {} 的属性.", person.getName(), unique);
	}
}
