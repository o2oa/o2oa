package com.x.calendar.assemble.control.jaxrs.setting;

import com.x.base.core.project.exception.PromptException;

class ExceptionSettingIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionSettingIdEmpty() {
		super("查询操作传入的参数Id为空，无法进行查询操作.");
	}
}
