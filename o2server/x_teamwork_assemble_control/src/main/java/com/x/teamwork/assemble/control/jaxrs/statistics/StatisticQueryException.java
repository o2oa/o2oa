package com.x.teamwork.assemble.control.jaxrs.statistics;

import com.x.base.core.project.exception.PromptException;

class StatisticQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	StatisticQueryException( Throwable e ) {
		super("统计查询工作任务信息时发生异常。" , e );
	}
	
	StatisticQueryException( Throwable e, String message ) {
		super("统计查询工作任务信息时发生异常。Message:" + message, e );
	}
}
