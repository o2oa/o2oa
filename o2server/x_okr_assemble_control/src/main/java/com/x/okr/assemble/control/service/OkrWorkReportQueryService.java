package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapInFilter;
import com.x.okr.entity.OkrWorkReportBaseInfo;

/**
 * 类   名：OkrWorkReportBaseInfoService<br/>
 * 实体类：OkrWorkReportBaseInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportQueryService{

	/**
	 * 根据传入的ID从数据库查询OkrWorkReportBaseInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkReportBaseInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkReportBaseInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	
	

	/**
	 * 根据工作ID，查询该工作的最大汇报次序
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Integer getMaxReportCount( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().getMaxReportCount( workId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据工作ID查询所有汇报信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public List<String> listByWorkId( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().listByWorkId( workId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据工作ID查询所有正在流转中的汇报信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public List<String> listProcessingReportIdsByWorkId( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().listProcessingReportIdsByWorkId( workId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据汇报信息ID列表，查询汇报信息对象
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportBaseInfo> listByIds(List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().list(ids);
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
	public List<OkrWorkReportBaseInfo> listNextWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = new ArrayList<OkrWorkReportBaseInfo>();
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
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkReportBaseInfo.class ),  JpaObject.sequence_FIELDNAME );
				}
			}
			okrWorkReportBaseInfoList = business.okrWorkReportBaseInfoFactory().listNextWithFilter( id, count, sequence, wrapIn );
		} catch ( Exception e ) {
			throw e;
		}
		return okrWorkReportBaseInfoList;
	}
	
	/**
	 * 上一页
	 * @param id
	 * @param count
	 * @param wrapIn
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkReportBaseInfo> listPrevWithFilter( String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = new ArrayList<OkrWorkReportBaseInfo>();
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
					sequence = PropertyUtils.getProperty( emc.find( id, OkrWorkReportBaseInfo.class ),  JpaObject.sequence_FIELDNAME );
				}
			}
			okrWorkReportBaseInfoList = business.okrWorkReportBaseInfoFactory().listPrevWithFilter( id, count, sequence, wrapIn );
		} catch ( Exception e ) {
			throw e;
		}
		return okrWorkReportBaseInfoList;
	}

	public Long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据工作ID获取该工作最后一次工作汇报
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public OkrWorkReportBaseInfo getLastReportBaseInfo(String workId) throws Exception {
		Business business = null;
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkReportBaseInfoFactory().getLastReportBaseInfo( workId );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
