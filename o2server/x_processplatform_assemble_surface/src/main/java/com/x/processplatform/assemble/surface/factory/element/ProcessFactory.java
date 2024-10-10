package com.x.processplatform.assemble.surface.factory.element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

public class ProcessFactory extends ElementFactory {

	public ProcessFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<Process> pick(List<String> flags) throws Exception {
		return this.pick(flags, Process.class);
	}

	public Process pick(String flag) throws Exception {
		return this.pick(flag, Process.class);
	}

	public Process pick(Application application, String flag) throws Exception {
		return this.pick(application, flag, Process.class);
	}

	public List<Process> listProcessEditionObject(String application, String edition) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application);
		p = cb.and(p, cb.equal(root.get(Process_.edition), edition));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 在启动方法中根据应用和流程标识找到流程,需要考虑如果启用版本管理,那么流程名是重复的
	 *
	 * @param application
	 * @param flag        流程ID、流程名称
	 * @return
	 * @throws Exception
	 */
	public Process pickProcessEditionEnabled(Application application, String flag) throws Exception {
		if (null == application) {
			return null;
		}
		CacheCategory cacheCategory = new CacheCategory(Process.class);
		CacheKey cacheKey = new CacheKey(application.getId(), flag, "pickProcessEditionEnabled");
		Process p = null;
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			p = (Process) optional.get();
		} else {
			p = this.entityManagerContainer().restrictFlagEqualAndNotEqual(flag, Process.class,
					Process.application_FIELDNAME, application.getId(), Process.editionEnable_FIELDNAME, false);
			if (p != null) {
				this.entityManagerContainer().get(Process.class).detach(p);
				CacheManager.put(cacheCategory, cacheKey, p);
			} else {
				p = this.entityManagerContainer().find(flag, Process.class);
				if (p != null && BooleanUtils.isFalse(p.getEditionEnable())) {
					p = this.getEnabledProcess(p.getApplication(), p.getEdition());
				}
				if (p != null) {
					this.entityManagerContainer().get(Process.class).detach(p);
					CacheManager.put(cacheCategory, cacheKey, p);
				}
			}
		}
		return p;
	}

	public Process pickEnabled(String application, String edition) throws Exception {
		if (StringUtils.isEmpty(application) || StringUtils.isEmpty(edition)) {
			return null;
		}
		CacheCategory cacheCategory = new CacheCategory(Process.class);
		CacheKey cacheKey = new CacheKey(application, edition, "enabled");
		Process o = null;
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			o = (Process) optional.get();
		} else {
			o = this.getEnabledProcess(application, edition);
			if (null != o) {
				this.entityManagerContainer().get(Process.class).detach(o);
				CacheManager.put(cacheCategory, cacheKey, o);
			}
		}
		return o;
	}

	// 获取Application下的所有流程
	public List<String> listWithApplication(Application application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application.getId());
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)), cb.isNull(root.get(Process_.editionEnable))));
		cq.select(root.get(Process_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	/* 获取用户可启动的流程，如果applicationId 为空则取到所有可启动流程 */
	public List<String> listStartableWithApplication(EffectivePerson effectivePerson, List<String> identities,
			List<String> units, List<String> groups, Application application, String terminal) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		if (BooleanUtils
				.isNotTrue(this.business().ifPersonCanManageApplicationOrProcess(effectivePerson, application, null))) {
			p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
					cb.isEmpty(root.get(Process_.startableUnitList)),
					cb.isEmpty(root.get(Process_.startableGroupList)));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(units)) {
				p = cb.or(p, root.get(Process_.startableUnitList).in(units));
			}
			if (ListTools.isNotEmpty(groups)) {
				p = cb.or(p, root.get(Process_.startableGroupList).in(groups));
			}
		}
		p = cb.and(p, cb.equal(root.get(Process_.application), application.getId()));
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)), cb.isNull(root.get(Process_.editionEnable))));
		// 如果限定了终端,对终端进行判断
		if (StringUtils.isNotEmpty(terminal)) {
			p = cb.and(p, cb.or(cb.equal(root.get(Process_.startableTerminal), Process.STARTABLETERMINAL_ALL),
					cb.equal(root.get(Process_.startableTerminal), terminal),
					cb.equal(root.get(Process_.startableTerminal), ""),
					cb.isNull(root.get(Process_.startableTerminal))));
		}
		cq.select(root.get(Process_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public List<String> listControllableProcess(EffectivePerson effectivePerson, Application application)
			throws Exception {
		List<String> ids = this.listWithApplication(application);
		boolean isManager = this.business().ifPersonCanManageApplicationOrProcess(effectivePerson, application, null);
		List<String> list = new ArrayList<>();
		for (String str : ids) {
			Process o = this.pick(str);
			boolean flag = isManager || effectivePerson.isPerson(o.getControllerList());
			if (flag) {
				list.add(str);
			}
		}
		return list;
	}

	public List<String> listControllableAndEnabledProcess(EffectivePerson effectivePerson, Application application)
			throws Exception {
		List<String> ids = this.listWithApplication(application);
		boolean isManager = this.business().ifPersonCanManageApplicationOrProcess(effectivePerson, application, null);
		List<String> list = new ArrayList<>();
		for (String str : ids) {
			Process o = this.pick(str);
			boolean flag = (!BooleanUtils.isFalse(o.getEditionEnable()))
					&& (isManager || effectivePerson.isPerson(o.getControllerList()));
			if (flag) {
				list.add(str);
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
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)), cb.isNull(root.get(Process_.editionEnable))));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
		List<Process> list = em.createQuery(cq).getResultList();
		if (list != null && !list.isEmpty()) {
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
		p = cb.and(p, cb.or(cb.equal(root.get(Process_.id), flag), cb.equal(root.get(Process_.name), flag),
				cb.equal(root.get(Process_.alias), flag)));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
		List<Process> list = em.createQuery(cq).getResultList();
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

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
		if (includeEdition) {
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

	/* 根据processList获取同版本的所有流程 */
	public List<String> listEditionProcess(List<String> processList) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		p = cb.and(p, root.get(Process_.id).in(processList));

		p = cb.and(p, cb.isNull(root.get(Process_.editionEnable)));
		Subquery<Process> subquery = cq.subquery(Process.class);
		Root<Process> subRoot = subquery.from(Process.class);
		Predicate subP = cb.conjunction();
		subP = cb.and(subP, cb.equal(root.get(Process_.edition), subRoot.get(Process_.edition)));
		subP = cb.and(subP, subRoot.get(Process_.id).in(processList));
		subP = cb.and(subP, cb.isNotNull(root.get(Process_.edition)));
		subquery.select(subRoot).where(subP);
		p = cb.or(p, cb.exists(subquery));

		cq.select(root.get(Process_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 判断用户是否可以启动此流程
	 *
	 * @param effectivePerson
	 * @param identities
	 * @param units
	 * @param groups
	 * @param process
	 * @return
	 * @throws Exception
	 */
	public boolean startable(EffectivePerson effectivePerson, List<String> identities, List<String> units,
			List<String> groups, Process process) throws Exception {
		if (BooleanUtils.isTrue(this.business().ifPersonCanManageApplicationOrProcess(effectivePerson,
				process.getApplication(), process.getId()))) {
			return true;
		}
		if (ListTools.isEmpty(process.getStartableIdentityList()) && ListTools.isEmpty(process.getStartableUnitList())
				&& ListTools.isEmpty(process.getStartableGroupList())) {
			return true;
		}
		if (ListTools.isNotEmpty(process.getStartableIdentityList())
				&& ListTools.containsAny(identities, process.getStartableIdentityList())) {
			return true;
		} else {
			if (ListTools.isNotEmpty(process.getStartableUnitList())
					&& ListTools.containsAny(units, process.getStartableUnitList())) {
				return true;
			} else {
				if (ListTools.isNotEmpty(process.getStartableGroupList())
						&& ListTools.containsAny(groups, process.getStartableGroupList())) {
					return true;
				}
			}
		}
		return false;
	}
}
