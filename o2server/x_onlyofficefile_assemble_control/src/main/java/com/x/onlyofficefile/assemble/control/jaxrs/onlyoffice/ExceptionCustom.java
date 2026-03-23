package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;


import com.x.base.core.project.exception.PromptException;

class ExceptionCustom extends PromptException {

	private static final long serialVersionUID = 609212426926385072L;

	ExceptionCustom(String name) {
		super(name);
	}
}
