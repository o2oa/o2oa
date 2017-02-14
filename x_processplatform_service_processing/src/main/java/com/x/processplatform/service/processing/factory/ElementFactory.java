package com.x.processplatform.service.processing.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Begin_;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Condition;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Route_;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Script_;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class ElementFactory extends AbstractFactory {

	public ElementFactory(Business business) throws Exception {
		super(business);
	}

	@SuppressWarnings("unchecked")
	public <T extends JpaObject> T get(String id, Class<T> clz) throws Exception {
		Ehcache cache = ApplicationCache.instance().getCache(clz);
		T t = null;
		String cacheKey = id;
		Element element = cache.get(cacheKey);
		if (null != element) {
			t = (T) element.getObjectValue();
		} else {
			t = this.entityManagerContainer().find(id, clz);
			if (null != t) {
				cache.put(new Element(cacheKey, t));
			}
		}
		return t;
	}

	/* 用Process的updateTime作为缓存值 */
	public Begin getBeginWithProcess(String id) throws Exception {
		Begin begin = null;
		Ehcache cache = ApplicationCache.instance().getCache(Begin.class);
		String cacheKey = id;
		Element element = cache.get(cacheKey);
		if (null != element) {
			begin = (Begin) element.getObjectValue();
		} else {
			EntityManager em = this.entityManagerContainer().get(Begin.class);
			Process process = this.get(id, Process.class);
			if (null != process) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Begin> cq = cb.createQuery(Begin.class);
				Root<Begin> root = cq.from(Begin.class);
				Predicate p = cb.equal(root.get(Begin_.process), process.getId());
				List<Begin> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
				if (!list.isEmpty()) {
					begin = list.get(0);
					cache.put(new Element(cacheKey, begin));
				}
			}
		}
		return begin;
	}

	@SuppressWarnings("unchecked")
	public List<Route> listRouteWithChoice(String id) throws Exception {
		List<Route> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(Route.class);
		String cacheKey = id + "#" + Choice.class.getCanonicalName();
		Element element = cache.get(cacheKey);
		if (null != element) {
			list = (List<Route>) element.getObjectValue();
		} else {
			EntityManager em = this.entityManagerContainer().get(Route.class);
			Choice choice = this.get(id, Choice.class);
			if (null != choice) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Route> cq = cb.createQuery(Route.class);
				Root<Route> root = cq.from(Route.class);
				Predicate p = root.get(Route_.id).in(choice.getRouteList());
				list = em.createQuery(cq.where(p)).getResultList();
				if (!list.isEmpty()) {
					cache.put(new Element(cacheKey, list));
				}
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Route> listRouteWithManual(String id) throws Exception {
		List<Route> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(Manual.class);
		String cacheKey = id + "#" + Manual.class.getCanonicalName();
		Element element = cache.get(cacheKey);
		if (null != element) {
			list = (List<Route>) element.getObjectValue();
		} else {
			EntityManager em = this.entityManagerContainer().get(Route.class);
			Manual manual = this.get(id, Manual.class);
			if (null != manual) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Route> cq = cb.createQuery(Route.class);
				Root<Route> root = cq.from(Route.class);
				Predicate p = root.get(Route_.id).in(manual.getRouteList());
				list = em.createQuery(cq.where(p)).getResultList();
				if (!list.isEmpty()) {
					cache.put(new Element(cacheKey, list));
				}
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Route> listRouteWithParallel(String id) throws Exception {
		List<Route> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(Parallel.class);
		String cacheKey = id + "#" + Parallel.class.getCanonicalName();
		Element element = cache.get(cacheKey);
		if (null != element) {
			list = (List<Route>) element.getObjectValue();
		} else {
			EntityManager em = this.entityManagerContainer().get(Route.class);
			Parallel parallel = this.get(id, Parallel.class);
			if (null != parallel) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Route> cq = cb.createQuery(Route.class);
				Root<Route> root = cq.from(Route.class);
				Predicate p = root.get(Route_.id).in(parallel.getRouteList());
				list = em.createQuery(cq.where(p)).getResultList();
				if (!list.isEmpty()) {
					cache.put(new Element(cacheKey, list));
				}
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Script> listScriptNestedWithApplicationWithUniqueName(String applicationId, String uniqueName)
			throws Exception {
		List<Script> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(Script.class);
		String cacheKey = applicationId + "." + uniqueName;
		Element element = cache.get(cacheKey);
		if (null != element) {
			list = (List<Script>) element.getObjectValue();
		} else {
			List<String> names = new ArrayList<>();
			names.add(uniqueName);
			while (!names.isEmpty()) {
				List<String> loops = new ArrayList<>();
				for (String name : names) {
					Script o = this.getScriptWithApplicationWithUniqueName(applicationId, name);
					if ((null != o) && (!list.contains(o))) {
						list.add(o);
						loops.addAll(o.getDependScriptList());
					}
				}
				names = loops;
			}
			if (!list.isEmpty()) {
				Collections.reverse(list);
				cache.put(new Element(cacheKey, list));
			}
		}
		return list;
	}

//	private <T extends JpaObject> Date getLastUpdateTimeOfAllScriptWithApplication(String applicationId)
//			throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Script.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
//		Root<Script> root = cq.from(Script.class);
//		Expression<? extends Date> selection = root.get("updateTime");
//		cq.select(root).orderBy(cb.desc(selection)).where(cb.equal(root.get(Script_.application), applicationId));
//		List<Script> list = em.createQuery(cq).setMaxResults(1).getResultList();
//		if (!list.isEmpty()) {
//			return list.get(0).getUpdateTime();
//		} else {
//			return null;
//		}
//	}

	private Script getScriptWithApplicationWithUniqueName(String applicationId, String uniqueName) throws Exception {
		Script script = null;
		EntityManager em = this.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Script> cq = cb.createQuery(Script.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.equal(root.get(Script_.name), uniqueName);
		p = cb.or(p, cb.equal(root.get(Script_.alias), uniqueName));
		p = cb.or(p, cb.equal(root.get(Script_.id), uniqueName));
		p = cb.and(p, cb.equal(root.get(Script_.application), applicationId));
		List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			script = list.get(0);
		}
		return script;
	}

	public List<Route> listRouteWithActvity(String id, ActivityType activityType) throws Exception {
		List<Route> list = new ArrayList<>();
		switch (activityType) {
		case agent:
			Agent agent = this.get(id, Agent.class);
			list.add(this.get(agent.getRoute(), Route.class));
			break;
		case begin:
			Begin begin = this.get(id, Begin.class);
			list.add(this.get(begin.getRoute(), Route.class));
			break;
		case cancel:
			break;
		case choice:
			Choice choice = this.get(id, Choice.class);
			for (String str : choice.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case condition:
			Condition condition = this.get(id, Condition.class);
			for (String str : condition.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case delay:
			Delay delay = this.get(id, Delay.class);
			list.add(this.get(delay.getRoute(), Route.class));
			break;
		case embed:
			Embed embed = this.get(id, Embed.class);
			list.add(this.get(embed.getRoute(), Route.class));
			break;
		case end:
			break;
		case invoke:
			Invoke invoke = this.get(id, Invoke.class);
			list.add(this.get(invoke.getRoute(), Route.class));
			break;
		case manual:
			Manual manual = this.get(id, Manual.class);
			for (String str : manual.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case merge:
			Merge merge = this.get(id, Merge.class);
			list.add(this.get(merge.getRoute(), Route.class));
			break;
		case message:
			Message message = this.get(id, Message.class);
			list.add(this.get(message.getRoute(), Route.class));
			break;
		case parallel:
			Parallel parallel = this.get(id, Parallel.class);
			for (String str : parallel.getRouteList()) {
				list.add(this.get(str, Route.class));
			}
			break;
		case service:
			Service service = this.get(id, Service.class);
			list.add(this.get(service.getRoute(), Route.class));
			break;
		case split:
			Split split = this.get(id, Split.class);
			list.add(this.get(split.getRoute(), Route.class));
			break;
		default:
			break;
		}
		return list;
	}
}