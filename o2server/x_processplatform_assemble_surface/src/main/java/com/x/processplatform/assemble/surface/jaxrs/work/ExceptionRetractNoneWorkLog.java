package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionRetractNoneWorkLog extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionRetractNoneWorkLog(String person, String title, String workId, String taskCompletedId) {
		super("person: {}, retract work title: {} id: {}, can not find workLog of taskCompleted:{}.", person, title,
				workId, taskCompletedId);
	}
}
