package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.dataadapter.workflow.WorkFlowDelete;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanDetail;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_C_WorkPlanNextDetail;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_C_WorkProgDetail;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Detail;
import com.x.report.core.entity.Report_I_Ext_Content;
import com.x.report.core.entity.Report_I_Ext_ContentDetail;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;
import com.x.report.core.entity.Report_P_Permission;
import com.x.report.core.entity.Report_P_Profile;
import com.x.report.core.entity.Report_P_ProfileDetail;

/**
 * 汇报生成概要记录文件信息服务类
 * @author O2LEE
 *
 */
public class Report_P_ProfileOperationServiceAdv{	

	/**
	 * 根据概要文件重新生成汇报文件, 已经存在的汇报就不会重发了，跳过
	 * 用于补发之前漏发汇报的补充工作
	 * @param profile
	 * @return
	 * @throws Exception 
	 */
	public boolean restart( Report_P_Profile profile ) throws Exception {
		
		
		return false;
	}

	/**
	 * 根据概要文件信息，邮件概要文件相关的所有生成过的信息
	 * @param profile
	 * @return
	 * @throws Exception 
	 */
	public Boolean deleteInfo(Report_P_Profile profile) throws Exception {
		if (profile == null ) {
			throw new Exception("profile is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( deleteInfoWithProfile(emc, profile ) ) {
				emc.commit();
				return true;
			}else {
				emc.rollback();
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	/**
	 * 根据概要文件信息，邮件概要文件以及所有生成过的信息
	 * @param profile
	 * @return
	 * @throws Exception 
	 */
	public Boolean delete( Report_P_Profile profile ) throws Exception {
		if (profile == null ) {
			throw new Exception("profile is null.");
		}
		Report_P_ProfileDetail detail = null;
		List<String> ids = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( deleteInfoWithProfile(emc, profile ) ) {
				business = new Business(emc);
				//删除profile
				profile = emc.find( profile.getId(), Report_P_Profile.class );
				ids = business.report_P_ProfileDetailFactory().listWithProfileId(profile.getId() );
				emc.beginTransaction( Report_P_Profile.class );
				emc.beginTransaction( Report_P_ProfileDetail.class );				
				if( ListTools.isNotEmpty( ids )) {
					for( String id : ids ) {
						detail = emc.find( id, Report_P_ProfileDetail.class );
						if( detail != null ) {
							emc.remove( detail, CheckRemoveType.all );
						}
					}
				}
				if( profile != null ) {
					emc.remove( profile, CheckRemoveType.all );
				}
				emc.commit();
				return true;
			}else {
				emc.rollback();
			}
		} catch (Exception e) {
			throw e;
		}
		
		return false;
	}	
	
	public Boolean deleteInfoWithProfile(EntityManagerContainer emc, Report_P_Profile profile) throws Exception {
		if (profile == null ) {
			throw new Exception("profile is null.");
		}
		String profileId = profile.getId();
		Report_I_Base report_base = null;
		Report_C_WorkPlan work_plan = null;
		Report_C_WorkPlanDetail work_planDetail = null;
		Report_C_WorkPlanNext work_planNext = null;
		Report_C_WorkProg work_prog = null;
		Report_I_Ext_Content  report_I_Ext_Content = null;
		Report_P_Permission permission = null;
		Report_I_WorkInfo workInfo = null;
		Report_I_WorkInfoDetail workDetail = null;
		WorkFlowDelete workFlowDelete = new WorkFlowDelete();
		List<String> ids = null;
		
		Business business = new Business(emc);
		
		emc.beginTransaction( Report_I_Base.class );
		emc.beginTransaction( Report_I_Detail.class );
		emc.beginTransaction( Report_C_WorkPlan.class );
		emc.beginTransaction( Report_C_WorkPlanDetail.class );
		emc.beginTransaction( Report_C_WorkPlanNextDetail.class );
		emc.beginTransaction( Report_C_WorkPlanNext.class );
		emc.beginTransaction( Report_C_WorkProg.class );
		emc.beginTransaction( Report_C_WorkProgDetail.class );
		emc.beginTransaction( Report_P_Permission.class );
		emc.beginTransaction( Report_I_WorkInfo.class );
		emc.beginTransaction( Report_I_WorkInfoDetail.class );
		emc.beginTransaction( Report_I_Ext_Content.class );
		emc.beginTransaction( Report_I_Ext_ContentDetail.class );
		
		//根据概要文件ID删除生成的汇报信息以及详细信息
		ids = business.report_I_BaseFactory().listIdsWithProfileId( profileId );
		if( ListTools.isNotEmpty( ids )) {
			List<Report_I_Detail> reportDetailList = null;
			for( String id : ids ) {
				report_base = emc.find( id, Report_I_Base.class );
				if( report_base != null  ) {
					emc.remove( report_base, CheckRemoveType.all );					
				}
				reportDetailList = business.report_I_DetailFactory().listWithReportId(id);
				if( ListTools.isNotEmpty( reportDetailList )) {
					for( Report_I_Detail report_I_Detail : reportDetailList ) {
						emc.remove( report_I_Detail, CheckRemoveType.all );
					}
				}
				//删除已经启动的流程实例
				try {
					if( StringUtils.isNotEmpty( report_base.getWf_WorkId() )) {
						workFlowDelete.deleteProcessInstance( report_base.getWf_WorkId() );
					}		
				}catch( Exception e) {
					e.printStackTrace();					
				}
			}
		}
		
		//根据概要文件ID删除生成的计划信息
		ids = business.report_C_WorkPlanFactory().listIdsWithProfileId(profileId);			
		if( ListTools.isNotEmpty( ids )) {
			for( String id : ids ) {
				work_plan = emc.find( id, Report_C_WorkPlan.class );
				if( work_plan != null  ) {
					emc.remove( work_plan, CheckRemoveType.all );
				}
				work_planDetail = emc.find( id, Report_C_WorkPlanDetail.class );
				if( work_planDetail != null  ) {
					emc.remove( work_planDetail, CheckRemoveType.all );
				}
			}
		}
		
		//根据概要文件ID删除生成的下周期计划信息
		ids = business.report_C_WorkPlanNextFactory().listIdsWithProfileId(profileId);			
		if( ListTools.isNotEmpty( ids )) {
			List<Report_C_WorkPlanNextDetail>  planDetailList = null;
			for( String id : ids ) {
				work_planNext = emc.find( id, Report_C_WorkPlanNext.class );
				if( work_planNext != null  ) {
					emc.remove( work_planNext, CheckRemoveType.all );
				}
				planDetailList = business.report_C_WorkPlanNextDetailFactory().listWorkPlanDetailWithPlanId(id);
				for( Report_C_WorkPlanNextDetail report_C_WorkPlanNextDetail : planDetailList ) {
					emc.remove( report_C_WorkPlanNextDetail, CheckRemoveType.all );
				}
			}
		}
		//根据概要文件ID删除生成的工作完成情况信息
		ids = business.report_C_WorkProgFactory().listIdsWithProfileId(profileId);			
		if( ListTools.isNotEmpty( ids )) {
			List<Report_C_WorkProgDetail>  progDetailList = null;
			for( String id : ids ) {
				work_prog = emc.find( id, Report_C_WorkProg.class );
				if( work_prog != null  ) {					
					emc.remove( work_prog, CheckRemoveType.all );
				}
				progDetailList = business.report_C_WorkProgDetailFactory().listDetailWithProgId(id);
				for( Report_C_WorkProgDetail report_C_WorkProgDetail : progDetailList ) {
					emc.remove( report_C_WorkProgDetail, CheckRemoveType.all );
				}
			}
		}
		
		//根据概要文件ID删除生成的汇报扩展信息信息
		ids = business.report_I_Ext_ContentFactory().listIdsWithProfile(profileId);
		if( ListTools.isNotEmpty( ids )) {
			List<Report_I_Ext_ContentDetail>  extContentDetailList = null;
			for( String id : ids ) {
				report_I_Ext_Content = emc.find( id, Report_I_Ext_Content.class );
				if( report_I_Ext_Content != null ) {
					emc.remove( report_I_Ext_Content, CheckRemoveType.all );
				}				
				extContentDetailList = business.report_I_Ext_ContentDetailFactory().listWithContentId(id);
				if(ListTools.isNotEmpty( extContentDetailList )) {
					for( Report_I_Ext_ContentDetail report_I_Ext_ContentDetail : extContentDetailList ) {
						emc.remove( report_I_Ext_ContentDetail, CheckRemoveType.all );
					}
				}
			}
		}
		
		//根据概要文件ID删除所有的权限信息
		ids = business.report_P_PermissionFactory().listIdsWithProfileId(profileId);			
		if( ListTools.isNotEmpty( ids )) {
			for( String id : ids ) {
				permission = emc.find( id, Report_P_Permission.class );
				if( permission != null  ) {
					emc.remove( permission, CheckRemoveType.all );
				}
			}
		}			
		
		//根据概要文件ID删除与汇报相关的所有工作信息和工作详细信息
		ids = business.report_I_WorkInfoFactory().listIdsWithProfileId(profileId);			
		if( ListTools.isNotEmpty( ids )) {
			for( String id : ids ) {
				workInfo = emc.find( id, Report_I_WorkInfo.class );
				if( workInfo != null  ) {
					emc.remove( workInfo, CheckRemoveType.all );
				}
				workDetail = emc.find( id, Report_I_WorkInfoDetail.class );
				if( workDetail != null  ) {
					emc.remove( workDetail, CheckRemoveType.all );
				}
			}
		}
		return true;
	}
}
