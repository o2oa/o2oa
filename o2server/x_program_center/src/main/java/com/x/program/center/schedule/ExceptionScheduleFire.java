package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionScheduleFire extends LanguagePromptException {

	private static final long serialVersionUID = -8597019540568284908L;

	ExceptionScheduleFire(Throwable th, String className, String cron, String node, String application) {
		super(th, "schedule fire failure, className:{}, cron:{}, node:{}, application:{}. ", className, cron, node,
				application);
	}
}