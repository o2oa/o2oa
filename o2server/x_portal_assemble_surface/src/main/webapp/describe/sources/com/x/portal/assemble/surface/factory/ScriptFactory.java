package com.x.portal.assemble.surface.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Script_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class ScriptFactory extends AbstractFactory {

	static Ehcache scriptCache = ApplicationCache.instance().getCache(Script.class);

	public ScriptFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Script flagWithPortalObject(String flag, String portalId) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey("flagObject", flag);
		Element element = scriptCache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			return (Script) element.getObjectValue();
		} else {
			EntityManager em = this.entityManagerContainer().get(Script.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Script> cq = cb.createQuery(Script.class);
			Root<Script> root = cq.from(Script.class);
			Predicate p = cb.equal(root.get(Script_.portal), portalId);
			p = cb.and(p, cb.or(cb.equal(root.get(Script_.name), flag), cb.equal(root.get(Script_.alias), flag)));
			List<Script> list = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
			if (list.isEmpty()) {
				return null;
			} else {
				Script o = list.get(0);
				em.detach(o);
				scriptCache.put(new Element(cacheKey, o));
				return o;
			}
		}
	}

	public Script pick(String id) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey(id);
		Element element = scriptCache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			return (Script) element.getObjectValue();
		} else {
			Script o = this.business().entityManagerContainer().find(id, Script.class);
			if (null != o) {
				this.business().entityManagerContainer().get(Script.class).detach(o);
				scriptCache.put(new Element(id, o));
				return o;
			}
			return null;
		}
	}

	public List<String> listWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.portal), portalId);
		List<String> list = em.createQuery(cq.select(root.get(Script_.id)).where(p)).getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Script> listScriptNestedWithPortalWithFlag(Portal portal, String flag) throws Exception {
		List<Script> list = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(flag, portal.getId(), "listScriptNestedWithPortalWithFlag");
		Element element = scriptCache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			list = (List<Script>) element.getObjectValue();
		} else {
			List<String> names = new ArrayList<>();
			names.add(flag);
			while (!names.isEmpty()) {
				List<String> loops = new ArrayList<>();
				for (String name : names) {
					Script o = this.getScriptWithPortalWithFlag(portal, name);
					if ((null != o) && (!list.contains(o))) {
						list.add(o);
						loops.addAll(o.getDependScriptList());
					}
				}
				names = loops;
			}
			if (!list.isEmpty()) {
				Collections.reverse(list);
				scriptCache.put(new Element(cacheKey, list));
			}
		}
		return list;
	}

	private Script getScriptWithPortalWithFlag(Portal portal, String flag) throws Exception {
		Script script = this.getWithPortalWithId(portal, flag);
		if (null == script) {
			script = this.getWithPortalWithAlias(portal, flag);
		}
		if (null == script) {
			script = this.getWithPortalWithName(portal, flag);
		}
		return script;
	}

	private Script getWithPortalWithId(Portal portal, String flag) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.id), flag);
		p = cb.and(p, cb.equal(root.get(Script_.portal), portal.getId()));
		List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	private Script getWithPortalWithAlias(Portal portal, String flag) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.alias), flag);
		p = cb.and(p, cb.equal(root.get(Script_.portal), portal.getId()));
		List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	private Script getWithPortalWithName(Portal portal, String flag) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.name), flag);
		p = cb.and(p, cb.equal(root.get(Script_.portal), portal.getId()));
		List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}