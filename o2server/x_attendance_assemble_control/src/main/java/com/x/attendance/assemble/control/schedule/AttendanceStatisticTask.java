package com.x.attendance.assemble.control.schedule;

import org.quartz.JobExecutionContext;

import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;

/**
 * 定时统计代理，定时对所有的统计需求进行统计
 * 
 * @author LIYI
 *
 */
public class AttendanceStatisticTask extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(AttendanceStatisticTask.class);
 
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
		attendanceStatisticServiceAdv.doStatistic(false);
		logger.info("Timertask completed and excute success.");
	}

}