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
import com.x.okr.assemble.control.jaxrs.okrroleinfo.WrapInOkrRoleInfo;
import com.x.okr.entity.OkrRoleInfo;

/**
 * 类   名：OkrRoleInfoService<br/>
 * 实体类：OkrRoleInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrRoleInfoService{
	private Logger logger = LoggerFactory.getLogger( OkrRoleInfoService.class );
	private BeanCopyTools<WrapInOkrRoleInfo, OkrRoleInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrRoleInfo.class, OkrRoleInfo.class, null, WrapInOkrRoleInfo.Excludes );
	/**
	 * 根据传入的ID从数据库查询OkrRoleInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrRoleInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrRoleInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrRoleInfo对象
	 * @param wrapIn
	 */
	public OkrRoleInfo save( WrapInOkrRoleInfo wrapIn ) throws Exception {
		OkrRoleInfo okrRoleInfo = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrRoleInfo =  emc.find( wrapIn.getId(), OkrRoleInfo.class );
				if( okrRoleInfo != null ){
					emc.beginTransaction( OkrRoleInfo.class );
					wrapin_copier.copy( wrapIn, okrRoleInfo );
					emc.check( okrRoleInfo, CheckPersistType.all );	
					emc.commit();
				}else{
					okrRoleInfo = new OkrRoleInfo();
					emc.beginTransaction( OkrRoleInfo.class );
					wrapin_copier.copy( wrapIn, okrRoleInfo );
					okrRoleInfo.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrRoleInfo, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrRoleInfo update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrRoleInfo = new OkrRoleInfo();
				emc.beginTransaction( OkrRoleInfo.class );
				wrapin_copier.copy( wrapIn, okrRoleInfo );
				emc.persist( okrRoleInfo, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrRoleInfo create got a error!", e);
				throw e;
			}
		}
		return okrRoleInfo;
	}
	
	/**
	 * 根据ID从数据库中删除OkrRoleInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrRoleInfo okrRoleInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrRoleInfo = emc.find(id, OkrRoleInfo.class);
			if (null == okrRoleInfo) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrRoleInfo.class );
				emc.remove( okrRoleInfo, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrRoleInfo> listAll() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrRoleInfoFactory().listAll();
		}catch( Exception e ){
			throw e;
		}
	}

	
}
