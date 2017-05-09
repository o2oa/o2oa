package com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception;

import com.x.base.core.exception.PromptException;

public class WorkTypeConfigListTypeCountException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkTypeConfigListTypeCountException( Throwable e ) {
		super("系统查询所有类别的工作数量列表时发生异常。", e);
	}
}
