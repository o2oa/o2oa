package com.x.okr.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrStatisticReportContent;

/**
 * 类   名：OkrStatisticReportContentService<br/>
 * 实体类：OkrStatisticReportContent<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>O
 * 日   期：2016-05-20 17:17:26
**/
public class OkrStatisticReportContentService{
	
	public OkrStatisticReportContent save( OkrStatisticReportContent statisticReportContent ) throws Exception {
		if( statisticReportContent  == null  ){
			throw new Exception( "statisticReportContent is null, system can not save entity!" );
		}
		List<String> ids = null;
		Business business = null;
		OkrStatisticReportContent _statisticReportContent = null;
		Integer count = 0;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( OkrStatisticReportContent.class );
			
			//查询数据库中是否已经存在该中心工作该周的每月统计信息，如果存在则进行更新，如果不存在则进行保存	
			ids = business.okrStatisticReportContentFactory().list( 
					statisticReportContent.getWorkId(), 
					statisticReportContent.getStatisticYear(), 
					statisticReportContent.getStatisticMonth(), 
					statisticReportContent.getStatisticWeek(), 
					statisticReportContent.getCycleType()
			);
			if( ids != null && !ids.isEmpty() ){
				//更新统计记录
				for( String id : ids  ){
					_statisticReportContent = emc.find( id, OkrStatisticReportContent.class );
					if( _statisticReportContent != null ){
						if( count == 0 ){
							_statisticReportContent.setStatisticTime( statisticReportContent.getStatisticTime() );
							_statisticReportContent.setStatisticTimeFlag( statisticReportContent.getStatisticTimeFlag() );
							_statisticReportContent.setCenterId( statisticReportContent.getCenterId() );
							_statisticReportContent.setCenterTitle( statisticReportContent.getCenterTitle() );
							_statisticReportContent.setParentId( statisticReportContent.getParentId() );
							_statisticReportContent.setWorkId( statisticReportContent.getWorkId() );
							_statisticReportContent.setWorkTitle( statisticReportContent.getWorkTitle() );
							_statisticReportContent.setWorkType( statisticReportContent.getWorkType() );
							_statisticReportContent.setWorkLevel( statisticReportContent.getWorkLevel() );
							_statisticReportContent.setIsCompleted( statisticReportContent.getIsCompleted() );
							_statisticReportContent.setIsOverTime( statisticReportContent.getIsOverTime() );
							_statisticReportContent.setStatisticYear( statisticReportContent.getStatisticYear() );
							_statisticReportContent.setStatisticMonth( statisticReportContent.getStatisticMonth() );
							_statisticReportContent.setStatisticWeek( statisticReportContent.getStatisticWeek() );
							_statisticReportContent.setCycleType( statisticReportContent.getCycleType() );
							_statisticReportContent.setReportDayInCycle( statisticReportContent.getReportDayInCycle() );
							_statisticReportContent.setReportId( statisticReportContent.getReportId() );
							_statisticReportContent.setResponsibilityTopUnitName( statisticReportContent.getResponsibilityTopUnitName() );
							_statisticReportContent.setResponsibilityEmployeeName( statisticReportContent.getResponsibilityEmployeeName() );
							_statisticReportContent.setResponsibilityIdentity( statisticReportContent.getResponsibilityIdentity() );
							_statisticReportContent.setResponsibilityUnitName( statisticReportContent.getResponsibilityUnitName() );
							_statisticReportContent.setReportStatus( statisticReportContent.getReportStatus() );
							_statisticReportContent.setWorkPlan( statisticReportContent.getWorkPlan() );
							_statisticReportContent.setAdminSuperviseInfo( statisticReportContent.getAdminSuperviseInfo() );
							_statisticReportContent.setProgressDescription( statisticReportContent.getProgressDescription() );
							_statisticReportContent.setWorkPointAndRequirements( statisticReportContent.getWorkPointAndRequirements() );
							_statisticReportContent.setMemo( statisticReportContent.getMemo() );
							_statisticReportContent.setOpinion( statisticReportContent.getOpinion() );
							_statisticReportContent.setWorkProcessStatus( statisticReportContent.getWorkProcessStatus() );
							_statisticReportContent.setStatus( statisticReportContent.getStatus() );
							emc.check( _statisticReportContent, CheckPersistType.all );
						}else{
							emc.remove( _statisticReportContent, CheckRemoveType.all );
						}
						count ++;
					}
				}
			}else{
				//保存统计记录
				emc.persist( statisticReportContent, CheckPersistType.all );
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
		return statisticReportContent;
	}
	
	/**
	 * 根据传入的ID从数据库查询OkrStatisticReportContent对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrStatisticReportContent get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrStatisticReportContent.class );
		}catch( Exception e ){
			throw e;
		}
	}	
	
	/**
	 * 根据ID从数据库中删除OkrStatisticReportContent对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrStatisticReportContent okrCenterWorkReportStatistic = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrCenterWorkReportStatistic = emc.find(id, OkrStatisticReportContent.class);
			if (null == okrCenterWorkReportStatistic) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrStatisticReportContent.class );
				emc.remove( okrCenterWorkReportStatistic, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据条件获取统计信息列表
	 * @param workTypeName
	 * @param reportCycle
	 * @param year
	 * @param month
	 * @param week
	 * @return
	 * @throws Exception 
	 */
	public List<String> list( String centerId, String centerTitle, String parentId, String workType, String statisticTime, String reportCycle, Integer year, Integer month, Integer week, String stauts ) throws Exception {
		Business business = null;
		Long count = 0L;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			count = business.okrStatisticReportContentFactory().count( centerId, centerTitle, parentId, workType, statisticTime, reportCycle, year, month, week, stauts );
			if( count > 0 ){
				return business.okrStatisticReportContentFactory().list( centerId, centerTitle, parentId, workType, statisticTime, reportCycle, year, month, week, stauts );
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据Id获取统计信息列表
	 * @return
	 * @throws Exception 
	 */
	public List<OkrStatisticReportContent> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.okrStatisticReportContentFactory().list( ids );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<String> listDateTimeFlags(String centerId, String centerTitle, String workId, String workType, String reportCycle,
			Integer year, Integer month, Integer week, Date startDate, Date endDate, String status) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.okrStatisticReportContentFactory().listDateTimeFlags( centerId, centerTitle, workId, workType, reportCycle, year, month, week, startDate, endDate, status );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<String> listFirstLayer(String centerId, String centerTitle, String workId, String workType, String statisticTimeFlag, String reportCycle, Integer year, Integer month, Integer week, String stauts) throws Exception {
		Business business = null;
		Long count = 0L;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			count = business.okrStatisticReportContentFactory().countFirstLayer( centerId, centerTitle, workId, workType, statisticTimeFlag, reportCycle, year, month, week, stauts );
			if( count > 0 ){
				return business.okrStatisticReportContentFactory().listFirstLayer( centerId, centerTitle, workId, workType, statisticTimeFlag, reportCycle, year, month, week, stauts );
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
}
