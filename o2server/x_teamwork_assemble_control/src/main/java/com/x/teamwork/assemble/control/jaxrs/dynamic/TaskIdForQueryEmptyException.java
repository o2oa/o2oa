package com.x.teamwork.assemble.control.jaxrs.dynamic;

import com.x.base.core.project.exception.PromptException;

class TaskIdForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskIdForQueryEmptyException() {
		super("根据工作任务查询的工作动态时，工作任务ID为空，无法继续查询数据。" );
	}
}
