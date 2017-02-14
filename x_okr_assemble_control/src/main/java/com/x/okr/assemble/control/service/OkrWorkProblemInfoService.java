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
import com.x.okr.assemble.control.jaxrs.okrworkprobleminfo.WrapInOkrWorkProblemInfo;
import com.x.okr.entity.OkrWorkProblemInfo;
import com.x.okr.entity.OkrWorkProblemPersonLink;
import com.x.okr.entity.OkrWorkProblemProcessLog;

/**
 * 类   名：OkrWorkProblemInfoService<br/>
 * 实体类：OkrWorkProblemInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkProblemInfoService{
	private Logger logger = LoggerFactory.getLogger( OkrWorkProblemInfoService.class );
	private BeanCopyTools<WrapInOkrWorkProblemInfo, OkrWorkProblemInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrWorkProblemInfo.class, OkrWorkProblemInfo.class, null, WrapInOkrWorkProblemInfo.Excludes );
	/**
	 * 根据传入的ID从数据库查询OkrWorkProblemInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkProblemInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkProblemInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkProblemInfo对象
	 * @param wrapIn
	 */
	public OkrWorkProblemInfo save( WrapInOkrWorkProblemInfo wrapIn ) throws Exception {
		OkrWorkProblemInfo okrWorkProblemInfo = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkProblemInfo =  emc.find( wrapIn.getId(), OkrWorkProblemInfo.class );
				if( okrWorkProblemInfo != null ){
					emc.beginTransaction( OkrWorkProblemInfo.class );
					wrapin_copier.copy( wrapIn, okrWorkProblemInfo );
					emc.check( okrWorkProblemInfo, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkProblemInfo = new OkrWorkProblemInfo();
					emc.beginTransaction( OkrWorkProblemInfo.class );
					wrapin_copier.copy( wrapIn, okrWorkProblemInfo );
					okrWorkProblemInfo.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkProblemInfo, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrWorkProblemInfo update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkProblemInfo = new OkrWorkProblemInfo();
				emc.beginTransaction( OkrWorkProblemInfo.class );
				wrapin_copier.copy( wrapIn, okrWorkProblemInfo );
				emc.persist( okrWorkProblemInfo, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrWorkProblemInfo create got a error!", e);
				throw e;
			}
		}
		return okrWorkProblemInfo;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkProblemInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkProblemInfo okrWorkProblemInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkProblemInfo = emc.find(id, OkrWorkProblemInfo.class);
			if (null == okrWorkProblemInfo) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkProblemInfo.class );
				emc.remove( okrWorkProblemInfo, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据工作信息ID，删除所有的问题请求记录
	 * @param workId
	 * @throws Exception 
	 */
	public void deleteByWorkId( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			logger.error( "workId is null, system can not delete any object." );
		}
		List<String> ids = null;
		Business business = null;
		OkrWorkProblemInfo okrWorkProblemInfo  = null;
		OkrWorkProblemPersonLink okrWorkProblemPersonLink = null;
		OkrWorkProblemProcessLog okrWorkProblemProcessLog  = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			emc.beginTransaction( OkrWorkProblemInfo.class );
			emc.beginTransaction( OkrWorkProblemPersonLink.class );
			emc.beginTransaction( OkrWorkProblemProcessLog.class );
			
			ids = business.okrWorkProblemInfoFactory().listByWorkId( workId );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					okrWorkProblemInfo = business.okrWorkProblemInfoFactory().get(id);
					okrWorkProblemInfo.setStatus( "已删除" );
					emc.check( okrWorkProblemInfo, CheckPersistType.all );
				}
			}
			
			ids = business.okrWorkProblemPersonLinkFactory().listByWorkId( workId );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					okrWorkProblemPersonLink = business.okrWorkProblemPersonLinkFactory().get(id);
					okrWorkProblemPersonLink.setStatus( "已删除" );
					emc.check( okrWorkProblemPersonLink, CheckPersistType.all );
				}
			}
			
			ids = business.okrWorkProblemProcessLogFactory().listByWorkId( workId );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					okrWorkProblemProcessLog = business.okrWorkProblemProcessLogFactory().get(id);
					okrWorkProblemProcessLog.setStatus( "已删除" );
					emc.check( okrWorkProblemProcessLog, CheckPersistType.all );
				}
			}
			
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
