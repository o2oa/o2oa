package com.x.attendance.assemble.control.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;

/**
 * 定时统计代理，定时对所有的统计需求进行统计
 * 
 * @author LIYI
 *
 */
public class AttendanceStatisticTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticTask.class );

	public void run() {
		AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
		attendanceStatisticServiceAdv.doStatistic();
		logger.debug( "Timertask[AttendanceStatisticTask] completed and excute success." );
	}
}