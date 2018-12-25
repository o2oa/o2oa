package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class WorkImportDataCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkImportDataCheckException( Throwable e ) {
		super("系统在校验所有待保存数据信息时发生未知异常。", e );
	}
}
