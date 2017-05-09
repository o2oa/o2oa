package com.x.attendance.assemble.control.task;

import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.clock.ClockScheduleTask;

/**
 * 定时统计代理，定时对所有的统计需求进行统计
 * 
 * @author LIYI
 *
 */
public class AttendanceStatisticTask extends ClockScheduleTask {

	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticTask.class );

	public AttendanceStatisticTask(Context context) {
		super(context);
	}
	
	public void execute() {
		AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
		attendanceStatisticServiceAdv.doStatistic();
		logger.info( "Timertask completed and excute success." );
	}
}