package com.x.okr.assemble.control.service;

import java.util.ArrayList;
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
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapInFilter;
import com.x.okr.entity.OkrWorkReportProcessLog;

/**
 * 类   名：OkrWorkReportProcessLogService<br/>
 * 实体类：OkrWorkReportProcessLog<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportProcessLogService{
	
	private static  Logger logger = LoggerFactory.getLogger( OkrWorkReportProcessLogService.class );

	/**
	 * 根据传入的ID从数据库查询OkrWorkReportProcessLog对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkReportProcessLog get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkReportProcessLog.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkReportProcessLog对象
	 * @param wrapIn
	 */
	public OkrWorkReportProcessLog save( OkrWorkReportProcessLog wrapIn ) throws Exception {
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkReportProcessLog =  emc.find( wrapIn.getId(), OkrWorkReportProcessLog.class );
				if( okrWorkReportProcessLog != null ){
					emc.beginTransaction( OkrWorkReportProcessLog.class );
					wrapIn.copyTo( okrWorkReportProcessLog, JpaObject.FieldsUnmodify );
					emc.check( okrWorkReportProcessLog, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkReportProcessLog = new OkrWorkReportProcessLog();
					emc.beginTransaction( OkrWorkReportProcessLog.class );
					wrapIn.copyTo( okrWorkReportProcessLog );
					okrWorkReportProcessLog.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkReportProcessLog, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrWorkReportProcessLog update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkReportProcessLog = new OkrWorkReportProcessLog();
				emc.beginTransaction( OkrWorkReportProcessLog.class );
				wrapIn.copyTo( okrWorkReportProcessLog );
				emc.persist( okrWorkReportProcessLog, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrWorkReportProcessLog create got a error!", e);
				throw e;
			}
		}
		return okrWorkReportProcessLog;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkReportProcessLog对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkReportProcessLog = emc.find(id, OkrWorkReportProcessLog.class);
			if (null == okrWorkReportProcessLog) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkReportProcessLog.class );
				emc.remove( okrWorkReportProcessLog, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrWorkReportProcessLog> listNextWithFilter( String id, Integer count, WrapInFilter wrapIn) throws Exception {
		Business business = null;
		Object sequence = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = new ArrayList<OkrWorkReportProcessLog>();
		List<String> ids = null;
		if( count == null ){
			count = 20;
		}
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkReportProcessLog.class ),  JpaObject.sequence_FIELDNAME );
				}
			}
			ids = business.okrWorkReportProcessLogFactory().listNextWithFilter( id, count, sequence, wrapIn );
			if( ids != null && !ids.isEmpty() ){
				for( String _id : ids ){
					okrWorkReportProcessLog = emc.find( _id, OkrWorkReportProcessLog.class );
					if( okrWorkReportProcessLogList != null && !okrWorkReportProcessLogList.contains( okrWorkReportProcessLog )){
						okrWorkReportProcessLogList.add( okrWorkReportProcessLog );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return okrWorkReportProcessLogList;
	}

	public Long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportProcessLogFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据汇报ID，环节和处理人查询正在处理的日志，“草稿”
	 * @param id
	 * @param activityName
	 * @param userIdentity
	 * @return
	 * @throws Exception 
	 */
	public List<String> listByReportIdAndProcessor(String reportId, String activityName, String userIdentity, String procrssStatus) throws Exception {
		Business business = null;
		if( reportId == null ){
			throw new Exception( "reportId is null!" );
		}
		if( activityName == null ){
			throw new Exception( "activityName is null!" );
		}
		if( userIdentity == null ){
			throw new Exception( "userIdentity is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportProcessLogFactory().listByReportIdAndProcessor(reportId, activityName, userIdentity, procrssStatus);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrWorkReportProcessLog> list(List<String> ids) throws Exception {
		Business business = null;
		if( ids == null ){
			throw new Exception( "ids is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportProcessLogFactory().list( ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByReportId( String id ) throws Exception {
		Business business = null;
		if( id == null ){
			throw new Exception( "id is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportProcessLogFactory().listIdsByReportId( id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
