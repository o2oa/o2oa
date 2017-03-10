package com.x.attendance.assemble.control.task;

import com.x.base.core.exception.PromptException;

class AnalyseMobileDetailByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AnalyseMobileDetailByIdException( String id ) {
		super("根据ID分析手机打卡信息发生异常.ID：" + id );
	}
}
