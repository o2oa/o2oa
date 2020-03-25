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
import com.x.okr.entity.OkrWorkDetailInfo;
import com.x.okr.entity.OkrWorkDetailInfo_;

/**
 * 类   名：OkrWorkDetailInfoFactory<br/>
 * 实体类：OkrWorkDetailInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkDetailInfoFactory extends AbstractFactory {

	public OkrWorkDetailInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrWorkDetailInfo实体信息对象" )
	public OkrWorkDetailInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkDetailInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrWorkDetailInfo实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkDetailInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkDetailInfo> root = cq.from( OkrWorkDetailInfo.class);
		cq.select(root.get(OkrWorkDetailInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrWorkDetailInfo实体信息列表" )
	public List<OkrWorkDetailInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkDetailInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkDetailInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkDetailInfo> cq = cb.createQuery(OkrWorkDetailInfo.class);
		Root<OkrWorkDetailInfo> root = cq.from(OkrWorkDetailInfo.class);
		Predicate p = root.get(OkrWorkDetailInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
//	@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkDetailInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkDetailInfo> root = cq.from(OkrWorkDetailInfo.class);
		Predicate p = cb.equal( root.get( OkrWorkDetailInfo_.centerId ), centerId );
		cq.select(root.get( OkrWorkDetailInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByWorkId(String workId) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( " workId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkDetailInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkDetailInfo> root = cq.from(OkrWorkDetailInfo.class);
		Predicate p = cb.equal( root.get( OkrWorkDetailInfo_.id ), workId );
		cq.select(root.get( OkrWorkDetailInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
