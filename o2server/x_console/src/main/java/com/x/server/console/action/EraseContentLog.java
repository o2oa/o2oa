package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.PromptErrorLog;
import com.x.program.center.core.entity.ScheduleLog;
import com.x.program.center.core.entity.UnexpectedErrorLog;
import com.x.program.center.core.entity.WarnLog;

public class EraseContentLog extends EraseContentProcessPlatform {

	private static Logger logger = LoggerFactory.getLogger(EraseContentLog.class);

	@Override
	public boolean execute() throws Exception {
		this.init("log", null);
		addClass(ScheduleLog.class);
		addClass(PromptErrorLog.class);
		addClass(UnexpectedErrorLog.class);
		addClass(WarnLog.class);
		this.run();
		return true;
	}
}