package com.x.okr.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.WrapInFilter;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.WrapInOkrWorkDynamics;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDynamics;

/**
 * 类   名：OkrWorkDynamicsService<br/>
 * 实体类：OkrWorkDynamics<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkDynamicsService{
	private Logger logger = LoggerFactory.getLogger( OkrWorkDynamicsService.class );
	private BeanCopyTools<WrapInOkrWorkDynamics, OkrWorkDynamics> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrWorkDynamics.class, OkrWorkDynamics.class, null, WrapInOkrWorkDynamics.Excludes );
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
	public OkrWorkDynamics save( WrapInOkrWorkDynamics wrapIn ) throws Exception {
		OkrWorkDynamics okrWorkDynamics = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkDynamics =  emc.find( wrapIn.getId(), OkrWorkDynamics.class );
				if( okrWorkDynamics != null ){
					emc.beginTransaction( OkrWorkDynamics.class );
					wrapin_copier.copy( wrapIn, okrWorkDynamics );
					emc.check( okrWorkDynamics, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkDynamics = new OkrWorkDynamics();
					emc.beginTransaction( OkrWorkDynamics.class );
					wrapin_copier.copy( wrapIn, okrWorkDynamics );
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
				wrapin_copier.copy( wrapIn, okrWorkDynamics );
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
	
	/**
	 * 向数据库保存OkrWorkDynamics对象
	 * @param wrapIn
	 */
	public void workDynamic( String centerId, String workId, String title, String dynamicType, String operatorName, String targetName, String targetIdentity, String content, String description ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, system can not delete any object." );
		}
		if( operatorName == null || operatorName.isEmpty() ){
			throw new Exception( "operatorName is null, system can not delete any object." );
		}
		if( content == null || content.isEmpty() ){
			throw new Exception( "content is null, system can not delete any object." );
		}
		OkrWorkBaseInfo okrWorkBaseInfo  = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			OkrWorkDynamics okrWorkDynamics = new OkrWorkDynamics();
			if( workId != null && !workId.isEmpty() ){
				okrWorkDynamics.setCenterId( centerId );
				okrWorkDynamics.setWorkId( workId );
				okrWorkDynamics.setDynamicObjectId( workId );
				okrWorkDynamics.setDynamicObjectTitle( title );
				okrWorkDynamics.setWorkTitle( title );
				okrWorkDynamics.setDynamicObjectType( "具体工作" );
				okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
				if( okrWorkBaseInfo != null ){
					okrWorkDynamics.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
				}
			}
			if( workId == null || workId.isEmpty() || okrWorkBaseInfo == null ){
				okrWorkDynamics.setCenterId( centerId );
				okrWorkDynamics.setCenterTitle( title );
				okrWorkDynamics.setDynamicObjectId( centerId );
				okrWorkDynamics.setDynamicObjectTitle( title );
				okrWorkDynamics.setDynamicObjectType( "中心工作" );
			}
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
	 * 
	 * @param configName
	 * @param configCode
	 * @param dynamicType
	 * @param operatorName
	 * @param targetName
	 * @param targetIdentity
	 * @param content
	 * @param description
	 * @throws Exception
	 */
	public void configSystemDynamic( String configName, String configCode, String dynamicType, String operatorName, String targetName, String targetIdentity, String content, String description ) throws Exception {
		if( configName == null || configName.isEmpty() ){
			throw new Exception( "configName is null, system can not delete any object." );
		}
		if( configCode == null || configCode.isEmpty() ){
			throw new Exception( "configCode is null, system can not delete any object." );
		}
		if( operatorName == null || operatorName.isEmpty() ){
			throw new Exception( "operatorName is null, system can not delete any object." );
		}
		if( content == null || content.isEmpty() ){
			throw new Exception( "content is null, system can not delete any object." );
		}
	
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			OkrWorkDynamics okrWorkDynamics = new OkrWorkDynamics();
			okrWorkDynamics.setDynamicObjectId( configCode );
			okrWorkDynamics.setDynamicObjectTitle( configName );
			okrWorkDynamics.setDynamicObjectType( "系统配置" );
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
	 * 
	 * @param workId
	 * @param reportId
	 * @param dynamicType
	 * @param operatorName
	 * @param targetName
	 * @param targetIdentity
	 * @param content
	 * @param description
	 * @throws Exception 
	 */
	public void reportDynamic( String centerId, String centerTitle, String workId, String workTitle, String title, String reportId, String dynamicType, String operatorName, String targetName, String targetIdentity, String content, String description  ) throws Exception {
		OkrWorkDynamics okrWorkDynamics = new OkrWorkDynamics();
		okrWorkDynamics.setCenterId( centerId );
		okrWorkDynamics.setCenterTitle( centerTitle );
		okrWorkDynamics.setWorkId( workId );
		okrWorkDynamics.setWorkTitle( workTitle );
		okrWorkDynamics.setDynamicObjectId( reportId );
		okrWorkDynamics.setDynamicObjectTitle( title );
		okrWorkDynamics.setDynamicObjectType( "工作汇报" );
		okrWorkDynamics.setContent( content );
		okrWorkDynamics.setDynamicType( dynamicType );
		okrWorkDynamics.setDateTime( new Date() );
		okrWorkDynamics.setDateTimeStr( dateOperation.getNowDateTime() );
		okrWorkDynamics.setDescription( description );
		okrWorkDynamics.setOperatorName(operatorName );
		okrWorkDynamics.setTargetIdentity( targetIdentity );
		okrWorkDynamics.setTargetName( targetName );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( OkrWorkDynamics.class );
			emc.persist( okrWorkDynamics, CheckPersistType.all );
			emc.commit();
		}catch( Exception e ){
			logger.warn( "OkrWorkDynamics update/ got a error!" );
			throw e;
		}
	}
	
	public void processReadDynamic( OkrTask okrTask, String dynamicType, String operatorName, String targetName, String targetIdentity, String content, String description  ) throws Exception {
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
	public List<OkrWorkDynamics> listDynamicNextWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase( id,StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkDynamics.class ), "sequence" );
				}
			}
			return business.okrWorkDynamicsFactory().listNextWithFilter( id, count, sequence, wrapIn );
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
	public List<OkrWorkDynamics> listDynamicPrevWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase( id,StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkDynamics.class ), "sequence" );
				}
			}
			return business.okrWorkDynamicsFactory().listPrevWithFilter( id, count, sequence, wrapIn );
			
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
	public Long getDynamicCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkDynamicsFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void workChatDynamic( OkrWorkBaseInfo okrWorkBaseInfo, String dynamicType, String operatorName, String targetName, String targetIdentity, String content, String description ) throws Exception {
		if( okrWorkBaseInfo == null ){
			throw new Exception( "okrWorkBaseInfo is null, system can not delete any object." );
		}
		if( operatorName == null || operatorName.isEmpty() ){
			throw new Exception( "operatorName is null, system can not delete any object." );
		}
		if( content == null || content.isEmpty() ){
			throw new Exception( "content is null, system can not delete any object." );
		}		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			OkrWorkDynamics okrWorkDynamics = new OkrWorkDynamics();
			okrWorkDynamics.setCenterId( okrWorkBaseInfo.getCenterId() );
			okrWorkDynamics.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
			okrWorkDynamics.setWorkId( okrWorkBaseInfo.getId() );
			okrWorkDynamics.setWorkTitle( okrWorkBaseInfo.getTitle() );
			okrWorkDynamics.setDynamicObjectId( okrWorkBaseInfo.getId() );
			okrWorkDynamics.setDynamicObjectTitle( okrWorkBaseInfo.getTitle() );
			okrWorkDynamics.setDynamicObjectType( "工作交流" );
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
			logger.warn( "OkrWorkChatDynamics update/ got a error!" );
			throw e;
		}
	}

	public void taskDynamic( String centerId, String centerTitle, String workId, String workTitle, String title, String id, String dynamicType, String operatorName, String content, String description, String targetName, String targetIdentity ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		if( dynamicType == null || dynamicType.isEmpty() ){
			throw new Exception( "operatorName is null." );
		}
		if( operatorName == null || operatorName.isEmpty() ){
			throw new Exception( "operatorName is null." );
		}
		if( content == null || content.isEmpty() ){
			throw new Exception( "content is null." );
		}
		if( description == null || description.isEmpty() ){
			throw new Exception( "description is null." );
		}
		OkrWorkDynamics okrWorkDynamics = new OkrWorkDynamics();
		okrWorkDynamics.setCenterId( centerId );
		okrWorkDynamics.setCenterTitle( centerTitle );
		okrWorkDynamics.setWorkId( workId );
		okrWorkDynamics.setWorkTitle( workTitle );
		okrWorkDynamics.setDynamicObjectId( id );
		okrWorkDynamics.setDynamicObjectTitle( title );
		okrWorkDynamics.setDynamicObjectType( "待办待阅" );
		okrWorkDynamics.setContent( content );
		okrWorkDynamics.setDynamicType( dynamicType );
		okrWorkDynamics.setDateTime( new Date() );
		okrWorkDynamics.setDateTimeStr( dateOperation.getNowDateTime() );
		okrWorkDynamics.setDescription( description );
		okrWorkDynamics.setOperatorName(operatorName );
		okrWorkDynamics.setTargetIdentity( targetIdentity );
		okrWorkDynamics.setTargetName( targetName );
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			emc.beginTransaction( OkrWorkDynamics.class );
			emc.persist( okrWorkDynamics, CheckPersistType.all );
			emc.commit();
		}catch( Exception e ){
			logger.warn( "OkrWorkDynamics update/ got a error!" );
			throw e;
		}
	}

	public void taskHandledDynamic( String centerId, String centerTitle, String workId, String workTitle, String title, String id, String dynamicType, String operatorName, String content, String description, String targetName, String targetIdentity ) 
			throws Exception { 
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		if( dynamicType == null || dynamicType.isEmpty() ){
			throw new Exception( "operatorName is null." );
		}
		if( operatorName == null || operatorName.isEmpty() ){
			throw new Exception( "operatorName is null." );
		}
		if( content == null || content.isEmpty() ){
			throw new Exception( "content is null." );
		}
		if( description == null || description.isEmpty() ){
			throw new Exception( "description is null." );
		}
		OkrWorkDynamics okrWorkDynamics = new OkrWorkDynamics();
		okrWorkDynamics.setCenterId( centerId );
		okrWorkDynamics.setCenterTitle( centerTitle );
		okrWorkDynamics.setWorkId( workId );
		okrWorkDynamics.setWorkTitle( workTitle );
		okrWorkDynamics.setDynamicObjectId( id );
		okrWorkDynamics.setDynamicObjectTitle( title );
		okrWorkDynamics.setDynamicObjectType( "已办已阅" );
		okrWorkDynamics.setContent( content );
		okrWorkDynamics.setDynamicType( dynamicType );
		okrWorkDynamics.setDateTime( new Date() );
		okrWorkDynamics.setDateTimeStr( dateOperation.getNowDateTime() );
		okrWorkDynamics.setDescription( description );
		okrWorkDynamics.setOperatorName(operatorName );
		okrWorkDynamics.setTargetIdentity( targetIdentity );
		okrWorkDynamics.setTargetName( targetName );
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			emc.beginTransaction( OkrWorkDynamics.class );
			emc.persist( okrWorkDynamics, CheckPersistType.all );
			emc.commit();
		}catch( Exception e ){
			logger.warn( "OkrWorkDynamics update/ got a error!" );
			throw e;
		}
	}	
}
