package com.x.attendance.assemble.control.schedule;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import org.quartz.JobExecutionContext;

import java.util.List;

/**
 * 每天凌晨分析前一天未签退的打卡数据
 * 有一些用户是经常会忘记签退，没有最终的下班打卡数据
 */
public class DetailLastDayRecordAnalyseTask extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(DetailLastDayRecordAnalyseTask.class);

	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		List<String> ids = null;
		DateOperation dateOperation = new DateOperation();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ids = attendanceDetailServiceAdv.listRecordWithDateAndNoOffDuty( dateOperation.getNowDate() );
		} catch (Exception e) {
			logger.error(new QueryMobileDetailWithStatusException(0));
		}
		if(ListTools.isNotEmpty( ids )){
			for( String id : ids ){
				try {
					ThisApplication.detailAnalyseQueue.send( id );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
			}
		}

		logger.info("Timertask DetailLastDayRecordAnalyseTask completed and excute success.");
	}

}