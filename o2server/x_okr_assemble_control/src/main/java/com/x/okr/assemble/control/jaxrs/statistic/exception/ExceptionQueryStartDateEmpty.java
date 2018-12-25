package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryStartDateEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryStartDateEmpty() {
		super("查询条件开始日期为空。");
	}
}
