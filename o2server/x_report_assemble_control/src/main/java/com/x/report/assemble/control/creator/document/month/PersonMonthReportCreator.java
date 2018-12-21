package com.x.report.assemble.control.creator.document.month;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoCompanyStrategy;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoCompanyStrategyWorks;
import com.x.report.assemble.control.service.Report_C_WorkPlanNextServiceAdv;
import com.x.report.assemble.control.service.Report_I_ServiceAdv;
import com.x.report.assemble.control.service.Report_P_PermissionServiceAdv;
import com.x.report.assemble.control.service.UserManagerService;
import com.x.report.common.date.DateOperation;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanDetail;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_C_WorkPlanNextDetail;
import com.x.report.core.entity.Report_C_WorkProgDetail;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_P_Profile;

public class PersonMonthReportCreator {
	
	private Logger logger = LoggerFactory.getLogger( PersonMonthReportCreator.class );
	private UserManagerService userManagerService = new UserManagerService();
	private Report_I_ServiceAdv report_I_ServiceAdv = new Report_I_ServiceAdv();
	private Report_C_WorkPlanNextServiceAdv report_C_WorkPlanNextServiceAdv = new Report_C_WorkPlanNextServiceAdv();
	private Report_P_PermissionServiceAdv report_I_PermissionServiceAdv = new Report_P_PermissionServiceAdv();
	private DateOperation dateOperation = new DateOperation();

