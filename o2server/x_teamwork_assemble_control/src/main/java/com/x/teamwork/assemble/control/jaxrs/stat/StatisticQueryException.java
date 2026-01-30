package com.x.teamwork.assemble.control.jaxrs.stat;

import com.x.base.core.project.exception.PromptException;

class StatisticQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	StatisticQueryException(String message) {
		super("统计查询工作任务信息失败:{}", message );
	}
}
