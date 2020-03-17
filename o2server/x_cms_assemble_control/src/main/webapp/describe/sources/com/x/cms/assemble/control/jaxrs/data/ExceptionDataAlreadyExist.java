package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionDataAlreadyExist extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionDataAlreadyExist(String title, String docId) {
		super("document title:{} id:{}, already has data.", title, docId );
	}
}
