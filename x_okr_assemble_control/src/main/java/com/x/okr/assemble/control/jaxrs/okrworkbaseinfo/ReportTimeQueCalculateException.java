package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class ReportTimeQueCalculateException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportTimeQueCalculateException( Throwable e ) {
		super("系统根据汇报周期信息计算汇报时间序列时发生异常。", e );
	}
}
