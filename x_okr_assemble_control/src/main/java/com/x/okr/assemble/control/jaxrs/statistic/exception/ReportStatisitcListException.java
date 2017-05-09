package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.exception.PromptException;

public class ReportStatisitcListException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReportStatisitcListException( Throwable e ) {
		super("根据条件查询汇报情况统计数据时发生异常。", e );
	}
}
