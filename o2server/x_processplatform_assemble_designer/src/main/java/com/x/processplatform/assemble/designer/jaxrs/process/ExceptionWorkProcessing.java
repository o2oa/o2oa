package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkProcessing extends LanguagePromptException {

	private static final long serialVersionUID = 3768001625178470667L;

	ExceptionWorkProcessing(String name, String id, Long count) {
		super("流程:{}, id:{}, 存在 {} 个在流转中工作实例,请确保没有在流转中的工作实例后再尝试删除流程.", name, id, count);
	}
}
