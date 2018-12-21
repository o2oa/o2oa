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
import com.x.okr.entity.OkrConfigWorkLevel;

/**
 * 类   名：OkrConfigWorkLevelService<br/>
 * 实体类：OkrConfigWorkLevel<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrConfigWorkLevelService{
	
	private static  Logger logger = LoggerFactory.getLogger( OkrConfigWorkLevelService.class );
	/**
	 * 根据传入的ID从数据库查询OkrConfigWorkLevel对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrConfigWorkLevel get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrConfigWorkLevel.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrConfigWorkLevel对象
	 * @param wrapIn
	 */
	public OkrConfigWorkLevel save( OkrConfigWorkLevel wrapIn ) throws Exception {
		OkrConfigWorkLevel okrConfigWorkLevel = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrConfigWorkLevel =  emc.find( wrapIn.getId(), OkrConfigWorkLevel.class );
				if( okrConfigWorkLevel != null ){
					emc.beginTransaction( OkrConfigWorkLevel.class );
					wrapIn.copyTo( okrConfigWorkLevel, JpaObject.FieldsUnmodify );
					emc.check( okrConfigWorkLevel, CheckPersistType.all );	
					emc.commit();
				}else{
					okrConfigWorkLevel = new OkrConfigWorkLevel();
					emc.beginTransaction( OkrConfigWorkLevel.class );
					wrapIn.copyTo( okrConfigWorkLevel );
					okrConfigWorkLevel.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrConfigWorkLevel, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrConfigWorkLevel update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrConfigWorkLevel = new OkrConfigWorkLevel();
				emc.beginTransaction( OkrConfigWorkLevel.class );
				wrapIn.copyTo( okrConfigWorkLevel );
				emc.persist( okrConfigWorkLevel, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrConfigWorkLevel create got a error!", e);
				throw e;
			}
		}
		return okrConfigWorkLevel;
	}
	
	/**
	 * 根据ID从数据库中删除OkrConfigWorkLevel对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrConfigWorkLevel okrConfigWorkLevel = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrConfigWorkLevel = emc.find(id, OkrConfigWorkLevel.class);
			if (null == okrConfigWorkLevel) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrConfigWorkLevel.class );
				emc.remove( okrConfigWorkLevel, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrConfigWorkLevel> listAll() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.okrConfigWorkLevelFactory().listAll();
		}catch( Exception e ){
			throw e;
		}
	}
	
}
