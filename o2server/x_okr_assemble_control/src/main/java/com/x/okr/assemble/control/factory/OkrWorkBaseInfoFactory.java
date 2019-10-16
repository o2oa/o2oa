package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapInFilter;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkBaseInfo_;

public class OkrWorkBaseInfoFactory extends AbstractFactory {
	
	public OkrWorkBaseInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe( "获取指定Id的OkrWorkBaseInfo应用信息对象" )
	public OkrWorkBaseInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkBaseInfo.class, ExceptionWhen.none);
	}
	
//	@MethodDescribe( "列示全部的OkrWorkBaseInfo应用信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkBaseInfo> root = cq.from( OkrWorkBaseInfo.class);
		cq.select(root.get(OkrWorkBaseInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrWorkBaseInfo应用信息列表" )
	public List<OkrWorkBaseInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkBaseInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkBaseInfo> cq = cb.createQuery(OkrWorkBaseInfo.class);
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		Predicate p = root.get(OkrWorkBaseInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据工作信息ID，查询该工作信息的所有下级工作ID
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "列示指定Id的OkrWorkBaseInfo应用信息列表" )
	public List<String> getSubNormalWorkBaseInfoIds( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return new ArrayList<String>();
		}
		List<String> status = new ArrayList<>();
		status.add( "正常" );
		status.add( "已归档" );
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		Predicate p = cb.equal( root.get(OkrWorkBaseInfo_.parentWorkId ), id );
		p = cb.and( p, root.get(OkrWorkBaseInfo_.status ).in( status ));
		cq.select(root.get(OkrWorkBaseInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 根据工作信息ID，查询该工作信息的所有下级工作ID
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "列示指定Id的OkrWorkBaseInfo应用信息列表" )
	public List<String> getSubAbnormalWorkBaseInfoIds( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return new ArrayList<String>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		Predicate p = cb.equal( root.get(OkrWorkBaseInfo_.parentWorkId ), id );
		p = cb.and( p, cb.notEqual( root.get(OkrWorkBaseInfo_.status ), "正常" ));
		cq.select(root.get(OkrWorkBaseInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 查询下一页的信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public List<OkrWorkBaseInfo> listNextWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+OkrWorkBaseInfo.class.getCanonicalName()+" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append( " and o."+wrapIn.getSequenceField()+" " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? "<" : ">" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getWorkIds()) && wrapIn.getWorkIds().size() > 0) {
			sql_stringBuffer.append( " and o.id in ( ?" + (index) + " )" );
			vs.add( wrapIn.getWorkIds() );
			index++;
		}
		if ((null != wrapIn.getCreatorNames()) && wrapIn.getCreatorNames().size() > 0) {
			sql_stringBuffer.append( " and o.creatorName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorNames() );
			index++;
		}
		if (null != wrapIn.getCreatorUnitNames() && wrapIn.getCreatorUnitNames().size()>0) {
			sql_stringBuffer.append( " and o.creatorUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorUnitNames() );
			index++;
		}
		if (null != wrapIn.getCreatorTopUnitNames() && wrapIn.getCreatorTopUnitNames().size() > 0 ) {
			sql_stringBuffer.append( " and o.creatorTopUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorTopUnitNames() );
			index++;
		}
		if ((null != wrapIn.getDeployerNames()) && wrapIn.getDeployerNames().size()>0 ) {
			sql_stringBuffer.append( " and o.deployerName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getDeployerNames() );
			index++;
		}
		if (null != wrapIn.getCreatorUnitNames() && wrapIn.getCreatorUnitNames().size()>0) {
			sql_stringBuffer.append( " and o.deployerUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorUnitNames() );
			index++;
		}
		if (null != wrapIn.getCreatorTopUnitNames() && wrapIn.getCreatorTopUnitNames().size() > 0 ) {
			sql_stringBuffer.append( " and o.deployerTopUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorTopUnitNames() );
			index++;
		}
		if (null != wrapIn.getWorkProcessStatusList() && wrapIn.getWorkProcessStatusList().size() > 0 ) {
			sql_stringBuffer.append( " and o.workProcessStatus in ( ?" + (index) + " )" );
			vs.add( wrapIn.getWorkProcessStatusList() );
			index++;
		}
		if (null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.title like ?" + (index) );
			vs.add( "%"+wrapIn.getTitle()+"%" );
			index++;
		}
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0 ) {
			sql_stringBuffer.append( " and o.status in ( ?" + (index) + " )" );
			vs.add( wrapIn.getQ_statuses() );
			index++;
		}
		sql_stringBuffer.append( " order by o." + wrapIn.getSequenceField() + " " + ( StringUtils.equalsIgnoreCase( order, "DESC" ) ? "DESC" : "ASC" ) );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkBaseInfo.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults(count).getResultList();
	}	
	
	/**
	 * 查询上一页的信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public List<OkrWorkBaseInfo> listPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM " + OkrWorkBaseInfo.class.getCanonicalName()+" o where 1=1" );
		if ((null != sequence) ) {
			sql_stringBuffer.append( " and o."+wrapIn.getSequenceField()+" " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? ">" : "<" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getWorkIds()) && wrapIn.getWorkIds().size() > 0) {
			sql_stringBuffer.append( " and o.id in ( ?" + (index) + " )" );
			vs.add( wrapIn.getWorkIds() );
			index++;
		}
		if ((null != wrapIn.getCreatorNames()) && wrapIn.getCreatorNames().size() > 0) {
			sql_stringBuffer.append( " and o.creatorName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorNames() );
			index++;
		}
		if (null != wrapIn.getCreatorUnitNames() && wrapIn.getCreatorUnitNames().size()>0) {
			sql_stringBuffer.append( " and o.creatorUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorUnitNames() );
			index++;
		}
		if (null != wrapIn.getCreatorTopUnitNames() && wrapIn.getCreatorTopUnitNames().size() > 0 ) {
			sql_stringBuffer.append( " and o.creatorTopUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorTopUnitNames() );
			index++;
		}
		if ((null != wrapIn.getDeployerNames()) && wrapIn.getDeployerNames().size()>0 ) {
			sql_stringBuffer.append( " and o.deployerName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getDeployerNames() );
			index++;
		}
		if (null != wrapIn.getCreatorUnitNames() && wrapIn.getCreatorUnitNames().size()>0) {
			sql_stringBuffer.append( " and o.deployerUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorUnitNames() );
			index++;
		}
		if (null != wrapIn.getCreatorTopUnitNames() && wrapIn.getCreatorTopUnitNames().size() > 0 ) {
			sql_stringBuffer.append( " and o.deployerTopUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorTopUnitNames() );
			index++;
		}
		if (null != wrapIn.getWorkProcessStatusList() && wrapIn.getWorkProcessStatusList().size() > 0 ) {
			sql_stringBuffer.append( " and o.workProcessStatus in ( ?" + (index) + " )" );
			vs.add( wrapIn.getWorkProcessStatusList() );
			index++;
		}
		if (null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.title like ?" + (index) );
			vs.add( "%"+wrapIn.getTitle()+"%" );
			index++;
		}
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0 ) {
			sql_stringBuffer.append( " and o.status in ( ?" + (index) + " )" );
			vs.add( wrapIn.getQ_statuses() );
			index++;
		}
		sql_stringBuffer.append( " order by o." + wrapIn.getSequenceField() + " " + ( StringUtils.equalsIgnoreCase( order, "DESC" ) ? "DESC" : "ASC" ) );
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkBaseInfo.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		
		return query.setMaxResults(count).getResultList();
	}
	
	/**
	 * 查询符合的信息总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+OkrWorkBaseInfo.class.getCanonicalName()+" o where 1=1" );
		
		if ((null != wrapIn.getWorkIds()) && wrapIn.getWorkIds().size() > 0) {
			sql_stringBuffer.append( " and o.id in ( ?" + (index) + " )" );
			vs.add( wrapIn.getWorkIds() );
			index++;
		}
		if ((null != wrapIn.getCreatorNames()) && wrapIn.getCreatorNames().size() > 0) {
			sql_stringBuffer.append( " and o.creatorName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorNames() );
			index++;
		}
		if (null != wrapIn.getCreatorUnitNames() && wrapIn.getCreatorUnitNames().size()>0) {
			sql_stringBuffer.append( " and o.creatorUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorUnitNames() );
			index++;
		}
		if (null != wrapIn.getCreatorTopUnitNames() && wrapIn.getCreatorTopUnitNames().size() > 0 ) {
			sql_stringBuffer.append( " and o.creatorTopUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorTopUnitNames() );
			index++;
		}
		if ((null != wrapIn.getDeployerNames()) && wrapIn.getDeployerNames().size()>0 ) {
			sql_stringBuffer.append( " and o.deployerName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getDeployerNames() );
			index++;
		}
		if (null != wrapIn.getCreatorUnitNames() && wrapIn.getCreatorUnitNames().size()>0) {
			sql_stringBuffer.append( " and o.deployerUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorUnitNames() );
			index++;
		}
		if (null != wrapIn.getCreatorTopUnitNames() && wrapIn.getCreatorTopUnitNames().size() > 0 ) {
			sql_stringBuffer.append( " and o.deployerTopUnitName in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCreatorTopUnitNames() );
			index++;
		}
		if (null != wrapIn.getWorkProcessStatusList() && wrapIn.getWorkProcessStatusList().size() > 0 ) {
			sql_stringBuffer.append( " and o.workProcessStatus in ( ?" + (index) + " )" );
			vs.add( wrapIn.getWorkProcessStatusList() );
			index++;
		}
		if (null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.title like ?" + (index) );
			vs.add( "%"+wrapIn.getTitle()+"%" );
			index++;
		}
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0 ) {
			sql_stringBuffer.append( " and o.status in ( ?" + (index) + " )" );
			vs.add( wrapIn.getQ_statuses() );
			index++;
		}
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrWorkBaseInfo.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}

	//@MethodDescribe( "在IDS范围内，查询所有状态正常并且待确认的工作" )
	public List<String> listUnConfirmWorkIdInIds(List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			throw new Exception( "ids is null" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		Predicate p = root.get(OkrWorkBaseInfo_.id).in(ids);
		p = cb.and( p, cb.equal( root.get(OkrWorkBaseInfo_.workProcessStatus ), "待确认" ));
		p = cb.and( p, cb.equal( root.get(OkrWorkBaseInfo_.status ), "正常" ));
		cq.select(root.get(OkrWorkBaseInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId( String centerId, List<String> statuses ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< String > cq = cb.createQuery( String.class );
		Root< OkrWorkBaseInfo > root = cq.from( OkrWorkBaseInfo.class );
		Predicate p = cb.equal( root.get( OkrWorkBaseInfo_.centerId ), centerId );
		if( statuses != null && statuses.size() > 0 ){
			p = cb.and( p, root.get( OkrWorkBaseInfo_.status ).in( statuses ));
		}
		cq.select( root.get( OkrWorkBaseInfo_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 根据中心工作ID和有权限访问的工作ID列表来查询工作信息列表
	 * @param centerId
	 * @param workIds
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "根据中心工作ID和有权限访问的工作ID列表来查询工作信息列表" )
	public List<OkrWorkBaseInfo> listWorkByCenterId( String centerId, List<String> workIds, List<String> statuses) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< OkrWorkBaseInfo > cq = cb.createQuery( OkrWorkBaseInfo.class );
		Root< OkrWorkBaseInfo > root = cq.from( OkrWorkBaseInfo.class );
		Predicate p = cb.equal( root.get( OkrWorkBaseInfo_.centerId ), centerId );
		if( workIds != null && workIds.size() > 0 ){
			p = cb.and( p, root.get( OkrWorkBaseInfo_.id ).in( workIds ));
		}
		if( statuses != null && statuses.size() > 0 ){
			p = cb.and( p, root.get( OkrWorkBaseInfo_.status ).in( statuses ));
		}
		return em.createQuery( cq.where(p) ).getResultList();
	}

	//@MethodDescribe( "查询需要立即进行工作汇报的工作ID列表" )
	public List<String> listNeedReportWorkIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkBaseInfo> root = cq.from( OkrWorkBaseInfo.class);
		cq.select( root.get( OkrWorkBaseInfo_.id ) );
		Predicate p = cb.isNotNull( root.get( OkrWorkBaseInfo_.nextReportTime) );
		//下一次汇报时间早于当前时间
		p = cb.and( p, cb.lessThanOrEqualTo(root.get( OkrWorkBaseInfo_.nextReportTime ), new Date()));
		//未完成的工作
		p = cb.and( p, cb.isFalse( root.get( OkrWorkBaseInfo_.isCompleted ) ));
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.status ), "正常" ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByParentId(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception( " id is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< String > cq = cb.createQuery( String.class );
		Root< OkrWorkBaseInfo > root = cq.from( OkrWorkBaseInfo.class );
		Predicate p = cb.equal( root.get( OkrWorkBaseInfo_.parentWorkId ), id );
		cq.select( root.get( OkrWorkBaseInfo_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 根据分析时间来查询需要进行进展分析的工作ID列表
	 * @param report_progress
	 * @param count
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "根据分析时间来查询需要进行进展分析的工作ID列表" )
	public List<String> listIdsForNeedProgressAnalyse( String report_progress, int count ) throws Exception {
		if( report_progress == null || report_progress.isEmpty() ){
			throw new Exception( "report_progress is null." );
		}
		if( count == 0 ){
			throw new Exception( "count is 0." );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< String > cq = cb.createQuery( String.class );
		Root< OkrWorkBaseInfo > root = cq.from( OkrWorkBaseInfo.class );
		Predicate p = cb.isFalse( root.get( OkrWorkBaseInfo_.isCompleted ) );
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.workProcessStatus ), "执行中" ) );
		Predicate p1 = cb.isNull( root.get( OkrWorkBaseInfo_.progressAnalyseTime ) );		
		p1 = cb.or( p1, cb.notEqual( root.get( OkrWorkBaseInfo_.progressAnalyseTime ), report_progress ));		
		p = cb.and( p, p1 );
		cq.select( root.get( OkrWorkBaseInfo_.id ) );
		return em.createQuery( cq.where(p) ).setMaxResults( count ).getResultList();
	}

	public Long getProcessingWorkCountByCenterId( String centerId, List<String> status ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		if( status == null || status.isEmpty() ){
			throw new Exception( "status is null." );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkBaseInfo> root = cq.from( OkrWorkBaseInfo.class);
		Predicate p = root.get( OkrWorkBaseInfo_.status ).in( status );
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.centerId ), centerId));
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.workProcessStatus ), "执行中"));
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getWorkTotalByCenterId( String centerId, List<String> status ) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		if( status == null || status.isEmpty() ){
			throw new Exception( "status is null." );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkBaseInfo> root = cq.from( OkrWorkBaseInfo.class);
		Predicate p = root.get( OkrWorkBaseInfo_.status ).in( status );
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.centerId ), centerId));
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getCompletedWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		if( status == null || status.isEmpty() ){
			throw new Exception( "status is null." );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkBaseInfo> root = cq.from( OkrWorkBaseInfo.class);
		Predicate p = root.get( OkrWorkBaseInfo_.status ).in( status );
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.centerId ), centerId));
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.workProcessStatus ), "已完成"));
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getOvertimeWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		if( status == null || status.isEmpty() ){
			throw new Exception( "status is null." );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkBaseInfo> root = cq.from( OkrWorkBaseInfo.class);
		Predicate p = root.get( OkrWorkBaseInfo_.status ).in( status );
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.centerId ), centerId));
		p = cb.and( p, cb.isTrue( root.get( OkrWorkBaseInfo_.isOverTime )));
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getDraftWorkCountByCenterId(String centerId, List<String> status) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null." );
		}
		if( status == null || status.isEmpty() ){
			throw new Exception( "status is null." );
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<OkrWorkBaseInfo> root = cq.from( OkrWorkBaseInfo.class);
		Predicate p = root.get( OkrWorkBaseInfo_.status ).in( status );
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.centerId ), centerId));
		p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.workProcessStatus ), "草稿"));
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<OkrWorkBaseInfo> listAllProcessingWorks() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkBaseInfo> cq = cb.createQuery(OkrWorkBaseInfo.class);
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		Predicate p = cb.equal( root.get(OkrWorkBaseInfo_.status ), "正常");
		p = cb.and( p, cb.equal( root.get(OkrWorkBaseInfo_.workProcessStatus ), "执行中" ) );
		p = cb.and( p, cb.lessThan( root.get(OkrWorkBaseInfo_.overallProgress ), 100 ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listAllProcessingWorkIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		Predicate p = cb.equal( root.get(OkrWorkBaseInfo_.status ), "正常");
		p = cb.and( p, cb.equal( root.get(OkrWorkBaseInfo_.workProcessStatus ), "执行中" ) );
		p = cb.and( p, cb.lessThan( root.get(OkrWorkBaseInfo_.overallProgress ), 100 ) );
		cq.select( root.get( OkrWorkBaseInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<OkrWorkBaseInfo> listAllDeployedWorks( String centerId, String status ) throws Exception {
		List<String> processStatus = new ArrayList<String>();
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkBaseInfo> cq = cb.createQuery(OkrWorkBaseInfo.class);
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		processStatus.add( "执行中" );
		processStatus.add( "已完成" );
		Predicate p = root.get(OkrWorkBaseInfo_.workProcessStatus ).in( processStatus );
		if( status != null && !status.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrWorkBaseInfo_.status ), status ) );
		}
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrWorkBaseInfo_.centerId ), centerId ) );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listAllDeployedWorkIds( String centerId, String status ) throws Exception {
		List<String> processStatus = new ArrayList<String>();
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		processStatus.add( "执行中" );
		processStatus.add( "已完成" );
		Predicate p = root.get( OkrWorkBaseInfo_.workProcessStatus ).in( processStatus );
		if( status != null && !"All".equals( status )) {
			p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.status ), status ) );
		}
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.centerId ), centerId ) );
		}
		cq.select( root.get( OkrWorkBaseInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 查询具体工作协助者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctCooperateIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		/*
		CriteriaQuery<List> cq = cb.createQuery( List.class );
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		cq.select(root.get( OkrWorkBaseInfo_.cooperateIdentityList ));
		List<List> allList = em.createQuery(cq).getResultList();
		*/
		
		CriteriaQuery<OkrWorkBaseInfo> cq = cb.createQuery( OkrWorkBaseInfo.class );
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);		
		List<OkrWorkBaseInfo> os = em.createQuery(cq.select(root)).getResultList();
		List<List> allList = new ArrayList<>();
		for (OkrWorkBaseInfo o : os) {
			allList.add(o.getCooperateIdentityList());
		}
		
		
		if(ListTools.isNotEmpty( allList )) {
			HashSet hashSet = new  HashSet();
			for( List<String> identities : allList ) {
				if(ListTools.isNotEmpty( identities )) {
					for( String identity : identities ) {
						if( ListTools.isNotEmpty(identities_ok) && identities_ok.contains( identity ) ){
							continue;
						}
						if( ListTools.isNotEmpty(identities_error) && identities_error.contains( identity ) ){
							continue;
						}
						hashSet.add( identity );
					}
				}
			}
			List<String> result = new ArrayList<>();
			result.addAll(hashSet);
			return result;
		}
		return null;
	}
	
	/**
	 * 查询具体工作创建者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctCreatorIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		
		Predicate p = cb.isNotNull( root.get( OkrWorkBaseInfo_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkBaseInfo_.creatorIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkBaseInfo_.creatorIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkBaseInfo_.creatorIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 查询具体工作部署者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctDeployerIdentity( List<String> identities_ok, List<String> identities_error ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		
		Predicate p = cb.isNotNull( root.get( OkrWorkBaseInfo_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkBaseInfo_.deployerIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkBaseInfo_.deployerIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkBaseInfo_.deployerIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 查询具体工作阅知领导身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctReportLeaderIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		/*
		CriteriaQuery<List> cq = cb.createQuery( List.class );		
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		cq.select(root.get( OkrWorkBaseInfo_.readLeaderIdentityList ));
		List<List> allList = em.createQuery(cq).getResultList();
		*/
		
		CriteriaQuery<OkrWorkBaseInfo> cq = cb.createQuery( OkrWorkBaseInfo.class );
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);		
		List<OkrWorkBaseInfo> os = em.createQuery(cq.select(root)).getResultList();
		List<List> allList = new ArrayList<>();
		for (OkrWorkBaseInfo o : os) {
			allList.add(o.getReadLeaderIdentityList());
		}
		
		if(ListTools.isNotEmpty( allList )) {
			HashSet hashSet = new  HashSet();
			for( List<String> identities : allList ) {
				if(ListTools.isNotEmpty( identities )) {
					for( String identity : identities ) {
						if( ListTools.isNotEmpty(identities_ok) && identities_ok.contains( identity ) ){
							continue;
						}
						if( ListTools.isNotEmpty(identities_error) && identities_error.contains( identity ) ){
							continue;
						}
						hashSet.add( identity );
					}
				}
			}
			List<String> result = new ArrayList<>();
			result.addAll(hashSet);
			return result;
		}
		return null;
	}
	/**
	 * 查询具体工作阅知领导身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctResponsibilityIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrWorkBaseInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrWorkBaseInfo> root = cq.from(OkrWorkBaseInfo.class);
		
		Predicate p = cb.isNotNull( root.get( OkrWorkBaseInfo_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkBaseInfo_.responsibilityIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrWorkBaseInfo_.responsibilityIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrWorkBaseInfo_.responsibilityIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据身份名称，从具体工作信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkBaseInfo> listErrorIdentitiesInWorkBaseInfo(String identity, String recordId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkBaseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkBaseInfo> cq = cb.createQuery( OkrWorkBaseInfo.class );
		Root<OkrWorkBaseInfo> root = cq.from( OkrWorkBaseInfo.class );
		Predicate p = cb.isNotNull(root.get( OkrWorkBaseInfo_.id ));
		
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrWorkBaseInfo_.id ), recordId ) );
		}
		
		Predicate p_creatorIdentity = cb.isNotNull(root.get( OkrWorkBaseInfo_.creatorIdentity ));
		p_creatorIdentity = cb.and( p_creatorIdentity, cb.equal( root.get( OkrWorkBaseInfo_.creatorIdentity ), identity ) );
		
		Predicate p_deployerIdentity = cb.isNotNull(root.get( OkrWorkBaseInfo_.deployerIdentity ));
		p_deployerIdentity = cb.and( p_deployerIdentity, cb.equal( root.get( OkrWorkBaseInfo_.deployerIdentity ), identity ) );
		
		Predicate p_reportAdminIdentity = cb.isNotNull(root.get( OkrWorkBaseInfo_.reportAdminIdentity ));
		p_reportAdminIdentity = cb.and( p_reportAdminIdentity, cb.equal( root.get( OkrWorkBaseInfo_.reportAdminIdentity ), identity ) );
		
		Predicate p_responsibilityIdentity = cb.isNotNull(root.get( OkrWorkBaseInfo_.responsibilityIdentity ));
		p_responsibilityIdentity = cb.and( p_responsibilityIdentity, cb.equal( root.get( OkrWorkBaseInfo_.responsibilityIdentity ), identity ) );
		
		Predicate p_cooperateIdentity = cb.isNotNull(root.get( OkrWorkBaseInfo_.cooperateIdentityList ));
		p_cooperateIdentity = cb.and( p_cooperateIdentity, cb.isMember( identity, root.get( OkrWorkBaseInfo_.cooperateIdentityList )) );
		
		Predicate p_readLeaderIdentity = cb.isNotNull(root.get( OkrWorkBaseInfo_.readLeaderIdentityList ));
		p_readLeaderIdentity = cb.and( p_readLeaderIdentity, cb.isMember( identity, root.get( OkrWorkBaseInfo_.readLeaderIdentityList ) ) );
		
		Predicate p_identity = cb.or( p_creatorIdentity, p_deployerIdentity, p_reportAdminIdentity, p_responsibilityIdentity, p_cooperateIdentity, p_readLeaderIdentity );
			
		p = cb.and( p, p_identity );
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
}