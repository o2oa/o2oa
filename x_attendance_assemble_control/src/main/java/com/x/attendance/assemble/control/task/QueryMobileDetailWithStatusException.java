package com.x.attendance.assemble.control.task;

import com.x.base.core.exception.PromptException;

class QueryMobileDetailWithStatusException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	QueryMobileDetailWithStatusException( int stauts ) {
		super("根据状态列示手机打卡记录查询异常.状态：" + stauts );
	}
}
