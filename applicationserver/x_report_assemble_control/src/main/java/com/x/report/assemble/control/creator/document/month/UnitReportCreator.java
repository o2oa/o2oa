package com.x.report.assemble.control.creator.document.month;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.service.Report_C_WorkPlanNextServiceAdv;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;
import com.x.report.assemble.control.service.Report_I_WorkInfoServiceAdv;
import com.x.report.assemble.control.service.Report_P_PermissionServiceAdv;
import com.x.report.assemble.control.service.UserManagerService;
import com.x.report.common.date.DateOperation;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanDetail;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_C_WorkPlanNextDetail;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;
import com.x.report.core.entity.Report_P_Profile;

public class UnitReportCreator {
	
	private Logger logger = LoggerFactory.getLogger( UnitReportCreator.class );
	private UserManagerService userManagerService = new UserManagerService();
	private Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	private Report_C_WorkPlanNextServiceAdv report_C_WorkPlanNextServiceAdv = new Report_C_WorkPlanNextServiceAdv();
	private Report_I_WorkInfoServiceAdv report_I_WorkInfoServiceAdv = new Report_I_WorkInfoServiceAdv();
	private Report_P_PermissionServiceAdv report_I_PermissionServiceAdv = new Report_P_PermissionServiceAdv();
	private DateOperation dateOperation = new DateOperation();

