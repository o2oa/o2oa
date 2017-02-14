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
import com.x.okr.assemble.control.jaxrs.okrpersonpermission.WrapInOkrPersonPermission;
import com.x.okr.entity.OkrPersonPermission;

/**
 * 类   名：OkrPersonPermissionService<br/>
 * 实体类：OkrPersonPermission<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrPersonPermissionService{
	private Logger logger = LoggerFactory.getLogger( OkrPersonPermissionService.class );
	private BeanCopyTools<WrapInOkrPersonPermission, OkrPersonPermission> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrPersonPermission.class, OkrPersonPermission.class, null, WrapInOkrPersonPermission.Excludes );
	/**
	 * 根据传入的ID从数据库查询OkrPersonPermission对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrPersonPermission get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrPersonPermission.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrPersonPermission对象
	 * @param wrapIn
	 */
	public OkrPersonPermission save( WrapInOkrPersonPermission wrapIn ) throws Exception {
		OkrPersonPermission okrPersonPermission = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrPersonPermission =  emc.find( wrapIn.getId(), OkrPersonPermission.class );
				if( okrPersonPermission != null ){
					emc.beginTransaction( OkrPersonPermission.class );
					wrapin_copier.copy( wrapIn, okrPersonPermission );
					emc.check( okrPersonPermission, CheckPersistType.all );	
					emc.commit();
				}else{
					okrPersonPermission = new OkrPersonPermission();
					emc.beginTransaction( OkrPersonPermission.class );
					wrapin_copier.copy( wrapIn, okrPersonPermission );
					okrPersonPermission.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrPersonPermission, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrPersonPermission update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrPersonPermission = new OkrPersonPermission();
				emc.beginTransaction( OkrPersonPermission.class );
				wrapin_copier.copy( wrapIn, okrPersonPermission );
				emc.persist( okrPersonPermission, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrPersonPermission create got a error!", e);
				throw e;
			}
		}
		return okrPersonPermission;
	}
	
	/**
	 * 根据ID从数据库中删除OkrPersonPermission对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrPersonPermission okrPersonPermission = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrPersonPermission = emc.find(id, OkrPersonPermission.class);
			if (null == okrPersonPermission) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrPersonPermission.class );
				emc.remove( okrPersonPermission, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrPersonPermission> listAll() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrPersonPermissionFactory().listAll();
		}catch( Exception e ){
			throw e;
		}
	}

	
}
