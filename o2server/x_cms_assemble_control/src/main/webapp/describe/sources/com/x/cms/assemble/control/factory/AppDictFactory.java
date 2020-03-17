package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDict_;


public class AppDictFactory extends AbstractFactory {

	public AppDictFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithAppInfo(String appId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDict.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<AppDict> root = cq.from(AppDict.class);
		Predicate p = cb.equal( root.get( AppDict_.appId ), appId );
		cq.select(root.get( AppDict_.id )).where(p);
		return em.createQuery( cq ).getResultList();
	}
	
	public List<AppDict> listDictWithAppInfo(String appId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDict.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppDict> cq = cb.createQuery( AppDict.class );
		Root<AppDict> root = cq.from(AppDict.class);
		Predicate p = cb.equal( root.get( AppDict_.appId ), appId );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	public String getWithAppWithUniqueName(String appId, String uniqueName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDict.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppDict> root = cq.from(AppDict.class);
		Predicate p = cb.equal(root.get(AppDict_.name), uniqueName);
		p = cb.or(p, cb.equal(root.get(AppDict_.alias), uniqueName));
		p = cb.or(p, cb.equal(root.get(AppDict_.id), uniqueName));
		p = cb.and(p, cb.equal(root.get(AppDict_.appId), appId));
		cq.select(root.get(AppDict_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

    public List<AppDict> list(List<String> ids) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AppDict.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AppDict> cq = cb.createQuery(AppDict.class);
        Root<AppDict> root = cq.from(AppDict.class);
        Predicate p = root.get(AppDict_.name).in( ids );
        return em.createQuery(cq.where(p)).getResultList();
    }
    
    public String getWithAppInfoWithUniqueName(String appInfoId, String uniqueName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDict.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppDict> root = cq.from(AppDict.class);
		Predicate p = cb.equal(root.get(AppDict_.name), uniqueName);
		p = cb.or(p, cb.equal(root.get(AppDict_.alias), uniqueName));
		p = cb.or(p, cb.equal(root.get(AppDict_.id), uniqueName));
		p = cb.and(p, cb.equal(root.get(AppDict_.appId), appInfoId));
		cq.select(root.get(AppDict_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}
}