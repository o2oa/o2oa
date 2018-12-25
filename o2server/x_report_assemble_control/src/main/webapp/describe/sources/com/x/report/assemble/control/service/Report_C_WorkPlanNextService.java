package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.jaxrs.workplan.ActionSaveWorkPlanNext.Wi;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_C_WorkPlanNextDetail;

/**
 * 汇报下一周期工作计划信息服务类
 * 
 * @author O2LEE
 *
 */
public class Report_C_WorkPlanNextService{

	public List<Report_C_WorkPlanNext> listWithReportId(EntityManagerContainer emc, String reportId) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception("reportId is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanNextFactory().listWithReportId( reportId );
	}
	
	public List<String> listWithReportIds(EntityManagerContainer emc, List<String> reportIds) throws Exception {
		if( ListTools.isEmpty( reportIds ) ){
			throw new Exception("reportId is empty!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanNextFactory().listWithReportIds( reportIds );
	}

	public List<Report_C_WorkPlanNextDetail> listDetailWithPlanId(EntityManagerContainer emc, String planId) throws Exception {
		if( planId == null || planId.isEmpty() ){
			throw new Exception("planId is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanNextFactory().listDetailNextWithPlanId( planId );
	}

	public Report_C_WorkPlanNext save(EntityManagerContainer emc, Wi wi) throws Exception {
		Report_C_WorkPlanNext plan = null;
		Report_C_WorkPlanNextDetail detail = null;
		List<Report_C_WorkPlanNextDetail> detailList = null;
		
		plan = emc.find( wi.getId(), Report_C_WorkPlanNext.class );
		Business business = new Business(emc);
		
		detailList = business.report_C_WorkPlanNextDetailFactory().listWorkPlanDetailWithPlanId( wi.getId() );
		
		emc.beginTransaction( Report_C_WorkPlanNext.class );
		emc.beginTransaction( Report_C_WorkPlanNextDetail.class );
		if( plan == null ) {
			//不存在，则需要进行添加，先删除该planId对应的所有detail
			if( detailList != null && !detailList.isEmpty() ) {
				for( Report_C_WorkPlanNextDetail _detail : detailList ) {
					emc.remove( _detail, CheckRemoveType.all );
				}
			}
			
			plan = Wi.copier.copy( wi );
			plan.setId( wi.getId() );
			
			detail = new Report_C_WorkPlanNextDetail();
			detail.setId( Report_C_WorkPlanNextDetail.createId() );
			detail.setPlanId( plan.getId() );
			detail.setReportId( plan.getReportId() );
			detail.setWorkInfoId( plan.getWorkInfoId() );
			detail.setKeyWorkId( plan.getKeyWorkId() );
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
				for( Report_C_WorkPlanNextDetail _detail : detailList ) {
					if( index == 0 ) {
						//更新
						_detail.setWorkInfoId( plan.getWorkInfoId() );
						_detail.setKeyWorkId( plan.getKeyWorkId() );
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
				detail = new Report_C_WorkPlanNextDetail();
				detail.setId( Report_C_WorkPlanNextDetail.createId() );
				detail.setPlanId( plan.getId() );
				detail.setReportId( plan.getReportId() );
				detail.setWorkInfoId( plan.getWorkInfoId() );
				detail.setKeyWorkId( plan.getKeyWorkId() );
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
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception("reportId is null!");
		}
		if( workInfoId == null || workInfoId.isEmpty() ){
			throw new Exception("workInfoId is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanNextFactory().listWithReportAndWorkInfoId( reportId, workInfoId );
	}

	public List<Report_C_WorkPlanNext> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			throw new Exception("ids is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkPlanNextFactory().list( ids );
	}

	public Boolean updateOrderNumber(EntityManagerContainer emc, String planId, Integer orderNumber) throws Exception {
		if (StringUtils.isEmpty( planId )) {
			throw new Exception("planId is null.");
		}
		if ( orderNumber == null ) {
			throw new Exception("orderNumber is null.");
		}
		Report_C_WorkPlanNext plan = emc.find( planId, Report_C_WorkPlanNext.class );
		if ( plan != null ) {
			emc.beginTransaction( Report_C_WorkPlanNext.class );
			plan.setOrderNumber(orderNumber);
			emc.check( plan, CheckPersistType.all );
			emc.commit();
			return true;
		}
		return false;
	}
}
