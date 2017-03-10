package com.x.okr.assemble.control.servlet.workimport;

import com.x.base.core.exception.PromptException;

class WorkImportDataCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkImportDataCheckException( Throwable e ) {
		super("系统在校验所有待保存数据信息时发生未知异常。", e );
	}
}
