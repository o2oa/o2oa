package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionUsedWithAgent extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionUsedWithAgent(String name, String id, List<String> names) {
		super("表单 name:{} id:{}, 被脚本节点使用: {}.", name, id, StringUtils.join(names, ","));
	}
}
