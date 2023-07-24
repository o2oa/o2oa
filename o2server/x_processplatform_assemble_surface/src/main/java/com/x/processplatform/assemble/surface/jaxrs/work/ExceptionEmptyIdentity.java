package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyIdentity extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyIdentity(List<String> identities) {
		super("指定的身份不存在或者为空:{}.", StringUtils.join(identities, ","));
	}

	ExceptionEmptyIdentity(String identity) {
		super("指定的身份不存在或者为空:{}.", identity);
	}
}
