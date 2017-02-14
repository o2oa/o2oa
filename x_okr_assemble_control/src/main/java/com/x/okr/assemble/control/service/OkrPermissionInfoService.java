package com.x.okr.assemble.control.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrpermissioninfo.WrapInOkrPermissionInfo;
import com.x.okr.entity.OkrPermissionInfo;

/**
 * 类   名：OkrPermissionInfoService<br/>
 * 实体类：OkrPermissionInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrPermissionInfoService{
	private Logger logger = LoggerFactory.getLogger( OkrPermissionInfoService.class );
	private BeanCopyTools<WrapInOkrPermissionInfo, OkrPermissionInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrPermissionInfo.class, OkrPermissionInfo.class, null, WrapInOkrPermissionInfo.Excludes );
	/**
	 * 根据传入的ID从数据库查询OkrPermissionInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrPermissionInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrPermissionInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrPermissionInfo对象
	 * @param wrapIn
	 */
	public OkrPermissionInfo save( WrapInOkrPermissionInfo wrapIn ) throws Exception {
		OkrPermissionInfo okrPermissionInfo = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrPermissionInfo =  emc.find( wrapIn.getId(), OkrPermissionInfo.class );
				if( okrPermissionInfo != null ){
					emc.beginTransaction( OkrPermissionInfo.class );
					wrapin_copier.copy( wrapIn, okrPermissionInfo );
					emc.check( okrPermissionInfo, CheckPersistType.all );	
					emc.commit();
				}else{
					okrPermissionInfo = new OkrPermissionInfo();
					emc.beginTransaction( OkrPermissionInfo.class );
					wrapin_copier.copy( wrapIn, okrPermissionInfo );
					okrPermissionInfo.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrPermissionInfo, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrPermissionInfo update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrPermissionInfo = new OkrPermissionInfo();
				emc.beginTransaction( OkrPermissionInfo.class );
				wrapin_copier.copy( wrapIn, okrPermissionInfo );
				emc.persist( okrPermissionInfo, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrPermissionInfo create got a error!", e);
				throw e;
			}
		}
		return okrPermissionInfo;
	}
	
	/**
	 * 根据ID从数据库中删除OkrPermissionInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrPermissionInfo okrPermissionInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrPermissionInfo = emc.find(id, OkrPermissionInfo.class);
			if (null == okrPermissionInfo) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrPermissionInfo.class );
				emc.remove( okrPermissionInfo, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrPermissionInfo> listAll() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrPermissionInfoFactory().listAll();
		}catch( Exception e ){
			throw e;
		}
	}

	public void checkAndInitPermissionInfo( String permissionName, String permissionCode, String description ) {
		
	}
	
}
