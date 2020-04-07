package com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkTypeConfigListTypeCount extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkTypeConfigListTypeCount( Throwable e ) {
		super("系统查询所有类别的工作数量列表时发生异常。", e);
	}
}
