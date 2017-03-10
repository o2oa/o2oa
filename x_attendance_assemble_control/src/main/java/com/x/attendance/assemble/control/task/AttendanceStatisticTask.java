package com.x.attendance.assemble.control.task;

import java.util.TimerTask;

import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

/**
 * 定时统计代理，定时对所有的统计需求进行统计
 * 
 * @author LIYI
 *
 */
public class AttendanceStatisticTask extends TimerTask {

	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticTask.class );

	public void run() {
		AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
		attendanceStatisticServiceAdv.doStatistic();
		logger.info( "Timertask completed and excute success." );
	}
}