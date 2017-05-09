package com.x.base.core.project.clock;

import java.util.Date;
import java.util.TimerTask;

import com.x.base.core.project.Context;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.server.Config;

public abstract class ClockTimerTask extends TimerTask {

	protected Context context;

	public ClockTimerTask(Context context) {
		this.context = context;
	}

	public void run() {
		try {
			Date start = new Date();
			this.execute();
			Date end = new Date();
			String url = Config.x_program_centerUrlRoot() + "clock/timer/report";
			TimerReport report = new TimerReport(Config.node(), context.clazz().getName(), this.getClass().getName(),
					start, end, end.getTime() - start.getTime());
			CipherConnectionAction.post(url, report);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract void execute() throws Exception;

}
