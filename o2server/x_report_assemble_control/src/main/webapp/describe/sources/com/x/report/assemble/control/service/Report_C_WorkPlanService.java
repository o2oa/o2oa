package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.jaxrs.workplan.ActionSaveWorkPlan.Wi;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanDetail;
import com.x.report.core.entity.Report_C_WorkProgDetail;

/**
 * 汇报工作计划信息服务类
 * @author O2LEE
 *
 */
public class Report_C_WorkPlanService{

	public List<Report_C_WorkPlan> listWithReportId(EntityManagerContainer emc, String reportId) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanFactory().listWorkPlanWithReportId( reportId );
	}
	
	public List<String> listWithReportIds(EntityManagerContainer emc, List<String> reportIds) throws Exception {
		if ( ListTools.isEmpty( reportIds )) {
			throw new Exception("reportId is empty.");
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanFactory().listWorkPlanWithReportIds(reportIds);
	}

	public List<Report_C_WorkPlanDetail> listDetailWithPlanId(EntityManagerContainer emc, String planId) throws Exception {
		if( planId == null || planId.isEmpty() ){
			throw new Exception("planId is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanFactory().listDetailNextWithPlanId( planId );
	}

	public Report_C_WorkPlan save( EntityManagerContainer emc, Wi wi ) throws Exception {
		Report_C_WorkPlan plan = null;
        Report_C_WorkPlanDetail detail = null;
		List<Report_C_WorkPlanDetail> detailList = null;
		
		plan = emc.find( wi.getId(), Report_C_WorkPlan.class );
		Business business = new Business(emc);
		
		detailList = business.report_C_WorkPlanFactory().listDetailNextWithPlanId( wi.getId() );
		
		emc.beginTransaction( Report_C_WorkPlan.class );
		emc.beginTransaction( Report_C_WorkPlanDetail.class );
		if( plan == null ) {
			//不存在，则需要进行添加，先删除该planId对应的所有detail
			if( detailList != null && !detailList.isEmpty() ) {
				for( Report_C_WorkPlanDetail _detail : detailList ) {
					emc.remove( _detail, CheckRemoveType.all );
				}
			}
			
			plan = Wi.copier.copy( wi );
			plan.setId( wi.getId() );
			
			detail = new Report_C_WorkPlanDetail();
			detail.setId( Report_C_WorkPlanDetail.createId() );
			detail.setPlanId( plan.getId() );
			detail.setReportId( plan.getReportId() );
			detail.setKeyWorkId( plan.getKeyWorkId() );
			detail.setWorkInfoId( plan.getWorkInfoId() );
			detail.setWorkTitle( plan.getWorkTitle() );
			detail.setPlanContent( wi.getPlanContent() );
			detail.setWorkContent( wi.getWorkContent() );
			
			emc.persist( plan, CheckPersistType.all );
			emc.persist( detail, CheckPersistType.all );
			
		}else {
			//已存在，需要进行修改
			wi.copyTo( plan, JpaObject.FieldsUnmodify );
			//已存在，更新第一条，删除其他
			if( detailList != null && !detailList.isEmpty() ) {
				int index = 0;
				for( Report_C_WorkPlanDetail _detail : detailList ) {
					if( index == 0 ) {
						//更新
						_detail.setKeyWorkId( plan.getKeyWorkId() );
						_detail.setWorkInfoId( plan.getWorkInfoId() );
						_detail.setWorkTitle( plan.getWorkTitle() );
						_detail.setPlanContent( wi.getPlanContent() );
						_detail.setWorkContent( wi.getWorkContent() );
						emc.check( _detail, CheckPersistType.all );
					}else {
						emc.remove( _detail, CheckRemoveType.all );
					}
					index++;
				}
			}else {
				//没有，就要创建
				detail = new Report_C_WorkPlanDetail();
				detail.setId( Report_C_WorkProgDetail.createId() );
				detail.setPlanId( plan.getId() );
				detail.setReportId( plan.getReportId() );
				detail.setKeyWorkId( plan.getKeyWorkId() );
				detail.setWorkInfoId( plan.getWorkInfoId() );
				detail.setWorkTitle( plan.getWorkTitle() );
				detail.setPlanContent( wi.getPlanContent() );
				detail.setWorkContent( wi.getWorkContent() );
				emc.persist( detail, CheckPersistType.all );
			}
			emc.check( plan, CheckPersistType.all );
		}
		
		emc.commit();
		return plan;
	}

	public List<String> listWithReportAndWorkInfoId(EntityManagerContainer emc, String reportId, String workInfoId) throws Exception {
		if (reportId == null || reportId.isEmpty()) {
			throw new Exception("reportId is null.");
		}
		if (workInfoId == null || workInfoId.isEmpty()) {
			throw new Exception("workInfoId is null.");
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanFactory().listWithReportAndWorkInfoId( reportId, workInfoId );
	}

	public List<Report_C_WorkPlan> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanFactory().list( ids );
	}

	public Boolean updateOrderNumber(EntityManagerContainer emc, String planId, Integer orderNumber) throws Exception {
		if (StringUtils.isEmpty( planId )) {
			throw new Exception("planId is null.");
		}
		if ( orderNumber == null ) {
			throw new Exception("orderNumber is null.");
		}
		Report_C_WorkPlan plan = emc.find( planId, Report_C_WorkPlan.class );
		if ( plan != null ) {
			emc.beginTransaction( Report_C_WorkPlan.class );
			plan.setOrderNumber(orderNumber);
			emc.check( plan, CheckPersistType.all );
			emc.commit();
			return true;
		}
		return false;
	}
}
