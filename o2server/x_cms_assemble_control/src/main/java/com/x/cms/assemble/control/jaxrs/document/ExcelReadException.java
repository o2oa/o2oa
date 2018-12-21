package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.PromptException;

class ExcelReadException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExcelReadException( Throwable e ) {
		super("系统从EXCEL文件获取数据时发生异常." , e );
	}
}
