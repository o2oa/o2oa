package com.x.program.center.schedule;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.tools.DateTools;
import com.x.program.center.schedule.TriggerAgent.Pair;

class ExceptionAgentLastNotEnd extends PromptException {

	private static final long serialVersionUID = -7954335762204386602L;

	ExceptionAgentLastNotEnd(Pair pair) {
		super("abandon trigger agent : {}, name :{}, cron: {}, last start time: {}, last run not ended.", pair.getId(),
				pair.getName(), pair.getCron(),
				(pair.getLastStartTime() == null ? "" : DateTools.format(pair.getLastStartTime())));
	}
}