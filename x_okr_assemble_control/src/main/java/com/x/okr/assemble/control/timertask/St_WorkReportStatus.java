package com.x.okr.assemble.control.timertask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.google.gson.Gson;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.common.date.MonthOfYear;
import com.x.okr.assemble.common.date.WeekOfYear;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrStatisticReportStatusService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.assemble.control.timertask.entity.WorkBaseReportSubmitEntity;
import com.x.okr.entity.OkrStatisticReportStatus;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;

/**
 * 定时代理，对工作的汇报提交情况进行统计分析
 * 
 * 1、遍历所有未归档的工作
 * 2、分析从工作开始日期到工作结束日期之间 所有周的工作汇报提交情况
 * 
 * @author LIYI
 *
 */
public class St_WorkReportStatus extends TimerTask{

	private Logger logger = LoggerFactory.getLogger( St_WorkReportStatus.class );
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	private OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	private OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	private OkrStatisticReportStatusService okrStatisticReportStatusService = new OkrStatisticReportStatusService();
	private DateOperation dateOperation = new DateOperation();

	public void run() {

		if ( ThisApplication.getWorkReportSubmitStatisticTaskRunning() ) {
			logger.info("Timertask service is running, wait for next time......");
			return;
		}
		ThisApplication.setWorkReportStatisticTaskRunning(true);

		List<String>  ids = null;
		String status = "正常";//如果不需要在统计里展示 ，就应该为已归档
		try {
			ids = okrWorkBaseInfoService.listAllDeployedWorkIds( null, status );
		} catch (Exception e) {
			logger.warn("Timertask service list all no archive centerworks got an exception." );
			logger.error(e);
		}
		if (ids != null && !ids.isEmpty()) {
			statisticWorksReportSubmit( ids );
		}

		ThisApplication.setWorkReportSubmitStatisticTaskRunning(false);
		logger.debug("Timertask completed and excute success.");
	}

