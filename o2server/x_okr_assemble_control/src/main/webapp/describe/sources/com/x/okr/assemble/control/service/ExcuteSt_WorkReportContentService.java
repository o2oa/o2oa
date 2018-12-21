package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.schedule.entity.BaseWorkReportStatisticEntity;
import com.x.okr.assemble.control.schedule.entity.WorkReportProcessOpinionEntity;
import com.x.okr.entity.OkrStatisticReportContent;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class ExcuteSt_WorkReportContentService {
	private static Logger logger = LoggerFactory.getLogger( ExcuteSt_WorkReportContentService.class);
	private OkrStatisticReportContentService okrStatisticReportContentService = new OkrStatisticReportContentService();
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	private OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	private OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	private OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	private DateOperation dateOperation = new DateOperation();
	
	public void execute() {

		List<String> workIds = null;
		String status = "All";// 如果不需要在统计里展示 ，就应该为已归档
		try {
			workIds = okrWorkBaseInfoService.listAllDeployedWorkIds( null, status );
		} catch (Exception e) {
			logger.warn("ExcuteSt_WorkReportContentService service list all no archive work ids got an exception." );
			logger.error(e);
		}
		if (workIds != null && !workIds.isEmpty()) {
			analyseWorksReportContent( workIds );
		}
	}
	
	public void executeAll() {

		List<String> workIds = null;
		try {
			workIds = okrWorkBaseInfoService.listAllDeployedWorkIds( null, null );
		} catch (Exception e) {
			logger.warn("ExcuteSt_WorkReportContentService service list all no archive work ids got an exception." );
			logger.error(e);
		}
		if (workIds != null && !workIds.isEmpty()) {
			analyseWorksReportContent( workIds );
		}
	}

	/**
	 * 分析所有工作的工作汇报情况数据
	 * @param workIds
	 */
	public void analyseWorksReportContent( List<String> workIds ) {
		if( workIds == null || workIds.isEmpty() ){
			return;
		}
		List<WorkReportProcessOpinionEntity> opinions = null;
		OkrStatisticReportContent statisticReportContent = null;
		BaseWorkReportStatisticEntity reportStatisticEntity = null;
		OkrWorkBaseInfo work = null;
		Boolean check = true;
		
		Integer weekNumber = dateOperation.getWeekNumOfYear(new Date());// 获取本周在全年中所在周期数
		Integer month = dateOperation.getMonthNumber(new Date());// 获取月份
		Integer year = dateOperation.getYearNumber( new Date() );// 获取月份
		Date now = new Date();
		String statisticDate = null;
		String opinionJson = null;
		Gson gson = XGsonBuilder.instance();
		
		try {
			statisticDate = dateOperation.getDateStringFromDate( now, "yyyy-MM-dd");
		} catch (Exception e ) {
			logger.warn( "system format date got an exception." );
			logger.error(e);
		}
		
		if( check ){
			for( String workId : workIds ){
				statisticReportContent = new OkrStatisticReportContent();
				try {
					work = okrWorkBaseInfoService.get( workId );
					if( work != null ){
						opinionJson = null;
						reportStatisticEntity = analyseWorkReport( work );

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
						statisticReportContent.setResponsibilityTopUnitName( work.getResponsibilityTopUnitName() );
						statisticReportContent.setResponsibilityEmployeeName( work.getResponsibilityEmployeeName() );
						statisticReportContent.setResponsibilityIdentity( work.getResponsibilityIdentity() );
						statisticReportContent.setResponsibilityUnitName( work.getResponsibilityUnitName() );
						statisticReportContent.setReportStatus( reportStatisticEntity.getReportStatus() );
						statisticReportContent.setWorkPlan( reportStatisticEntity.getWorkPlan() );
						statisticReportContent.setAdminSuperviseInfo( reportStatisticEntity.getAdminSuperviseInfo() );
						statisticReportContent.setProgressDescription( reportStatisticEntity.getProgressDescription() );
						statisticReportContent.setWorkPointAndRequirements( reportStatisticEntity.getWorkPointAndRequirements() );
						statisticReportContent.setMemo( reportStatisticEntity.getReportMemo() );
						statisticReportContent.setWorkProcessStatus( work.getWorkProcessStatus() );
						statisticReportContent.setStatus( work.getStatus() );
						opinions = reportStatisticEntity.getOpinions();
						if( opinions != null && !opinions.isEmpty() ){
							opinionJson = gson.toJson( opinions );
						}
						if( opinionJson == null || opinionJson.isEmpty() ){
							opinionJson = "{}";
						}
						statisticReportContent.setOpinion( opinionJson );
						
						okrStatisticReportContentService.save( statisticReportContent );
					}else{
						throw new Exception("work is not exists! workId:" + workId );
					}
				} catch ( Exception e ) {
					logger.warn( "system save work report content got an exception." );
					logger.error(e);
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
			if ( reportMonth >= month || "已完成".equals( work.getWorkProcessStatus()) || work.getIsCompleted() ) {
				if ( "已完成".equals( work.getWorkProcessStatus()) || work.getIsCompleted() ) {
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
							workReportProcessOpinionEntity.setProcessorTopUnitName(okrWorkReportProcessLog.getProcessorTopUnitName());
							workReportProcessOpinionEntity.setProcessorName(okrWorkReportProcessLog.getProcessorName());
							workReportProcessOpinionEntity.setProcessorUnitName(okrWorkReportProcessLog.getProcessorUnitName());
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
			if (reportWeekNumber >= weekNumber || "已完成".equals( work.getWorkProcessStatus()) || work.getIsCompleted()) {
				if ("已完成".equals( work.getWorkProcessStatus()) || work.getIsCompleted()) {
					workReportStatisticEntity.setReportStatus("工作已完成");
				} else {
					workReportStatisticEntity.setReportStatus("已提交汇报");
				}
				// 将当前汇报作为最终汇报内容,获取汇报详细内容
				if ( okrWorkReportBaseInfo != null ) {
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

				if ( okrWorkReportProcessLogList != null && !okrWorkReportProcessLogList.isEmpty() ) {
					// 组织所有的审核意见
					for (OkrWorkReportProcessLog okrWorkReportProcessLog : okrWorkReportProcessLogList) {
						if ("领导批示".equals( okrWorkReportProcessLog.getActivityName()) && "正常".equals(okrWorkReportProcessLog.getStatus()) ) {
							workReportProcessOpinionEntity = new WorkReportProcessOpinionEntity();
							workReportProcessOpinionEntity.setOpinion(okrWorkReportProcessLog.getOpinion());
							workReportProcessOpinionEntity.setProcessorTopUnitName(okrWorkReportProcessLog.getProcessorTopUnitName());
							workReportProcessOpinionEntity.setProcessorName(okrWorkReportProcessLog.getProcessorName());
							workReportProcessOpinionEntity.setProcessorUnitName(okrWorkReportProcessLog.getProcessorUnitName());
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
