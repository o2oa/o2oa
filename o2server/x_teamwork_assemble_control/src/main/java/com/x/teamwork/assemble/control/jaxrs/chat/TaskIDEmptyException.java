package com.x.teamwork.assemble.control.jaxrs.chat;

import com.x.base.core.project.exception.PromptException;

class TaskIDEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskIDEmptyException() {
		super("工作任务信息ID为空。" );
	}
}
