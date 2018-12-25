package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportStatisitcListWithIds extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportStatisitcListWithIds( Throwable e ) {
		super("根据ID列表查询汇报情况统计数据时发生异常。", e );
	}
}