    /**
     * 根据汇报生成概要文件信息以及需要生成汇报的个人标识，为该用户生成月度汇报文档信息
     * @param effectivePerson
     * @param profile
     * @param companyStrategies
     * @param unitStrategyWorks
     * @param identity
     * @return
     * @throws Exception
     */
	public Boolean create( EffectivePerson effectivePerson, Report_P_Profile profile, List<WoCompanyStrategy> companyStrategies, List<WoCompanyStrategyWorks> unitStrategyWorks, String identity ) throws Exception {
		Date reportTargetDate = null;
		Date lastTargetMonthDate = null;
		String dateString = null;
		String lastTargetMonth_month = null;
		String lastTargetMonth_year = null;
		Report_I_Base report_I_Base = null;
		List<Report_I_Base> reportList = null;
		
		//根据汇报概要文件信息以及个人的身份来组织一个月度组织汇报基础信息对象
		report_I_Base = composeReportBaseInfo( profile, identity );
		
		//查询该员工该月是否已经生成过汇报了，如果已经生成过，就跳过该员工
		reportList = report_I_ServiceAdv.list( 
				report_I_Base.getReportType(), 
				report_I_Base.getReportObjType(), 
				report_I_Base.getTargetPerson(),
				null, 
				report_I_Base.getYear(), 
				report_I_Base.getMonth(), 
				null, 
				null, 
				null , false 
		);
		if( reportList != null && !reportList.isEmpty() ) {
			logger.debug( effectivePerson, "该员工的月度汇报信息已经存在，不需要再次生成!");
			return true;
		}
		
		//获取汇报的目标年月组织的时刻
		reportTargetDate = dateOperation.getDateFromString( report_I_Base.getYear() + "-" + report_I_Base.getMonth() + "-01" );
				
		//组织所有的工作计划（查询上个月汇报的下个有工作计划内容）
		//计算上个月的年份和月份
		dateString = dateOperation.getMonthAdd( reportTargetDate, -1 ); //时间回溯1个月
		lastTargetMonthDate = dateOperation.getDateFromString( dateString );
		lastTargetMonth_year = dateOperation.getYear( lastTargetMonthDate );
		lastTargetMonth_month = dateOperation.getMonth( lastTargetMonthDate );
		
		List<Report_C_WorkPlanNext> workPlanNextList = null;
		List<Report_C_WorkPlan> workPlanList = new ArrayList<>();
		List<Report_C_WorkPlanDetail> workPlanDetailList = new ArrayList<>();
		Report_I_Base report_I_BaseNext = null;
		Report_C_WorkPlan report_C_WorkPlan = null;
		Report_C_WorkPlanDetail report_C_WorkPlanDetail = null;
		Report_C_WorkPlanNextDetail report_C_WorkPlanNextDetail = null;
		
		//查询上个月的工作汇报信息，根据汇报信息ID，查询上个月的所有工作计划列表
		reportList = report_I_ServiceAdv.list( 
				report_I_Base.getReportType(), 
				report_I_Base.getReportObjType(), 
				report_I_Base.getTargetPerson(),
				null, 
				lastTargetMonth_year, 
				lastTargetMonth_month, 
				null, 
				null,
				null, false 
		);
		//处理当月工作计划内容
		//如果有上次汇报，则取上次汇报的下周期工作汇报信息转换为当月工作计划内容
		if( reportList != null && !reportList.isEmpty() ) {
			report_I_BaseNext = reportList.get( 0 );
			workPlanNextList = report_C_WorkPlanNextServiceAdv.listWithReportId( report_I_BaseNext.getId() );
			if( workPlanNextList != null && !workPlanNextList.isEmpty() ) {
				for( Report_C_WorkPlanNext nextPlan : workPlanNextList ) {
					report_C_WorkPlanNextDetail = report_C_WorkPlanNextServiceAdv.getDetailWithPlanId( nextPlan.getId() );					
					//转为当月的工作计划内容
					report_C_WorkPlan = new Report_C_WorkPlan();
					report_C_WorkPlanDetail = new Report_C_WorkPlanDetail();					
					//COPY信息到当月的工作计划中
					nextPlan.copyTo( report_C_WorkPlan, JpaObject.FieldsUnmodify );
					report_C_WorkPlanNextDetail.copyTo( report_C_WorkPlanDetail, JpaObject.FieldsUnmodify );					
					report_C_WorkPlan.setId( Report_C_WorkPlan.createId() );
					report_C_WorkPlan.setReportId( report_I_Base.getId() );
                    report_C_WorkPlanDetail.setId( Report_C_WorkProgDetail.createId() );
                    report_C_WorkPlanDetail.setPlanId( report_C_WorkPlan.getId() );
                    report_C_WorkPlanDetail.setReportId( report_I_Base.getId() );	
                    
					workPlanList.add( report_C_WorkPlan );
					workPlanDetailList.add( report_C_WorkPlanDetail );
				}
			}
		}
		
		report_I_ServiceAdv.save( report_I_Base, null, workPlanList, workPlanDetailList, null, null, null, null );
		
		//将读者以及作者信息持久化到数据库中
		report_I_PermissionServiceAdv.refreshReportPermission( report_I_Base.getId() );
		
		return true;
	}
	
	private Report_I_Base composeReportBaseInfo( Report_P_Profile profile, String identity ) throws Exception {
		String person = userManagerService.getPersonNameByIdentity(identity);
		String unitName = userManagerService.getUnitNameByIdentity(identity) ;
		String unitShortName = unitName.split( "@" )[0];
		String personShortName = person.split("@")[0];
		String title = profile.getReportYear() + "年"+profile.getReportMonth()+"月个人战略工作汇报（"+ unitShortName + "-" + personShortName +"）";
		
		Report_I_Base report_I_Base = new Report_I_Base();		
		report_I_Base.setProfileId( profile.getId() );
		report_I_Base.setTitle( title );
		report_I_Base.setYear( profile.getReportYear() );
		report_I_Base.setMonth( profile.getReportMonth() );
		report_I_Base.setWeek( profile.getReportWeek() );
		report_I_Base.setReportDate(null );
		report_I_Base.setReportDateString( null );
		report_I_Base.setCreateDateString( profile.getCreateDateString() );
		report_I_Base.setReportObjType( "PERSON" );
		report_I_Base.setTargetPerson( person );
		report_I_Base.setTargetUnit( unitName );
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
