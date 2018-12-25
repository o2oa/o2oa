package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionConfigSettingValueEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigSettingValueEmpty() {
		super("配置内容value为空， 无法进行保存." );
	}
}
