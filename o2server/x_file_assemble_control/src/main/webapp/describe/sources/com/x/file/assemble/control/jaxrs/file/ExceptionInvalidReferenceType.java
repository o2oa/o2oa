package com.x.file.assemble.control.jaxrs.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidReferenceType extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionInvalidReferenceType(String name) {
		super("参考类型未知: {}.", name);
	}
}
