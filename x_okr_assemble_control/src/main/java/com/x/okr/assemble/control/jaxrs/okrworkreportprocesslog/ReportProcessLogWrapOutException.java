package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog;

import com.x.base.core.exception.PromptException;

class ReportProcessLogWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportProcessLogWrapOutException( Throwable e ) {
		super("将查询结果转换为可输出的数据信息时发生异常。", e );
	}
}
