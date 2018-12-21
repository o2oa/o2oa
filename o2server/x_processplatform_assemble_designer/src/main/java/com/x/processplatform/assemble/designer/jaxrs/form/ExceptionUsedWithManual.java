package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.PromptException;

class ExceptionUsedWithManual extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionUsedWithManual(String name, String id, List<String> names) {
		super("form name:{} id:{}, used with begin: {}.", name, id, StringUtils.join(names, ","));
	}
}
