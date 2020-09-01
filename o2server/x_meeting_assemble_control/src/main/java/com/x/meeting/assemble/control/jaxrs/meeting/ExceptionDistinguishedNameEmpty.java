package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.project.exception.PromptException;

class ExceptionDistinguishedNameEmpty extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDistinguishedNameEmpty() {
		super("DistinguishedName is empty.");
	}
}
