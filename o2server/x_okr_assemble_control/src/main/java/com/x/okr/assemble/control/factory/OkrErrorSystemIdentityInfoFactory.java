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
import com.x.okr.entity.OkrErrorIdentityRecords;
import com.x.okr.entity.OkrErrorIdentityRecords_;
import com.x.okr.entity.OkrErrorSystemIdentityInfo;
import com.x.okr.entity.OkrErrorSystemIdentityInfo_;

/**
 * 类   名：OkrErrorSystemIdentityInfoFactory<br/>
 * 实体类：OkrErrorSystemIdentityInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrErrorSystemIdentityInfoFactory extends AbstractFactory {

	public OkrErrorSystemIdentityInfoFactory(Business business) throws Exception {
		super(business);
	}
	
//	@MethodDescribe( "获取指定Id的OkrErrorSystemIdentityInfo实体信息对象" )
	public OkrErrorSystemIdentityInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrErrorSystemIdentityInfo.class, ExceptionWhen.none);
	}
	
//	@MethodDescribe( "获取指定Id的OkrErrorIdentityRecords实体信息对象" )
	public OkrErrorIdentityRecords getRecords( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrErrorIdentityRecords.class, ExceptionWhen.none);
	}

	public List<OkrErrorSystemIdentityInfo> listByIdentityName(String identity) throws Exception {
		if( identity == null || identity.isEmpty() ){
			return new ArrayList<OkrErrorSystemIdentityInfo>();
		}
		EntityManager em = this.entityManagerContainer().get( OkrErrorSystemIdentityInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrErrorSystemIdentityInfo> cq = cb.createQuery(OkrErrorSystemIdentityInfo.class);
		Root<OkrErrorSystemIdentityInfo> root = cq.from(OkrErrorSystemIdentityInfo.class);
		Predicate p = cb.equal( root.get(OkrErrorSystemIdentityInfo_.identity), identity );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<OkrErrorIdentityRecords> listRecordsByIdentityName(String identity) throws Exception {
		if( identity == null || identity.isEmpty() ){
			return new ArrayList<OkrErrorIdentityRecords>();
		}
		EntityManager em = this.entityManagerContainer().get( OkrErrorIdentityRecords.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrErrorIdentityRecords> cq = cb.createQuery(OkrErrorIdentityRecords.class);
		Root<OkrErrorIdentityRecords> root = cq.from(OkrErrorIdentityRecords.class);
		Predicate p = cb.equal( root.get(OkrErrorIdentityRecords_.identity), identity );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<OkrErrorSystemIdentityInfo> listNotFlag(String flag) throws Exception {
		if( flag == null || flag.isEmpty() ){
			return new ArrayList<OkrErrorSystemIdentityInfo>();
		}
		EntityManager em = this.entityManagerContainer().get( OkrErrorSystemIdentityInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrErrorSystemIdentityInfo> cq = cb.createQuery(OkrErrorSystemIdentityInfo.class);
		Root<OkrErrorSystemIdentityInfo> root = cq.from(OkrErrorSystemIdentityInfo.class);
		Predicate p = cb.notEqual( root.get(OkrErrorSystemIdentityInfo_.flag), flag );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<OkrErrorIdentityRecords> listRecordNotFlag(String flag) throws Exception {
		if( flag == null || flag.isEmpty() ){
			return new ArrayList<OkrErrorIdentityRecords>();
		}
		EntityManager em = this.entityManagerContainer().get( OkrErrorIdentityRecords.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrErrorIdentityRecords> cq = cb.createQuery(OkrErrorIdentityRecords.class);
		Root<OkrErrorIdentityRecords> root = cq.from(OkrErrorIdentityRecords.class);
		Predicate p = cb.notEqual( root.get(OkrErrorIdentityRecords_.flag), flag );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
}
