package com.x.program.center.schedule;

import java.util.Arrays;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(Cleanup.class);

	private static List<Class<? extends JpaObject>> list = Arrays.asList(ScheduleLog.class, PromptErrorLog.class,
			UnexpectedErrorLog.class, WarnLog.class);

	private static volatile int tag = 0;

	public Cleanup() {
		// nothing
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				ThisApplication.logQueue.send(new NameValuePair(list.get(tag).getName(), null));
				updateTag();
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new JobExecutionException(e);
		}
	}

	private static void updateTag() {
		tag = (tag + 1) % list.size();
	}
}