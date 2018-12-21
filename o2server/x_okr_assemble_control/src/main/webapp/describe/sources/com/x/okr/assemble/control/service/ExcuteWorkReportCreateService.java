package com.x.okr.assemble.control.service;

import java.util.List;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.dataadapter.webservice.sms.SmsMessageOperator;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteWorkReportCreateService {
	
	private static  Logger logger = LoggerFactory.getLogger( ExcuteWorkReportCreateService.class );
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	private OkrWorkReportOperationService okrWorkReportBaseInfoService = new OkrWorkReportOperationService();
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	
	public void execute(){
		String config_report_autocreate = "CLOSE";
		String report_auto_over = "CLOSE";
		Boolean check = true;

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
						if (check && okrWorkBaseInfo != null && okrWorkBaseInfo.getNextReportTime() !=null ) {
							try {
								 logger.info( "system is creating report draft for work{'id':'"+id+"','title':'"+okrWorkBaseInfo.getTitle()+"'}......");
								/**
								 * 根据基础的信息，生成工作汇报的草稿信息，并且向责任者推送待办信息
								 */
								okrWorkReportBaseInfo = okrWorkReportBaseInfoService.createReportDraft( okrWorkBaseInfo, report_auto_over );
								if (okrWorkReportBaseInfo != null) {
									WrapInWorkDynamic.sendWithWorkReport( okrWorkReportBaseInfo, 
											"SYSTEM", 
											"SYSTEM", 
											"SYSTEM" , 
											"创建工作汇报", 
											"工作汇报创建成功！"
									);
									SmsMessageOperator.sendWithPersonName(okrWorkBaseInfo.getResponsibilityEmployeeName(), "您的工作'"+okrWorkReportBaseInfo.getTitle()+"'定期汇报已经生成，请及时填写！");
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
				logger.info("Timertask_OKR_WorkReportCreate completed and excute success.");
			}
		}
	}
}
