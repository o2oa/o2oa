package com.x.report.assemble.control.jaxrs.setting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSettingNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSettingNameEmpty() {
		super("查询操作传入的设置名称Name为空，无法进行查询或者保存操作.");
	}
}
