package com.x.processplatform.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;
import com.x.processplatform.core.entity.content.Record;

class ExceptionInvokeCreateRecord extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionInvokeCreateRecord(Record record) {
		super("调用创建记录失败, record:{}.", record);
	}

}
