package com.x.okr.assemble.control.servlet.task;

import com.x.base.core.exception.PromptException;

class TaskCountQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskCountQueryException( Throwable e, String person ) {
		super("系统通过用户姓名查询用户待办数量时发生异常!Person:" + person +".", e );
	}
}
