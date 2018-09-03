package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrTaskHandled;
import com.x.okr.entity.OkrTaskHandled_;

/**
 * 类   名：OkrTaskHandledFactory<br/>
 * 实体类：OkrTaskHandled<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrTaskHandledFactory extends AbstractFactory {

	public OkrTaskHandledFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrTaskHandled实体信息对象" )
	public OkrTaskHandled get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrTaskHandled.class, ExceptionWhen.none);
	}
	
//	@MethodDescribe( "列示全部的OkrTaskHandled实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrTaskHandled.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTaskHandled> root = cq.from( OkrTaskHandled.class);
		cq.select(root.get(OkrTaskHandled_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrTaskHandled实体信息列表" )
	public List<OkrTaskHandled> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrTaskHandled>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrTaskHandled.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrTaskHandled> cq = cb.createQuery(OkrTaskHandled.class);
		Root<OkrTaskHandled> root = cq.from(OkrTaskHandled.class);
		Predicate p = root.get(OkrTaskHandled_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrTaskHandled.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTaskHandled> root = cq.from(OkrTaskHandled.class);
		Predicate p = cb.equal( root.get( OkrTaskHandled_.centerId ), centerId );
		cq.select(root.get(OkrTaskHandled_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作信息ID，列示所有的数据信息
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "根据工作信息ID，列示所有的数据信息" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrTaskHandled.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTaskHandled> root = cq.from( OkrTaskHandled.class);
		Predicate p = cb.equal( root.get( OkrTaskHandled_.workId), workId );
		cq.select(root.get( OkrTaskHandled_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsByReportId(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception( " id is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( OkrTaskHandled.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrTaskHandled> root = cq.from(OkrTaskHandled.class);
		Predicate p = cb.equal( root.get( OkrTaskHandled_.dynamicObjectId), id);
		p = cb.and(p, cb.equal( root.get( OkrTaskHandled_.dynamicObjectType), "工作汇报") );
		cq.select(root.get( OkrTaskHandled_.id));
		return em.createQuery(cq.where(p)).setMaxResults(5000).getResultList();
	}
	/**
	 * 查询已办处理者身份列表（去重复）
	 * @param identities_ok 排除身份
	 * @param identities_error 排除身份
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllDistinctTargetIdentity(List<String> identities_ok, List<String> identities_error) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrTaskHandled.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<OkrTaskHandled> root = cq.from(OkrTaskHandled.class);
		
		Predicate p = cb.isNotNull( root.get( OkrTaskHandled_.id ) );
		if( identities_ok != null && identities_ok.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrTaskHandled_.targetIdentity ).in( identities_ok )) );
		}
		if( identities_error != null && identities_error.size() > 0 ){
			p = cb.and( p, cb.not(root.get( OkrTaskHandled_.targetIdentity ).in( identities_error )) );
		}
		cq.distinct(true).select(root.get( OkrTaskHandled_.targetIdentity ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	/**
	 * 根据身份名称，从具体工作已办已阅信息中查询与该身份有关的所有信息列表
	 * @param identity
	 * @param recordId 
	 * @return
	 * @throws Exception 
	 */
	public List<OkrTaskHandled> listErrorIdentitiesInTaskhandled(String identity, String recordId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( OkrTaskHandled.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrTaskHandled> cq = cb.createQuery( OkrTaskHandled.class );
		Root<OkrTaskHandled> root = cq.from( OkrTaskHandled.class );
		Predicate p = cb.isNotNull(root.get( OkrTaskHandled_.id ));		
		
		if( recordId != null && !recordId.isEmpty() && !"all".equals( recordId ) ){
			p = cb.and( p, cb.equal( root.get( OkrTaskHandled_.id ), recordId ) );
		}
		
		Predicate p_targetIdentity = cb.isNotNull(root.get( OkrTaskHandled_.targetIdentity ));
		p_targetIdentity = cb.and( p_targetIdentity, cb.equal( root.get( OkrTaskHandled_.targetIdentity ), identity ) );		
		p = cb.and( p, p_targetIdentity );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
}
