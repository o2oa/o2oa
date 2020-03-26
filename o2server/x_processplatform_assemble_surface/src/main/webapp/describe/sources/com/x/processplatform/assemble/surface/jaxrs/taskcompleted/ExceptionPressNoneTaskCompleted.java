package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionPressNoneTaskCompleted extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionPressNoneTaskCompleted(String workId, String person) {
		super("无法找到当前用户用于提醒的已办,工作标识:{}, 当前用户:{}.", workId, person);
	}
}
