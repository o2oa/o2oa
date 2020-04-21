package com.x.processplatform.assemble.surface.factory.element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

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
			o = this.restrictProcess(application.getId(), flag);
			if (null != o) {
				this.entityManagerContainer().get(Process.class).detach(o);
				cache.put(new Element(cacheKey, o));
			}
		}
		return o;
	}

	public Process pickEnabled(String application, String edition) throws Exception {
		if (StringUtils.isEmpty(application) || StringUtils.isEmpty(edition)) {
			return null;
		}
		Process o = null;
		String cacheKey = ApplicationCache.concreteCacheKey(application, "e:" + edition);
		Element element = cache.get(cacheKey);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (Process) element.getObjectValue();
			}
		} else {
			o = this.getEnabledProcess(application, edition);
			if (null != o) {
				this.entityManagerContainer().get(Process.class).detach(o);
				cache.put(new Element(cacheKey, o));
			}
		}
		return o;
	}

	private Process pickObject(String flag) throws Exception {
		Process o = this.business().entityManagerContainer().flag(flag, Process.class);
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
			o = this.restrictProcess(application.getId(), flag);
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
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)),
				cb.isNull(root.get(Process_.editionEnable))));
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
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)),
				cb.isNull(root.get(Process_.editionEnable))));
		cq.select(root.get(Process_.id)).where(p).distinct(true);
		list = em.createQuery(cq).getResultList();
		return list;
	}

	/* 获取用户可启动的流程，如果applicationId 为空则取到所有可启动流程 */
	public boolean startable(EffectivePerson effectivePerson, List<String> identities, List<String> units,
			Process process) throws Exception {
		if (effectivePerson.isManager()
				|| (BooleanUtils.isTrue(this.business().organization().person().hasRole(effectivePerson,
						OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager)))) {
			return true;
		}
		if (ListTools.isEmpty(process.getStartableIdentityList())
				&& ListTools.isEmpty(process.getStartableUnitList())) {
			return true;
		}
		if (ListTools.isNotEmpty(process.getStartableIdentityList())
				&& ListTools.containsAny(identities, process.getStartableIdentityList())) {
			return true;
		} else {
			if (ListTools.isNotEmpty(process.getStartableUnitList())
					&& ListTools.containsAny(units, process.getStartableUnitList())) {
				return true;
			}
		}
		return false;
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

	public Process getEnabledProcess(String application, String edition) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		p = cb.and(p, cb.equal(root.get(Process_.edition), edition));
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)),
				cb.isNull(root.get(Process_.editionEnable))));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
		List<Process> list = em.createQuery(cq).getResultList();
		if(list!=null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	public Process restrictProcess(String application, String flag) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		p = cb.and(p, cb.or(cb.equal(root.get(Process_.id), flag),
				cb.equal(root.get(Process_.name), flag),
				cb.equal(root.get(Process_.alias), flag)));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
		List<Process> list = em.createQuery(cq).getResultList();
		if(list!=null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
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

	/* 根据processList获取同版本的所有流程或者仅获取processList的流程 */
	public List<Process> listObjectWithProcess(List<String> processList, boolean includeEdition) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		p = cb.and(p, root.get(Process_.id).in(processList));
		if(includeEdition){
			p = cb.and(p, cb.isNull(root.get(Process_.editionEnable)));
			Subquery<Process> subquery = cq.subquery(Process.class);
			Root<Process> subRoot = subquery.from(Process.class);
			Predicate subP = cb.conjunction();
			subP = cb.and(subP, cb.equal(root.get(Process_.edition), subRoot.get(Process_.edition)));
			subP = cb.and(subP, subRoot.get(Process_.id).in(processList));
			subP = cb.and(subP, cb.isNotNull(root.get(Process_.edition)));
			subquery.select(subRoot).where(subP);
			p = cb.or(p, cb.exists(subquery));
		}

		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}
}