package com.x.server.console.action;

import com.x.processplatform.core.entity.element.FormVersion;
import com.x.processplatform.core.entity.element.ProcessVersion;
import com.x.processplatform.core.entity.element.ScriptVersion;
import com.x.program.center.core.entity.PromptErrorLog;
import com.x.program.center.core.entity.ScheduleLog;
import com.x.program.center.core.entity.UnexpectedErrorLog;
import com.x.program.center.core.entity.WarnLog;

public class EraseContentLog extends EraseContent {

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
		this.run();
		return true;
	}
}