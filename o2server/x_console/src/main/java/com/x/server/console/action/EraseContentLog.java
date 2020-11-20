package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.element.FormVersion;
import com.x.processplatform.core.entity.element.ProcessVersion;
import com.x.processplatform.core.entity.element.ScriptVersion;
import com.x.processplatform.core.entity.log.SignalStackLog;
import com.x.program.center.core.entity.PromptErrorLog;
import com.x.program.center.core.entity.ScheduleLog;
import com.x.program.center.core.entity.UnexpectedErrorLog;
import com.x.program.center.core.entity.WarnLog;

public class EraseContentLog extends EraseContent {

	private static Logger logger = LoggerFactory.getLogger(EraseContentLog.class);
	
	@Override
	public boolean execute() throws Exception {
		this.init("log", null);
		addClass(ScheduleLog.class);
		addClass(PromptErrorLog.class);
		addClass(UnexpectedErrorLog.class);
		addClass(WarnLog.class);
		addClass(ProcessVersion.class);
		addClass(FormVersion.class);
		addClass(ScriptVersion.class);
		addClass(SignalStackLog.class);
		this.run();
		return true;
	}
}