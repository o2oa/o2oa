package com.x.attendance.assemble.control.schedule;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.attendance.assemble.control.service.AttendanceDetailMobileAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailMobileService;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;

/**
 * 定期对用户手机打卡记录进行分析
 */
public class MobileRecordAnalyseTask extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(MobileRecordAnalyseTask.class);

	private AttendanceDetailMobileService attendanceDetailMobileService = new AttendanceDetailMobileService();

	private AttendanceDetailMobileAnalyseServiceAdv attendanceDetailMobileAnalyseServiceAdv = new AttendanceDetailMobileAnalyseServiceAdv();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		List<String> ids = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ids = attendanceDetailMobileService.listAllAnalyseWithStatus(emc, 0);
		} catch (Exception e) {
			logger.error(new QueryMobileDetailWithStatusException(0));
		}
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				try {
					attendanceDetailMobileAnalyseServiceAdv.analyseAttendanceDetailMobile(id, false);
				} catch (Exception e) {
					logger.error(new AnalyseMobileDetailByIdException(id));
				}
			}
		}
		logger.info("Timertask MobileRecordAnalyseTask completed and excute success.");
	}

}