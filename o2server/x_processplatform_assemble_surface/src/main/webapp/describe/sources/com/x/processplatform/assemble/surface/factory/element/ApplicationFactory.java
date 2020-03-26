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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;

import net.sf.ehcache.Element;

public class ApplicationFactory extends ElementFactory {

	public ApplicationFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
		this.cache = ApplicationCache.instance().getCache(Application.class);
	}

	public List<Application> pick(List<String> flags) throws Exception {
		List<Application> list = new ArrayList<>();
		for (String str : flags) {
			Element element = cache.get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((Application) element.getObjectValue());
				}
			} else {
				Application o = this.pickObject(str);
				cache.put(new Element(str, o));
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public Application pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Application o = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (Application) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
		}
		return o;
	}

	private Application pickObject(String flag) throws Exception {
		Application o = this.business().entityManagerContainer().flag(flag, Application.class);
		if (o != null) {
			this.entityManagerContainer().get(Application.class).detach(o);
		}
		return o;
	}

	/* 判断用户是否有管理权限 */
	public boolean allowControl(EffectivePerson effectivePerson, Application application) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (null != application) {
			if (effectivePerson.isPerson(application.getControllerList())) {
				return true;
			}
			if (effectivePerson.isPerson(application.getCreatorPerson())) {
				return true;
			}
		}
		return false;
	}

	/* 判断是否有阅读的权限 */
	public boolean allowRead(EffectivePerson effectivePerson, List<String> roles, List<String> identities,
			List<String> units, Application application) throws Exception {
		if (null == application) {
			return false;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (StringUtils.equals(effectivePerson.getDistinguishedName(), application.getCreatorPerson())
				|| application.getControllerList().contains(effectivePerson.getDistinguishedName())) {
			return true;
		}
		if (application.getAvailableIdentityList().isEmpty() && application.getAvailableUnitList().isEmpty()) {
			return true;
		}
		if (this.business().organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformManager)) {
			return true;
		}
		if (CollectionUtils.containsAny(application.getAvailableIdentityList(), identities)) {
			return true;
		}
		if (CollectionUtils.containsAny(application.getAvailableUnitList(), units)) {
			return true;
		}
		return false;
	}

	/* 获取用户可启动的流程，如果applicationId 为空则取到所有可启动流程 */
	public List<String> listAvailable(EffectivePerson effectivePerson, List<String> roles, List<String> identities,
			List<String> units) throws Exception {
		List<String> list = new ArrayList<>();
		EntityManager em = this.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		cq.select(root.get(Application_.id)).distinct(true);
		if (effectivePerson.isNotManager() && (!this.business().organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
			Predicate p = cb.and(cb.isEmpty(root.get(Application_.availableIdentityList)),
					cb.isEmpty(root.get(Application_.availableUnitList)));
			p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList)));
			p = cb.or(p, cb.equal(root.get(Application_.creatorPerson), effectivePerson.getDistinguishedName()));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Application_.availableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(units)) {
				p = cb.or(p, root.get(Application_.availableUnitList).in(units));
			}
			cq.where(p);
		}
		list = em.createQuery(cq.distinct(true)).getResultList();
		return list;
	}

	public <T extends Application> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(Application::getAlias, Comparator.nullsLast(String::compareTo))
						.thenComparing(Application::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}