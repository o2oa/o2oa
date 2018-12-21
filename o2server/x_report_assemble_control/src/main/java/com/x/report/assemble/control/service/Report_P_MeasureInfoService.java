package com.x.report.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_P_MeasureInfo;

/**
 * 战略举措信息服务类
 * 
 * @author O2LEE
 *
 */
public class Report_P_MeasureInfoService {

	public Report_P_MeasureInfo get(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id, Report_P_MeasureInfo.class );
	}

	/**
	 * 根据指定的ID列示战略举措信息
	 * @param emc
	 * @param ids 汇报生成概要文件记录信息ID列表
	 * @return
	 * @throws Exception
	 */
	public List<Report_P_MeasureInfo> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = new Business( emc );
		return business.report_P_MeasureInfoFactory().list( ids );
	}

	public List<String> listWithYear(EntityManagerContainer emc, String thisYear) throws Exception {
		if( StringUtils.isEmpty( thisYear ) ){
			throw new Exception("year is null");
		}
		Business business = new Business( emc );
		return business.report_P_MeasureInfoFactory().listWithYear( thisYear );
	}
	
	/**
	 * 根据ID删除战略举措信息
	 * @param emc
	 * @param id 战略举措信息ID
	 * @throws Exception
	 */
	public void delete( EntityManagerContainer emc, String id ) throws Exception {
        Report_P_MeasureInfo entity = emc.find( id, Report_P_MeasureInfo.class );
		if( entity != null ){
			emc.beginTransaction(Report_P_MeasureInfo.class);
			emc.remove( entity, CheckRemoveType.all);
			emc.commit();
		}else{
			throw new Exception("Report_P_MeasureInfo entity{'id':'"+id+"'} is not exists!");
		}
	}

	/**
	 * 保存战略举措信息
	 * @param emc
	 * @param entity 汇报生成概要文件记录信息
	 * @return
	 * @throws Exception
	 */
	public Report_P_MeasureInfo save( EntityManagerContainer emc, Report_P_MeasureInfo entity ) throws Exception {
		if( entity == null ){
			throw new Exception( "entity is null!" );
		}
		
        Report_P_MeasureInfo oldEntity = emc.find( entity.getId(), Report_P_MeasureInfo.class );        
		emc.beginTransaction( Report_P_MeasureInfo.class );
		
		if( oldEntity != null ){ //说明存在，直接更新

            oldEntity.setOrderNumber( entity.getOrderNumber() );
            oldEntity.setParentId( entity.getParentId() );
            oldEntity.setStatus( entity.getStatus() );
            oldEntity.setTitle( entity.getTitle() );
            oldEntity.setUnitList( entity.getUnitList() );
            oldEntity.setUpdateTime( new Date() );
            oldEntity.setYear( entity.getYear() );
            if( null == oldEntity.getCreateTime() ) {
            	oldEntity.setCreateTime( new Date() );
            }
			emc.check( oldEntity, CheckPersistType.all );
		}else{
            oldEntity = new Report_P_MeasureInfo();
            
            oldEntity.setId( Report_P_MeasureInfo.createId() );
            oldEntity.setCreateTime( new Date()  );
            oldEntity.setOrderNumber( entity.getOrderNumber() );
            oldEntity.setParentId( entity.getParentId() );
            oldEntity.setStatus( entity.getStatus() );
            oldEntity.setTitle( entity.getTitle() );
            oldEntity.setUnitList( entity.getUnitList() );
            oldEntity.setUpdateTime( new Date() );
            oldEntity.setYear( entity.getYear() );
            
			emc.persist( oldEntity, CheckPersistType.all );
		}
		emc.commit();
		return oldEntity;
	}

	
}
