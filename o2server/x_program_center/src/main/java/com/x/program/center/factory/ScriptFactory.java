package com.x.program.center.factory;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.program.center.AbstractFactory;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Script;
import com.x.program.center.core.entity.Script_;

/**
 * @author sword
 */
public class ScriptFactory extends AbstractFactory {

	static Cache.CacheCategory cache = new Cache.CacheCategory(Script.class);

	public ScriptFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public String getWithId(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.id), id);
		List<String> list = em.createQuery(cq.select(root.get(Script_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.name), name);
		List<String> list = em.createQuery(cq.select(root.get(Script_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithAlias(String alias) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.alias), alias);
		List<String> list = em.createQuery(cq.select(root.get(Script_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	private Script getObjectWithAlias(String flag) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.alias), flag);
		List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	private Script getObjectWithName(String flag) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.name), flag);
		List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<String> list() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.conjunction();
		List<String> list = em.createQuery(cq.select(root.get(Script_.id)).where(p)).getResultList();
		return list;
	}

	public Script pick(String id) throws Exception {
		Cache.CacheKey cacheKey = new Cache.CacheKey(id);
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			return (Script) optional.get();
		} else {
			Script o = this.entityManagerContainer().find(id, Script.class);
			if (null != o) {
				this.entityManagerContainer().get(Script.class).detach(o);
				CacheManager.put(cache, cacheKey, o);
				return o;
			}
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Script> listScriptNestedWithFlag( String flag) throws Exception {
		List<Script> list = new ArrayList<>();
		Cache.CacheKey cacheKey = new Cache.CacheKey(flag, "listScriptNestedWithFlag");
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			list = (List<Script>) optional.get();
		} else {
			List<String> names = new ArrayList<>();
			names.add(flag);
			while (!names.isEmpty()) {
				List<String> loops = new ArrayList<>();
				for (String name : names) {
					Script o = this.getScriptWithFlag(name);
					if ((null != o) && (!list.contains(o))) {
						list.add(o);
						loops.addAll(o.getDependScriptList());
					}
				}
				names = loops;
			}
			if (!list.isEmpty()) {
				Collections.reverse(list);
				CacheManager.put(cache, cacheKey, list);
			}
		}
		return list;
	}

	private Script getScriptWithFlag(String flag) throws Exception {
		Script script = this.entityManagerContainer().find(flag, Script.class);
		if (null == script) {
			script = this.getObjectWithName(flag);
		}
		if (null == script) {
			script = this.getObjectWithAlias(flag);
		}
		return script;
	}

}
