package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.exception.PromptException;

class MultiReferencedException extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	MultiReferencedException(String name, String id) {
		super("attachment name:{} id:{}, referenced by multi work.", name, id);
	}

}
