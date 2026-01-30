package com.x.teamwork.assemble.control.jaxrs.tasktag;

import com.x.base.core.project.exception.PromptException;

class TagEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TagEmptyException() {
		super("工作任务标签信息中标签文字tag不允许为空。" );
	}
}
