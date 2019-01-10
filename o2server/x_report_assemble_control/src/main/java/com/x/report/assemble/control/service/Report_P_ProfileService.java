package com.x.report.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.assemble.control.EnumReportTypes;
import com.x.report.core.entity.Report_P_Profile;
import com.x.report.core.entity.Report_P_ProfileDetail;

/**
 * 汇报生成概要记录文件信息服务类
 * 
 * @author O2LEE
 *
 */
public class Report_P_ProfileService {

	public Report_P_Profile get(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id, Report_P_Profile.class );
	}

	/**
	 * 根据指定的ID列示汇报生成概要文件记录信息
	 * @param emc
	 * @param ids 汇报生成概要文件记录信息ID列表
	 * @return
	 * @throws Exception
	 */
	public List<Report_P_Profile> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.report_P_ProfileFactory().list( ids );
	}

	/**
	 * 根据ID删除汇报生成概要文件记录信息，汇报生成概要文件记录详细信息也会被删除
	 * @param emc
	 * @param id 汇报生成概要文件记录信息ID
	 * @throws Exception
	 */
	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		List<Report_P_ProfileDetail> detailList = null;
		Report_P_Profile entity = emc.find( id, Report_P_Profile.class );
		Business business = new Business(emc);
		if( entity != null ){
			emc.beginTransaction(Report_P_Profile.class);
			emc.beginTransaction(Report_P_ProfileDetail.class);
			//查询所有的概要文件信息详情，全部需要删除
			detailList = business.report_P_ProfileDetailFactory().listWithRecordId( id, null, null );
			if( detailList != null && !detailList.isEmpty() ) {
				for( Report_P_ProfileDetail detail : detailList ) {
					emc.remove( detail, CheckRemoveType.all);
				}
			}			
			emc.remove( entity, CheckRemoveType.all);
			emc.commit();
		}else{
			throw new Exception("Report_P_Profile entity{'id':'"+id+"'} is not exists!");
		}
	}

	/**
	 * 保存汇报生成概要文件记录信息
	 * @param emc
	 * @param profile 汇报生成概要文件记录信息
	 * @param detailList 汇报生成概要文件记录详细信息列表
	 * @return
	 * @throws Exception
	 */
	public Report_P_Profile save( EntityManagerContainer emc, Report_P_Profile profile, List<Report_P_ProfileDetail> detailList ) throws Exception {
		if( profile == null ){
			throw new Exception( "profile is null!" );
		}
		
		Report_P_Profile save_entity = null;
		List<Report_P_ProfileDetail> detailList_tmp = null;
		
		Business business = new Business(emc);
		//确定是否重复
		save_entity = getWithTypeAndCycle( emc, profile.getModules(), profile.getReportType(), 
				profile.getReportYear(), 
				profile.getReportMonth(), 
				profile.getReportWeek(), 
				profile.getReportDateString()
		);
		emc.beginTransaction( Report_P_Profile.class );
		emc.beginTransaction( Report_P_ProfileDetail.class );
		
		if( save_entity != null ){
			profile.copyTo( save_entity, JpaObject.FieldsUnmodify );
			save_entity.setId( profile.getId() );
			emc.check( save_entity, CheckPersistType.all );
		}else{
			save_entity = new Report_P_Profile();
			profile.copyTo( save_entity );
			if( profile.getId() != null && !profile.getId().isEmpty() ) {
				save_entity.setId( profile.getId());
			}
			emc.persist( save_entity, CheckPersistType.all );
		}
		
		//先根据ID删除原先有的详细信息
		detailList_tmp = business.report_P_ProfileDetailFactory().listWithRecordId( save_entity.getId(), null, null );
		if( detailList_tmp != null && !detailList_tmp.isEmpty() ) {
			for( Report_P_ProfileDetail detail : detailList_tmp ) {
				emc.remove( detail, CheckRemoveType.all);
			}
		}
		
		//再保存新的详细信息
		for( Report_P_ProfileDetail detail : detailList ) {
			emc.persist( detail, CheckPersistType.all );
		}
		
		emc.commit();		
		return save_entity;
	}

	public Report_P_Profile updateDetails(EntityManagerContainer emc, String id, List<Report_P_ProfileDetail> recordProfileDetailList) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			throw new Exception( "id is null!" );
		}
		if( ListTools.isEmpty( recordProfileDetailList )) {
			throw new Exception("recordProfileDetailList is null.");
		}
		Report_P_Profile update_entity = null;
		List<Report_P_ProfileDetail> detailList_tmp = null;
		
		Business business = new Business(emc);
		
		//确定是否存在
		update_entity = emc.find( id, Report_P_Profile.class );		
		if( update_entity == null ){
			throw new Exception("Report_P_Profile is not exists!");
		}
		
		emc.beginTransaction( Report_P_Profile.class );
		emc.beginTransaction( Report_P_ProfileDetail.class );
		
		//先根据ID删除原先有的详细信息
		detailList_tmp = business.report_P_ProfileDetailFactory().listWithRecordId( id, null, null );
		if( detailList_tmp != null && !detailList_tmp.isEmpty() ) {
			for( Report_P_ProfileDetail detail : detailList_tmp ) {
				emc.remove( detail, CheckRemoveType.all);
			}
		}
		
		//再保存新的详细信息
		for( Report_P_ProfileDetail detail : recordProfileDetailList ) {
			emc.persist( detail, CheckPersistType.all );
		}
		
		emc.commit();		
		return update_entity;
	}
	
	public Report_P_Profile updateWithId( EntityManagerContainer emc, Report_P_Profile profile ) throws Exception {
		if( profile == null ){
			throw new Exception( "profile is null!" );
		}
		
		Report_P_Profile save_entity = null;		
		
		//确定是否重复
		save_entity = emc.find( profile.getId(), Report_P_Profile.class );
		emc.beginTransaction( Report_P_Profile.class );
		emc.beginTransaction( Report_P_ProfileDetail.class );
		
		if( save_entity != null ){
			profile.copyTo( save_entity, JpaObject.FieldsUnmodify );
			save_entity.setId( profile.getId() );
			emc.check( save_entity, CheckPersistType.all );
		}else{
			save_entity = new Report_P_Profile();
			profile.copyTo( save_entity );
			if( profile.getId() != null && !profile.getId().isEmpty() ) {
				save_entity.setId( profile.getId());
			}
			emc.persist( save_entity, CheckPersistType.all );
		}
		emc.commit();		
		return save_entity;
	}
	
	/**
	 * 根据应用模块，汇报类别，以及周期信息来查询指定的汇报生成概要文件信息
	 * @param emc
	 * @param modules 应用模块
	 * @param reportType 汇报类别
	 * @param reportYear 汇报年份
	 * @param reportMonth 汇报月份
	 * @param reportWeek 汇报周
	 * @param reportDateString 汇报日期
	 * @return
	 * @throws Exception
	 */
	private Report_P_Profile getWithTypeAndCycle( EntityManagerContainer emc, String modules, String reportType, String reportYear,
			String reportMonth, String reportWeek, String reportDateString) throws Exception {
		List<Report_P_Profile> records = null;
		Business business = new Business(emc);
		if( EnumReportTypes.WEEKREPORT.toString().equals( reportType )) {
			//周汇报
			records = business.report_P_ProfileFactory().getWithCycleAndType( modules, reportType, reportYear, null,  reportWeek,  null );
		}else if( EnumReportTypes.MONTHREPORT.toString().equals( reportType )) {
			//月汇报
			records = business.report_P_ProfileFactory().getWithCycleAndType( modules, reportType, reportYear, reportMonth,  null,  null );
		}else if( EnumReportTypes.DAILYREPORT.toString().equals( reportType )) {
			//日汇报
			records = business.report_P_ProfileFactory().getWithCycleAndType( modules, reportType, null, null,  null,  reportDateString );
		}
		if( records != null && !records.isEmpty() ) {
			return records.get(0);
		}
		return null;
	}

	public List<Report_P_Profile> listErrorCreateRecord(EntityManagerContainer emc, String reportType, String reportYear, String reportMonth, String reportWeek ) throws Exception {
		Business business = new Business(emc);
		return business.report_P_ProfileFactory().listErrorCreateRecord( reportType, reportYear, reportMonth, reportWeek );
	}

	public List<Report_P_Profile> listWithCondition(EntityManagerContainer emc, String reportType, String year,
			String month, String week, String date) throws Exception {
		Business business = new Business(emc);
		return business.report_P_ProfileFactory().listErrorCreateRecord( reportType, year, month, week, date );
	}

	public List<Report_P_ProfileDetail> listDetailValue(EntityManagerContainer emc, String profileId, String reportModule, String snapType) throws Exception {
		Business business = new Business(emc);
		return business.report_P_ProfileDetailFactory().getDetailValue( profileId, reportModule, snapType );
	}

	public List<String> listIdsWithCondition(EntityManagerContainer emc, String reportType, String year, String month, String week, String reportDate) throws Exception {
		Business business = new Business(emc);
		return business.report_P_ProfileFactory().listIdsWithCondition( reportType, year, month, week, reportDate );
	}

	public List<String> listDetailValueListWithCondition(EntityManagerContainer emc, List<String> profileIds, String snapType) throws Exception {
		Business business = new Business(emc);
		return business.report_P_ProfileDetailFactory().listDetailValueListWithCondition( profileIds, snapType );
	}

	public List<String> listWithYear(EntityManagerContainer emc, String year) throws Exception {
		if( year == null || year.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.report_P_ProfileFactory().listWithYear( year );
	}

	public Date getMaxCreateTime(EntityManagerContainer emc, String enumReportType) throws Exception {
		if( enumReportType == null || enumReportType.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		List<Report_P_Profile> profiles = business.report_P_ProfileFactory().listCreateTimes( enumReportType );
		if( ListTools.isNotEmpty( profiles )) {
			return profiles.get(0).getCreateTime();
		}
		return null;
	}

	
}
