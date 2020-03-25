package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionConfigSettingIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigSettingIdEmpty() {
		super("id为空， 无法进行查询." );
	}
}
