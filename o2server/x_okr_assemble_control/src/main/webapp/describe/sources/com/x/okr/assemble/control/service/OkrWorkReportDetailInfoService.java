package com.x.okr.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.entity.OkrWorkReportDetailInfo;

/**
 * 类   名：OkrWorkReportDetailInfoService<br/>
 * 实体类：OkrWorkReportDetailInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportDetailInfoService{
	
	private static  Logger logger = LoggerFactory.getLogger( OkrWorkReportDetailInfoService.class );

	/**
	 * 根据传入的ID从数据库查询OkrWorkReportDetailInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkReportDetailInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkReportDetailInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkReportDetailInfo对象
	 * @param wrapIn
	 */
	public OkrWorkReportDetailInfo save( OkrWorkReportDetailInfo wrapIn ) throws Exception {
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkReportDetailInfo =  emc.find( wrapIn.getId(), OkrWorkReportDetailInfo.class );
				if( okrWorkReportDetailInfo != null ){
					emc.beginTransaction( OkrWorkReportDetailInfo.class );
					wrapIn.copyTo( okrWorkReportDetailInfo, JpaObject.FieldsUnmodify );
					emc.check( okrWorkReportDetailInfo, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkReportDetailInfo = new OkrWorkReportDetailInfo();
					emc.beginTransaction( OkrWorkReportDetailInfo.class );
					wrapIn.copyTo( okrWorkReportDetailInfo );
					okrWorkReportDetailInfo.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkReportDetailInfo, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrWorkReportDetailInfo update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkReportDetailInfo = new OkrWorkReportDetailInfo();
				emc.beginTransaction( OkrWorkReportDetailInfo.class );
				wrapIn.copyTo( okrWorkReportDetailInfo );
				emc.persist( okrWorkReportDetailInfo, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrWorkReportDetailInfo create got a error!", e);
				throw e;
			}
		}
		return okrWorkReportDetailInfo;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkReportDetailInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkReportDetailInfo = emc.find(id, OkrWorkReportDetailInfo.class);
			if (null == okrWorkReportDetailInfo) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkReportDetailInfo.class );
				emc.remove( okrWorkReportDetailInfo, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
