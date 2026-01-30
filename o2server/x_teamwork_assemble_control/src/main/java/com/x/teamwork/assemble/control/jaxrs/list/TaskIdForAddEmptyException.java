package com.x.teamwork.assemble.control.jaxrs.list;

import com.x.base.core.project.exception.PromptException;

class TaskIdForAddEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskIdForAddEmptyException() {
		super("需要添加到列表的工作任务信息ID为空，无法继续操作。" );
	}
}
