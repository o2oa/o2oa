package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.exception.PromptException;

class ReviewNotExistedException extends PromptException {

	private static final long serialVersionUID = -2925120598877380881L;

	ReviewNotExistedException(String flag) {
		super("review: {} not existed.", flag);
	}
}
