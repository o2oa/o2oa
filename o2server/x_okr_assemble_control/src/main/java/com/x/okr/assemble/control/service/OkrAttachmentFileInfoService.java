package com.x.okr.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrAttachmentFileInfo;

/**
 * 类   名：OkrAttachmentFileInfoService<br/>
 * 实体类：OkrAttachmentFileInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrAttachmentFileInfoService{
	
	private static  Logger logger = LoggerFactory.getLogger( OkrAttachmentFileInfoService.class );
	
	/**
	 * 根据传入的ID从数据库查询OkrAttachmentFileInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrAttachmentFileInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrAttachmentFileInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 根据传入的ID从数据库查询OkrAttachmentFileInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<OkrAttachmentFileInfo> list( List<String> ids ) throws Exception {
		if( ids  == null || ids.isEmpty() ){
			return null;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.okrAttachmentFileInfoFactory().list(ids);
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrAttachmentFileInfo对象
	 * @param wrapIn
	 */
	public OkrAttachmentFileInfo save( OkrAttachmentFileInfo wrapIn ) throws Exception {
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrAttachmentFileInfo =  emc.find( wrapIn.getId(), OkrAttachmentFileInfo.class );
				if( okrAttachmentFileInfo != null ){
					emc.beginTransaction( OkrAttachmentFileInfo.class );
					wrapIn.copyTo( okrAttachmentFileInfo, JpaObject.FieldsUnmodify );
					emc.check( okrAttachmentFileInfo, CheckPersistType.all );	
					emc.commit();
				}else{
					okrAttachmentFileInfo = new OkrAttachmentFileInfo();
					emc.beginTransaction( OkrAttachmentFileInfo.class );
					wrapIn.copyTo( okrAttachmentFileInfo );
					okrAttachmentFileInfo.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrAttachmentFileInfo, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrAttachmentFileInfo update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrAttachmentFileInfo = new OkrAttachmentFileInfo();
				emc.beginTransaction( OkrAttachmentFileInfo.class );
				wrapIn.copyTo( okrAttachmentFileInfo );
				emc.persist( okrAttachmentFileInfo, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrAttachmentFileInfo create got a error!", e);
				throw e;
			}
		}
		return okrAttachmentFileInfo;
	}
	
	/**
	 * 根据ID从数据库中删除OkrAttachmentFileInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrAttachmentFileInfo = emc.find(id, OkrAttachmentFileInfo.class);
			if ( null == okrAttachmentFileInfo ) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrAttachmentFileInfo.class );
				emc.remove( okrAttachmentFileInfo, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
