package com.x.server.console.action;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.PromptErrorLog;
import com.x.program.center.core.entity.ScheduleLog;
import com.x.program.center.core.entity.UnexpectedErrorLog;
import com.x.program.center.core.entity.WarnLog;

public class ActionEraseContentLog extends ActionEraseContentProcessPlatform {

	private static Logger logger = LoggerFactory.getLogger(ActionEraseContentLog.class);

	public boolean execute(String password) throws Exception {
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			logger.print("password not match.");
			return false;
		}
		this.init("log", null);
		addClass(ScheduleLog.class);
		addClass(PromptErrorLog.class);
		addClass(UnexpectedErrorLog.class);
		addClass(WarnLog.class);
		this.run();
		return true;
	}
}