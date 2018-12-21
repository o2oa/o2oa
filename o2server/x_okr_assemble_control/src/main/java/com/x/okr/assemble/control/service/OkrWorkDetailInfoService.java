package com.x.okr.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.entity.OkrWorkDetailInfo;

/**
 * 类   名：OkrWorkDetailInfoService<br/>
 * 实体类：OkrWorkDetailInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkDetailInfoService{
	
	private static  Logger logger = LoggerFactory.getLogger( OkrWorkDetailInfoService.class );
	
	/**
	 * 根据传入的ID从数据库查询OkrWorkDetailInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkDetailInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkDetailInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkDetailInfo对象
	 * @param wrapIn
	 */
	public OkrWorkDetailInfo save( OkrWorkDetailInfo wrapIn ) throws Exception {
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkDetailInfo =  emc.find( wrapIn.getId(), OkrWorkDetailInfo.class );
				if( okrWorkDetailInfo != null ){
					emc.beginTransaction( OkrWorkDetailInfo.class );
					wrapIn.copyTo( okrWorkDetailInfo , JpaObject.FieldsUnmodify);
					emc.check( okrWorkDetailInfo, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkDetailInfo = new OkrWorkDetailInfo();
					emc.beginTransaction( OkrWorkDetailInfo.class );
					wrapIn.copyTo( okrWorkDetailInfo );
					okrWorkDetailInfo.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkDetailInfo, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrWorkDetailInfo update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkDetailInfo = new OkrWorkDetailInfo();
				emc.beginTransaction( OkrWorkDetailInfo.class );
				wrapIn.copyTo( okrWorkDetailInfo );
				emc.persist( okrWorkDetailInfo, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrWorkDetailInfo create got a error!", e);
				throw e;
			}
		}
		return okrWorkDetailInfo;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkDetailInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkDetailInfo = emc.find(id, OkrWorkDetailInfo.class);
			if (null == okrWorkDetailInfo) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkDetailInfo.class );
				emc.remove( okrWorkDetailInfo, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public String getWorkDetailWithId( String workId ) throws Exception {
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkDetailInfo = emc.find( workId, OkrWorkDetailInfo.class);
			if ( null == okrWorkDetailInfo ) {
				logger.warn( "object is not exist {'id':'"+ workId +"'}" );
				return "无";
			}else{
				return okrWorkDetailInfo.getWorkDetail();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
