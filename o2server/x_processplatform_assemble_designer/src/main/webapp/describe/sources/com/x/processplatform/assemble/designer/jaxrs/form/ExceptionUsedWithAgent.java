package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.PromptException;

class ExceptionUsedWithAgent extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionUsedWithAgent(String name, String id, List<String> names) {
		super("form name:{} id:{}, used with agent: {}.", name, id, StringUtils.join(names, ","));
	}
}
