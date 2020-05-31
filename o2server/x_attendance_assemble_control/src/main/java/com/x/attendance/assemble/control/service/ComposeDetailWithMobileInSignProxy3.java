package com.x.attendance.assemble.control.service;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.List;

/**
 * 考勤打卡记录分析服务类
 * 3、一天四次打卡：打上午上班，上午下班，下午上班，下午下班四次卡
 */
class ComposeDetailWithMobileInSignProxy3 {
	
	private static  Logger logger = LoggerFactory.getLogger( ComposeDetailWithMobileInSignProxy3.class );
	private DateOperation dateOperation = new DateOperation();


	public AttendanceDetail compose(List<AttendanceDetailMobile> mobileDetails, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		return null;
	}
}
