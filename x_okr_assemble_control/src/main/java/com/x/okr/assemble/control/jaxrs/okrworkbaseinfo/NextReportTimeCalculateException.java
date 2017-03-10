package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class NextReportTimeCalculateException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	NextReportTimeCalculateException( Throwable e ) {
		super("系统根据汇报周期信息计算下一次汇报时间时发生异常。", e );
	}
}
