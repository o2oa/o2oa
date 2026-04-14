package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class TaskGroupIdAndProjectEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskGroupIdAndProjectEmptyException() {
		super("保存的工作任务信息时项目ID和工作任务组ID不能全部为空。" );
	}
}
