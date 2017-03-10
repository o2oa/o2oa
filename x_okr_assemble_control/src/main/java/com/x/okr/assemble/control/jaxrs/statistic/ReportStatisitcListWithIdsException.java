package com.x.okr.assemble.control.jaxrs.statistic;

import com.x.base.core.exception.PromptException;

class ReportStatisitcListWithIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportStatisitcListWithIdsException( Throwable e ) {
		super("根据ID列表查询汇报情况统计数据时发生异常。", e );
	}
}
