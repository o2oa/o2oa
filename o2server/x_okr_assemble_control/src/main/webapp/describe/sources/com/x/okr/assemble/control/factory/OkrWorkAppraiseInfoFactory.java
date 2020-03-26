package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrWorkAppraiseInfo;
import com.x.okr.entity.OkrWorkAppraiseInfo_;

public class OkrWorkAppraiseInfoFactory extends AbstractFactory {

	public OkrWorkAppraiseInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe( "获取指定Id的OkrWorkAppraiseInfo应用信息对象" )
	public OkrWorkAppraiseInfo get(String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkAppraiseInfo.class, ExceptionWhen.none);
	}

	//@MethodDescribe( "列示指定Id的OkrWorkAppraiseInfo应用信息列表" )
	public List<OkrWorkAppraiseInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkAppraiseInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkAppraiseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkAppraiseInfo> cq = cb.createQuery(OkrWorkAppraiseInfo.class);
		Root<OkrWorkAppraiseInfo> root = cq.from(OkrWorkAppraiseInfo.class);
		Predicate p = root.get(OkrWorkAppraiseInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据工作ID列示指定Id的OkrWorkAppraiseInfo应用信息列表" )
	public List<String> listIdsWithWorkId(String workId ) throws Exception {
		if( StringUtils.isEmpty( workId )){
			return new ArrayList<String>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkAppraiseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkAppraiseInfo> root = cq.from(OkrWorkAppraiseInfo.class);
		Predicate p = cb.equal( root.get(OkrWorkAppraiseInfo_.workId), workId );
		cq.select( root.get(OkrWorkAppraiseInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据工作ID列示指定Id的OkrWorkAppraiseInfo应用信息列表" )
	public List<String> listIdsWithWorkId(String workId, String wf_workId ) throws Exception {
		if( StringUtils.isEmpty( workId )){
			return new ArrayList<String>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkAppraiseInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkAppraiseInfo> root = cq.from(OkrWorkAppraiseInfo.class);
		Predicate p = cb.equal( root.get(OkrWorkAppraiseInfo_.workId), workId );
		p = cb.and( p, cb.equal( root.get(OkrWorkAppraiseInfo_.wf_workId ), wf_workId ) );
		cq.select( root.get(OkrWorkAppraiseInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
}