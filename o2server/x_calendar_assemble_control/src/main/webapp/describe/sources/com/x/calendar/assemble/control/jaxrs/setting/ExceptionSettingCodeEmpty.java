package com.x.calendar.assemble.control.jaxrs.setting;

import com.x.base.core.project.exception.PromptException;

class ExceptionSettingCodeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionSettingCodeEmpty() {
		super("查询操作传入的设置编码Code为空，无法进行查询或者保存操作.");
	}
}