    /**
     * 根据汇报生成概要文件信息以及需要生成汇报的个人标识，为该用户生成月度汇报文档信息
     * @param effectivePerson
     * @param profile
     * @param unitStrategyWorks
     * @param identity
     * @return
     * @throws Exception
     */
	public Boolean create( EffectivePerson effectivePerson, Report_P_Profile profile,  String identity, String unitName ) throws Exception {
		Date reportTargetDate = null;
		Date lastTargetMonthDate = null;
		String dateString = null;
		String unitManager = null;
		String lastTargetMonth_month = null;
		String lastTargetMonth_year = null;
		Report_I_Base report_I_Base = null;
		List<Report_I_Base> reportList = null;
		
		//根据汇报概要文件信息以及个人的身份来组织一个月度组织汇报基础信息对象
		report_I_Base = composeReportBaseInfo( profile, identity, unitName );		
		
		//查询该组织该月是否已经生成过汇报了，如果已经生成过，就跳过该部门的汇报生成过程
		reportList = report_I_ServiceAdv.list( 
				report_I_Base.getReportType(), 
				report_I_Base.getReportObjType(), 
				null, 
				report_I_Base.getTargetUnit(), 
				report_I_Base.getYear(), 
				report_I_Base.getMonth(), 
				null, 
				null, 
				null, true 
		);
		if( ListTools.isNotEmpty(reportList) ) {
			logger.debug( effectivePerson, "2.5.2.1 该组织的月度汇报信息已经存在，不需要再次生成。");
			return true;
		}
		
		//查询部门主管
		unitManager = userManagerService.getPersonWithDutyAndUnitName( unitName, "部主管");
		report_I_Base.setUnitManager( unitManager );
		
		//获取汇报的目标年月组织的时刻
		reportTargetDate = dateOperation.getDateFromString( report_I_Base.getYear() + "-" + report_I_Base.getMonth() + "-01" );
		
		//组织所有的工作计划（查询上个月汇报的，下个月，工作计划内容）
		//计算上一个月的汇报目标年份和月份
		dateString = dateOperation.getMonthAdd( reportTargetDate, -1 ); //时间回溯1个月
		lastTargetMonthDate = dateOperation.getDateFromString( dateString );
		lastTargetMonth_year = dateOperation.getYear( lastTargetMonthDate );
		lastTargetMonth_month = dateOperation.getMonth( lastTargetMonthDate );
		
		List<String>  ids = null;
		List<Report_I_WorkInfo>next_month_workInfoList = null;
		List<Report_C_WorkPlanNext> last_workPlanNextList = null;
		List<Report_C_WorkPlan> workPlanList = new ArrayList<>();
		List<Report_C_WorkPlanDetail> workPlanDetailList = new ArrayList<>();
		Report_I_Base last_Report_I_Base = null;
		Report_C_WorkPlanNextDetail last_report_C_WorkPlanNextDetail = null;
		Report_C_WorkPlan  new_report_C_WorkPlan = null;
		Report_C_WorkPlanDetail  new_report_C_WorkPlanDetail = null;
		Report_I_WorkInfo new_report_I_WorkInfo = null;
		
		//查询上个月的工作汇报信息，根据汇报信息ID，查询上个月的所有工作计划列表
		reportList = report_I_ServiceAdv.list(
				report_I_Base.getReportType(), 
				report_I_Base.getReportObjType(), 
				null, 
				report_I_Base.getTargetUnit(), 
				lastTargetMonth_year,
				lastTargetMonth_month, 
				null, null,null, true   );
		
		if( ListTools.isNotEmpty(reportList) ) {//说明有上次汇报，如果有，则取上次汇报的下周期工作计划信息
			last_Report_I_Base = reportList.get( 0 );
			//转存上个月的下月重点工作、下月重点工作计划内容为当月的重点工作内容
			ids = report_I_WorkInfoServiceAdv.listIdsWithReport(last_Report_I_Base.getId(), "NEXTMONTH");
			next_month_workInfoList = report_I_WorkInfoServiceAdv.list(ids);
			report_I_Base.setLastReportId(last_Report_I_Base.getId());
			if( ListTools.isNotEmpty( next_month_workInfoList )) {
				//如果COPY了上个月的下月工作计划，那么说明是继承计划，不允许修改
				report_I_Base.setWorkPlanModifyable(false);
				for( Report_I_WorkInfo workInfo : next_month_workInfoList ) {
					//转存下月工作workInfo为当月工作THISMONTH
					new_report_I_WorkInfo = composeWorkInfoWithNextWorkInfo(report_I_Base, workInfo,"THISMONTH");
					//下月工作的计划也要转存过来  workPlanList、workPlanDetailList
					ids = report_C_WorkPlanNextServiceAdv.listWithReportAndWorkInfoId(last_Report_I_Base.getId(), workInfo.getId());
					last_workPlanNextList = report_C_WorkPlanNextServiceAdv.list(ids);
					if( ListTools.isNotEmpty( last_workPlanNextList )) {
						
						for( Report_C_WorkPlanNext last_nextPlan : last_workPlanNextList ) {
							//转存工作计划的详细信息
							last_report_C_WorkPlanNextDetail = report_C_WorkPlanNextServiceAdv.getDetailWithPlanId( last_nextPlan.getId() );
							
							//转为当月的工作计划内容
		                    new_report_C_WorkPlan = new Report_C_WorkPlan();
							last_nextPlan.copyTo( new_report_C_WorkPlan, JpaObject.FieldsUnmodify );
							new_report_C_WorkPlan.setId( Report_C_WorkPlan.createId() );
							new_report_C_WorkPlan.setReportId( report_I_Base.getId() );
							new_report_C_WorkPlan.setYear( report_I_Base.getYear() );
							new_report_C_WorkPlan.setMonth( report_I_Base.getMonth() );
							new_report_C_WorkPlan.setWorkInfoId( new_report_I_WorkInfo.getId() );
							//放入List以备保存
							workPlanList.add( new_report_C_WorkPlan );
							
							if( last_report_C_WorkPlanNextDetail != null ) {
								new_report_C_WorkPlanDetail = new Report_C_WorkPlanDetail();		
								last_report_C_WorkPlanNextDetail.copyTo( new_report_C_WorkPlanDetail, JpaObject.FieldsUnmodify );	
								new_report_C_WorkPlanDetail.setId( new_report_C_WorkPlan.getId() );	
								new_report_C_WorkPlanDetail.setReportId( report_I_Base.getId() );
								new_report_C_WorkPlanDetail.setWorkInfoId( new_report_I_WorkInfo.getId() );
								
								//给详细信息设置计划的ID作为关联
								new_report_C_WorkPlanDetail.setPlanId( new_report_C_WorkPlan.getId() );
								workPlanDetailList.add( new_report_C_WorkPlanDetail );
							}
						}
					}
				}
			}
		}else {
			// 说明是第一个月，没有下个月工作计划，生成5个工作计划内容，工作标题默认为 : x年x月重点工作i，并且保存
			// 工作与举措不用关联
			createReportWorkInfoWithReport(report_I_Base,"THISMONTH", report_I_Base.getYear(), report_I_Base.getMonth() , "重点工作一", 1 );
			createReportWorkInfoWithReport(report_I_Base,"THISMONTH",report_I_Base.getYear(), report_I_Base.getMonth() ,"重点工作二", 2 );
			createReportWorkInfoWithReport(report_I_Base,"THISMONTH",report_I_Base.getYear(),report_I_Base.getMonth(),"重点工作三", 3 );
			createReportWorkInfoWithReport(report_I_Base,"THISMONTH",report_I_Base.getYear(),report_I_Base.getMonth(),"重点工作四", 4 );
			createReportWorkInfoWithReport(report_I_Base,"THISMONTH",report_I_Base.getYear() ,report_I_Base.getMonth(),"重点工作五", 5 );
		}
		
		//添加下一个月的工作信息
		dateString = dateOperation.getMonthAdd( reportTargetDate, 1 ); //时间加1个月
		Date nextTargetMonthDate = dateOperation.getDateFromString( dateString );
		String nextTargetMonth_year = dateOperation.getYear( nextTargetMonthDate );
		String nextTargetMonth_month = dateOperation.getMonth( nextTargetMonthDate );
		//初始化下个月工作计划
		createReportWorkInfoWithReport(report_I_Base,"NEXTMONTH",nextTargetMonth_year ,nextTargetMonth_month ,"", 1 );
		createReportWorkInfoWithReport(report_I_Base,"NEXTMONTH",nextTargetMonth_year ,nextTargetMonth_month ,"", 2 );
		createReportWorkInfoWithReport(report_I_Base,"NEXTMONTH",nextTargetMonth_year ,nextTargetMonth_month , "", 3 );
		createReportWorkInfoWithReport(report_I_Base,"NEXTMONTH",nextTargetMonth_year ,nextTargetMonth_month ,"", 4 );
		createReportWorkInfoWithReport(report_I_Base,"NEXTMONTH",nextTargetMonth_year ,nextTargetMonth_month ,"", 5 );

		report_I_ServiceAdv.save( report_I_Base, null,  workPlanList, workPlanDetailList, null, null, null, null );
		report_I_PermissionServiceAdv.refreshReportPermission( report_I_Base.getId() );
		return true;
	}

