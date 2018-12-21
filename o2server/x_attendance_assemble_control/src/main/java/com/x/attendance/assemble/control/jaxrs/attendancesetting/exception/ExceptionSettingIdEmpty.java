package com.x.attendance.assemble.control.jaxrs.attendancesetting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSettingIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSettingIdEmpty() {
		super("查询操作传入的参数Id为空，无法进行查询操作.");
	}
}
