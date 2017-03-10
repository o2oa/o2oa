package com.x.processplatform.assemble.surface.jaxrs.review;

import com.x.base.core.exception.PromptException;

class ReviewNotMatchApplicationException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ReviewNotMatchApplicationException(String id, String applicationFlag) {
		super("review: {} not match with application: {}.", id, applicationFlag);
	}
}
