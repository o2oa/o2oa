package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.common.date.MonthOfYear;
import com.x.okr.assemble.common.date.WeekOfYear;
import com.x.okr.assemble.control.schedule.St_WorkReportStatus;
import com.x.okr.assemble.control.schedule.entity.WorkBaseReportSubmitEntity;
import com.x.okr.entity.OkrStatisticReportStatus;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;

public class ExcuteSt_WorkReportStatusService {
	private static  Logger logger = LoggerFactory.getLogger( St_WorkReportStatus.class );
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	private OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	private OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	private OkrStatisticReportStatusService okrStatisticReportStatusService = new OkrStatisticReportStatusService();
	private DateOperation dateOperation = new DateOperation();
	
	public void execute() {
		List<String>  ids = null;
		String status = "All";//如果不需要在统计里展示 ，就应该为已归档
		try {
			ids = okrWorkBaseInfoService.listAllDeployedWorkIds( null, status );
		} catch (Exception e) {
			logger.warn("Timertask OKR_St_WorkReportStatus service list all no archive centerworks got an exception." );
			logger.error(e);
		}
		if (ids != null && !ids.isEmpty()) {
			statisticWorksReportSubmit( ids );
		}
		logger.info("Timertask OKR_St_WorkReportStatus completed and excute success.");
	}
	
