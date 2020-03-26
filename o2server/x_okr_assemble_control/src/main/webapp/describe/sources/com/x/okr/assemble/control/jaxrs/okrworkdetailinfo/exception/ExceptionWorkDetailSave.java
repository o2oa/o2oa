package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkDetailSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkDetailSave( Throwable e ) {
		super("系统在保存工作详细信息时发生异常.", e );
	}
}
