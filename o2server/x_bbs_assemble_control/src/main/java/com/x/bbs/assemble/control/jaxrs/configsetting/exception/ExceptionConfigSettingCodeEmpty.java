package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionConfigSettingCodeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigSettingCodeEmpty() {
		super("code为空， 无法进行查询或者保存." );
	}
}
