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
import com.x.okr.assemble.control.jaxrs.okrrolepermission.WrapInOkrRolePermission;
import com.x.okr.entity.OkrRolePermission;

/**
 * 类   名：OkrRolePermissionService<br/>
 * 实体类：OkrRolePermission<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrRolePermissionService{
	private Logger logger = LoggerFactory.getLogger( OkrRolePermissionService.class );
	private BeanCopyTools<WrapInOkrRolePermission, OkrRolePermission> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrRolePermission.class, OkrRolePermission.class, null, WrapInOkrRolePermission.Excludes );
	/**
	 * 根据传入的ID从数据库查询OkrRolePermission对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrRolePermission get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrRolePermission.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrRolePermission对象
	 * @param wrapIn
	 */
	public OkrRolePermission save( WrapInOkrRolePermission wrapIn ) throws Exception {
		OkrRolePermission okrRolePermission = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrRolePermission =  emc.find( wrapIn.getId(), OkrRolePermission.class );
				if( okrRolePermission != null ){
					emc.beginTransaction( OkrRolePermission.class );
					wrapin_copier.copy( wrapIn, okrRolePermission );
					emc.check( okrRolePermission, CheckPersistType.all );	
					emc.commit();
				}else{
					okrRolePermission = new OkrRolePermission();
					emc.beginTransaction( OkrRolePermission.class );
					wrapin_copier.copy( wrapIn, okrRolePermission );
					okrRolePermission.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrRolePermission, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrRolePermission update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrRolePermission = new OkrRolePermission();
				emc.beginTransaction( OkrRolePermission.class );
				wrapin_copier.copy( wrapIn, okrRolePermission );
				emc.persist( okrRolePermission, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrRolePermission create got a error!", e);
				throw e;
			}
		}
		return okrRolePermission;
	}
	
	/**
	 * 根据ID从数据库中删除OkrRolePermission对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrRolePermission okrRolePermission = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrRolePermission = emc.find(id, OkrRolePermission.class);
			if (null == okrRolePermission) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrRolePermission.class );
				emc.remove( okrRolePermission, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrRolePermission> listAll() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrRolePermissionFactory().listAll();
		}catch( Exception e ){
			throw e;
		}
	}

	
}