	/**
	 * 分析所有未归档的工作中所有工作的汇报情况
	 * 
	 * @param okrCenterWorkInfoList
	 * @return
	 */
	public void statisticWorksReportSubmit( List<String> workIds ) {
		String statisticContent = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;		
		if ( workIds != null && !workIds.isEmpty() ) {
			int total = workIds.size();
			int i = 0;
			for ( String workId : workIds) {
				i++;
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
					if( okrWorkBaseInfo != null ){
						logger.info( "system getting report status statistic for work "+ i +"/"+total+" named:["+ okrWorkBaseInfo.getTitle() +"]......" );
						statisticContent = statisticWorkReports( okrWorkBaseInfo );
					}
					//将获取到的统计数据存储到数据库中，如果数据已经存在，则进行统计数据更新
					saveStatisticContentToDB( okrWorkBaseInfo, statisticContent );
				} catch (Exception e) {
					logger.warn("Timertask service list all processing works got an exception." );
					logger.error(e);
				}
			}
		}
	}

	private void saveStatisticContentToDB( OkrWorkBaseInfo okrWorkBaseInfo, String statisticContent ) throws Exception {
		OkrStatisticReportStatus statistic = new OkrStatisticReportStatus();
		statistic.setCenterId( okrWorkBaseInfo.getCenterId() );
		statistic.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
		statistic.setReportStatistic( statisticContent );
		statistic.setResponsibilityEmployeeName( okrWorkBaseInfo.getResponsibilityEmployeeName() );
		statistic.setResponsibilityCompanyName( okrWorkBaseInfo.getResponsibilityCompanyName() );
		statistic.setResponsibilityIdentity( okrWorkBaseInfo.getResponsibilityIdentity() );
		statistic.setResponsibilityOrganizationName( okrWorkBaseInfo.getResponsibilityOrganizationName() );
		statistic.setWorkId( okrWorkBaseInfo.getId() );
		if( okrWorkBaseInfo.getParentWorkId() != null && !okrWorkBaseInfo.getParentWorkId().isEmpty() && okrWorkBaseInfo.getParentWorkId().trim().length() > 1  ){
			statistic.setParentId( okrWorkBaseInfo.getParentWorkId() );
		}else{
			statistic.setParentId( null );
		}
		statistic.setWorkTitle( okrWorkBaseInfo.getTitle() );
		statistic.setWorkLevel( okrWorkBaseInfo.getWorkLevel() );
		statistic.setWorkType( okrWorkBaseInfo.getWorkType() );
		statistic.setWorkProcessStatus( okrWorkBaseInfo.getWorkProcessStatus() );
		statistic.setReportCycle( okrWorkBaseInfo.getReportCycle() );
		statistic.setReportDayInCycle( okrWorkBaseInfo.getReportDayInCycle() );
		statistic.setWorkDateTimeType( okrWorkBaseInfo.getWorkDateTimeType() );
		statistic.setStatisticYear( Integer.parseInt( dateOperation.getYear( new Date()) ) );
		statistic.setDeployDateStr( okrWorkBaseInfo.getDeployDateStr() );
		statistic.setCompleteDateLimitStr( okrWorkBaseInfo.getCompleteDateLimitStr() );
		okrStatisticReportStatusService.save( statistic );
	}

	/**
	 * 对单个工作,按部署日期 到工作完成时限期间的时间段，以及汇报方式进行汇报情况信息统计
	 * @param okrWorkBaseInfo
	 */
	private String statisticWorkReports( OkrWorkBaseInfo okrWorkBaseInfo ) {
		List<WorkBaseReportSubmitEntity> workBaseReportSubmitEntityList = new ArrayList<>();
		WorkBaseReportSubmitEntity workBaseReportSubmitEntity = null;
		String statisticContent = null;
		
		if( "每月汇报".equals( okrWorkBaseInfo.getReportCycle() )){
			List<MonthOfYear> monthOfYearList = null;
			monthOfYearList = dateOperation.getMonthsOfYear( okrWorkBaseInfo.getDeployDateStr(), okrWorkBaseInfo.getCompleteDateLimitStr() );
			if( monthOfYearList != null && !monthOfYearList.isEmpty() ){
				if( "长期工作".equals( okrWorkBaseInfo.getWorkDateTimeType() )){
					for( MonthOfYear monthOfYear : monthOfYearList ){
						try {
							workBaseReportSubmitEntity = getMonthReportSubmitEntity( okrWorkBaseInfo, monthOfYear );
							workBaseReportSubmitEntityList.add( workBaseReportSubmitEntity );
						} catch (Exception e) {
							logger.warn( "system get week report submit enitty got an exception." );
							logger.error(e);
						}
					}
				}else{
					//如果是短期工作,根本不需要进行汇报,所以不进行汇报情况统计
					for( MonthOfYear monthOfYear : monthOfYearList ){
						workBaseReportSubmitEntity = new WorkBaseReportSubmitEntity();
						workBaseReportSubmitEntity.setCycleNumber( monthOfYear.getMonth() );
						workBaseReportSubmitEntity.setStartDate( monthOfYear.getStartDateString() );
						workBaseReportSubmitEntity.setEndDate( monthOfYear.getEndDateString() );
						workBaseReportSubmitEntity.setCycleType( "每月汇报" );
						workBaseReportSubmitEntity.setReportStatus( -1 ); //无须汇报
						workBaseReportSubmitEntity.setSubmitTime( null );
						workBaseReportSubmitEntity.setDescription( "该工作为短期工作,无须汇报" );
						workBaseReportSubmitEntityList.add( workBaseReportSubmitEntity );
					}
				}
			}
		}else if( "每周汇报".equals( okrWorkBaseInfo.getReportCycle() )){
			List<WeekOfYear> weekOfYearList = null;
			weekOfYearList = dateOperation.getWeeksOfYear( okrWorkBaseInfo.getDeployDateStr(), okrWorkBaseInfo.getCompleteDateLimitStr() );
			if( weekOfYearList != null && !weekOfYearList.isEmpty() ){
				if( "长期工作".equals( okrWorkBaseInfo.getWorkDateTimeType() )){
					for( WeekOfYear weekOfYear : weekOfYearList ){
						try {
							workBaseReportSubmitEntity = getWeekReportSubmitEntity( okrWorkBaseInfo, weekOfYear );
							workBaseReportSubmitEntityList.add( workBaseReportSubmitEntity );
						} catch (Exception e) {
							logger.warn( "system get week report submit enitty got an exception." );
							logger.error(e);
						}
					}
				}else{
					//如果是短期工作,根本不需要进行汇报,所以不进行汇报情况统计
					for( WeekOfYear weekOfYear : weekOfYearList ){
						workBaseReportSubmitEntity = new WorkBaseReportSubmitEntity();
						workBaseReportSubmitEntity.setCycleNumber( weekOfYear.getWeekNo() );
						workBaseReportSubmitEntity.setStartDate( weekOfYear.getStartDateString() );
						workBaseReportSubmitEntity.setEndDate( weekOfYear.getEndDateString() );
						workBaseReportSubmitEntity.setCycleType( "每周汇报" );
						workBaseReportSubmitEntity.setReportStatus( -1 ); //无须汇报
						workBaseReportSubmitEntity.setSubmitTime( null );
						workBaseReportSubmitEntity.setDescription( "该工作为短期工作,无须汇报" );
						workBaseReportSubmitEntityList.add( workBaseReportSubmitEntity );
					}
				}
			}
		}else{//不需要汇报
			workBaseReportSubmitEntity = new WorkBaseReportSubmitEntity();
			workBaseReportSubmitEntity.setCycleType( okrWorkBaseInfo.getReportCycle() );
			workBaseReportSubmitEntity.setReportStatus( -1 ); //无须汇报
			workBaseReportSubmitEntity.setSubmitTime( null );
			workBaseReportSubmitEntity.setDescription( okrWorkBaseInfo.getReportCycle() + "暂无汇报" );
			workBaseReportSubmitEntityList.add( workBaseReportSubmitEntity );
		}
		
		//组织好了workBaseReportSubmitEntityList， 也许为空
		Gson gson = XGsonBuilder.pureGsonDateFormated();
		if( workBaseReportSubmitEntityList != null && !workBaseReportSubmitEntityList.isEmpty() ){
			statisticContent = gson.toJson( workBaseReportSubmitEntityList );
		}else{
			statisticContent = "{}";
		}
		return statisticContent;
	}

	private WorkBaseReportSubmitEntity getMonthReportSubmitEntity( OkrWorkBaseInfo okrWorkBaseInfo, MonthOfYear monthOfYear ) throws Exception {
		List<OkrWorkReportBaseInfo> reportBaseList = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo  = null;
		WorkBaseReportSubmitEntity workBaseReportSubmitEntity = null;

		workBaseReportSubmitEntity = new WorkBaseReportSubmitEntity();
		workBaseReportSubmitEntity.setCycleNumber( monthOfYear.getMonth() );
		workBaseReportSubmitEntity.setCycleType( "每月汇报" );
		workBaseReportSubmitEntity.setStartDate( monthOfYear.getStartDateString() );
		workBaseReportSubmitEntity.setEndDate( monthOfYear.getEndDateString() );
		
		//查询该工作在该时间区间内所有的工作汇报ID
		reportBaseList = getWeekReportInTimeQuarter( okrWorkBaseInfo.getId(), monthOfYear.getStartDate(), monthOfYear.getEndDate() );
		
		if( reportBaseList != null && !reportBaseList.isEmpty() ){
			//遍历每一个工作信息,看看是否提交和是否填写汇报信息
			for( OkrWorkReportBaseInfo okrWorkReportBaseInfo : reportBaseList ){
				try {
					okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( okrWorkReportBaseInfo.getId() );
				} catch (Exception e) {
					logger.warn( "system query report detail with report id got an exception.id:" + okrWorkReportBaseInfo.getId() );
					throw e;
				}
				if( okrWorkReportBaseInfo.getSubmitTime() != null ){//已提交汇报信息
					workBaseReportSubmitEntity.setSubmitTime( okrWorkReportBaseInfo.getSubmitTime() );
					workBaseReportSubmitEntity.setReportId( okrWorkReportBaseInfo.getId() );
					if( okrWorkReportDetailInfo != null && okrWorkReportDetailInfo.getProgressDescription() != null && !okrWorkReportDetailInfo.getProgressDescription().isEmpty() ){
						workBaseReportSubmitEntity.setReportStatus( 1 ); //有汇报内容
						workBaseReportSubmitEntity.setDescription( "汇报已提交，有汇报内容" );
						break;
					}else{
						workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
						workBaseReportSubmitEntity.setDescription( "汇报已提交，无汇报内容" );
					}
				}else{
					workBaseReportSubmitEntity.setSubmitTime( null );
					if( okrWorkReportDetailInfo != null && okrWorkReportDetailInfo.getProgressDescription() != null && !okrWorkReportDetailInfo.getProgressDescription().isEmpty() ){
						workBaseReportSubmitEntity.setReportStatus( 1 ); //有汇报内容
						workBaseReportSubmitEntity.setDescription( "汇报未提交，有汇报内容" );
						break;
					}else{
						workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
						workBaseReportSubmitEntity.setDescription( "汇报未提交，无汇报内容" );
					}
				}
			}
		}else{
			workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
			workBaseReportSubmitEntity.setDescription( "该工作暂无汇报信息" );
		}
		return workBaseReportSubmitEntity;
	}

	private WorkBaseReportSubmitEntity getWeekReportSubmitEntity( OkrWorkBaseInfo okrWorkBaseInfo, WeekOfYear weekOfYear) throws Exception {
		List<OkrWorkReportBaseInfo> reportBaseList = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo  = null;
		WorkBaseReportSubmitEntity workBaseReportSubmitEntity = null;

		workBaseReportSubmitEntity = new WorkBaseReportSubmitEntity();
		workBaseReportSubmitEntity.setCycleNumber( weekOfYear.getWeekNo() );
		workBaseReportSubmitEntity.setCycleType( "每周汇报" );
		workBaseReportSubmitEntity.setStartDate( weekOfYear.getStartDateString() );
		workBaseReportSubmitEntity.setEndDate( weekOfYear.getEndDateString() );
		
		//查询该工作在该时间区间内所有的工作汇报ID
		reportBaseList = getWeekReportInTimeQuarter( okrWorkBaseInfo.getId(), weekOfYear.getStartDate(), weekOfYear.getEndDate() );
		
		if( reportBaseList != null && !reportBaseList.isEmpty() ){
			//遍历每一个工作信息,看看是否提交和是否填写汇报信息
			for( OkrWorkReportBaseInfo okrWorkReportBaseInfo : reportBaseList ){
				try {
					okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( okrWorkReportBaseInfo.getId() );
				} catch (Exception e) {
					logger.warn( "system query report detail with report id got an exception.id:" + okrWorkReportBaseInfo.getId() );
					throw e;
				}
				if( okrWorkReportBaseInfo.getSubmitTime() != null ){//已提交汇报信息
					workBaseReportSubmitEntity.setSubmitTime( okrWorkReportBaseInfo.getSubmitTime() );
					workBaseReportSubmitEntity.setReportId( okrWorkReportBaseInfo.getId() );
					if( okrWorkReportDetailInfo != null && okrWorkReportDetailInfo.getProgressDescription() != null && !okrWorkReportDetailInfo.getProgressDescription().isEmpty() ){
						workBaseReportSubmitEntity.setReportStatus( 1 ); //有汇报内容
						workBaseReportSubmitEntity.setDescription( "汇报已提交，有汇报内容" );
						break;
					}else{
						workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
						workBaseReportSubmitEntity.setDescription( "汇报已提交，无汇报内容" );
					}
				}else{
					workBaseReportSubmitEntity.setSubmitTime( null );
					if( okrWorkReportDetailInfo != null && okrWorkReportDetailInfo.getProgressDescription() != null && !okrWorkReportDetailInfo.getProgressDescription().isEmpty() ){
						workBaseReportSubmitEntity.setReportStatus( 1 ); //有汇报内容
						workBaseReportSubmitEntity.setDescription( "汇报未提交，有汇报内容" );
						break;
					}else{
						workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
						workBaseReportSubmitEntity.setDescription( "汇报未提交，无汇报内容" );
					}
				}
			}
		}else{
			workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
			workBaseReportSubmitEntity.setDescription( "该工作暂无汇报信息" );
		}
		return workBaseReportSubmitEntity;
	}

	/**
	 * 根据工作ID,以及开始和结束时间来查询这一时间段内所有的工作汇报信息列表
	 * @param workId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception 
	 */
	private List<OkrWorkReportBaseInfo> getWeekReportInTimeQuarter( String workId, Date startDate, Date endDate ) throws Exception {
		List<OkrWorkReportBaseInfo> allReportList = null;
		List<OkrWorkReportBaseInfo> reportList = new ArrayList<>();
		List<String> ids = null;
		try {
			ids = okrWorkReportQueryService.listByWorkId( workId );
		} catch (Exception e) {
			logger.warn( "system list report info with work id got an exception.workId:" + workId );
			throw e;
		}
		if( ids != null && !ids.isEmpty() ){
			try {
				allReportList = okrWorkReportQueryService.listByIds( ids );
			} catch (Exception e) {
				logger.warn( "system list report info with ids got an exception." );
				throw e;
			}
		}
		if( allReportList != null && !allReportList.isEmpty() ){
			for( OkrWorkReportBaseInfo okrWorkReportBaseInfo : allReportList ){
				if( okrWorkReportBaseInfo.getUpdateTime().after( startDate )
				  && okrWorkReportBaseInfo.getUpdateTime().before( endDate )
				){
					//是需要的时间区间内的汇报
					reportList.add( okrWorkReportBaseInfo );
				}
			}
		}
		try {
			SortTools.desc( reportList, "updateTime" );
		} catch (Exception e) {
			logger.warn( "system sort report list got an exception." );
			throw e;
		}
		return reportList;
	}
}