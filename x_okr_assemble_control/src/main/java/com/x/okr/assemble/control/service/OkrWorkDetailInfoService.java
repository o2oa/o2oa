package com.x.okr.assemble.control.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.WrapInOkrWorkDetailInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

/**
 * 类   名：OkrWorkDetailInfoService<br/>
 * 实体类：OkrWorkDetailInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkDetailInfoService{
	private Logger logger = LoggerFactory.getLogger( OkrWorkDetailInfoService.class );
	private BeanCopyTools<WrapInOkrWorkDetailInfo, OkrWorkDetailInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrWorkDetailInfo.class, OkrWorkDetailInfo.class, null, WrapInOkrWorkDetailInfo.Excludes );
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
	public OkrWorkDetailInfo save( WrapInOkrWorkDetailInfo wrapIn ) throws Exception {
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkDetailInfo =  emc.find( wrapIn.getId(), OkrWorkDetailInfo.class );
				if( okrWorkDetailInfo != null ){
					emc.beginTransaction( OkrWorkDetailInfo.class );
					wrapin_copier.copy( wrapIn, okrWorkDetailInfo );
					emc.check( okrWorkDetailInfo, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkDetailInfo = new OkrWorkDetailInfo();
					emc.beginTransaction( OkrWorkDetailInfo.class );
					wrapin_copier.copy( wrapIn, okrWorkDetailInfo );
					okrWorkDetailInfo.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkDetailInfo, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrWorkDetailInfo update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkDetailInfo = new OkrWorkDetailInfo();
				emc.beginTransaction( OkrWorkDetailInfo.class );
				wrapin_copier.copy( wrapIn, okrWorkDetailInfo );
				emc.persist( okrWorkDetailInfo, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrWorkDetailInfo create got a error!", e);
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
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkDetailInfo = emc.find(id, OkrWorkDetailInfo.class);
			if (null == okrWorkDetailInfo) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkDetailInfo.class );
				emc.remove( okrWorkDetailInfo, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
