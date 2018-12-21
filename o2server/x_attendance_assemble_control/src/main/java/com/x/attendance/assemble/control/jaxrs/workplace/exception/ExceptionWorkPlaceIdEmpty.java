package com.x.attendance.assemble.control.jaxrs.workplace.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkPlaceIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkPlaceIdEmpty() {
		super("工作场所ID不允许为空，无法进行数据查询。");
	}
}
