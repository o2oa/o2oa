package com.x.cms.assemble.control.jaxrs.appdictdesign;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppDictNotExisted extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionAppDictNotExisted(String str) {
		super("标识为:{}, 的数据字典不存在.", str);
	}
}
