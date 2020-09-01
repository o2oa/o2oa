package com.x.program.center.schedule;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.PromptErrorLog;
import com.x.program.center.core.entity.ScheduleLog;
import com.x.program.center.core.entity.UnexpectedErrorLog;
import com.x.program.center.core.entity.WarnLog;

public class Cleanup extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(Cleanup.class);

	private List<Class<? extends JpaObject>> list = new ArrayList<>();

	private volatile int tag = 0;

	public Cleanup() {
		list.add(ScheduleLog.class);
		list.add(PromptErrorLog.class);
		list.add(UnexpectedErrorLog.class);
		list.add(WarnLog.class);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				ThisApplication.logQueue.send(new NameValuePair(list.get(tag).getName(), null));
				tag = (tag + 1) % list.size();
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}
}