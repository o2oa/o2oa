package com.x.processplatform.assemble.designer.jaxrs.form;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionUsedDefaultForm extends LanguagePromptException {

	private static final long serialVersionUID = 5028290914582880718L;

	ExceptionUsedDefaultForm(String name, String id) {
		super("表单 name:{} id:{}, 是应用的默认表单.", name, id);
	}
}
