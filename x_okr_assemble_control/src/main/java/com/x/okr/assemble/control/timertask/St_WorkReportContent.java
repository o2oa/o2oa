package com.x.okr.assemble.control.timertask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.google.gson.Gson;
import com.x.base.core.gson.XGsonBuilder;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrStatisticReportContentService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.assemble.control.timertask.entity.BaseWorkReportStatisticEntity;
import com.x.okr.assemble.control.timertask.entity.WorkReportProcessOpinionEntity;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportProcessLog;

/**
 * 定时代理，对工作的汇报情况进行统计分析。 
 * 1、遍历所有未归档的工作，以工作为记录维度 ,有多少工作就有多少条记录
 * 2、分析当前这一周的工作汇报情况
 * 
 * @author LIYI
 *
 */
public class St_WorkReportContent extends TimerTask {

	private Logger logger = LoggerFactory.getLogger( St_WorkReportContent.class );
	private OkrStatisticReportContentService okrStatisticReportContentService = new OkrStatisticReportContentService();
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	private OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	private OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	private OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	private DateOperation dateOperation = new DateOperation();

	public void run() {

		if ( ThisApplication.getWorkReportStatisticTaskRunning() ) {
			logger.info("Timertask[WorkReportStatistic] service is running, wait for next time......");
			return;
		}
		ThisApplication.setWorkReportStatisticTaskRunning(true);

		List<String> workIds = null;
		String status = "正常";// 如果不需要在统计里展示 ，就应该为已归档
		try {
			workIds = okrWorkBaseInfoService.listAllDeployedWorkIds( null, status );
		} catch (Exception e) {
			logger.warn("Timertask service list all no archive work ids got an exception." );
			logger.error(e);
		}
		if (workIds != null && !workIds.isEmpty()) {
			analyseWorksReportContent( workIds );
		}

		ThisApplication.setWorkReportStatisticTaskRunning(false);
		logger.debug("Timertask[WorkReportStatistic] completed and excute success.");
	}

	/**
	 * 分析所有工作的工作汇报情况数据
	 * @param workIds
	 */
	public void analyseWorksReportContent( List<String> workIds ) {
		if( workIds == null || workIds.isEmpty() ){
			return;
		}
		
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<WorkReportProcessOpinionEntity> opinions = null;
		OkrStatisticReportContent statisticReportContent = null;
		BaseWorkReportStatisticEntity reportStatisticEntity = null;
		Boolean check = true;
		
		Integer weekNumber = dateOperation.getWeekNumOfYear(new Date());// 获取本周在全年中所在周期数
		Integer month = dateOperation.getMonthNumber(new Date());// 获取月份
		Integer year = dateOperation.getYearNumber( new Date() );// 获取月份
		Date now = new Date();
		String statisticDate = null;
		String opinionJson = null;
		Gson gson = XGsonBuilder.pureGsonDateFormated();
		
		try {
			statisticDate = dateOperation.getDateStringFromDate( now, "yyyy-MM-dd");
		} catch (Exception e ) {
			logger.warn( "system format date got an exception." );
			logger.error(e);
		}
		
		if( check ){
			try {
				okrWorkBaseInfoList = okrWorkBaseInfoService.listByIds( workIds );
			} catch (Exception e) {
				check = false;
				logger.warn("Timertask service list all processing works got an exception." );
				logger.error(e);
			}
		}
		if( check ){
			if( okrWorkBaseInfoList != null && !okrWorkBaseInfoList.isEmpty() ){
				int i=0;
				int total = okrWorkBaseInfoList.size();
				for( OkrWorkBaseInfo work : okrWorkBaseInfoList ){
					i++;
					logger.info( "system getting report content statistic for work "+i+"/"+total+" named:["+ work.getTitle() +"]......" );
					opinionJson = null;
					reportStatisticEntity = analyseWorkReport( work );
					
					statisticReportContent = new OkrStatisticReportContent();
					statisticReportContent.setStatisticTime( now );
					statisticReportContent.setStatisticTimeFlag( statisticDate );
					statisticReportContent.setCenterId( work.getCenterId() );
					statisticReportContent.setCenterTitle( work.getCenterTitle() );
					if( work.getParentWorkId() != null && !work.getParentWorkId().isEmpty() && work.getParentWorkId().trim().length() > 1  ){
						statisticReportContent.setParentId( work.getParentWorkId() );
					}else{
						statisticReportContent.setParentId( null );
					}
					statisticReportContent.setWorkId( work.getId() );
					statisticReportContent.setWorkTitle( work.getTitle() );
					statisticReportContent.setWorkType( work.getWorkType() );
					statisticReportContent.setWorkLevel( work.getWorkLevel() );
					statisticReportContent.setIsCompleted( work.getIsCompleted() );
					statisticReportContent.setIsOverTime( work.getIsOverTime() );
					
					statisticReportContent.setStatisticYear( year );
					statisticReportContent.setStatisticMonth( month );
					statisticReportContent.setStatisticWeek( weekNumber );
					statisticReportContent.setCycleType( work.getReportCycle() );
					statisticReportContent.setReportDayInCycle( work.getReportDayInCycle() );
					statisticReportContent.setReportId( reportStatisticEntity.getReportId() );
					
					statisticReportContent.setResponsibilityCompanyName( work.getResponsibilityCompanyName() );
					statisticReportContent.setResponsibilityEmployeeName( work.getResponsibilityEmployeeName() );
					statisticReportContent.setResponsibilityIdentity( work.getResponsibilityIdentity() );
					statisticReportContent.setResponsibilityOrganizationName( work.getResponsibilityOrganizationName() );
					
					statisticReportContent.setReportStatus( reportStatisticEntity.getReportStatus() );
					statisticReportContent.setWorkPlan( reportStatisticEntity.getWorkPlan() );
					statisticReportContent.setAdminSuperviseInfo( reportStatisticEntity.getAdminSuperviseInfo() );
					statisticReportContent.setProgressDescription( reportStatisticEntity.getProgressDescription() );
					statisticReportContent.setWorkPointAndRequirements( reportStatisticEntity.getWorkPointAndRequirements() );
					statisticReportContent.setMemo( reportStatisticEntity.getReportMemo() );
					
					opinions = reportStatisticEntity.getOpinions();
					if( opinions != null && !opinions.isEmpty() ){
						opinionJson = gson.toJson( opinions );
					}
					if( opinionJson == null || opinionJson.isEmpty() ){
						opinionJson = "{}";
					}
					statisticReportContent.setOpinion( opinionJson );
					statisticReportContent.setWorkProcessStatus( work.getWorkProcessStatus() );
					statisticReportContent.setStatus( work.getStatus() );
					
					try {
						okrStatisticReportContentService.save( statisticReportContent );
					} catch (Exception e) {
						logger.warn( "system save work report content got an exception." );
						logger.error(e);
					}
				}
			}
		}
	}

