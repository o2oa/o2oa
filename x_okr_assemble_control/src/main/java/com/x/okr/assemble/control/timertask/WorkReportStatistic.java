package com.x.okr.assemble.control.timertask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.x.base.core.gson.XGsonBuilder;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrCenterWorkInfoService;
import com.x.okr.assemble.control.service.OkrCenterWorkReportStatisticService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;
import com.x.okr.assemble.control.timertask.entity.BaseWorkReportStatisticEntity;
import com.x.okr.assemble.control.timertask.entity.CenterWorkReportStatisticEntity;
import com.x.okr.assemble.control.timertask.entity.WorkReportProcessOpinionEntity;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportProcessLog;

/**
 * 定时代理，对工作的汇报情况进行统计分析。
 * 1、遍历所有未归档的工作，以中心工作为记录维度
 * 2、分析当前这一周的工作汇报情况
 * @author LIYI
 *
 */
public class WorkReportStatistic implements Runnable {

	private Logger logger = LoggerFactory.getLogger( WorkReportStatistic.class );
	private OkrCenterWorkReportStatisticService okrCenterWorkReportStatisticService = new OkrCenterWorkReportStatisticService();	
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	private OkrCenterWorkInfoService okrCenterWorkInfoService = new OkrCenterWorkInfoService();
	private OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	private OkrWorkReportBaseInfoService okrWorkReportBaseInfoService = new OkrWorkReportBaseInfoService();
	private OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new  OkrWorkReportDetailInfoService();
	private OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	private DateOperation dateOperation = new DateOperation();
	
	public void run() {
		
		if( ThisApplication.getWorkReportStatisticTaskRunning() ){
			logger.info( "Timertask[WorkReportStatistic] service is running, wait for next time......" );
			return;
		}
		ThisApplication.setWorkReportStatisticTaskRunning( true );		
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>此处编写定时任务的业务逻辑
		
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		String status = "正常";//如果不需要在统计里展示 ，就应该为已归档
		try {
			okrCenterWorkInfoList = okrCenterWorkInfoService.listAllCenterWorks( status );
		} catch (Exception e) {
			logger.error( "Timertask[WorkReportStatistic] service list all no archive centerworks got an exception.", e );
		}		
		if( okrCenterWorkInfoList != null && !okrCenterWorkInfoList.isEmpty() ){
			analyseCenterWorkReports( okrCenterWorkInfoList );
		}
		
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>业务逻辑结束
		ThisApplication.setWorkReportStatisticTaskRunning( false );
		logger.debug( "Timertask[WorkReportStatistic] completed and excute success." );
	}

	/**
	 * 分析所有未归档的中心工作中所有工作的汇报情况
	 * @param okrCenterWorkInfoList
	 * @return
	 */
	private void analyseCenterWorkReports( List<OkrCenterWorkInfo> okrCenterWorkInfoList ) {
		
		List<BaseWorkReportStatisticEntity> workReportStatisticList = null;
		CenterWorkReportStatisticEntity centerWorkReportStatisticEntity = null;
		String statisticContent = null;
		Gson gson = XGsonBuilder.pureGsonDateFormated();
		Date now = new Date();
		if( okrCenterWorkInfoList != null && !okrCenterWorkInfoList.isEmpty() ){
			List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
			for( OkrCenterWorkInfo okrCenterWorkInfo : okrCenterWorkInfoList ){
				try {
					okrWorkBaseInfoList = okrWorkBaseInfoService.listAllWorks( okrCenterWorkInfo.getId(), "正常" );
				} catch (Exception e) {
					logger.error( "Timertask[WorkReportStatistic] service list all processing works got an exception.", e );
				}
				workReportStatisticList = analyseWorkReports( okrWorkBaseInfoList );
				
				//查询该中心工作下所有的工作信息对象
				centerWorkReportStatisticEntity = new CenterWorkReportStatisticEntity();
				centerWorkReportStatisticEntity.setCenterId( okrCenterWorkInfo.getId() );
				centerWorkReportStatisticEntity.setCenterTitle( okrCenterWorkInfo.getTitle() );
				centerWorkReportStatisticEntity.setWorkTypeName( okrCenterWorkInfo.getDefaultWorkType() );
				centerWorkReportStatisticEntity.setWorkReportStatisticEntityList( workReportStatisticList );
				
				//将统计结果以JSON形式保存到数据库中
				statisticContent = gson.toJson( centerWorkReportStatisticEntity );
				
				try {
					okrCenterWorkReportStatisticService.saveWeekStatistic( now, okrCenterWorkInfo, statisticContent );
				} catch (Exception e) {
					logger.error( "Timertask[WorkReportStatistic] service saveWeekStatistic got an exception.", e );
				}
				try {
					okrCenterWorkReportStatisticService.saveMonthStatistic( now, okrCenterWorkInfo, statisticContent );
				} catch (Exception e) {
					logger.error( "Timertask[WorkReportStatistic] service saveMonthStatistic got an exception.", e );
				}
			}
		}
	}

