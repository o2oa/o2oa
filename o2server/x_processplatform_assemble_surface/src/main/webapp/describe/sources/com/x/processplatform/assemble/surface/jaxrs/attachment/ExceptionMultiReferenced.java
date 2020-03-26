package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionMultiReferenced extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionMultiReferenced(String name, String id) {
		super("attachment name:{} id:{}, referenced by multi work.", name, id);
	}

}
