package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import com.x.base.core.project.exception.PromptException;

class ExceptionJobNotMatch extends PromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionJobNotMatch(String job1, String job2) {
		super("任务标识不匹配:{},{}.", job1, job2);
	}
}
