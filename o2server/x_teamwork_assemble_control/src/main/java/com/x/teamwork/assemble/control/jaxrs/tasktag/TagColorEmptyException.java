package com.x.teamwork.assemble.control.jaxrs.tasktag;

import com.x.base.core.project.exception.PromptException;

class TagColorEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TagColorEmptyException() {
		super("工作任务标签信息中标签颜色color不允许为空。" );
	}
}
