package com.x.teamwork.assemble.control.jaxrs.taskListTemplate;

import com.x.base.core.project.exception.PromptException;

class TaskListTemplateFlagForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskListTemplateFlagForQueryEmptyException() {
		super("查询的项目模板对应的泳道信息ID为空，无法继续查询数据。" );
	}
}
