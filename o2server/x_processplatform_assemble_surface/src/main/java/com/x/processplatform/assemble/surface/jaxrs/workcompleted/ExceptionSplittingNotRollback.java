package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionSplittingNotRollback extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionSplittingNotRollback(String workId, String workLogId) {
		super("工作无法回滚到处于拆分的状态,work:{}, workLog:{}.", workId, workLogId);
	}
}
