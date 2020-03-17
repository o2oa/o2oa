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
import com.x.okr.assemble.control.jaxrs.WorkPersonSearchFilter;
import com.x.okr.entity.OkrWorkReportPersonLink;

/**
 * 类   名：OkrWorkReportPersonLinkService<br/>
 * 实体类：OkrWorkReportPersonLink<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportPersonLinkService{
	private static  Logger logger = LoggerFactory.getLogger( OkrWorkReportPersonLinkService.class );
	
	/**
	 * 根据传入的ID从数据库查询OkrWorkReportPersonLink对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkReportPersonLink get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkReportPersonLink.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkReportPersonLink对象
	 * @param wrapIn
	 */
	public OkrWorkReportPersonLink save( OkrWorkReportPersonLink wrapIn ) throws Exception {
		OkrWorkReportPersonLink okrWorkReportPersonLink = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkReportPersonLink =  emc.find( wrapIn.getId(), OkrWorkReportPersonLink.class );
				if( okrWorkReportPersonLink != null ){
					emc.beginTransaction( OkrWorkReportPersonLink.class );
					wrapIn.copyTo( okrWorkReportPersonLink, JpaObject.FieldsUnmodify );
					emc.check( okrWorkReportPersonLink, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkReportPersonLink = new OkrWorkReportPersonLink();
					emc.beginTransaction( OkrWorkReportPersonLink.class );
					wrapIn.copyTo( okrWorkReportPersonLink );
					okrWorkReportPersonLink.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkReportPersonLink, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "OkrWorkReportPersonLink update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkReportPersonLink = new OkrWorkReportPersonLink();
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				wrapIn.copyTo( okrWorkReportPersonLink );
				emc.persist( okrWorkReportPersonLink, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "OkrWorkReportPersonLink create got a error!" );
				throw e;
			}
		}
		return okrWorkReportPersonLink;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkReportPersonLink对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkReportPersonLink okrWorkReportPersonLink = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkReportPersonLink = emc.find(id, OkrWorkReportPersonLink.class);
			if (null == okrWorkReportPersonLink) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkReportPersonLink.class );
				emc.remove( okrWorkReportPersonLink, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 根据汇报ID获取该汇报的最高处理过程级别
	 * @param reportId
	 * @return
	 * @throws Exception 
	 */
	public Integer getMaxProcessLevel( String reportId ) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception( "reportId is null, system can not get maxProcessLevel for report." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			return business.okrWorkReportPersonLinkFactory().getMaxProcessLevel(reportId);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 列示指定Id的OkrWorkReportPersonLink实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkReportPersonLink> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			throw new Exception( "ids is null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			return business.okrWorkReportPersonLinkFactory().list(ids);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据汇报ID和处理等级来查询指定等级的处理人
	 * @param id
	 * @param processLevel
	 * @return
	 * @throws Exception 
	 */
	public List<String> getProcessPersonLinkInfoByReportAndLevel(String reportId, Integer processLevel, String processorIdentity, String processStatus, String status ) throws Exception {
		if( reportId == null || reportId.isEmpty() ){
			throw new Exception( "reportId is null." );
		}
		if( processLevel == null || processLevel < 0 ){
			throw new Exception( "processLevel is invalid, processLevel = "+ processLevel );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			return business.okrWorkReportPersonLinkFactory().getProcessPersonLinkInfoByReportAndLevel( reportId, processLevel, processorIdentity, processStatus, status );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 下一页
	 * @param id
	 * @param count
	 * @param wrapIn
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportPersonLink> listNextWithFilter( String id, Integer count, WorkPersonSearchFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		List<OkrWorkReportPersonLink> okrWorkReportPersonLinkList = new ArrayList<OkrWorkReportPersonLink>();
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
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkReportPersonLink.class ),  JpaObject.sequence_FIELDNAME );
				}
			}
			okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().listNextWithFilter( id, count, sequence, wrapIn );
		} catch ( Exception e ) {
			throw e;
		}
		return okrWorkReportPersonLinkList;
	}
	
	/**
	 * 上一页
	 * @param id
	 * @param count
	 * @param wrapIn
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportPersonLink> listPrevWithFilter( String id, Integer count, WorkPersonSearchFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		List<OkrWorkReportPersonLink> okrWorkReportPersonLinkList = new ArrayList<OkrWorkReportPersonLink>();
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
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkReportPersonLink.class ),  JpaObject.sequence_FIELDNAME );
				}
			}
			okrWorkReportPersonLinkList = business.okrWorkReportPersonLinkFactory().listPrevWithFilter( id, count, sequence, wrapIn );
		} catch ( Exception e ) {
			throw e;
		}
		return okrWorkReportPersonLinkList;
	}

	public Long getCountWithFilter( WorkPersonSearchFilter wrapIn ) throws Exception {
		Business business = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportPersonLinkFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
