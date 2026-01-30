package com.x.teamwork.assemble.control.jaxrs.tasktag;

import com.x.base.core.project.exception.PromptException;

class TaskTagIdForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskTagIdForQueryEmptyException() {
		super("查询的工作任务标签信息ID为空，无法继续查询数据。" );
	}
}
