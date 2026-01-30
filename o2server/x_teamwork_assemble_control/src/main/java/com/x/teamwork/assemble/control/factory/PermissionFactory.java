package com.x.teamwork.assemble.control.factory;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.AbstractFactory;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sword
 */
public class PermissionFactory extends AbstractFactory {

	private Cache.CacheCategory cacheCategory = new Cache.CacheCategory(Project.class, Task.class);

	public PermissionFactory(Business business) throws Exception {
		super(business);
	}

	/**
	 * 是否是管理员
	 * @param targetId
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean isManager(String targetId, String person) throws Exception {
		Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), targetId, person, "isManager");
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			return ((Boolean) optional.get()).booleanValue();
		}
		EntityManager em = this.entityManagerContainer().get(ProjectPermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ProjectPermission> root = cq.from(ProjectPermission.class);
		Predicate p = cb.equal(root.get(ProjectPermission_.targetId), targetId);
		p = cb.and(p, cb.equal(root.get(ProjectPermission_.name), person));
		p = cb.and(p, cb.equal(root.get(ProjectPermission_.role), ProjectRoleEnum.MANAGER.getValue()));
		cq.select(cb.count(root)).where(p);
		Boolean flag = em.createQuery(cq).getSingleResult() > 0;
		CacheManager.put(cacheCategory, cacheKey, flag);
		return flag.booleanValue();
	}

	/**
	 * 是否是阅读者
	 * @param targetId
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean isReader(String targetId, String person, boolean isProject) throws Exception {
		Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), targetId, person, "isZoneReader");
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			return ((Boolean) optional.get()).booleanValue();
		}
		EntityManager em = this.entityManagerContainer().get(ProjectPermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ProjectPermission> root = cq.from(ProjectPermission.class);
		Predicate p = cb.equal(root.get(ProjectPermission_.name), person);
		if(isProject) {
			p = cb.and(p, cb.equal(root.get(ProjectPermission_.projectId), targetId));
		}else{
			p = cb.and(p, cb.equal(root.get(ProjectPermission_.targetId), targetId));
		}
		cq.select(cb.count(root)).where(p);
		Boolean flag = em.createQuery(cq).getSingleResult() > 0;
		CacheManager.put(cacheCategory, cacheKey, flag);
		return flag.booleanValue();
	}

	/**
	 * 获取指定对象（人员、组织或群组）在的权限
	 * @param name
	 * @param targetId
	 * @return
	 * @throws Exception
	 */
	public ProjectPermission getPermission(String name, String targetId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ProjectPermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectPermission> cq = cb.createQuery(ProjectPermission.class);
		Root<ProjectPermission> root = cq.from(ProjectPermission.class);
		Predicate p = cb.equal(root.get(ProjectPermission_.targetId), targetId);
		p = cb.and(p, cb.equal(root.get(ProjectPermission_.name), name));
		List<ProjectPermission> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		return ListTools.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * 获取指定项目（任务）和指定角色(可选)的所有权限
	 * @param targetId
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public List<ProjectPermission> listPermission(String targetId, String role) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ProjectPermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectPermission> cq = cb.createQuery(ProjectPermission.class);
		Root<ProjectPermission> root = cq.from(ProjectPermission.class);
		Predicate p = cb.equal(root.get(ProjectPermission_.targetId), targetId);
		if(StringUtils.isNotBlank(role)) {
			p = cb.and(p, cb.equal(root.get(ProjectPermission_.role), role));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 获取指定项目（任务）和指定角色(可选)的所有权限
	 * @param targetId
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public List<String> listPermissionName(String targetId, String role) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ProjectPermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ProjectPermission> root = cq.from(ProjectPermission.class);
		Predicate p = cb.equal(root.get(ProjectPermission_.targetId), targetId);
		if(StringUtils.isNotBlank(role)) {
			p = cb.and(p, cb.equal(root.get(ProjectPermission_.role), role));
		}
		cq.select(root.get(ProjectPermission_.name)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listPermissionName(String projectId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ProjectPermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ProjectPermission> root = cq.from(ProjectPermission.class);
		Predicate p = cb.equal(root.get(ProjectPermission_.projectId), projectId);
		cq.select(root.get(ProjectPermission_.name)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

}
