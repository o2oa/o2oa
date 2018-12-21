package com.x.report.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_I_Ext_Content;
import com.x.report.core.entity.Report_I_Ext_ContentDetail;

/**
 * 汇报扩展信息数据服务
 * 
 * @author O2LEE
 *
 */
public class Report_I_Ext_ContentService{

	public Report_I_Ext_ContentDetail get(EntityManagerContainer emc, String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		Business business = new Business( emc );
		return business.report_I_Ext_ContentDetailFactory().get(id);
	}
	
	public List<Report_I_Ext_Content> listWithReportId(EntityManagerContainer emc, String reportId, String infoLevel, String targetPerson) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception("reportId is null!");
		}
		Business business = new Business( emc );
		return business.report_I_Ext_ContentFactory().listWithReport( reportId, infoLevel, targetPerson );
	}
	
	public List<Report_I_Ext_Content> listWithReportId(EntityManagerContainer emc, String reportId ) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception("reportId is null!");
		}
		Business business = new Business( emc );
		return business.report_I_Ext_ContentFactory().listWithReport( reportId, null, null );
	}
	
	public List<String> listWithReportIds(EntityManagerContainer emc, List<String> reportIds, String infoLevel, String targetPerson ) throws Exception {
		if ( ListTools.isEmpty( reportIds )) {
			throw new Exception("reportIds is empty.");
		}
		Business business = new Business( emc );
		return business.report_I_Ext_ContentFactory().listWithReports( reportIds, infoLevel, targetPerson );
	}
	
	public List<Report_I_Ext_Content> listWithProfile(EntityManagerContainer emc, String profileId) throws Exception {
		if( profileId == null || profileId.isEmpty() ){
			throw new Exception("profileId is null!");
		}
		Business business = new Business( emc );
		return business.report_I_Ext_ContentFactory().listWithProfile( profileId );
	}
	
	public List<Report_I_Ext_Content> listWithInfoLevel(EntityManagerContainer emc, String infoLevel) throws Exception {
		if( infoLevel == null || infoLevel.isEmpty() ){
			throw new Exception("infoLevel is null!");
		}
		Business business = new Business( emc );
		return business.report_I_Ext_ContentFactory().listWithInfoLevel( infoLevel );
	}
	
	public List<Report_I_Ext_ContentDetail> listDetailWithContentId(EntityManagerContainer emc, String contentId) throws Exception {
		if( contentId == null || contentId.isEmpty() ){
			throw new Exception("contentId is null!");
		}
		Business business = new Business( emc );
		return business.report_I_Ext_ContentDetailFactory().listWithContentId(contentId);
	}
	
	public Report_I_Ext_Content save(EntityManagerContainer emc, Report_I_Ext_Content report_I_Ext_Content ) throws Exception {
		Report_I_Ext_Content info = null;		
		info = emc.find( report_I_Ext_Content.getId(), Report_I_Ext_Content.class );
		emc.beginTransaction( Report_I_Ext_Content.class );
		if( info == null ) {
			info = new Report_I_Ext_Content();
			report_I_Ext_Content.copyTo( info );
			info.setId( report_I_Ext_Content.getId() );	
			if( info.getCreateTime() == null ) {
				info.setCreateTime( new Date());
			}
			emc.persist( info, CheckPersistType.all );			
		}else {
			//已存在，需要进行修改
			report_I_Ext_Content.copyTo( info, JpaObject.FieldsUnmodify );
			if( info.getCreateTime() == null ) {
				info.setCreateTime( new Date());
			}
			emc.check( info, CheckPersistType.all );
		}
		emc.commit();
		return info;
	}
	
	public Report_I_Ext_ContentDetail saveDetail(EntityManagerContainer emc, Report_I_Ext_ContentDetail report_I_Ext_ContentDetail ) throws Exception {
		Report_I_Ext_ContentDetail info = null;		
		info = emc.find( report_I_Ext_ContentDetail.getId(), Report_I_Ext_ContentDetail.class );
		emc.beginTransaction( Report_I_Ext_ContentDetail.class );
		if( info == null ) {
			info = new Report_I_Ext_ContentDetail();
			report_I_Ext_ContentDetail.copyTo( info );
			info.setId( report_I_Ext_ContentDetail.getId() );	
			if( info.getCreateTime() == null ) {
				info.setCreateTime( new Date());
			}
			emc.persist( info, CheckPersistType.all );			
		}else {
			//已存在，需要进行修改
			report_I_Ext_ContentDetail.copyTo( info, JpaObject.FieldsUnmodify );
			if( info.getCreateTime() == null ) {
				info.setCreateTime( new Date());
			}
			emc.check( info, CheckPersistType.all );
		}
		emc.commit();
		return info;
	}

	public List<Report_I_Ext_Content> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			throw new Exception("ids is null!");
		}
		Business business = new Business( emc );
		return business.report_I_Ext_ContentFactory().list( ids );
	}
}
