package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionRestful extends PromptException {

	private static final long serialVersionUID = 6235890108470383271L;

	ExceptionRestful(String title, String person, String url) {
		super("restful call failed, title:{}, person:{}, url:{}.", title, person, url);
	}
}