	private BaseWorkReportStatisticEntity analyseWorkReport( OkrWorkBaseInfo work ) {
		Integer weekNumber = dateOperation.getWeekNumOfYear(new Date());// 获取本周在全年中所在周期数
		Integer month = dateOperation.getMonthNumber( new Date() );// 获取月份
		Integer reportWeekNumber = 0;
		Integer reportMonth = 0;
		List<String> ids = null;
		List<WorkReportProcessOpinionEntity> workReportProcessOpinionEntityList = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		BaseWorkReportStatisticEntity workReportStatisticEntity = null;
		WorkReportProcessOpinionEntity workReportProcessOpinionEntity = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		workReportProcessOpinionEntityList = new ArrayList<WorkReportProcessOpinionEntity>();
		workReportStatisticEntity = new BaseWorkReportStatisticEntity();


		// 查询该工作最近一次的汇报基础信息,不是草稿，一定要有提交时间数据
		try {
			okrWorkReportBaseInfo = okrWorkReportQueryService.getLastReportBaseInfo( work.getId() );
		} catch (Exception e) {
			logger.warn("system get last report info by work id with id got an exception." );
			logger.error(e);
		}

		// 判断工作最近一次的汇报时间是否是本周，或者大于本周，如果已经完成，则取最后一次汇报
		if (okrWorkReportBaseInfo != null && okrWorkReportBaseInfo.getSubmitTime() != null) {
			reportWeekNumber = dateOperation.getWeekNumOfYear(okrWorkReportBaseInfo.getSubmitTime());
			reportMonth = dateOperation.getMonthNumber(okrWorkReportBaseInfo.getSubmitTime());
		}

		// 不需要汇报|每月汇报|每周汇报
		if ("每月汇报".equals(  work.getReportCycle()) ) {
			// 如果最近一次的汇报时间是本月，那么查询该汇报的详细 信息。
			// 看看汇报所在的周期是否大于或者等于当前月份，或者工作是否已经完成
			if (reportMonth >= month || "已完成".equals( work.getWorkProcessStatus())) {
				if ("已完成".equals( work.getWorkProcessStatus())) {
					workReportStatisticEntity.setReportStatus("工作已完成");
				} else {
					workReportStatisticEntity.setReportStatus("已提交汇报");
				}
				// 将当前汇报作为最终汇报内容,获取汇报详细内容
				if (okrWorkReportBaseInfo != null) {
					workReportStatisticEntity.setReportId( okrWorkReportBaseInfo.getId() );
					try {
						okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get(okrWorkReportBaseInfo.getId());
					} catch (Exception e) {
						logger.warn("system get okrWorkReportDetailInfo by id got an exception. " );
						logger.error( e );
					}
					// 查询汇报的所有处理记录，获取所有的领导审核意见
					try {
						ids = okrWorkReportProcessLogService.listByReportId(okrWorkReportBaseInfo.getId());
					} catch (Exception e) {
						logger.warn("system list workReportProcessLog ids by workReport id got an exception. " );
						logger.error( e );
					}
					try {
						okrWorkReportProcessLogList = okrWorkReportProcessLogService.list(ids);
					} catch (Exception e) {
						logger.warn("system list workReportProcessLog by ids got an exception. " );
						logger.error(e);
					}
				}

				if ( okrWorkReportDetailInfo != null ) {
					workReportStatisticEntity.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
					workReportStatisticEntity.setProgressDescription(okrWorkReportDetailInfo.getProgressDescription());
					workReportStatisticEntity.setWorkPointAndRequirements(okrWorkReportDetailInfo.getWorkPointAndRequirements());
					workReportStatisticEntity.setAdminSuperviseInfo(okrWorkReportDetailInfo.getAdminSuperviseInfo());
				}

				if (okrWorkReportProcessLogList != null && !okrWorkReportProcessLogList.isEmpty()) {
					// 组织所有的审核意见
					for (OkrWorkReportProcessLog okrWorkReportProcessLog : okrWorkReportProcessLogList) {
						if ( "领导批示".equals(okrWorkReportProcessLog.getActivityName()) && "正常".equals(okrWorkReportProcessLog.getStatus())) {
							workReportProcessOpinionEntity = new WorkReportProcessOpinionEntity();
							workReportProcessOpinionEntity.setOpinion(okrWorkReportProcessLog.getOpinion());
							workReportProcessOpinionEntity.setProcessorCompanyName(okrWorkReportProcessLog.getProcessorCompanyName());
							workReportProcessOpinionEntity.setProcessorName(okrWorkReportProcessLog.getProcessorName());
							workReportProcessOpinionEntity.setProcessorOrganizationName(okrWorkReportProcessLog.getProcessorOrganizationName());
							workReportProcessOpinionEntity.setProcessTimeStr(okrWorkReportProcessLog.getProcessTimeStr());
							workReportProcessOpinionEntityList.add(workReportProcessOpinionEntity);
							workReportStatisticEntity.setOpinions(workReportProcessOpinionEntityList);
						}
					}
				}
			}
		} else if ("每周汇报".equals( work.getReportCycle()) ) {
			// 如果最近一次的汇报时间是本周，那么查询该汇报的详细 信息。
			// 看看汇报所在的周期是否大于或者等于当前周数，或者工作是否已经完成
			if (reportWeekNumber >= weekNumber || "已完成".equals( work.getWorkProcessStatus())) {
				if ("已完成".equals( work.getWorkProcessStatus())) {
					workReportStatisticEntity.setReportStatus("工作已完成");
				} else {
					workReportStatisticEntity.setReportStatus("已提交汇报");
				}
				// 将当前汇报作为最终汇报内容,获取汇报详细内容
				if (okrWorkReportBaseInfo != null) {
					try {
						okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get(okrWorkReportBaseInfo.getId());
					} catch (Exception e) {
						logger.warn("system get okrWorkReportDetailInfo by id got an exception. " );
						logger.error(e);
					}
					// 查询汇报的所有处理记录，获取所有的领导审核意见
					try {
						ids = okrWorkReportProcessLogService.listByReportId(okrWorkReportBaseInfo.getId());
					} catch (Exception e) {
						logger.warn("system list workReportProcessLog ids by workReport id got an exception. " );
						logger.error(e);
					}
					try {
						okrWorkReportProcessLogList = okrWorkReportProcessLogService.list(ids);
					} catch (Exception e) {
						logger.warn("system list workReportProcessLog by ids got an exception. " );
						logger.error(e);
					}
				}

				if ( okrWorkReportDetailInfo != null ) {
					workReportStatisticEntity.setWorkPlan(okrWorkReportDetailInfo.getWorkPlan());
					workReportStatisticEntity.setProgressDescription(okrWorkReportDetailInfo.getProgressDescription());
					workReportStatisticEntity.setWorkPointAndRequirements(okrWorkReportDetailInfo.getWorkPointAndRequirements());
					workReportStatisticEntity.setAdminSuperviseInfo(okrWorkReportDetailInfo.getAdminSuperviseInfo());
				}

				if (okrWorkReportProcessLogList != null && !okrWorkReportProcessLogList.isEmpty()) {
					// 组织所有的审核意见
					for (OkrWorkReportProcessLog okrWorkReportProcessLog : okrWorkReportProcessLogList) {
						if ("领导批示".equals(okrWorkReportProcessLog.getActivityName()) && "正常".equals(okrWorkReportProcessLog.getStatus())) {
							workReportProcessOpinionEntity = new WorkReportProcessOpinionEntity();
							workReportProcessOpinionEntity.setOpinion(okrWorkReportProcessLog.getOpinion());
							workReportProcessOpinionEntity.setProcessorCompanyName(okrWorkReportProcessLog.getProcessorCompanyName());
							workReportProcessOpinionEntity.setProcessorName(okrWorkReportProcessLog.getProcessorName());
							workReportProcessOpinionEntity.setProcessorOrganizationName(okrWorkReportProcessLog.getProcessorOrganizationName());
							workReportProcessOpinionEntity.setProcessTimeStr(okrWorkReportProcessLog.getProcessTimeStr());
							workReportProcessOpinionEntityList.add(workReportProcessOpinionEntity);
							workReportStatisticEntity.setOpinions( workReportProcessOpinionEntityList );
						}
					}
				}
			}
		} else {
			workReportStatisticEntity.setReportStatus("不需要汇报");
		}
		return workReportStatisticEntity;
	}
}