	private Report_I_WorkInfo composeWorkInfoWithNextWorkInfo(Report_I_Base report_I_Base, Report_I_WorkInfo workInfo, String workMonthFlag) throws Exception {
		Report_I_WorkInfo new_workInfo = new Report_I_WorkInfo();
		workInfo.copyTo( new_workInfo, JpaObject.FieldsUnmodify );
		new_workInfo.setId( Report_I_WorkInfo.createId() );
		new_workInfo.setReportId( report_I_Base.getId() );
		new_workInfo.setWorkYear( report_I_Base.getYear() );
		new_workInfo.setWorkReportYear( report_I_Base.getYear());
		new_workInfo.setWorkReportMonth(report_I_Base.getMonth() );
		new_workInfo.setWorkMonthFlag(workMonthFlag);
		new_workInfo.setMeasuresList(workInfo.getMeasuresList());  //与举措的关联
		new_workInfo.setModifyAble( false );
		
		Report_I_WorkInfoDetail report_I_WorkInfoDetail = new Report_I_WorkInfoDetail();
		//查询的时候，根据上次汇报的ID和原来的工作信息来查询详细信息
		Report_I_WorkInfoDetail oldDetail = report_I_WorkInfoServiceAdv.getDetailWithWorkInfoId(workInfo.getReportId(), workInfo.getId() );
		if( oldDetail != null ) {
			report_I_WorkInfoDetail.setId( new_workInfo.getId() );
			report_I_WorkInfoDetail.setKeyWorkId("");
			report_I_WorkInfoDetail.setReportId(report_I_Base.getId());
			report_I_WorkInfoDetail.setDescribe(oldDetail.getDescribe());
			report_I_WorkInfoDetail.setWorkPlanSummary(oldDetail.getWorkPlanSummary());
			report_I_WorkInfoDetail.setWorkProgSummary(oldDetail.getWorkProgSummary());
		}else {
			report_I_WorkInfoDetail.setId( new_workInfo.getId() );
			report_I_WorkInfoDetail.setKeyWorkId("");
			report_I_WorkInfoDetail.setReportId(report_I_Base.getId());
			report_I_WorkInfoDetail.setDescribe("");
			report_I_WorkInfoDetail.setWorkPlanSummary("");
			report_I_WorkInfoDetail.setWorkProgSummary("");
		}
		
		report_I_WorkInfoServiceAdv.save(new_workInfo, report_I_WorkInfoDetail );
		return new_workInfo;
	}

