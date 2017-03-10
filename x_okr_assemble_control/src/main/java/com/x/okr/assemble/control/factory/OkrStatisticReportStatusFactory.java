package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.utils.annotation.MethodDescribe;
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
	
	@MethodDescribe( "获取指定Id的OkrStatisticReportStatus实体信息对象" )
	public OkrStatisticReportStatus get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrStatisticReportStatus.class );
	}
	
	@MethodDescribe( "获取指定Id的OkrStatisticReportStatus实体信息对象" )
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
	
	@MethodDescribe( "列示全部的OkrStatisticReportStatus实体信息列表" )
	public List<OkrStatisticReportStatus> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrStatisticReportStatus> cq = cb.createQuery(OkrStatisticReportStatus.class);
		@SuppressWarnings("unused")
		Root<OkrStatisticReportStatus> root = cq.from( OkrStatisticReportStatus.class);
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrStatisticReportStatus实体信息列表" )
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

	public List<OkrStatisticReportStatus> list( String centerId, String workId, String organization, String cycleType, String status ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrStatisticReportStatus.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrStatisticReportStatus> cq = cb.createQuery(OkrStatisticReportStatus.class);
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
		if( organization != null && !organization.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.responsibilityOrganizationName ), organization ));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIds(String centerId, String workId, String organization, String cycleType, String status) throws Exception {
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
		if( organization != null && !organization.isEmpty() ){
			p = cb.and( p, cb.equal( root.get(OkrStatisticReportStatus_.responsibilityOrganizationName ), organization ));
		}
		cq.select(root.get(OkrStatisticReportStatus_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
