package com.x.report.assemble.control.service;

import java.util.List;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.jaxrs.report.element.PermissionInfo;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanDetail;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_C_WorkPlanNextDetail;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_C_WorkProgDetail;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Detail;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;
import com.x.report.core.entity.Report_P_Permission;
import com.x.report.core.entity.Report_P_Profile;

/**
 * 汇报信息服务类
 * @author O2LEE
 *
 */
public class Report_I_SaveService{
	private Gson gson = XGsonBuilder.instance();

	/**
	 * 保存工作汇报，需要做成一个事务
	 * @param emc 
	 * @param report_I_Base
	 * @param report_I_Detail
	 * @param workPlanList
	 * @param workPlanDetailList
	 * @param workProgList
	 * @param workProgDetailList
	 * @param workPlanNextList
	 * @param workPlanDetailNextList
	 * @throws Exception
	 */
	public void save( EntityManagerContainer emc, Report_I_Base report_I_Base, Report_I_Detail report_I_Detail,
			List<Report_C_WorkPlan> workPlanList,  List<Report_C_WorkPlanDetail> workPlanDetailList,
			List<Report_C_WorkProg> workProgList, List<Report_C_WorkProgDetail> workProgDetailList,
			List<Report_C_WorkPlanNext> workPlanNextList, List<Report_C_WorkPlanNextDetail> workPlanDetailNextList
	) throws Exception {
		
		Report_I_Base report_base_old = null;
		Business business = null;
		
		business = new Business(emc);
		
		emc.beginTransaction( Report_I_Base.class );
		emc.beginTransaction( Report_I_Detail.class );
		emc.beginTransaction( Report_C_WorkPlan.class );
		emc.beginTransaction( Report_C_WorkPlanDetail.class );
		emc.beginTransaction( Report_C_WorkPlanNext.class );
		emc.beginTransaction( Report_C_WorkPlanNextDetail.class );
		emc.beginTransaction( Report_C_WorkProg.class );
		emc.beginTransaction( Report_C_WorkProgDetail.class );
		emc.beginTransaction( Report_P_Permission.class );
		emc.beginTransaction( Report_P_Profile.class );
		
		report_base_old = emc.find( report_I_Base.getId(), Report_I_Base.class );
		
		//1、更新或者创建新的汇报基础信息
		if( report_base_old != null ){
			report_I_Base = update( emc, report_I_Base, false );
		}else{
			report_I_Base = create( emc, report_I_Base, false );	
		}
		
		//更新汇报详细信息，先删除再新增
		Report_I_Detail report_I_Detail_old = null;
		report_I_Detail_old = emc.find( report_I_Base.getId(), Report_I_Detail.class );
		
		if( report_I_Detail_old != null ) {
			emc.remove( report_I_Detail_old, CheckRemoveType.all );
		}
		
		if( report_I_Detail != null ) {
			report_I_Detail.setId( report_I_Base.getId() );
			report_I_Detail.setReportId( report_I_Base.getId() );
			emc.persist( report_I_Detail, CheckPersistType.all );
		}
		
		//2、更新或者创建新的工作计划
		List<Report_C_WorkPlan> workPlanList_old = null;
		List<String> workPlanDetailIds_old = null;
		//先删除所有的工作计划信息
		workPlanList_old = business.report_C_WorkPlanFactory().listWorkPlanWithReportId( report_I_Base.getId() );
        workPlanDetailIds_old = business.report_C_WorkProgDetailFactory().listDetailIdsWithReportId(report_I_Base.getId());
		if( workPlanList_old != null && !workPlanList_old.isEmpty() ) {
			for( Report_C_WorkPlan plan : workPlanList_old ) {
				emc.remove( plan, CheckRemoveType.all );
			}
		}
		if( ListTools.isNotEmpty(workPlanDetailIds_old) ) {
			for( String _id : workPlanDetailIds_old ) {
                Report_C_WorkProgDetail detail = emc.find( _id, Report_C_WorkProgDetail.class );
				emc.remove( detail, CheckRemoveType.all );
			}
		}
		//再新增所有工作计划
		if( workPlanList != null && !workPlanList.isEmpty() ) {
			for( Report_C_WorkPlan plan : workPlanList ) {
				emc.persist( plan, CheckPersistType.all );
			}
		}
		if( workPlanDetailList != null && !workPlanDetailList.isEmpty() ) {
			for( Report_C_WorkPlanDetail detail : workPlanDetailList ) {
				emc.persist( detail, CheckPersistType.all );
			}
		}
		
		//3、更新或者创建新的实际工作情况信息
		List<Report_C_WorkProg> workProcList_old = null;
		List<String> workProcDetailIds_old = null;

		//先删除所有的实际工作情况信息
		workProcList_old = business.report_C_WorkProgFactory().listWorkProgWithReportId( report_I_Base.getId() );
        workProcDetailIds_old = business.report_C_WorkProgDetailFactory().listDetailIdsWithReportId( report_I_Base.getId() );
		if( workProcList_old != null && !workProcList_old.isEmpty() ) {
			for( Report_C_WorkProg prog : workProcList_old ) {
				emc.remove( prog, CheckRemoveType.all );
			}
		}
		if( ListTools.isNotEmpty(workProcDetailIds_old) ) {
			for( String _id : workProcDetailIds_old ) {
                Report_C_WorkProgDetail detail = emc.find( _id, Report_C_WorkProgDetail.class );
				emc.remove( detail, CheckRemoveType.all );
			}
		}
		//再新增新的工作完成情况信息
		if( workProgList != null && !workProgList.isEmpty() ) {
			for( Report_C_WorkProg prog : workProgList ) {
				emc.persist( prog, CheckPersistType.all );
			}
		}
		if( workProgDetailList != null && !workProgDetailList.isEmpty() ) {
			for( Report_C_WorkProgDetail detail : workProgDetailList ) {
				emc.persist( detail, CheckPersistType.all );
			}
		}
		
		//4、更新或者创建新的下月工作计划
		List<Report_C_WorkPlanNext> workPlanNextList_old = null;
		List<Report_C_WorkPlanNextDetail> workPlanDetailNextList_old = null;
		//先删除所有的下月工作计划信息
		workPlanNextList_old = business.report_C_WorkPlanNextFactory().listWithReportId( report_I_Base.getId() );
		workPlanDetailNextList_old = business.report_C_WorkPlanNextDetailFactory().listWorkPlanDetailNext( report_I_Base.getId() );
		if( workPlanNextList_old != null && !workPlanNextList_old.isEmpty() ) {
			for( Report_C_WorkPlanNext plan : workPlanNextList_old ) {
				emc.remove( plan, CheckRemoveType.all );
			}
		}
		if( workPlanDetailNextList_old != null && !workPlanDetailNextList_old.isEmpty() ) {
			for( Report_C_WorkPlanNextDetail detail : workPlanDetailNextList_old ) {
				emc.remove( detail, CheckRemoveType.all );
			}
		}			
		//再新增所有下月工作计划
		if( workPlanNextList != null && !workPlanNextList.isEmpty() ) {
			for( Report_C_WorkPlanNext plan : workPlanNextList ) {
				emc.persist( plan, CheckPersistType.all );
			}
		}
		if( workPlanDetailNextList != null && !workPlanDetailNextList.isEmpty() ) {
			for( Report_C_WorkPlanNextDetail detail : workPlanDetailNextList ) {
				emc.persist( detail, CheckPersistType.all );
			}
		}			
		emc.commit();
	}

