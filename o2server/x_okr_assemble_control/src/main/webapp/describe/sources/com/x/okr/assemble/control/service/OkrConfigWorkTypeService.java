package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrConfigWorkType;

/**
 * 类   名：OkrConfigWorkTypeService<br/>
 * 实体类：OkrConfigWorkType<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrConfigWorkTypeService{
	
	private static  Logger logger = LoggerFactory.getLogger( OkrConfigWorkTypeService.class );

	/**
	 * 根据传入的ID从数据库查询OkrConfigWorkType对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrConfigWorkType get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrConfigWorkType.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrConfigWorkType对象
	 * @param wrapIn
	 */
	public OkrConfigWorkType save( OkrConfigWorkType wrapIn ) throws Exception {
		OkrConfigWorkType okrConfigWorkType = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrConfigWorkType =  emc.find( wrapIn.getId(), OkrConfigWorkType.class );
				if( okrConfigWorkType != null ){
					emc.beginTransaction( OkrConfigWorkType.class );
					wrapIn.copyTo( okrConfigWorkType, JpaObject.FieldsUnmodify );
					emc.check( okrConfigWorkType, CheckPersistType.all );	
					emc.commit();
				}else{
					okrConfigWorkType = new OkrConfigWorkType();
					emc.beginTransaction( OkrConfigWorkType.class );
					wrapIn.copyTo( okrConfigWorkType );
					okrConfigWorkType.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrConfigWorkType, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrConfigWorkType update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrConfigWorkType = new OkrConfigWorkType();
				emc.beginTransaction( OkrConfigWorkType.class );
				wrapIn.copyTo( okrConfigWorkType );
				emc.persist( okrConfigWorkType, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrConfigWorkType create got a error!", e);
				throw e;
			}
		}
		return okrConfigWorkType;
	}
	
	/**
	 * 根据ID从数据库中删除OkrConfigWorkType对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrConfigWorkType okrConfigWorkType = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrConfigWorkType = emc.find(id, OkrConfigWorkType.class);
			if (null == okrConfigWorkType) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrConfigWorkType.class );
				emc.remove( okrConfigWorkType, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrConfigWorkType> listAll() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrConfigWorkTypeFactory().listAll();
		}catch( Exception e ){
			throw e;
		}
	}

	public List<String> listAllTypeName() throws Exception {
		List<OkrConfigWorkType> workTypeList = null;
		List<String> workTypeNameList = new ArrayList<>();
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			workTypeList = business.okrConfigWorkTypeFactory().listAll();
			if( workTypeList != null && !workTypeList.isEmpty() ){
				for( OkrConfigWorkType type : workTypeList ){
					workTypeNameList.add( type.getWorkTypeName() );
				}
			}
		}catch( Exception e ){
			throw e;
		}
		return workTypeNameList;
	}
	
}
