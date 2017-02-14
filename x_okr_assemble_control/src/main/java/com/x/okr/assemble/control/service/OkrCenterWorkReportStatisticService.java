package com.x.okr.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrCenterWorkReportStatistic;

/**
 * 类   名：OkrCenterWorkReportStatisticService<br/>
 * 实体类：OkrCenterWorkReportStatistic<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>O
 * 日   期：2016-05-20 17:17:26
**/
public class OkrCenterWorkReportStatisticService{
	private Logger logger = LoggerFactory.getLogger( OkrCenterWorkReportStatisticService.class );
	
	/**
	 * 根据传入的ID从数据库查询OkrCenterWorkReportStatistic对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrCenterWorkReportStatistic get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrCenterWorkReportStatistic.class );
		}catch( Exception e ){
			throw e;
		}
	}	
	
	/**
	 * 根据ID从数据库中删除OkrCenterWorkReportStatistic对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrCenterWorkReportStatistic okrCenterWorkReportStatistic = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrCenterWorkReportStatistic = emc.find(id, OkrCenterWorkReportStatistic.class);
			if (null == okrCenterWorkReportStatistic) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrCenterWorkReportStatistic.class );
				emc.remove( okrCenterWorkReportStatistic, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void saveWeekStatistic( Date now, OkrCenterWorkInfo okrCenterWorkInfo, String statisticContent ) throws Exception {
		if( okrCenterWorkInfo  == null  ){
			throw new Exception( "okrCenterWorkInfo is null, return null!" );
		}
		if( statisticContent  == null ||  statisticContent.isEmpty() ){
			throw new Exception( "statisticContent is null, return null!" );
		}
		DateOperation dateOperation = new DateOperation();
		List<String> ids = null;
		Business business = null;
		Long recordCount = 0L;
		Integer year = dateOperation.getYearNumber( now );//获取年份
		Integer month = dateOperation.getMonthNumber( now );//获取月份
		Integer week = dateOperation.getWeekNumOfYear( now );//获取周数
		OkrCenterWorkReportStatistic okrCenterWorkReportStatistic = null;
		Integer count = 0;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( OkrCenterWorkReportStatistic.class );
			//查询数据库中是否已经存在该中心工作该周的每周统计信息，如果存在则进行更新，如果不存在则进行保存
			recordCount = business.okrCenterWorkReportStatisticFactory().count( okrCenterWorkInfo.getId(), year, month, week, "每周统计" );
			if( recordCount > 0 ){
				ids = business.okrCenterWorkReportStatisticFactory().list( okrCenterWorkInfo.getId(), year, month, week, "每周统计" );
			}
			if( ids != null && !ids.isEmpty() ){
				//更新统计记录
				for( String id : ids  ){
					okrCenterWorkReportStatistic = emc.find( id, OkrCenterWorkReportStatistic.class );
					if( okrCenterWorkReportStatistic != null ){
						if( count == 0 ){
							okrCenterWorkReportStatistic.setStatisticTime( now );
							okrCenterWorkReportStatistic.setReportStatistic( statisticContent );
							emc.check( okrCenterWorkReportStatistic, CheckPersistType.all );
						}else{
							emc.remove( okrCenterWorkReportStatistic, CheckRemoveType.all );
						}
						count ++;
					}
				}
			}else{
				okrCenterWorkReportStatistic = new OkrCenterWorkReportStatistic();
				okrCenterWorkReportStatistic.setCenterId( okrCenterWorkInfo.getId() );
				okrCenterWorkReportStatistic.setCenterTitle( okrCenterWorkInfo.getTitle() );
				okrCenterWorkReportStatistic.setYear(year);
				okrCenterWorkReportStatistic.setMonth(month);
				okrCenterWorkReportStatistic.setWeek(week);
				okrCenterWorkReportStatistic.setDefaultWorkType( okrCenterWorkInfo.getDefaultWorkType() );
				okrCenterWorkReportStatistic.setDefaultWorkLevel( okrCenterWorkInfo.getDefaultWorkLevel() );
				okrCenterWorkReportStatistic.setStatisticTime( now );
				okrCenterWorkReportStatistic.setReportStatistic( statisticContent );
				okrCenterWorkReportStatistic.setStatus( "正常" );
				okrCenterWorkReportStatistic.setStatisticCycle( "每周统计" );
				//保存统计记录
				emc.persist( okrCenterWorkReportStatistic, CheckPersistType.all );
			}
			
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void saveMonthStatistic( Date now, OkrCenterWorkInfo okrCenterWorkInfo, String statisticContent ) throws Exception {
		if( okrCenterWorkInfo  == null  ){
			throw new Exception( "okrCenterWorkInfo is null, return null!" );
		}
		if( statisticContent  == null ||  statisticContent.isEmpty() ){
			throw new Exception( "statisticContent is null, return null!" );
		}
		DateOperation dateOperation = new DateOperation();
		List<String> ids = null;
		Business business = null;
		Long recordCount = 0L;
		Integer year = dateOperation.getYearNumber( now );//获取年份
		Integer month = dateOperation.getMonthNumber( now );//获取月份
		OkrCenterWorkReportStatistic okrCenterWorkReportStatistic = null;
		Integer count = 0;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( OkrCenterWorkReportStatistic.class );
			//查询数据库中是否已经存在该中心工作该周的每月统计信息，如果存在则进行更新，如果不存在则进行保存	
			recordCount = business.okrCenterWorkReportStatisticFactory().count( okrCenterWorkInfo.getId(), year, month, null, "每月统计" );
			if( recordCount > 0 ){
				ids = business.okrCenterWorkReportStatisticFactory().list( okrCenterWorkInfo.getId(), year, month, null, "每月统计" );
			}
			
			if( ids != null && !ids.isEmpty() ){
				//更新统计记录
				for( String id : ids  ){
					okrCenterWorkReportStatistic = emc.find( id, OkrCenterWorkReportStatistic.class );
					if( okrCenterWorkReportStatistic != null ){
						if( count == 0 ){
							okrCenterWorkReportStatistic.setStatisticTime( now );
							okrCenterWorkReportStatistic.setReportStatistic( statisticContent );
							emc.check( okrCenterWorkReportStatistic, CheckPersistType.all );
						}else{
							emc.remove( okrCenterWorkReportStatistic, CheckRemoveType.all );
						}
						count ++;
					}
				}
			}else{
				okrCenterWorkReportStatistic = new OkrCenterWorkReportStatistic();
				okrCenterWorkReportStatistic.setCenterId( okrCenterWorkInfo.getId() );
				okrCenterWorkReportStatistic.setCenterTitle( okrCenterWorkInfo.getTitle() );
				okrCenterWorkReportStatistic.setYear(year);
				okrCenterWorkReportStatistic.setMonth(month);
				okrCenterWorkReportStatistic.setWeek( null );
				okrCenterWorkReportStatistic.setDefaultWorkType( okrCenterWorkInfo.getDefaultWorkType() );
				okrCenterWorkReportStatistic.setDefaultWorkLevel( okrCenterWorkInfo.getDefaultWorkLevel() );
				okrCenterWorkReportStatistic.setStatisticTime( now );
				okrCenterWorkReportStatistic.setReportStatistic( statisticContent );
				okrCenterWorkReportStatistic.setStatus( "正常" );
				okrCenterWorkReportStatistic.setStatisticCycle( "每月统计" );
				//保存统计记录
				emc.persist( okrCenterWorkReportStatistic, CheckPersistType.all );
			}
			
			emc.commit();
		}catch( Exception e ){
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
	public List<OkrCenterWorkReportStatistic> list( String centerId, String reportCycle, Integer year, Integer month, Integer week ) throws Exception {
		Business business = null;
		Long count = 0L;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			count = business.okrCenterWorkReportStatisticFactory().countBaseInfo(centerId, reportCycle, year, month, week);
			if( count > 0 ){
				return business.okrCenterWorkReportStatisticFactory().listBaseInfo( centerId, reportCycle, year, month, week );
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
}