	/**
	 * 更新汇报的 部门重点工作信息、重点工作举措关联信息以及汇报处理人，读者，作者待信息
	 * @param emc
	 * @param reportId
	 * @param workList
	 * @param workeportPersonList
	 * @param readerList
	 * @param authorList
	 * @throws Exception
	 */
	public void save(EntityManagerContainer emc, String reportId, List<Report_I_WorkInfo> workList, List<String> workeportPersonList, List<PermissionInfo> readerList, List<PermissionInfo> authorList) throws Exception {
		emc.beginTransaction( Report_I_Base.class );
		emc.beginTransaction( Report_I_Detail.class );
		emc.beginTransaction( Report_I_WorkInfo.class );
		emc.beginTransaction( Report_I_WorkInfoDetail.class );
		emc.beginTransaction( Report_P_Permission.class );

		Report_I_Base report_I_Base = emc.find( reportId, Report_I_Base.class );
		//1、更新或者创建新的汇报基础信息
		if( report_I_Base == null ){
			throw new Exception("report not exits, id:" + reportId);
		}

		//更新汇报业务处理人列表
		report_I_Base.setWorkreportPersonList(workeportPersonList);
		emc.check( report_I_Base, CheckPersistType.all );

		//查询并且更新汇报详细信息
		Report_I_Detail report_I_Detail = emc.find( reportId, Report_I_Detail.class );

		if( report_I_Detail == null ){
			report_I_Detail = new Report_I_Detail();		
			report_I_Detail.setReaders( "{}" );
			report_I_Detail.setAuthors( "{}" );
			report_I_Detail.setTitle( report_I_Base.getTitle() );
			report_I_Detail.setWorkflowLog( "{}" );
			
			report_I_Detail.setReportId( report_I_Base.getId() );
			report_I_Detail.setId( report_I_Base.getId() );
			emc.persist( report_I_Detail, CheckPersistType.all );
			
			report_I_Detail = emc.find(  report_I_Base.getId(), Report_I_Detail.class );
		}
		
		if( ListTools.isNotEmpty( readerList )){
			report_I_Detail.setReaders( gson.toJson( readerList ));
		}
		
		if( ListTools.isNotEmpty( authorList )){
			report_I_Detail.setAuthors( gson.toJson( authorList ));
		}
		
		report_I_Detail.setId( reportId );
		emc.check( report_I_Detail, CheckPersistType.all );


		//更新部门重点工作信息以及重点工作和举措的关联信息
		Report_I_WorkInfo workInfo;
		if( ListTools.isNotEmpty( workList )){
			Business business = new Business(emc);
			for( Report_I_WorkInfo _workInfo : workList ){
				workInfo = business.report_I_WorkInfoFactory().getWithReportAndWorkId( reportId, _workInfo.getId() );				
				if( workInfo != null ){
					workInfo.setOrderNumber( _workInfo.getOrderNumber());
					workInfo.setWorkType( _workInfo.getWorkType() );
					workInfo.setWorkTag( _workInfo.getWorkTag() );
					workInfo.setWorkTitle(_workInfo.getWorkTitle());
					workInfo.setMeasuresList( _workInfo.getMeasuresList() );
					emc.check( workInfo, CheckPersistType.all );
				}
			}
		}
		emc.commit();
	}

