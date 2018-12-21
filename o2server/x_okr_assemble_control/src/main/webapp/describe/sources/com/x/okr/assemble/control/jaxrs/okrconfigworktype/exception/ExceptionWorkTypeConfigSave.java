package com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkTypeConfigSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkTypeConfigSave( Throwable e ) {
		super("系统保存工作类别配置时发生异常。", e);
	}
}
