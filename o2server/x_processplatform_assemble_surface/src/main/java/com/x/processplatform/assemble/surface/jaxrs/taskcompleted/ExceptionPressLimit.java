package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPressLimit extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionPressLimit(Integer interval, Integer minutes) {
		super("受到 {} 分钟内 {} 次触发的限制.", interval, minutes);
	}
}
