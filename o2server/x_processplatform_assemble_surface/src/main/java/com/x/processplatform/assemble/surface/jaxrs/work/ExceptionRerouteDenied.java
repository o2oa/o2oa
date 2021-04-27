package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRerouteDenied extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionRerouteDenied(String person, String title, String activityName) {
		super("用户 {} 调度工作{} 到活动:{}, 被拒绝.", person, title, activityName);
	}
}
