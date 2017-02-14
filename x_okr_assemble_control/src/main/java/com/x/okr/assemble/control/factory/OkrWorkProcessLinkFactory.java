package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrWorkProcessLink;
import com.x.okr.entity.OkrWorkProcessLink_;

/**
 * 类   名：OkrWorkProcessLinkFactory<br/>
 * 实体类：OkrWorkProcessLink<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkProcessLinkFactory extends AbstractFactory {

	public OkrWorkProcessLinkFactory(Business business) throws Exception {
		super(business);
	}
	
	@MethodDescribe( "获取指定Id的OkrWorkProcessLink实体信息对象" )
	public OkrWorkProcessLink get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkProcessLink.class, ExceptionWhen.none);
	}
	
	@MethodDescribe( "列示全部的OkrWorkProcessLink实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkProcessLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkProcessLink> root = cq.from( OkrWorkProcessLink.class);
		cq.select(root.get(OkrWorkProcessLink_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe( "列示指定Id的OkrWorkProcessLink实体信息列表" )
	public List<OkrWorkProcessLink> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkProcessLink>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkProcessLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkProcessLink> cq = cb.createQuery(OkrWorkProcessLink.class);
		Root<OkrWorkProcessLink> root = cq.from(OkrWorkProcessLink.class);
		Predicate p = root.get(OkrWorkProcessLink_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据工作的ID和审核层级获取工作审核处理链对象
	 * @param id
	 * @param workAuditLevel
	 * @throws Exception 
	 */
	public OkrWorkProcessLink listByWorkIdAndProcessLevel( String id, Integer workAuditLevel ) throws Exception {
		List<OkrWorkProcessLink> okrWorkProcessLink = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null!" );
		}
		if( workAuditLevel == null ){
			workAuditLevel = 1;
		}
		EntityManager em = this.entityManagerContainer().get( OkrWorkProcessLink.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkProcessLink> cq = cb.createQuery(OkrWorkProcessLink.class);
		Root<OkrWorkProcessLink> root = cq.from( OkrWorkProcessLink.class );
		Predicate p = cb.equal( root.get( OkrWorkProcessLink_.workId), id );
		p = cb.and( p, cb.equal( root.get( OkrWorkProcessLink_.processLevel), workAuditLevel ));
		okrWorkProcessLink =  em.createQuery(cq.where(p)).getResultList();
		if( okrWorkProcessLink != null && okrWorkProcessLink.size() > 0  ){
			return okrWorkProcessLink.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 根据工作信息ID，获取工作部署审核链信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	@MethodDescribe( "根据工作信息ID，获取工作部署审核链信息ID列表" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkProcessLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkProcessLink> root = cq.from( OkrWorkProcessLink.class);
		Predicate p = cb.equal( root.get(OkrWorkProcessLink_.workId), workId );
		cq.select(root.get( OkrWorkProcessLink_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
	@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkProcessLink.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkProcessLink> root = cq.from(OkrWorkProcessLink.class);
		Predicate p = cb.equal( root.get( OkrWorkProcessLink_.centerId ), centerId );
		cq.select(root.get( OkrWorkProcessLink_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
