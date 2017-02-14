package com.x.okr.assemble.control.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.control.jaxrs.okrworkproblemprocesslog.WrapInOkrWorkProblemProcessLog;
import com.x.okr.entity.OkrWorkProblemProcessLog;

/**
 * 类   名：OkrWorkProblemProcessLogService<br/>
 * 实体类：OkrWorkProblemProcessLog<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkProblemProcessLogService{
	private Logger logger = LoggerFactory.getLogger( OkrWorkProblemProcessLogService.class );
	private BeanCopyTools<WrapInOkrWorkProblemProcessLog, OkrWorkProblemProcessLog> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrWorkProblemProcessLog.class, OkrWorkProblemProcessLog.class, null, WrapInOkrWorkProblemProcessLog.Excludes );
	/**
	 * 根据传入的ID从数据库查询OkrWorkProblemProcessLog对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkProblemProcessLog get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkProblemProcessLog.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkProblemProcessLog对象
	 * @param wrapIn
	 */
	public OkrWorkProblemProcessLog save( WrapInOkrWorkProblemProcessLog wrapIn ) throws Exception {
		OkrWorkProblemProcessLog okrWorkProblemProcessLog = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkProblemProcessLog =  emc.find( wrapIn.getId(), OkrWorkProblemProcessLog.class );
				if( okrWorkProblemProcessLog != null ){
					emc.beginTransaction( OkrWorkProblemProcessLog.class );
					wrapin_copier.copy( wrapIn, okrWorkProblemProcessLog );
					emc.check( okrWorkProblemProcessLog, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkProblemProcessLog = new OkrWorkProblemProcessLog();
					emc.beginTransaction( OkrWorkProblemProcessLog.class );
					wrapin_copier.copy( wrapIn, okrWorkProblemProcessLog );
					okrWorkProblemProcessLog.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkProblemProcessLog, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrWorkProblemProcessLog update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkProblemProcessLog = new OkrWorkProblemProcessLog();
				emc.beginTransaction( OkrWorkProblemProcessLog.class );
				wrapin_copier.copy( wrapIn, okrWorkProblemProcessLog );
				emc.persist( okrWorkProblemProcessLog, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrWorkProblemProcessLog create got a error!", e);
				throw e;
			}
		}
		return okrWorkProblemProcessLog;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkProblemProcessLog对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkProblemProcessLog okrWorkProblemProcessLog = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkProblemProcessLog = emc.find(id, OkrWorkProblemProcessLog.class);
			if (null == okrWorkProblemProcessLog) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkProblemProcessLog.class );
				emc.remove( okrWorkProblemProcessLog, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
