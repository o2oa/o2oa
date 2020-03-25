package com.x.program.center.schedule;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.schedule.ScheduleRequest;

class ExceptionScheduleLastNotEnd extends PromptException {

	private static final long serialVersionUID = -7954335762204386602L;

	ExceptionScheduleLastNotEnd(ScheduleRequest request, String application) {
		super("abandon fire schedule className: {}, cron: {}, application: {}, last start time: {}, last run not ended.",
				request.getClassName(), request.getCron(), application, request.getLastStartTime());
	}
}