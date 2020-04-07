package com.x.server.console.action;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionVersion extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionVersion.class);

	public void execute() throws Exception {
		logger.print(Config.version());
	}
}
