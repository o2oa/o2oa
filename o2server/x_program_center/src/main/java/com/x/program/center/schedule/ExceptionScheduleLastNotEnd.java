package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.schedule.ScheduleRequest;

class ExceptionScheduleLastNotEnd extends LanguagePromptException {

	private static final long serialVersionUID = -7954335762204386602L;

	ExceptionScheduleLastNotEnd(ScheduleRequest request, String application) {
		super("放弃定时代理: {}, Cron表达式: {}, 应用: {},最后一次起动时间: {}, 最后一次没有运行结束.",
				request.getClassName(), request.getCron(), application, request.getLastStartTime());
	}
}