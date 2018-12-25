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
import com.x.okr.entity.OkrConfigSecretary;

public class OkrConfigSecretaryService {
	
	private static  Logger logger = LoggerFactory.getLogger( OkrConfigSecretaryService.class );
	
	/**
	 * 根据传入的ID从数据库查询OkrConfigSecretary对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrConfigSecretary get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			return emc.find( id, OkrConfigSecretary.class );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 向数据库保存OkrConfigSecretary对象
	 * @param wrapIn
	 */
	public OkrConfigSecretary save( OkrConfigSecretary wrapIn ) throws Exception {
		OkrConfigSecretary okrConfigSecretary = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
			//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
				okrConfigSecretary =  emc.find( wrapIn.getId(), OkrConfigSecretary.class );
				if( okrConfigSecretary != null ){
					emc.beginTransaction( OkrConfigSecretary.class );
					wrapIn.copyTo( okrConfigSecretary, JpaObject.FieldsUnmodify );
					emc.check( okrConfigSecretary, CheckPersistType.all );	
					emc.commit();
				}else{
					okrConfigSecretary = new OkrConfigSecretary();
					emc.beginTransaction( OkrConfigSecretary.class );
					wrapIn.copyTo( okrConfigSecretary );
					okrConfigSecretary.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					okrConfigSecretary.setUpdateTime( okrConfigSecretary.getCreateTime() );
					emc.persist( okrConfigSecretary, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrConfigSecretary update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
				okrConfigSecretary = new OkrConfigSecretary();
				emc.beginTransaction( OkrConfigSecretary.class );
				wrapIn.copyTo( okrConfigSecretary );
				emc.persist( okrConfigSecretary, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrConfigSecretary create got a error!", e);
				throw e;
			}
		}
		return okrConfigSecretary;
	}

	/**
	 * 根据ID从数据库中删除OkrConfigSecretary对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrConfigSecretary okrConfigSecretary = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrConfigSecretary = emc.find(id, OkrConfigSecretary.class);
			if (null == okrConfigSecretary) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{		
				emc.beginTransaction( OkrConfigSecretary.class );
				emc.remove( okrConfigSecretary, CheckRemoveType.all );
				emc.commit();
			}			
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listIdsByPerson( String name, String leaderName ) throws Exception {
		if( name  == null || name.isEmpty() ){
			throw new Exception( "name is null, return null!" );
		}
		if( leaderName  == null || leaderName.isEmpty() ){
			throw new Exception( "leaderName is null, return null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			business = new Business( emc );
			return business.okrConfigSecretaryFactory().listIdsByPerson( name, leaderName );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<String> listIdsByLeaderIdentity( String name, String leaderIdentity ) throws Exception {
		if( name  == null || name.isEmpty() ){
			throw new Exception( "name is null, return null!" );
		}
		if( leaderIdentity  == null || leaderIdentity.isEmpty() ){
			throw new Exception( "leaderIdentity is null, return null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			business = new Business( emc );
			return business.okrConfigSecretaryFactory().listIdsByLeaderIdentity( name, leaderIdentity );
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<String> listIdsByPerson( String name ) throws Exception {
		if( name  == null || name.isEmpty() ){
			throw new Exception( "name is null, return null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			business = new Business( emc );
			
			
			
			
			return business.okrConfigSecretaryFactory().listBySecretaryName( name );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<OkrConfigSecretary> listByIds(List<String> ids) throws Exception {
		if( ids  == null || ids.isEmpty() ){
			throw new Exception( "ids is null, return null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			business = new Business( emc );
			return business.okrConfigSecretaryFactory().list(ids);
		}catch( Exception e ){
			throw e;
		}
	}

	public List<String> listIdsByIdentities(List<String> identities) throws Exception {
		if( identities  == null || identities.isEmpty() ){
			throw new Exception( "identities is null, return null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			business = new Business( emc );
			return business.okrConfigSecretaryFactory().listIdsByIdentities( identities );
		}catch( Exception e ){
			throw e;
		}
	}
}