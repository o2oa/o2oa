package com.x.processplatform.assemble.surface.factory.element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

import net.sf.ehcache.Element;

public class ProcessFactory extends ElementFactory {

	public ProcessFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
		this.cache = ApplicationCache.instance().getCache(Process.class);
	}

	public List<Process> pick(List<String> flags) throws Exception {
		List<Process> list = new ArrayList<>();
		for (String str : flags) {
			Element element = cache.get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((Process) element.getObjectValue());
				}
			} else {
				Process o = this.pickObject(str);
				cache.put(new Element(str, o));
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public Process pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Process o = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (Process) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
		}
		return o;
	}

	public Process pick(Application application, String flag) throws Exception {
		if ((null == application) || StringUtils.isEmpty(flag)) {
			return null;
		}
		Process o = null;
		String cacheKey = ApplicationCache.concreteCacheKey(application.getId(), flag);
		Element element = cache.get(cacheKey);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (Process) element.getObjectValue();
			}
		} else {
			o = this.entityManagerContainer().restrictFlag(flag, Process.class, Process.application_FIELDNAME,
					application.getId());
			if (null != o) {
				cache.put(new Element(cacheKey, o));
			}
		}
		return o;
	}

	private Process pickObject(String flag) throws Exception {
		Process o = this.business().entityManagerContainer().flag(flag, Process.class );
		if (o != null) {
			this.entityManagerContainer().get(Process.class).detach(o);
		}
		if (null == o) {
			EntityManager em = this.entityManagerContainer().get(Process.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Process> cq = cb.createQuery(Process.class);
			Root<Process> root = cq.from(Process.class);
			Predicate p = cb.equal(root.get(Process_.name), flag);
			List<Process> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
			if (os.size() == 1) {
				o = os.get(0);
				em.detach(o);
			}
		}
		return o;
	}

	public Process pickObject(Application application, String flag) throws Exception {
		if (null == application || StringUtils.isEmpty(flag)) {
			return null;
		}
		Process o = null;
		String cacheKey = ApplicationCache.concreteCacheKey(Process.class, application.getId(), flag);
		Element element = cache.get(cacheKey);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (Process) element.getObjectValue();
			}
		} else {
			o = this.entityManagerContainer().restrictFlag(flag, Process.class, Process.application_FIELDNAME,
					application.getId());
			if (null != o) {
				cache.put(new Element(cacheKey, o));
			}
		}
		return o;
	}

	/* 获取Application下的所有流程 */
	public List<String> listWithApplication(Application application) throws Exception {
		List<String> list = new ArrayList<>();
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application.getId());
		cq.select(root.get(Process_.id)).where(p).distinct(true);
		list = em.createQuery(cq).getResultList();
		return list;
	}

	/* 获取用户可启动的流程，如果applicationId 为空则取到所有可启动流程 */
	public List<String> listStartableWithApplication(EffectivePerson effectivePerson, List<String> identities,
			List<String> units, Application application) throws Exception {
		List<String> list = new ArrayList<>();
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		if (effectivePerson.isNotManager() && (!this.business().organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
			p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
					cb.isEmpty(root.get(Process_.startableUnitList)));
			p = cb.or(p, cb.equal(root.get(Process_.creatorPerson), effectivePerson.getDistinguishedName()));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(units)) {
				p = cb.or(p, root.get(Process_.startableUnitList).in(units));
			}
		}
		p = cb.and(p, cb.equal(root.get(Process_.application), application.getId()));
		cq.select(root.get(Process_.id)).where(p).distinct(true);
		list = em.createQuery(cq).getResultList();
		return list;
	}

	public List<String> listControlableProcess(EffectivePerson effectivePerson, Application application)
			throws Exception {
		Business business = this.business();
		List<String> ids = this.listWithApplication(application);
		List<String> list = new ArrayList<>();
		for (String str : ids) {
			Process o = business.process().pick(str);
			if (null != o) {
				if (effectivePerson.isPerson(o.getControllerList())) {
					list.add(str);
				}
			}
		}
		return list;
	}

	// /* 判断用户是否有管理权限 */
	// public boolean allowControl(EffectivePerson effectivePerson, Process process)
	// throws Exception {
	// if (effectivePerson.isManager()) {
	// return true;
	// }
	// if (null != process) {
	// if (effectivePerson.isUser(process.getControllerList())) {
	// return true;
	// }
	// Application application =
	// this.business().application().pick(process.getApplication());
	// if (null != application) {
	// if (effectivePerson.isUser(application.getControllerList())) {
	// return true;
	// }
	// if (effectivePerson.isUser(application.getCreatorPerson())) {
	// return true;
	// }
	// }
	// }
	// return false;
	// }

	public <T extends Process> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(Process::getAlias, Comparator.nullsLast(String::compareTo))
						.thenComparing(Process::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}
}