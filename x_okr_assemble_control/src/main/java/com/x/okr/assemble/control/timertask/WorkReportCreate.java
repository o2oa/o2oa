package com.x.okr.assemble.control.timertask;

import java.util.List;
import java.util.TimerTask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportOperationService;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

/**
 * 定时代理，定时对需要汇报的工作发起工作汇报拟稿的待办
 * 
 * @author LIYI
 *
 */
public class WorkReportCreate extends TimerTask {

	private Logger logger = LoggerFactory.getLogger(WorkReportCreate.class);
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrWorkReportOperationService okrWorkReportBaseInfoService = new OkrWorkReportOperationService();
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();

	public void run() {

		String config_report_autocreate = "CLOSE";
		String report_auto_over = "CLOSE";
		Boolean check = true;

		if (ThisApplication.getWorkReportCreateTaskRunning()) {
			logger.info("Timertask[WorkReportTaskCreate] service is running, wait for next time......");
			return;
		}

		// 判断系统是否已经启用了，REPORT_AUTOCREATE
		try {
			config_report_autocreate = okrConfigSystemService.getValueWithConfigCode("REPORT_AUTOCREATE");
		} catch (Exception e) {
			config_report_autocreate = "CLOSE";
			check = false;
			logger.warn("system get system config 'REPORT_SUPERVISOR' got an exception" );
			logger.error(e);
		}

		if (check) {
			if ("OPEN".equalsIgnoreCase(config_report_autocreate.toUpperCase())) {
				ThisApplication.setWorkReportCreateTaskRunning(true);
				List<String> ids = null;
				OkrWorkBaseInfo okrWorkBaseInfo = null;
				OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
				check = true;
				try {
					ids = okrWorkBaseInfoService.listNeedReportWorkIds();
				} catch (Exception e) {
					logger.warn("system list work ids what needs report new got an exception." );
					logger.error(e);
				}
				if (ids != null && ids.size() > 0) {
					for (String id : ids) {
						if (!ThisApplication.getWorkReportCreateTaskRunning()) {
							logger.info("工作汇报创建代理被中断。");
							break;
						}
						check = true;
						if (check) {
							try {
								okrWorkBaseInfo = okrWorkBaseInfoService.get(id);
							} catch (Exception e) {
								check = false;
								logger.warn("system get work{'id':'" + id + "'} got an exception.");
								logger.error(e);
							}
						}
						if( check ){
							try {
								report_auto_over = okrConfigSystemService.getValueWithConfigCode( "REPORT_AUTO_OVER" );
							} catch (Exception e) {
								report_auto_over = "CLOSE";
								logger.warn( "system get system config 'REPORT_AUTO_OVER' got an exception" );
								logger.error(e);
								
							}
						}
						if (check && okrWorkBaseInfo != null) {
							try {
								 logger.info( "system is creating report draft for work{'id':'"+id+"','title':'"+okrWorkBaseInfo.getTitle()+"'}......");
								/**
								 * 根据基础的信息，生成工作汇报的草稿信息，并且向责任者推送待办信息
								 */
								okrWorkReportBaseInfo = okrWorkReportBaseInfoService.createReportDraft( okrWorkBaseInfo, report_auto_over );
								if (okrWorkReportBaseInfo != null) {
									okrWorkDynamicsService.reportDynamic(okrWorkReportBaseInfo.getCenterId(),
											okrWorkReportBaseInfo.getCenterTitle(), okrWorkReportBaseInfo.getWorkId(),
											okrWorkReportBaseInfo.getWorkTitle(), okrWorkReportBaseInfo.getTitle(),
											okrWorkReportBaseInfo.getId(), "创建工作汇报", "SYSTEM", "SYSTEM", "SYSTEM",
											"创建工作汇报：" + okrWorkReportBaseInfo.getTitle(), "创建工作汇报成功！");
								}
							} catch (Exception e) {
								logger.warn("system create report draft for work{'id':'" + id + "'} got an exception." );
								logger.error(e);
							}
						} else {
							logger.warn("work{'id':'" + id + "'} not exists.");
						}
					}
				}
				ThisApplication.setWorkReportCreateTaskRunning(false);
				logger.debug("Timertask[WorkReportTaskCreate] completed and excute success.");
			}
		}
	}
}