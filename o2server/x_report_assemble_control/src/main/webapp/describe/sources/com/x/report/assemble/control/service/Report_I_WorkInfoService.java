package com.x.report.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;
import com.x.report.core.entity.Report_I_WorkTag;
import com.x.report.core.entity.Report_I_WorkTagUnit;

/**
 * 战略举措信息服务类
 * 
 * @author O2LEE
 *
 */
public class Report_I_WorkInfoService {

	public Report_I_WorkInfo get(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id, Report_I_WorkInfo.class );
	}

	/**
	 * 根据指定的ID列示战略举措信息
	 * @param emc
	 * @param ids 汇报生成概要文件记录信息ID列表
	 * @return
	 * @throws Exception
	 */
	public List<Report_I_WorkInfo> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.report_I_WorkInfoFactory().list( ids );
	}

	/**
	 * 根据ID删除战略举措信息
	 * @param emc
	 * @param id 战略举措信息ID
	 * @throws Exception
	 */
	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		Report_I_WorkInfo entity = emc.find( id, Report_I_WorkInfo.class );
		if( entity != null ){
			emc.beginTransaction(Report_I_WorkInfo.class);
			emc.remove( entity, CheckRemoveType.all);
			emc.commit();
		}else{
			throw new Exception("Report_P_MeasureInfo entity{'id':'"+id+"'} is not exists!");
		}
	}

	/**
	 * 保存部门重点工作信息
	 * 检查标签是否已经存在，如果不存在，则添加一个新的标签，并且让该工作和部门关联标签
	 * 
	 * @param emc
	 * @param entity
	 * @param detail
	 * @return
	 * @throws Exception
	 */
	public Report_I_WorkInfo save(EntityManagerContainer emc, Report_I_WorkInfo entity, Report_I_WorkInfoDetail detail) throws Exception {
		if( entity == null ){
			throw new Exception( "entity is null!" );
		}
		Report_I_WorkInfo oldEntity = emc.find( entity.getId(), Report_I_WorkInfo.class );
		Report_I_WorkInfoDetail oldDetail = emc.find( detail.getId(), Report_I_WorkInfoDetail.class );

		emc.beginTransaction( Report_I_WorkInfo.class );
		emc.beginTransaction( Report_I_WorkInfoDetail.class );
		emc.beginTransaction( Report_I_WorkTag.class );
		emc.beginTransaction( Report_I_WorkTagUnit.class );
		
		if( oldEntity != null ){ //说明存在，直接更新
            entity.copyTo( oldEntity, JpaObject.FieldsUnmodify );
			emc.check( oldEntity, CheckPersistType.all );
		}else{
            oldEntity = new Report_I_WorkInfo();
			entity.copyTo( oldEntity );
			emc.persist( oldEntity, CheckPersistType.all );
		}

		if( oldDetail != null ){ //说明存在，直接更新
			detail.copyTo( oldDetail, JpaObject.FieldsUnmodify );
			emc.check( oldDetail, CheckPersistType.all );
		}else{
			oldDetail = new Report_I_WorkInfoDetail();
			detail.copyTo( oldDetail );
			emc.persist( oldDetail, CheckPersistType.all );
		}
		
		//如果在这里标签为空，那么就不用处理标签信息
		//否则，判断标签是否已经存在，如果不存在，则新一个标签
		if( StringUtils.isNotEmpty( entity.getWorkTag() )) {
			Business business = new Business(emc);
			Report_I_WorkTag workTag = null;
			Report_I_WorkTagUnit workTagUnit = null;
			List<Report_I_WorkTag> tagList = null;
			List<String> tagIds = null;
			
			tagIds = business.report_I_WorkTagFactory().listWithTag( entity.getWorkTag(), "部门级" );		
			if(ListTools.isNotEmpty( tagIds )) {
				tagList = business.report_I_WorkTagFactory().list( tagIds );
			}
			if( ListTools.isNotEmpty( tagList )) {
				workTag = tagList.get(0);
			}else {
				//需要创建一个新的标签
				workTag = new Report_I_WorkTag();
				workTag.setTagName(entity.getWorkTag());
				workTag.setTagType("部门级");
				emc.persist( workTag, CheckPersistType.all );
			}
			
			//检查标签与部门的关联关系
			tagIds = business.report_I_WorkTagUnitFactory().listWithTagAndUnit( entity.getWorkTag(), entity.getWorkUnit() );
			if(ListTools.isEmpty( tagIds )) {
				workTagUnit = new Report_I_WorkTagUnit();
				workTagUnit.setTagId( workTag.getId() );
				workTagUnit.setTagName( workTag.getTagName() );
				workTagUnit.setUnitName( entity.getWorkUnit() );
				emc.persist( workTagUnit, CheckPersistType.all );
			}
		}		
		emc.commit();		
		return oldEntity;
	}

    public List<String> listIdsWithReport(EntityManagerContainer emc, String reportId, String workMonthFlag) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception( "reportId is null!" );
		}
		Business business = new Business( emc );
		return business.report_I_WorkInfoFactory().listIdsWithReport( reportId, workMonthFlag );
    }
    
    public List<String> listIdsWithReports(EntityManagerContainer emc, List<String> reportIds, String workMonthFlag) throws Exception {
    	if ( ListTools.isEmpty( reportIds )) {
			throw new Exception("reportIds is empty.");
		}
		Business business = new Business( emc );
		return business.report_I_WorkInfoFactory().listIdsWithReports( reportIds, workMonthFlag );
    }

	public List<String> listWorkTagIdsWithUnitName(EntityManagerContainer emc, String unitName) throws Exception {
		if( unitName == null || unitName.isEmpty() ){
			throw new Exception( "unitName is null!" );
		}
		Business business = new Business( emc );
		return business.report_I_WorkTagUnitFactory().listTagIdsWithUnit( unitName );
	}

	public List<Report_I_WorkTag> listWorkTags(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			throw new Exception( "unitName is null!" );
		}
		Business business = new Business( emc );
		return business.report_I_WorkTagFactory().list(ids);
	}

	public List<Report_I_WorkInfo> listWithKeyWorkId(EntityManagerContainer emc, String keyWorkId , String reportId) throws Exception {
		if( StringUtils.isEmpty( keyWorkId ) ){
			throw new Exception( "keyWorkId is null!" );
		}
		Business business = new Business( emc );
		return business.report_I_WorkInfoFactory().listWithKeyWorkId(keyWorkId, reportId);
	}

	public Report_I_WorkInfoDetail getDetailWithWorkInfoId(EntityManagerContainer emc, String reportId, String workInfoId) throws Exception {
		if( StringUtils.isEmpty( workInfoId ) ){
			throw new Exception( "workInfoId is null!" );
		}
		Business business = new Business( emc );
		List<String> ids = business.report_I_WorkInfoFactory().getDetailIdsWithWorkInfoId(reportId, workInfoId);
		if( ListTools.isNotEmpty( ids )) {
			return emc.find(ids.get(0), Report_I_WorkInfoDetail.class );
		}
		return null;
	}

	public List<String> listKeyWorkInfoIdsWithUnitAndMeasure(EntityManagerContainer emc, String unitName, String measureId) throws Exception {
		if( StringUtils.isEmpty( unitName ) ){
			throw new Exception( "unitName is null!" );
		}
		if( StringUtils.isEmpty( measureId ) ){
			throw new Exception( "measureId is null!" );
		}
		Business business = new Business( emc );
		return business.report_I_WorkInfoFactory().listKeyWorkInfoIdsWithUnitAndMeasure(unitName, measureId);
	}
}
