package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
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
import com.x.okr.assemble.control.jaxrs.WorkCommonQueryFilter;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrCenterWorkInfo_;

public class OkrCenterWorkInfoFactory extends AbstractFactory {
	
	public OkrCenterWorkInfoFactory( Business business ) throws Exception {
		super(business);
	}

	//@MethodDescribe( "获取指定Id的OkrCenterWorkInfo应用信息对象" )
	public OkrCenterWorkInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrCenterWorkInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrCenterWorkInfo应用信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrCenterWorkInfo> root = cq.from( OkrCenterWorkInfo.class);
		cq.select(root.get(OkrCenterWorkInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrCenterWorkInfo应用信息列表" )
	public List<OkrCenterWorkInfo> list( List<String> ids, List<String> statuses ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrCenterWorkInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrCenterWorkInfo> cq = cb.createQuery(OkrCenterWorkInfo.class);
		Root<OkrCenterWorkInfo> root = cq.from(OkrCenterWorkInfo.class);
		Predicate p = root.get(OkrCenterWorkInfo_.id).in(ids);
		if( statuses != null && statuses.size() > 0 ){
			p = cb.and( p, root.get(OkrCenterWorkInfo_.status ).in(statuses) );
		}
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
	public List<OkrCenterWorkInfo> listNextWithFilter( String id, Integer count, Object sequence, WorkCommonQueryFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrCenterWorkInfo.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+OkrCenterWorkInfo.class.getCanonicalName()+" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append( " and o." + wrapIn.getSequenceField()+" " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? "<" : ">" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getDefaultWorkTypes()) && wrapIn.getDefaultWorkTypes().size() > 0) {
			sql_stringBuffer.append( " and o.defaultWorkType in ( ?" + (index) + " )" );
			vs.add( wrapIn.getDefaultWorkTypes() );
			index++;
		}
		if ((null != wrapIn.getCenterIds()) && wrapIn.getCenterIds().size() > 0) {
			sql_stringBuffer.append( " and o.id in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCenterIds() );
			index++;
		}
		
		if (null != wrapIn.getProcessStatusList() && wrapIn.getProcessStatusList().size() > 0 ) {
			sql_stringBuffer.append( " and o.processStatus in ( ?" + (index) + " )" );
			vs.add( wrapIn.getProcessStatusList() );
			index++;
		}
		if (null != wrapIn.getTitle() && !wrapIn.getTitle().isEmpty() ) {
			sql_stringBuffer.append( " and o.title like ?" + (index) );
			vs.add( "%"+wrapIn.getTitle()+"%" );
			index++;
		}
		if (null != wrapIn.getQ_statuses() && wrapIn.getQ_statuses().size() > 0 ) {
			sql_stringBuffer.append( " and o.status in (?" + (index) + " )" );
			vs.add( wrapIn.getQ_statuses() );
			index++;
		}
		
		sql_stringBuffer.append( " order by o." + wrapIn.getSequenceField() + " " + ( StringUtils.equalsIgnoreCase( order, "DESC" ) ? "DESC" : "ASC" ) );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrCenterWorkInfo.class );

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
	public List<OkrCenterWorkInfo> listPrevWithFilter( String id, Integer count, Object sequence, WorkCommonQueryFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrCenterWorkInfo.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM " + OkrCenterWorkInfo.class.getCanonicalName()+" o where 1=1" );
		if ((null != sequence) ) {
			sql_stringBuffer.append( " and o."+wrapIn.getSequenceField()+" " + (StringUtils.equalsIgnoreCase(order, "DESC" ) ? ">" : "<" ) + ( " ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getDefaultWorkTypes()) && wrapIn.getDefaultWorkTypes().size() > 0) {
			sql_stringBuffer.append( " and o.defaultWorkType in ( ?" + (index) + " )" );
			vs.add( wrapIn.getDefaultWorkTypes() );
			index++;
		}
		if ((null != wrapIn.getCenterIds()) && wrapIn.getCenterIds().size() > 0) {
			sql_stringBuffer.append( " and o.id in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCenterIds() );
			index++;
		}

		if (null != wrapIn.getProcessStatusList() && wrapIn.getProcessStatusList().size() > 0 ) {
			sql_stringBuffer.append( " and o.processStatus in ( ?" + (index) + " )" );
			vs.add( wrapIn.getProcessStatusList() );
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
		//logger.debug( "listIdsNextWithFilter:[" +sql_stringBuffer.toString()+ "]" );
		//logger.debug(vs);
		
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrCenterWorkInfo.class );
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
	public long getCountWithFilter( WorkCommonQueryFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( OkrCenterWorkInfo.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+OkrCenterWorkInfo.class.getCanonicalName()+" o where 1=1" );
		
		if ((null != wrapIn.getCenterIds()) && wrapIn.getCenterIds().size() > 0) {
			sql_stringBuffer.append( " and o.id in ( ?" + (index) + " )" );
			vs.add( wrapIn.getCenterIds() );
			index++;
		}
		
		if (null != wrapIn.getProcessStatusList() && wrapIn.getProcessStatusList().size() > 0 ) {
			sql_stringBuffer.append( " and o.processStatus in ( ?" + (index) + " )" );
			vs.add( wrapIn.getProcessStatusList() );
			index++;
		}
		
		if ((null != wrapIn.getDefaultWorkTypes()) && wrapIn.getDefaultWorkTypes().size() > 0) {
			sql_stringBuffer.append( " and o.defaultWorkType in ( ?" + (index) + " )" );
			vs.add( wrapIn.getDefaultWorkTypes() );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), OkrCenterWorkInfo.class );

		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}

	public List<String> listAllProcessingCenterWorkIds( List<String> processStatus, List<String> status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrCenterWorkInfo> root = cq.from(OkrCenterWorkInfo.class);
		cq.select(root.get( OkrCenterWorkInfo_.id ));
		Predicate p = root.get(OkrCenterWorkInfo_.processStatus).in( processStatus );
		if( status != null && status.size() > 0 ){
			p = cb.and( p, root.get( OkrCenterWorkInfo_.status ).in( status ) );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<OkrCenterWorkInfo> listAllCenterWorks( String status ) throws Exception {
		if( status == null || status.isEmpty() ){
			throw new Exception("status is null!");
		}
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrCenterWorkInfo> cq = cb.createQuery(OkrCenterWorkInfo.class);
		Root<OkrCenterWorkInfo> root = cq.from(OkrCenterWorkInfo.class);
		Predicate p = cb.equal( root.get( OkrCenterWorkInfo_.status ), status );
		
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 查询中心工作审核领导身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> listAllDistinctAuditLeaderIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrCenterWorkInfo> cq = cb.createQuery( OkrCenterWorkInfo.class );
		
		Root<OkrCenterWorkInfo> root = cq.from(OkrCenterWorkInfo.class);
//		cq.select(root.get( OkrCenterWorkInfo_.reportAuditLeaderIdentityList ));
		List<OkrCenterWorkInfo> centerWorkInfos = em.createQuery(cq).getResultList();
		//ListTools.trim(list, ignoreNull, unique, ts);
		if(ListTools.isNotEmpty( centerWorkInfos )) {
			HashSet hashSet = new  HashSet();
			for(OkrCenterWorkInfo centerWorkInfo : centerWorkInfos ) {
				
				if( ListTools.isNotEmpty( centerWorkInfo.getReportAuditLeaderIdentityList() ) ) {
					for( String identity : centerWorkInfo.getReportAuditLeaderIdentityList() ) {
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
	 * 查询中心工作创建者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctCreatorIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrCenterWorkInfo> root = cq.from(OkrCenterWorkInfo.class);
		
		Predicate p = cb.isNotNull( root.get( OkrCenterWorkInfo_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrCenterWorkInfo_.creatorIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrCenterWorkInfo_.creatorIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrCenterWorkInfo_.creatorIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 查询中心工作部署者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctDeployerIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrCenterWorkInfo> root = cq.from(OkrCenterWorkInfo.class);
		
		Predicate p = cb.isNotNull( root.get( OkrCenterWorkInfo_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrCenterWorkInfo_.deployerIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrCenterWorkInfo_.deployerIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrCenterWorkInfo_.deployerIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 查询中心工作汇报审核领导身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> listAllDistinctReportAuditLeaderIdentity(List<String> identities_ok,
			List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrCenterWorkInfo> cq = cb.createQuery(OkrCenterWorkInfo.class);
		Root<OkrCenterWorkInfo> root = cq.from(OkrCenterWorkInfo.class);
		List<OkrCenterWorkInfo> os = em.createQuery(cq.select(root)).getResultList();
		List<List> allList = new ArrayList<>();
		for (OkrCenterWorkInfo o : os) {
			allList.add(o.getReportAuditLeaderIdentityList());
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
	 * 根据身份名称，从中心工作信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception
	 */
	public List<OkrCenterWorkInfo> listErrorIdentitiesInCenterInfo( String identity, String recordId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrCenterWorkInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrCenterWorkInfo> cq = cb.createQuery( OkrCenterWorkInfo.class );
		Root<OkrCenterWorkInfo> root = cq.from( OkrCenterWorkInfo.class );
		Predicate p = cb.isNotNull(root.get( OkrCenterWorkInfo_.id ));
		
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrCenterWorkInfo_.id ), recordId ) );
		}
		
		Predicate p_creatorIdentity = cb.isNotNull(root.get( OkrCenterWorkInfo_.creatorIdentity ));
		p_creatorIdentity = cb.and( p_creatorIdentity, cb.equal( root.get( OkrCenterWorkInfo_.creatorIdentity ), identity ) );
		
		Predicate p_deployerIdentity = cb.isNotNull(root.get( OkrCenterWorkInfo_.deployerIdentity ));
		p_deployerIdentity = cb.and( p_deployerIdentity, cb.equal( root.get( OkrCenterWorkInfo_.deployerIdentity ), identity ) );
		
		Predicate p_reportAuditLeaderIdentity = cb.isNotNull(root.get( OkrCenterWorkInfo_.reportAuditLeaderIdentityList ));
		p_reportAuditLeaderIdentity = cb.and( p_reportAuditLeaderIdentity,cb.isMember(identity, root.get( OkrCenterWorkInfo_.reportAuditLeaderIdentityList )));
		
		Predicate p_auditLeaderIdentity = cb.isNotNull(root.get( OkrCenterWorkInfo_.reportAuditLeaderIdentityList ));
		p_auditLeaderIdentity = cb.and( p_auditLeaderIdentity, cb.isMember( identity, root.get( OkrCenterWorkInfo_.reportAuditLeaderIdentityList ) ) );
		
		Predicate p_identity = cb.or( p_creatorIdentity, p_deployerIdentity, p_reportAuditLeaderIdentity, p_auditLeaderIdentity );
			
		p = cb.and( p, p_identity );
		
		return em.createQuery(cq.where(p)).getResultList();
	}
}