	public Report_I_Base update( EntityManagerContainer emc, Report_I_Base report_I_Base, Boolean autoCommit ) throws Exception {
		if( report_I_Base == null ){
			throw new Exception("report_I_Base is null, can not update object!");
		}
		Report_I_Base report_I_Base_old = null;
		report_I_Base_old = emc.find( report_I_Base.getId(), Report_I_Base.class );
		if( report_I_Base_old != null ){
			if( autoCommit ) {
				emc.beginTransaction( Report_I_Base.class );
			}
			report_I_Base.copyTo( report_I_Base_old, JpaObject.FieldsUnmodify );
			emc.check( report_I_Base_old, CheckPersistType.all);
			if( autoCommit ) {
				emc.commit();
			}
		}else{
			throw new Exception("old object report_I_Base{'id':' "+ report_I_Base.getId() +" '} is not exists. ");
		}
		return report_I_Base;
	}

	public Report_I_Base create( EntityManagerContainer emc, Report_I_Base report_I_Base, Boolean autoCommit ) throws Exception {
		Report_I_Base report_I_Base_old = null;
		report_I_Base_old = emc.find( report_I_Base.getId(), Report_I_Base.class );
		if( report_I_Base_old != null ){
			throw new Exception("report_I_Base{'id':' "+ report_I_Base.getId() +" '} exists, can not create new object");
		}else{
			if( autoCommit ) {
				emc.beginTransaction( Report_I_Base.class );
			}
			emc.persist( report_I_Base, CheckPersistType.all);
			if( autoCommit ) {
				emc.commit();
			}
		}
		return report_I_Base;
	}
}
