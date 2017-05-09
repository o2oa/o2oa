package com.x.okr.assemble.control.timertask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.clock.ClockScheduleTask;
import com.x.okr.assemble.control.service.ExcuteSt_WorkReportContentService;

/**
 * 定时代理，对工作的汇报情况进行统计分析。 
 * 1、遍历所有未归档的工作，以工作为记录维度 ,有多少工作就有多少条记录
 * 2、分析当前这一周的工作汇报情况
 * 
 * @author LIYI
 *
 */
public class St_WorkReportContent extends ClockScheduleTask {

	private static Logger logger = LoggerFactory.getLogger(St_WorkReportContent.class);
	
	public St_WorkReportContent(Context context) {
		super(context);
	}
	
	public void execute() {
		try {
			new ExcuteSt_WorkReportContentService().execute();
			logger.info("Timertask OKR_St_WorkReportContent completed and excute success.");
		} catch (Exception e) {
			logger.error(e);
		}
	}

}