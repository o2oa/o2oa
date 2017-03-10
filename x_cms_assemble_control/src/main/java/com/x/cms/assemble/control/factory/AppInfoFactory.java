package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.AppInfo_;

/**
 * 应用信息表基础功能服务类
 * @author liyi
 */
public class AppInfoFactory extends AbstractFactory {
	
	public AppInfoFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的Appinfo应用信息对象")
	public AppInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AppInfo.class, ExceptionWhen.none);
	}
	public List<String> listAllIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get(AppInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	@SuppressWarnings("unused")
	@MethodDescribe("列示全部的Appinfo应用信息列表")
	public List<AppInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppInfo> cq = cb.createQuery( AppInfo.class );
		Root<AppInfo> root = cq.from( AppInfo.class );
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的Appinfo应用信息列表")
	public List<AppInfo> list(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppInfo> cq = cb.createQuery(AppInfo.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = root.get(AppInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	@MethodDescribe("对应用信息进行模糊查询，并且返回信息列表.")
	public List<String> listLike(String keyStr) throws Exception {
		String str = keyStr.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.like(root.get(AppInfo_.appName), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(AppInfo_.appAlias), str + "%", '\\'));
		cq.select(root.get(AppInfo_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	public List<String> listNoPermissionAppInfoIds( List<String> permissionedAppInfoIds ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AppInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		cq.select(root.get( AppInfo_.id ));
		if( permissionedAppInfoIds != null && !permissionedAppInfoIds.isEmpty() ){
			Predicate p = cb.not( root.get(AppInfo_.id).in( permissionedAppInfoIds ));
			return em.createQuery(cq.where( p )).getResultList();
		}
		return em.createQuery(cq).getResultList();
	}

	public List<String> listByAppName(String appName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppInfo> root = cq.from(AppInfo.class);
		Predicate p = cb.equal( root.get(AppInfo_.appName ), appName );
		cq.select(root.get( AppInfo_.id ));
		return em.createQuery(cq.where( p )).getResultList();
	}	
}