	/**
	 * 分析一组工作信息的汇报情况（一般为同一个中心工作的所有具体工作信息，一个中心工作下可能有几百个工作信息）
	 * @param okrWorkBaseInfoList
	 * @return
	 */
	private List<BaseWorkReportStatisticEntity> analyseWorkReports( List<OkrWorkBaseInfo> okrWorkBaseInfoList ) {
		List<BaseWorkReportStatisticEntity> workReportStatisticList = new ArrayList<BaseWorkReportStatisticEntity>();
		if( okrWorkBaseInfoList != null && !okrWorkBaseInfoList.isEmpty() ){
			Integer weekNumber = dateOperation.getWeekNumOfYear( new Date() );//获取本周在全年中所在周期数
			Integer reportWeekNumber = 0;
			Integer month = dateOperation.getMonthNumber( new Date() );//获取月份
			Integer reportMonth = 0;
			List<String> ids = null;
			List<WorkReportProcessOpinionEntity> workReportProcessOpinionEntityList = null;
			List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
			BaseWorkReportStatisticEntity workReportStatisticEntity = null;
			WorkReportProcessOpinionEntity workReportProcessOpinionEntity = null;
			OkrWorkDetailInfo okrWorkDetailInfo = null;
			OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
			OkrWorkReportDetailInfo okrWorkReportDetailInfo  = null;
			for( OkrWorkBaseInfo okrWorkBaseInfo : okrWorkBaseInfoList ){
				workReportProcessOpinionEntityList = new ArrayList<WorkReportProcessOpinionEntity>();
				workReportStatisticEntity = new BaseWorkReportStatisticEntity();
				//组织工作基础信息
				//workReportStatisticEntity.setCenterId(centerId);
				//workReportStatisticEntity.setCenterTitle(centerTitle);
				workReportStatisticEntity.setWorkId( okrWorkBaseInfo.getId() );
				workReportStatisticEntity.setParentWorkId( okrWorkBaseInfo.getParentWorkId() );
				workReportStatisticEntity.setWorkLevel( okrWorkBaseInfo.getWorkAuditLevel() + "" );
				workReportStatisticEntity.setReportDayInCycle( okrWorkBaseInfo.getReportDayInCycle() );
				workReportStatisticEntity.setWorkTitle( okrWorkBaseInfo.getTitle() );
				workReportStatisticEntity.setOrganizationName( okrWorkBaseInfo.getResponsibilityOrganizationName() );
				workReportStatisticEntity.setCompanyName( okrWorkBaseInfo.getResponsibilityCompanyName() );
				workReportStatisticEntity.setResponsibilityIdentity( okrWorkBaseInfo.getResponsibilityIdentity() );
				workReportStatisticEntity.setWorkTypeName( okrWorkBaseInfo.getWorkType() );				
				
				//查询该工作的具体详细信息对象
				try {
					okrWorkDetailInfo = okrWorkDetailInfoService.get( okrWorkBaseInfo.getId() );
				} catch (Exception e) {
					logger.error( "system get work detail with id got an exception.", e );
				}
				
				if( okrWorkDetailInfo != null ){
					workReportStatisticEntity.setWorkDetail( okrWorkDetailInfo.getWorkDetail() );
					workReportStatisticEntity.setLandmarkDescription( okrWorkDetailInfo.getLandmarkDescription() );
					workReportStatisticEntity.setProgressAction( okrWorkDetailInfo.getProgressAction() );
				}
				
				
				//查询该工作最近一次的汇报基础信息,不是草稿，一定要有提交时间数据 
				try {
					okrWorkReportBaseInfo = okrWorkReportBaseInfoService.getLastReportBaseInfo( okrWorkBaseInfo.getId() );
				} catch (Exception e) {
					logger.error( "system get last report info by work id with id got an exception.", e );
				}
				
				//判断工作最近一次的汇报时间是否是本周，或者大于本周，如果已经完成，则取最后一次汇报
				if( okrWorkReportBaseInfo != null && okrWorkReportBaseInfo.getSubmitTime() != null ){
					reportWeekNumber = dateOperation.getWeekNumOfYear( okrWorkReportBaseInfo.getSubmitTime() );
					reportMonth = dateOperation.getMonthNumber( okrWorkReportBaseInfo.getSubmitTime() );
				}
				
				//不需要汇报|每月汇报|每周汇报
				if( "每月汇报".equals( okrWorkBaseInfo.getReportCycle() ) ){
					/////////////////////////////////////////////////////////////////////////////////////////////////////
					workReportStatisticEntity.setReportCycle( "每月汇报" );
					//如果最近一次的汇报时间是本月，那么查询该汇报的详细 信息。
					//看看汇报所在的周期是否大于或者等于当前月份，或者工作是否已经完成
					if( reportMonth >= month || "已完成".equals( okrWorkBaseInfo.getWorkProcessStatus() ) ){
						if( "已完成".equals( okrWorkBaseInfo.getWorkProcessStatus() ) ){
							workReportStatisticEntity.setReportStatus( "工作已完成" );
						}else{
							workReportStatisticEntity.setReportStatus( "已提交汇报" );
						}
						//将当前汇报作为最终汇报内容,获取汇报详细内容
						if( okrWorkReportBaseInfo != null ){
							try {
								okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( okrWorkReportBaseInfo.getId() );
							} catch (Exception e) {
								logger.error( "system get okrWorkReportDetailInfo by id got an exception. ", e );
							}
							//查询汇报的所有处理记录，获取所有的领导审核意见
							try {
								ids = okrWorkReportProcessLogService.listByReportId( okrWorkReportBaseInfo.getId() );
							} catch (Exception e) {
								logger.error( "system list workReportProcessLog ids by workReport id got an exception. ", e );
							}
							try {
								okrWorkReportProcessLogList = okrWorkReportProcessLogService.list( ids );
							} catch (Exception e) {
								logger.error( "system list workReportProcessLog by ids got an exception. ", e );
							}
						}
						
						if( okrWorkReportDetailInfo != null ){
							workReportStatisticEntity.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
							workReportStatisticEntity.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
							workReportStatisticEntity.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
							workReportStatisticEntity.setAdminSuperviseInfo( okrWorkReportDetailInfo.getAdminSuperviseInfo() );
						}
						
						if( okrWorkReportProcessLogList != null && !okrWorkReportProcessLogList.isEmpty() ){
							//组织所有的审核意见
							for( OkrWorkReportProcessLog okrWorkReportProcessLog : okrWorkReportProcessLogList ){
								if( "领导审核".equals( okrWorkReportProcessLog.getActivityName() ) && "正常".equals( okrWorkReportProcessLog.getStatus() ) ){
									workReportProcessOpinionEntity = new WorkReportProcessOpinionEntity();
									workReportProcessOpinionEntity.setOpinion( okrWorkReportProcessLog.getOpinion() );
									workReportProcessOpinionEntity.setProcessorCompanyName( okrWorkReportProcessLog.getProcessorCompanyName() );
									workReportProcessOpinionEntity.setProcessorName( okrWorkReportProcessLog.getProcessorName() );
									workReportProcessOpinionEntity.setProcessorOrganizationName( okrWorkReportProcessLog.getProcessorOrganizationName() );
									workReportProcessOpinionEntity.setProcessTimeStr( okrWorkReportProcessLog.getProcessTimeStr() );
									workReportProcessOpinionEntityList.add( workReportProcessOpinionEntity );
									workReportStatisticEntity.setOpinions( workReportProcessOpinionEntityList );
								}
							}
						}
					}
				}else if ( "每周汇报".equals( okrWorkBaseInfo.getReportCycle() ) ){
					///////////////////////////////////////////////////////////////////////////////////////////////////////
					workReportStatisticEntity.setReportCycle( "每周汇报" );
					//如果最近一次的汇报时间是本周，那么查询该汇报的详细 信息。
					//看看汇报所在的周期是否大于或者等于当前周数，或者工作是否已经完成
					if( reportWeekNumber >= weekNumber || "已完成".equals( okrWorkBaseInfo.getWorkProcessStatus() ) ){
						if( "已完成".equals( okrWorkBaseInfo.getWorkProcessStatus() ) ){
							workReportStatisticEntity.setReportStatus( "工作已完成" );
						}else{
							workReportStatisticEntity.setReportStatus( "已提交汇报" );
						}
						//将当前汇报作为最终汇报内容,获取汇报详细内容
						if( okrWorkReportBaseInfo != null ){
							try {
								okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( okrWorkReportBaseInfo.getId() );
							} catch (Exception e) {
								logger.error( "system get okrWorkReportDetailInfo by id got an exception. ", e );
							}
							//查询汇报的所有处理记录，获取所有的领导审核意见
							try {
								ids = okrWorkReportProcessLogService.listByReportId( okrWorkReportBaseInfo.getId() );
							} catch (Exception e) {
								logger.error( "system list workReportProcessLog ids by workReport id got an exception. ", e );
							}
							try {
								okrWorkReportProcessLogList = okrWorkReportProcessLogService.list( ids );
							} catch (Exception e) {
								logger.error( "system list workReportProcessLog by ids got an exception. ", e );
							}
						}
						
						if( okrWorkReportDetailInfo != null ){
							workReportStatisticEntity.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
							workReportStatisticEntity.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
							workReportStatisticEntity.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
							workReportStatisticEntity.setAdminSuperviseInfo( okrWorkReportDetailInfo.getAdminSuperviseInfo() );
						}
						
						if( okrWorkReportProcessLogList != null && !okrWorkReportProcessLogList.isEmpty() ){
							//组织所有的审核意见
							for( OkrWorkReportProcessLog okrWorkReportProcessLog : okrWorkReportProcessLogList ){
								if( "领导批示".equals( okrWorkReportProcessLog.getActivityName() ) && "正常".equals( okrWorkReportProcessLog.getStatus() ) ){
									workReportProcessOpinionEntity = new WorkReportProcessOpinionEntity();
									workReportProcessOpinionEntity.setOpinion( okrWorkReportProcessLog.getOpinion() );
									workReportProcessOpinionEntity.setProcessorCompanyName( okrWorkReportProcessLog.getProcessorCompanyName() );
									workReportProcessOpinionEntity.setProcessorName( okrWorkReportProcessLog.getProcessorName() );
									workReportProcessOpinionEntity.setProcessorOrganizationName( okrWorkReportProcessLog.getProcessorOrganizationName() );
									workReportProcessOpinionEntity.setProcessTimeStr( okrWorkReportProcessLog.getProcessTimeStr() );
									workReportProcessOpinionEntityList.add( workReportProcessOpinionEntity );
									workReportStatisticEntity.setOpinions( workReportProcessOpinionEntityList );
								}
							}
						}
					}
				}else{
					//不需要汇报//////////////////////////////////////////////////////////////////////////////////////////
					workReportStatisticEntity.setReportCycle( "不需要汇报" );
					workReportStatisticEntity.setReportStatus( "不需要汇报" );
					workReportStatisticEntity.setNeedReport( false );
				}
				workReportStatisticList.add( workReportStatisticEntity );
			}
		}
		return workReportStatisticList;
	}
}