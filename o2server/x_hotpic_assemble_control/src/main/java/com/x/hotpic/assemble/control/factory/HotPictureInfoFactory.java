package com.x.hotpic.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.hotpic.assemble.control.AbstractFactory;
import com.x.hotpic.assemble.control.Business;
import com.x.hotpic.entity.HotPictureInfo;
import com.x.hotpic.entity.HotPictureInfo_;


/**
 * 类   名：HotPictureInfoFactory<br/>
 * 实体类：HotPictureInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-11-15 17:17:26 
**/
public class HotPictureInfoFactory extends AbstractFactory {

	public HotPictureInfoFactory( Business business ) throws Exception {
		super(business);
	}

	//@MethodDescribe( "获取指定Id的HotPictureInfo实体信息对象" )
	public HotPictureInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, HotPictureInfo.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "列示指定Id的HotPictureInfo实体信息列表" )
	public List<HotPictureInfo> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<HotPictureInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(HotPictureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HotPictureInfo> cq = cb.createQuery(HotPictureInfo.class);
		Root<HotPictureInfo> root = cq.from(HotPictureInfo.class);
		Predicate p = root.get(HotPictureInfo_.id).in(ids);
		cq.orderBy( cb.desc( root.get( HotPictureInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@SuppressWarnings("unused")
	//@MethodDescribe( "列示全部的HotPictureInfo实体信息列表" )
	public List<HotPictureInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(HotPictureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HotPictureInfo> cq = cb.createQuery(HotPictureInfo.class);
		Root<HotPictureInfo> root = cq.from(HotPictureInfo.class);
		cq.orderBy( cb.desc( root.get( HotPictureInfo_.updateTime ) ) );
		return em.createQuery( cq ).setMaxResults( 100 ).getResultList();
	}

	public List<String> listByApplication(String application) throws Exception {
		if( application == null || application.isEmpty() ){
			throw new Exception("application can not be null!");
		}
		EntityManager em = this.entityManagerContainer().get(HotPictureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<HotPictureInfo> root = cq.from(HotPictureInfo.class);
		Predicate p = cb.equal( root.get(HotPictureInfo_.application), application );
		cq.orderBy( cb.desc( root.get( HotPictureInfo_.updateTime ) ) );
		cq.select( root.get(HotPictureInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Long count(String application, String infoId, String title ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(HotPictureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<HotPictureInfo> root = cq.from(HotPictureInfo.class);
		Predicate p = cb.isNotNull( root.get(HotPictureInfo_.id ) );
		if( application != null && !application.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( HotPictureInfo_.application ), application));
		}
		if( infoId != null && !infoId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( HotPictureInfo_.infoId ), infoId));
		}
		if( title != null && !title.isEmpty() ){
			p = cb.and( p, cb.like( root.get( HotPictureInfo_.title ), "%" + title + "%" ));
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<HotPictureInfo> listForPage( String application, String infoId, String title, Integer selectTotal ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(HotPictureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HotPictureInfo> cq = cb.createQuery(HotPictureInfo.class);
		Root<HotPictureInfo> root = cq.from(HotPictureInfo.class);
		Predicate p = cb.isNotNull( root.get(HotPictureInfo_.id ) );
		if( application != null && !application.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( HotPictureInfo_.application ), application));
		}
		if( infoId != null && !infoId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( HotPictureInfo_.infoId ), infoId));
		}
		if( title != null && !title.isEmpty() ){
			p = cb.and( p, cb.like( root.get( HotPictureInfo_.title ), "%" + title + "%" ));
		}
		cq.orderBy( cb.desc( root.get( HotPictureInfo_.sequence ) ) );
		return em.createQuery(cq.where(p)).setMaxResults(selectTotal).getResultList();
	}

	public List<HotPictureInfo> listForPage( String application, String infoId, String title,Integer first, Integer selectTotal ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(HotPictureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HotPictureInfo> cq = cb.createQuery(HotPictureInfo.class);
		Root<HotPictureInfo> root = cq.from(HotPictureInfo.class);
		Predicate p = cb.isNotNull( root.get(HotPictureInfo_.id ) );
		if( application != null && !application.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( HotPictureInfo_.application ), application));
		}
		if( infoId != null && !infoId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( HotPictureInfo_.infoId ), infoId));
		}
		if( title != null && !title.isEmpty() ){
			p = cb.and( p, cb.like( root.get( HotPictureInfo_.title ), "%" + title + "%" ));
		}
		cq.orderBy( cb.desc( root.get( HotPictureInfo_.sequence ) ) );
		return em.createQuery(cq.where(p)).setFirstResult(first).setMaxResults(selectTotal).getResultList();
	}
	
	public List<HotPictureInfo> listByApplicationInfoId( String application, String infoId ) throws Exception {
		if( application == null || application.isEmpty() ){
			throw new Exception("application can not be null!");
		}
		EntityManager em = this.entityManagerContainer().get(HotPictureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HotPictureInfo> cq = cb.createQuery(HotPictureInfo.class);
		Root<HotPictureInfo> root = cq.from(HotPictureInfo.class);
		Predicate p = cb.equal( root.get(HotPictureInfo_.application), application );
		if( infoId != null && !infoId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( HotPictureInfo_.infoId ), infoId));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<HotPictureInfo> getWithInfoId(String infoId) throws Exception {
		if( infoId == null || infoId.isEmpty() ){
			throw new Exception("infoId can not be null!");
		}
		EntityManager em = this.entityManagerContainer().get(HotPictureInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HotPictureInfo> cq = cb.createQuery(HotPictureInfo.class);
		Root<HotPictureInfo> root = cq.from(HotPictureInfo.class);
		Predicate p = cb.equal( root.get(HotPictureInfo_.infoId), infoId );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
