package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrStatisticReportStatus;
import com.x.okr.entity.OkrStatisticReportStatus_;

/**
 * 类   名：OkrStatisticReportStatusFactory<br/>
 * 实体类：OkrStatisticReportStatus<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrStatisticReportStatusFactory extends AbstractFactory {

	public OkrStatisticReportStatusFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrStatisticReportStatus实体信息对象" )
	public OkrStatisticReportStatus get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrStatisticReportStatus.class );
	}
	
	//@MethodDescribe( "获取指定Id的OkrStatisticReportStatus实体信息对象" )
	public List<OkrStatisticReportStatus> listWithWorkId( String workId ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception("workId is null!");
		}
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrStatisticReportStatus> cq = cb.createQuery(OkrStatisticReportStatus.class);
		Root<OkrStatisticReportStatus> root = cq.from(OkrStatisticReportStatus.class);
		Predicate p = cb.equal( root.get(OkrStatisticReportStatus_.workId), workId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "列示全部的OkrStatisticReportStatus实体信息列表" )
	public List<OkrStatisticReportStatus> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrStatisticReportStatus> cq = cb.createQuery(OkrStatisticReportStatus.class);
		@SuppressWarnings("unused")
		Root<OkrStatisticReportStatus> root = cq.from( OkrStatisticReportStatus.class);
		return em.createQuery(cq).getResultList();
	}
	
	public List<String> listAllIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrStatisticReportStatus> root = cq.from( OkrStatisticReportStatus.class);
		cq.select(root.get(OkrStatisticReportStatus_.id) );
		return em.createQuery(cq).getResultList();
	}
	
//	@MethodDescribe( "列示指定Id的OkrStatisticReportStatus实体信息列表" )
	public List<OkrStatisticReportStatus> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrStatisticReportStatus>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrStatisticReportStatus> cq = cb.createQuery(OkrStatisticReportStatus.class);
		Root<OkrStatisticReportStatus> root = cq.from(OkrStatisticReportStatus.class);
		Predicate p = root.get(OkrStatisticReportStatus_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<OkrStatisticReportStatus> list( String centerId, String centerTitle, String workId, String workType, String unitName, String cycleType, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrStatisticReportStatus> cq = cb.createQuery(OkrStatisticReportStatus.class);
		Root<OkrStatisticReportStatus> root = cq.from(OkrStatisticReportStatus.class);
		Predicate p = cb.isNotNull( root.get(OkrStatisticReportStatus_.id) );
		
		if( workType != null && !workType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.workType ), workType ));
		}
		if( cycleType != null && !cycleType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.reportCycle ), cycleType ));
		}
		if( centerTitle != null && !centerTitle.isEmpty() ){
			p = cb.and( p, cb.like( root.get( OkrStatisticReportStatus_.centerTitle ), "%"+centerTitle+"%" ));
		}
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.centerId ), centerId ));
		}
		if( workId != null && !workId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.workId ), workId ));
		}
		if( status != null && !"All".equals(status) ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.status ), status ));
		}
		if( unitName != null && !unitName.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.responsibilityUnitName ), unitName ));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIds(String centerId, String workId, String unitName, String cycleType, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrStatisticReportStatus> root = cq.from(OkrStatisticReportStatus.class);
		Predicate p = cb.isNotNull( root.get(OkrStatisticReportStatus_.id) );
		
		if( cycleType != null && !cycleType.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.reportCycle ), cycleType ));
		}
		if( centerId != null && !centerId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.centerId ), centerId ));
		}
		if( workId != null && !workId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.workId ), workId ));
		}
		if( status != null && !status.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.status ), status ));
		}
		if( unitName != null && !unitName.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.responsibilityUnitName ), unitName ));
		}
		cq.select(root.get(OkrStatisticReportStatus_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 查询统计数据中工作责任者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctResponsibilityIdentity( List<String> identities_ok, List<String> identities_error ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrStatisticReportStatus> root = cq.from(OkrStatisticReportStatus.class);
		
		Predicate p = cb.isNotNull( root.get( OkrStatisticReportStatus_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrStatisticReportStatus_.responsibilityIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrStatisticReportStatus_.responsibilityIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrStatisticReportStatus_.responsibilityIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据身份名称，从工作汇报状态统计信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrStatisticReportStatus> listErrorIdentitiesInStReportStatus(String identity, String recordId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrStatisticReportStatus> cq = cb.createQuery( OkrStatisticReportStatus.class );
		Root<OkrStatisticReportStatus> root = cq.from( OkrStatisticReportStatus.class );
		Predicate p = cb.isNotNull(root.get( OkrStatisticReportStatus_.id ));
		
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrStatisticReportStatus_.id ), recordId ) );
		}
		
		Predicate p_responsibilityIdentity = cb.isNotNull(root.get( OkrStatisticReportStatus_.responsibilityIdentity ));
		p_responsibilityIdentity = cb.and( p_responsibilityIdentity, cb.equal( root.get( OkrStatisticReportStatus_.responsibilityIdentity ), identity ) );		
		p = cb.and( p, p_responsibilityIdentity );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
