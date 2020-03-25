package com.x.okr.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrStatisticReportStatus;

/**
 * 类   名：OkrStatisticReportStatusService<br/>
 * 实体类：OkrStatisticReportStatus<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>O
 * 日   期：2016-05-20 17:17:26
**/
public class OkrStatisticReportStatusService{
	private static  Logger logger = LoggerFactory.getLogger( OkrStatisticReportStatusService.class );
	
	/**
	 * 根据传入的ID从数据库查询OkrStatisticReportStatus对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrStatisticReportStatus get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrStatisticReportStatus.class );
		}catch( Exception e ){
			throw e;
		}
	}	
	
	/**
	 * 根据ID从数据库中删除OkrStatisticReportStatus对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrStatisticReportStatus okrCenterWorkReportStatistic = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrCenterWorkReportStatistic = emc.find(id, OkrStatisticReportStatus.class);
			if (null == okrCenterWorkReportStatistic) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrStatisticReportStatus.class );
				emc.remove( okrCenterWorkReportStatistic, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public OkrStatisticReportStatus save( OkrStatisticReportStatus statistic ) throws Exception {
		if( statistic  == null  ){
			throw new Exception( "okrReportStatusStatistic is null, return null!" );
		}
		List<OkrStatisticReportStatus> list = null;
		OkrStatisticReportStatus _okrReportStatusStatistic = null;
		Business business = null;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			list = business.okrStatisticReportStatusFactory().listWithWorkId( statistic.getWorkId() );
			emc.beginTransaction( OkrStatisticReportStatus.class );
			if( list != null && !list.isEmpty() ){
				for( int i=0; i< list.size(); i++ ){
					if( i == 0 ){
						_okrReportStatusStatistic = list.get(i);
					}
					if( i > 0 ){
						emc.remove( list.get(i), CheckRemoveType.all );
					}
				}
			}
			if( _okrReportStatusStatistic != null  ){
				_okrReportStatusStatistic.setReportStatistic( statistic.getReportStatistic() );
				emc.check( _okrReportStatusStatistic, CheckPersistType.all );
			}else{
				emc.persist( statistic, CheckPersistType.all );
			}
			emc.commit();
		}catch( Exception e ){
			logger.warn( "OkrConfigWorkLevel update/ got a error!" );
			throw e;
		}
		return statistic;
	}

	
	public List<OkrStatisticReportStatus> list( List<String> ids ) throws Exception {
		if( ids  == null  ){
			throw new Exception( "ids is null, return null!" );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.okrStatisticReportStatusFactory().list( ids );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<OkrStatisticReportStatus> list(String centerId, String centerTitle, String workId, String workType, String unitName, String cycleType, String status ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.okrStatisticReportStatusFactory().list( centerId, centerTitle, workId, workType, unitName, cycleType, status );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<String> listIds(String centerId, String workId, String unitName, String cycleType, String status ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.okrStatisticReportStatusFactory().listIds( centerId, workId, unitName, cycleType, status );
		}catch( Exception e ){
			throw e;
		}
	}
	
}
