package com.x.okr.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrWorkDynamics;

/**
 * 类   名：OkrWorkDynamicsService<br/>
 * 实体类：OkrWorkDynamics<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkDynamicsService{
	private static  Logger logger = LoggerFactory.getLogger( OkrWorkDynamicsService.class );
	private DateOperation dateOperation = new DateOperation();
	
	/**
	 * 根据传入的ID从数据库查询OkrWorkDynamics对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkDynamics get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkDynamics.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkDynamics对象
	 * @param wrapIn
	 */
	public OkrWorkDynamics save( OkrWorkDynamics wrapIn ) throws Exception {
		OkrWorkDynamics okrWorkDynamics = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkDynamics =  emc.find( wrapIn.getId(), OkrWorkDynamics.class );
				if( okrWorkDynamics != null ){
					emc.beginTransaction( OkrWorkDynamics.class );
					wrapIn.copyTo( okrWorkDynamics, JpaObject.FieldsUnmodify );
					emc.check( okrWorkDynamics, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkDynamics = new OkrWorkDynamics();
					emc.beginTransaction( OkrWorkDynamics.class );
					wrapIn.copyTo( okrWorkDynamics );
					okrWorkDynamics.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkDynamics, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrWorkDynamics update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkDynamics = new OkrWorkDynamics();
				emc.beginTransaction( OkrWorkDynamics.class );
				wrapIn.copyTo( okrWorkDynamics );
				emc.persist( okrWorkDynamics, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrWorkDynamics create got a error!", e);
				throw e;
			}
		}
		return okrWorkDynamics;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkDynamics对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkDynamics okrWorkDynamics = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkDynamics = emc.find(id, OkrWorkDynamics.class);
			if ( null == okrWorkDynamics ) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkDynamics.class );
				emc.remove( okrWorkDynamics, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public void processReadDynamic1( OkrTask okrTask, String dynamicType, String operatorName, String targetName, String targetIdentity, String content, String description  ) throws Exception {
		if( okrTask == null ){
			throw new Exception( "okrTask is null, system can not delete any object." );
		}
		if( operatorName == null || operatorName.isEmpty() ){
			throw new Exception( "operatorName is null, system can not delete any object." );
		}
		if( content == null || content.isEmpty() ){
			throw new Exception( "content is null, system can not delete any object." );
		}		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			OkrWorkDynamics okrWorkDynamics = new OkrWorkDynamics();
			okrWorkDynamics.setCenterId( okrTask.getCenterId() );
			okrWorkDynamics.setCenterTitle( okrTask.getCenterTitle() );
			okrWorkDynamics.setWorkId( okrTask.getWorkId() );
			okrWorkDynamics.setWorkTitle( okrTask.getTitle() );
			okrWorkDynamics.setDynamicObjectId( okrTask.getId() );
			okrWorkDynamics.setDynamicObjectTitle( okrTask.getTitle() );
			okrWorkDynamics.setDynamicObjectType( "工作汇报待阅" );
			okrWorkDynamics.setContent( content );
			okrWorkDynamics.setDynamicType( dynamicType );
			okrWorkDynamics.setDateTime( new Date() );
			okrWorkDynamics.setDateTimeStr( dateOperation.getNowDateTime() );
			okrWorkDynamics.setDescription( description );
			okrWorkDynamics.setOperatorName(operatorName );
			okrWorkDynamics.setTargetIdentity( targetIdentity );
			okrWorkDynamics.setTargetName( targetName );
			
			emc.beginTransaction( OkrWorkDynamics.class );
			emc.persist( okrWorkDynamics, CheckPersistType.all );
			emc.commit();
		}catch( Exception e ){
			logger.warn( "OkrWorkDynamics update/ got a error!" );
			throw e;
		}
	}

	/**
	 * 查询下一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkDynamics> listDynamicNextWithFilter( String id, Integer count, 
			List<String> centerIds, List<String> workIds, String sequenceField,
			String order, Boolean isOkrSystemAdmin ) throws Exception {		
		Business business = null;
		Object sequence = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase( id,StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkDynamics.class ),  JpaObject.sequence_FIELDNAME );
				}
			}
			return business.okrWorkDynamicsFactory().listNextWithFilter( id, count, sequence, centerIds, workIds, 
					sequenceField, order, isOkrSystemAdmin );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 查询上一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkDynamics> listDynamicPrevWithFilter( String id, Integer count, List<String> centerIds, List<String> workIds, String sequenceField,
			String order, Boolean isOkrSystemAdmin ) throws Exception {
		Business business = null;
		Object sequence = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase( id,StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkDynamics.class ),  JpaObject.sequence_FIELDNAME );
				}
			}
			return business.okrWorkDynamicsFactory().listPrevWithFilter( id, count, sequence, centerIds, workIds, 
					sequenceField, order, isOkrSystemAdmin );
			
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 查询符合条件的数据总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public Long getDynamicCountWithFilter( List<String> centerIds, List<String> workIds, String sequenceField,
			String order, Boolean isOkrSystemAdmin ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkDynamicsFactory().getCountWithFilter( centerIds, workIds, 
					sequenceField, order, isOkrSystemAdmin );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
