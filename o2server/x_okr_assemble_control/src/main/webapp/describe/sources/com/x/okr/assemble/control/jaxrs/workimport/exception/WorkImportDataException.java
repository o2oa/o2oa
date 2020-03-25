package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class WorkImportDataException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkImportDataException( Throwable e ) {
		super("系统在导入所有待保存数据信息时发生未知异常。", e );
	}
}
