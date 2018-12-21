package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.openjpa.lib.util.StringUtil;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.jaxrs.workprog.ActionSaveWorkProg.Wi;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_C_WorkProgDetail;

/**
 * 工作实际完成情况汇报信息服务类
 * 
 * @author O2LEE
 *
 */
public class Report_C_WorkProgService{

	public List<Report_C_WorkProg> listWithReportId(EntityManagerContainer emc, String reportId) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception("reportId is null.");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().listWorkProgWithReportId( reportId );
	}
	
	public List<String> listWithReportIds(EntityManagerContainer emc, List<String> reportIds ) throws Exception {
		if( ListTools.isEmpty( reportIds ) ){
			throw new Exception("reportId is empty.");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().listWorkProgWithReportIds( reportIds );
	}

	public List<Report_C_WorkProgDetail> listDetailWithReportId(EntityManagerContainer emc, String reportId) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception("reportId is null.");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().listDetailWithReportId( reportId );
	}

	public List<Report_C_WorkProgDetail> listDetailWithProgId(EntityManagerContainer emc, String progId) throws Exception {
		if( progId == null || progId.isEmpty() ){
			throw new Exception("progId is null.");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgDetailFactory().listDetailWithProgId( progId );
	}

	public Report_C_WorkProg save(EntityManagerContainer emc, Wi wi) throws Exception {
		Report_C_WorkProg prog = null;
        Report_C_WorkProgDetail detail = null;
		List<Report_C_WorkProgDetail> detailList = null;
		
		prog = emc.find( wi.getId(), Report_C_WorkProg.class );
		Business business = new Business(emc);
		
		detailList = business.report_C_WorkProgDetailFactory().listDetailWithProgId( wi.getId() );
		
		emc.beginTransaction( Report_C_WorkProg.class );
		emc.beginTransaction( Report_C_WorkProgDetail.class );
		if( prog == null ) {
			//不存在，则需要进行添加，先删除该progId对应的所有detail
			if( detailList != null && !detailList.isEmpty() ) {
				for( Report_C_WorkProgDetail _detail : detailList ) {
					emc.remove( _detail, CheckRemoveType.all );
				}
			}
			
			prog = Wi.copier.copy( wi );
			prog.setId( wi.getId() );
			
			detail = new Report_C_WorkProgDetail();
			detail.setId( Report_C_WorkProgDetail.createId() );
			detail.setProgId( prog.getId() );
			detail.setReportId( prog.getReportId() );
			detail.setKeyWorkId( prog.getKeyWorkId() );
			detail.setWorkInfoId( prog.getWorkInfoId() );
			detail.setWorkTitle( prog.getWorkTitle() );
			detail.setProgressContent( wi.getProgressContent() );
			detail.setWorkContent( wi.getWorkDescribe() );
			emc.persist( prog, CheckPersistType.all );
			emc.persist( detail, CheckPersistType.all );
		}else {
			//已存在，需要进行修改
			wi.copyTo( prog, JpaObject.FieldsUnmodify );
			//已存在，更新第一条，删除其他
			if( detailList != null && !detailList.isEmpty() ) {
				int index = 0;
				for( Report_C_WorkProgDetail _detail : detailList ) {
					if( index == 0 ) {
						//更新
						_detail.setKeyWorkId( prog.getKeyWorkId() );
						_detail.setWorkInfoId( prog.getWorkInfoId() );
						_detail.setWorkTitle( prog.getWorkTitle() );
						_detail.setProgressContent( wi.getProgressContent() );
						_detail.setWorkContent( wi.getWorkDescribe() );
						emc.check( _detail, CheckPersistType.all );
					}else {
						emc.remove( _detail, CheckRemoveType.all );
					}
					index++;
				}
			}else {
				//没有，就要创建
				detail = new Report_C_WorkProgDetail();
				detail.setId( Report_C_WorkProgDetail.createId() );
				detail.setProgId( prog.getId() );
				detail.setReportId( prog.getReportId() );
				detail.setKeyWorkId( prog.getKeyWorkId() );
				detail.setWorkInfoId( prog.getWorkInfoId() );
				detail.setWorkTitle( prog.getWorkTitle() );
				detail.setProgressContent( wi.getProgressContent() );
				detail.setWorkContent( wi.getWorkDescribe() );
				emc.persist( detail, CheckPersistType.all );
			}
			emc.check( prog, CheckPersistType.all );
		}
		
		emc.commit();
		return prog;
	}

	public List<String> listReportIdsWithKeyWorkId(EntityManagerContainer emc, String year, String month, String week,
			String reportDate, String reportType, List<String> keyWorkIds, String reportStatus) throws Exception {
		if( keyWorkIds == null || keyWorkIds.isEmpty() ) {
			throw new Exception("keyWorkIds is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().listReportIdsWithKeyWorkId( year, month, week,
				reportDate, reportType, keyWorkIds, reportStatus );
	}

	public List<Report_C_WorkProg> listWithKeyWorkIds(EntityManagerContainer emc, String reportId, List<String> keyWorkIds) throws Exception {
		if( reportId == null || reportId.isEmpty() ) {
			throw new Exception("reportId is null!");
		}
		if( keyWorkIds == null || keyWorkIds.isEmpty() ) {
			throw new Exception("keyWorkIds is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().listWithKeyWorkIds( reportId, keyWorkIds );
	}

	public List<String> listWithReportAndWorkInfoId(EntityManagerContainer emc, String reportId, String workInfoId) throws Exception {
		if( StringUtil.isEmpty(reportId)) {
			throw new Exception("reportId is null!");
		}
		if( StringUtil.isEmpty(workInfoId) ) {
			throw new Exception("workInfoId is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().listWithReportAndWorkInfoId( reportId, workInfoId );
	}

	public List<Report_C_WorkProg> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ) {
			throw new Exception("ids is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().list( ids );
	}

	public List<String> listWithReportAndWorkId(EntityManagerContainer emc, String reportId, String workId) throws Exception {
		if( StringUtil.isEmpty(reportId)) {
			throw new Exception("reportId is null!");
		}
		if( StringUtil.isEmpty(workId) ) {
			throw new Exception("workId is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().listWithReportAndWorkId( reportId, workId );
	}

	public Boolean updateOrderNumber(EntityManagerContainer emc, String progId, Integer orderNumber) throws Exception {
		if( StringUtil.isEmpty(progId)) {
			throw new Exception("progId is null!");
		}
		if( orderNumber == null ) {
			throw new Exception("orderNumber is null!");
		}
		Report_C_WorkProg prog = emc.find(progId, Report_C_WorkProg.class);
		if( prog != null ) {
			emc.beginTransaction(Report_C_WorkProg.class);
			prog.setOrderNumber(orderNumber);
			emc.check( prog, CheckPersistType.all);
			emc.commit();
			return true;
		}
		return false;
	}

	public List<Report_C_WorkProg> listWithKeyWorkIdAndYear(EntityManagerContainer emc, String workId, String year) throws Exception {
		if( StringUtil.isEmpty(workId)) {
			throw new Exception("workId is null!");
		}
		if( StringUtil.isEmpty(year) ) {
			throw new Exception("year is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().listWithKeyWorkIdAndYear( workId,year );
	}

	public List<String> listWithYear(EntityManagerContainer emc, String year) throws Exception {
		if( StringUtil.isEmpty(year) ) {
			throw new Exception("year is null!");
		}
		Business business = new Business( emc );
		return business.report_C_WorkProgFactory().listWithYear( year );
	}
	
}
