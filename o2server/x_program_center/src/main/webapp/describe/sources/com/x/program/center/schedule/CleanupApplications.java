package com.x.program.center.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.Application;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.ReportToCenter;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.ThisApplication;

public class CleanupApplications implements Job {

	private static Logger logger = LoggerFactory.getLogger(CleanupApplications.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			boolean changed = false;
			List<String> removes = new ArrayList<>();
			for (Entry<String, CopyOnWriteArrayList<Application>> en : ThisApplication.context().applications()
					.entrySet()) {
				clearApplication(en.getValue());
				if (en.getValue().isEmpty()) {
					if (removes.add(en.getKey())) {
						changed = true;
					}
				}
			}
			for (String str : removes) {
				ThisApplication.context().applications().remove(str);
			}
			if (changed) {
				ThisApplication.context().applications().setToken(StringTools.uniqueToken());
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private boolean clearApplication(CopyOnWriteArrayList<Application> list) throws Exception {
		List<Application> removeApplications = new ArrayList<>();
		Date now = new Date();
		for (Application application : list) {
			/** 报告时间的2倍 */
			if ((now.getTime() - application.getReportDate().getTime()) > ReportToCenter.INTERVAL * 2 * 1000) {
				removeApplications.add(application);
			}
		}
		return list.removeAll(removeApplications);
	}

}