	private Report_I_WorkInfo createReportWorkInfoWithReport(Report_I_Base report_I_Base, String workMonthFlag, String year, String month, String workTitle, Integer orderNumber ) throws Exception {
		Report_I_WorkInfo new_report_I_WorkInfo = new Report_I_WorkInfo();
		new_report_I_WorkInfo.setId(Report_I_WorkInfo.createId());
		new_report_I_WorkInfo.setProfileId(report_I_Base.getProfileId());
		new_report_I_WorkInfo.setReportId(report_I_Base.getId());
		new_report_I_WorkInfo.setWorkCreator("System");
		new_report_I_WorkInfo.setWorkReportMonth(month);
		new_report_I_WorkInfo.setWorkReportYear(year);
		new_report_I_WorkInfo.setWorkUnit(report_I_Base.getTargetUnit());
		new_report_I_WorkInfo.setWorkYear(year);
		if( StringUtils.isEmpty( workTitle )) {
			new_report_I_WorkInfo.setWorkTitle( "" );
		}else {
			new_report_I_WorkInfo.setWorkTitle(year + "年" + month + "月" + workTitle);
		}
		new_report_I_WorkInfo.setWorkTag("部门重点工作");
		new_report_I_WorkInfo.setWorkMonthFlag(workMonthFlag);
		new_report_I_WorkInfo.setOrderNumber(orderNumber);
//		new_report_I_WorkInfo.setWorkTitle(workTitle);
//		new_report_I_WorkInfo.setWorkType();
//		new_report_I_WorkInfo.setKeyWorkId(keyWorkId);
//		new_report_I_WorkInfo.setMeasuresList(measuresList);
		Report_I_WorkInfoDetail report_I_WorkInfoDetail = new Report_I_WorkInfoDetail();
		report_I_WorkInfoDetail.setId( new_report_I_WorkInfo.getId() );
		report_I_WorkInfoDetail.setKeyWorkId("");
		report_I_WorkInfoDetail.setReportId(report_I_Base.getId());
		report_I_WorkInfoDetail.setDescribe("");
		report_I_WorkInfoDetail.setWorkPlanSummary("");
		report_I_WorkInfoDetail.setWorkProgSummary("");
		
		report_I_WorkInfoServiceAdv.save(new_report_I_WorkInfo, report_I_WorkInfoDetail );
		
		return new_report_I_WorkInfo;
	}

	/**
	 * 根据启动概要和身份组织一个汇报信息对象
	 * 
	 * 标题：浙江兰德纵横2018年1月工作总结和2018年2月工作计划
	 * 
	 * @param profile
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	private Report_I_Base composeReportBaseInfo( Report_P_Profile profile, String identity, String report_unitName ) throws Exception {
		
		Date reportTargetDate = dateOperation.getDateFromString( profile.getReportYear() + "-" + profile.getReportMonth() + "-01" );
		//计算下一个月的年份和月份
		Date nextMonthDate = dateOperation.getMonthAddDate( reportTargetDate, 1 ); //下1个月
		String nextMonth_year = dateOperation.getYear( nextMonthDate );
		String nextMonth_month = dateOperation.getMonth( nextMonthDate );
		String person = userManagerService.getPersonNameByIdentity(identity);
		String unitShortName = report_unitName.split( "@" )[0];
		StringBuffer titleBuf = new StringBuffer();
		//拼接标题信息
		titleBuf.append( profile.getReportYear() );
		titleBuf.append( "年" );
		titleBuf.append( profile.getReportMonth() );
		titleBuf.append( "月工作汇报总结和" );
		titleBuf.append( nextMonth_year );
		titleBuf.append( "年" );
		titleBuf.append( nextMonth_month );
		titleBuf.append( "月工作计划" );
		titleBuf.append( "(" );
		titleBuf.append( unitShortName );
		titleBuf.append( ")" );
	
		Report_I_Base report_I_Base = new Report_I_Base();
		
		report_I_Base.setProfileId( profile.getId() );
		report_I_Base.setTitle( titleBuf.toString() );
		report_I_Base.setYear( profile.getReportYear() );
		report_I_Base.setMonth( profile.getReportMonth() );
		report_I_Base.setWeek( profile.getReportWeek() );
		report_I_Base.setReportDate(null );
		report_I_Base.setReportDateString( null );
		report_I_Base.setCreateDateString( profile.getCreateDateString() );
		report_I_Base.setReportObjType( "UNIT" );
		report_I_Base.setTargetIdentity( identity );
		report_I_Base.setTargetPerson( person );
		report_I_Base.setTargetUnit( report_unitName );
		report_I_Base.setReportType( profile.getReportType() );
		if( profile.getReportYear() != null &&  !profile.getReportYear().isEmpty()  ) {
			report_I_Base.setFlag( profile.getReportYear() );
		}
		if( profile.getReportMonth() != null &&  !profile.getReportMonth().isEmpty()  ) {
			report_I_Base.setFlag( report_I_Base.getFlag() + profile.getReportMonth() );
		}
		report_I_Base.setReportStatus( "待启动" );
		report_I_Base.setActivityName( "待启动" );
		
		return report_I_Base;
	}

}
