package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidDistinguishedName extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionInvalidDistinguishedName(List<String> names) {
		super("无效的专有组织标识:{}.", StringUtils.join(names, ","));
	}

}
