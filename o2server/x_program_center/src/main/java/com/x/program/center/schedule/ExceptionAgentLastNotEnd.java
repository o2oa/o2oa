package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.tools.DateTools;
import com.x.program.center.schedule.TriggerAgent.Pair;

class ExceptionAgentLastNotEnd extends LanguagePromptException {

	private static final long serialVersionUID = -7954335762204386602L;

	ExceptionAgentLastNotEnd(Pair pair) {
		super("放弃触发代理: {}, 名称 :{}, Cron表达式: {}, 上一次起动时间: {}, 上一次没有运行结束.", pair.getId(),
				pair.getName(), pair.getCron(),
				(pair.getLastStartTime() == null ? "" : DateTools.format(pair.getLastStartTime())));
	}
}