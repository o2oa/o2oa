package com.x.message.assemble.communicate.jaxrs.connector;

import com.x.base.core.project.exception.PromptException;

class ExceptionCalendarApplicationNotFound extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCalendarApplicationNotFound() {
		super("没有找到 x_calendar_assemble_control 应用.");
	}
}
