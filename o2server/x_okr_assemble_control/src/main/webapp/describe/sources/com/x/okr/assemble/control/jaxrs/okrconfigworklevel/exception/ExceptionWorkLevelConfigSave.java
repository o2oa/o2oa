package com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkLevelConfigSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkLevelConfigSave( Throwable e ) {
		super("系统保存工作级别配置时发生异常。", e);
	}
}
