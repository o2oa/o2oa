package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.exception.PromptException;

class RetractNoneTaskCompletedException extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	RetractNoneTaskCompletedException(String person, String title, String id) {
		super("person: {}, retract work title: {} id: {}, can not find taskCompleted.", person, title, id);
	}
}
