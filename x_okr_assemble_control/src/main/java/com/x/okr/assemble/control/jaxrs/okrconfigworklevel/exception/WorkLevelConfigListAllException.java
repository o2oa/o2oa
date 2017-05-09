package com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception;

import com.x.base.core.exception.PromptException;

public class WorkLevelConfigListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkLevelConfigListAllException( Throwable e ) {
		super("系统根据ID查询指定的工作级别配置时发生异常。", e);
	}
}