	public void executeAll() {
		List<String>  ids = null;
		try {
			ids = okrWorkBaseInfoService.listAllDeployedWorkIds( null, null );
		} catch (Exception e) {
			logger.warn("Timertask OKR_St_WorkReportStatus service list all no archive centerworks got an exception." );
			logger.error(e);
		}
		if (ids != null && !ids.isEmpty()) {
			statisticWorksReportSubmit( ids );
		}
		logger.info("Timertask OKR_St_WorkReportStatus completed and excute success.");
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
			for ( String workId : workIds) {
				System.out.println("");
				System.out.println("==================================================");
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
					if( okrWorkBaseInfo != null ){
						//logger.debug( "system getting report status statistic for work "+ i +"/"+total+" named:["+ okrWorkBaseInfo.getTitle() +"]......" );
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
		statistic.setResponsibilityTopUnitName( okrWorkBaseInfo.getResponsibilityTopUnitName() );
		statistic.setResponsibilityIdentity( okrWorkBaseInfo.getResponsibilityIdentity() );
		statistic.setResponsibilityUnitName( okrWorkBaseInfo.getResponsibilityUnitName() );
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
	 * @throws Exception 
	 */
	private String statisticWorkReports( OkrWorkBaseInfo okrWorkBaseInfo ) throws Exception {
		List<String> ids = null;
		List<OkrWorkReportBaseInfo> allReportList = null;
		List<WorkBaseReportSubmitEntity> workBaseReportSubmitEntityList = new ArrayList<>();
		WorkBaseReportSubmitEntity workBaseReportSubmitEntity = null;
		String statisticContent = null;
		
		try {
			ids = okrWorkReportQueryService.listByWorkId( okrWorkBaseInfo.getId() );
			if( ids != null && !ids.isEmpty() ){
				allReportList = okrWorkReportQueryService.listByIds( ids );
			}	
		} catch (Exception e) {
			logger.warn( "system list report info with work id got an exception.workId:" + okrWorkBaseInfo.getId() );
			throw e;
		}
			
		if( "每月汇报".equals( okrWorkBaseInfo.getReportCycle() )){
			List<MonthOfYear> monthOfYearList = null;
			monthOfYearList = dateOperation.getMonthsOfYear( okrWorkBaseInfo.getDeployDateStr(), okrWorkBaseInfo.getCompleteDateLimitStr() );
			if( monthOfYearList != null && !monthOfYearList.isEmpty() ){
				if( "长期工作".equals( okrWorkBaseInfo.getWorkDateTimeType() )){
					for( MonthOfYear monthOfYear : monthOfYearList ){
						try {
							workBaseReportSubmitEntity = getMonthReportSubmitEntity( okrWorkBaseInfo, allReportList, monthOfYear );
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
							workBaseReportSubmitEntity = getWeekReportSubmitEntity( okrWorkBaseInfo, allReportList, weekOfYear );
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
		Gson gson = XGsonBuilder.instance();
		if( workBaseReportSubmitEntityList != null && !workBaseReportSubmitEntityList.isEmpty() ){
			statisticContent = gson.toJson( workBaseReportSubmitEntityList );
		}else{
			statisticContent = "{}";
		}
		return statisticContent;
	}

	private WorkBaseReportSubmitEntity getMonthReportSubmitEntity( OkrWorkBaseInfo okrWorkBaseInfo, List<OkrWorkReportBaseInfo> allReportList, MonthOfYear monthOfYear ) throws Exception {
		List<OkrWorkReportBaseInfo> reportBaseList = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo  = null;
		WorkBaseReportSubmitEntity workBaseReportSubmitEntity = null;

		workBaseReportSubmitEntity = new WorkBaseReportSubmitEntity();
		workBaseReportSubmitEntity.setCycleNumber( monthOfYear.getMonth() );
		workBaseReportSubmitEntity.setCycleType( "每月汇报" );
		workBaseReportSubmitEntity.setStartDate( monthOfYear.getStartDateString() );
		workBaseReportSubmitEntity.setEndDate( monthOfYear.getEndDateString() );
		
		if( okrWorkBaseInfo.getIsCompleted() || okrWorkBaseInfo.getOverallProgress() == 100 ){
			workBaseReportSubmitEntity.setReportStatus( -1 );
			workBaseReportSubmitEntity.setDescription( "该工作已经完成" );
		}else{
			//查询该工作在该时间区间内所有的工作汇报ID
			reportBaseList = getReportInTimeQuarter( allReportList, monthOfYear.getStartDate(), monthOfYear.getEndDate() );
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
							if( okrWorkBaseInfo.getArchiveDate() != null && okrWorkBaseInfo.getArchiveDate().before( monthOfYear.getStartDate() )){
								workBaseReportSubmitEntity.setReportStatus( -1 );
								workBaseReportSubmitEntity.setDescription( "工作已经归档，不需要汇报" );
							}else{
								workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
								workBaseReportSubmitEntity.setDescription( "汇报未提交，无汇报内容" );
							}
						}
					}
				}
			}else{
				if( okrWorkBaseInfo.getArchiveDate() != null && okrWorkBaseInfo.getArchiveDate().before( monthOfYear.getStartDate() )){
					workBaseReportSubmitEntity.setReportStatus( -1 );
					workBaseReportSubmitEntity.setDescription( "工作已经归档，不需要汇报" );
				}else{
					workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
					workBaseReportSubmitEntity.setDescription( "该工作暂无汇报信息" );
				}
			}
		}
		
		logger.info( "工作："+ okrWorkBaseInfo.getTitle() +", 月汇报情况("+ workBaseReportSubmitEntity.getCycleType() + "-" + workBaseReportSubmitEntity.getCycleNumber() +")：" + workBaseReportSubmitEntity.getReportStatus() + ", 说明：" + workBaseReportSubmitEntity.getDescription() );
		return workBaseReportSubmitEntity;
	}

	private WorkBaseReportSubmitEntity getWeekReportSubmitEntity( OkrWorkBaseInfo okrWorkBaseInfo, List<OkrWorkReportBaseInfo> allReportList, WeekOfYear weekOfYear ) throws Exception {
		List<OkrWorkReportBaseInfo> reportBaseList = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo  = null;
		WorkBaseReportSubmitEntity workBaseReportSubmitEntity = null;

		workBaseReportSubmitEntity = new WorkBaseReportSubmitEntity();
		workBaseReportSubmitEntity.setCycleNumber( weekOfYear.getWeekNo() );
		workBaseReportSubmitEntity.setCycleType( "每周汇报" );
		workBaseReportSubmitEntity.setStartDate( weekOfYear.getStartDateString() );
		workBaseReportSubmitEntity.setEndDate( weekOfYear.getEndDateString() );
		
		//查询该工作在该时间区间内所有的工作汇报ID
		reportBaseList = getReportInTimeQuarter( allReportList, weekOfYear.getStartDate(), weekOfYear.getEndDate() );
		
		if( reportBaseList != null && !reportBaseList.isEmpty() ){
			//遍历每一个工作汇报信息,看看是否提交和是否填写汇报信息
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
						if( okrWorkBaseInfo.getIsCompleted() || okrWorkBaseInfo.getOverallProgress() == 100 ){
							workBaseReportSubmitEntity.setReportStatus( -1 );
							workBaseReportSubmitEntity.setDescription( "该工作已经完成" );
						}else if( weekOfYear.getStartDate().after( new Date() )){
							workBaseReportSubmitEntity.setReportStatus( -1 );
							workBaseReportSubmitEntity.setDescription( "汇报日期未到，不需要汇报" );
						}else{
							workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
							workBaseReportSubmitEntity.setDescription( "汇报已提交，无汇报内容" );
						}
					}
				}else{
					workBaseReportSubmitEntity.setSubmitTime( null );
					if( okrWorkReportDetailInfo != null && okrWorkReportDetailInfo.getProgressDescription() != null && !okrWorkReportDetailInfo.getProgressDescription().isEmpty() ){
						workBaseReportSubmitEntity.setReportStatus( 1 ); //有汇报内容
						workBaseReportSubmitEntity.setDescription( "汇报未提交，有汇报内容" );
						break;
					}else{
						if( okrWorkBaseInfo.getIsCompleted() || okrWorkBaseInfo.getOverallProgress() == 100 ){
							workBaseReportSubmitEntity.setReportStatus( -1 );
							workBaseReportSubmitEntity.setDescription( "该工作已经完成" );
						}else{
							//在这个时候，工作已经归档过了，所以不需要汇报
							if( okrWorkBaseInfo.getArchiveDate() != null && okrWorkBaseInfo.getArchiveDate().before( weekOfYear.getStartDate() )){
								workBaseReportSubmitEntity.setReportStatus( -1 );
								workBaseReportSubmitEntity.setDescription( "工作已经归档，不需要汇报" );
							}else if( weekOfYear.getStartDate().after( new Date() )){
								workBaseReportSubmitEntity.setReportStatus( -1 );
								workBaseReportSubmitEntity.setDescription( "汇报日期未到，不需要汇报" );
							}else{
								workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
								workBaseReportSubmitEntity.setDescription( "汇报未提交，无汇报内容" );
							}
						}
					}
				}
			}
		}else{
			if( okrWorkBaseInfo.getIsCompleted() || okrWorkBaseInfo.getOverallProgress() == 100 ){
				workBaseReportSubmitEntity.setReportStatus( -1 );
				workBaseReportSubmitEntity.setDescription( "该工作已经完成" );
			}else{
				if( okrWorkBaseInfo.getArchiveDate() != null && okrWorkBaseInfo.getArchiveDate().before( weekOfYear.getStartDate() )){
					workBaseReportSubmitEntity.setReportStatus( -1 );
					workBaseReportSubmitEntity.setDescription( "工作已经归档，不需要汇报" );
				}else if( weekOfYear.getStartDate().after( new Date() )){
					workBaseReportSubmitEntity.setReportStatus( -1 );
					workBaseReportSubmitEntity.setDescription( "汇报日期未到，不需要汇报" );
				}else{
					workBaseReportSubmitEntity.setReportStatus( 0 ); //无汇报内容
					workBaseReportSubmitEntity.setDescription( "该工作暂无汇报信息" );
				}
			}
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
	private List<OkrWorkReportBaseInfo> getReportInTimeQuarter( List<OkrWorkReportBaseInfo> allReportList, Date startDate, Date endDate ) throws Exception {
		List<OkrWorkReportBaseInfo> reportList = new ArrayList<>();
		Boolean check = false;
		if( allReportList != null && !allReportList.isEmpty() ){
			for( OkrWorkReportBaseInfo okrWorkReportBaseInfo : allReportList ){
				check = ( okrWorkReportBaseInfo.getCreateTime().after( startDate ) && okrWorkReportBaseInfo.getCreateTime().before( endDate ) );
				if( check ){
					//是需要的时间区间内的汇报
					reportList.add( okrWorkReportBaseInfo );
				}
			}
		}
		try {
			SortTools.desc( reportList, "createTime" );
		} catch (Exception e) {
			logger.warn( "system sort report list got an exception." );
			throw e;
		}
		return reportList;
	}
}
