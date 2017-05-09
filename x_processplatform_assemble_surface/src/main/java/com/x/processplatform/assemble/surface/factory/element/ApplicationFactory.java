package com.x.processplatform.assemble.surface.factory.element;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;

public class ApplicationFactory extends ElementFactory {

	public ApplicationFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Application pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Application pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Application.class, exceptionWhen, Application.FLAGS);
	}

	/* 判断用户是否有管理权限 */
	public boolean allowControl(EffectivePerson effectivePerson, Application application) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (null != application) {
			if (effectivePerson.isUser(application.getControllerList())) {
				return true;
			}
			if (effectivePerson.isUser(application.getCreatorPerson())) {
				return true;
			}
		}
		return false;
	}

	/* 判断是否有阅读的权限 */
	public boolean allowRead(EffectivePerson effectivePerson, List<String> roles, List<String> identities,
			List<String> departments, List<String> companies, Application application) throws Exception {
		if (null == application) {
			return false;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (StringUtils.equals(effectivePerson.getName(), application.getCreatorPerson())
				|| application.getControllerList().contains(effectivePerson.getName())) {
			return true;
		}
		if (application.getAvailableIdentityList().isEmpty() && application.getAvailableDepartmentList().isEmpty()
				&& application.getAvailableCompanyList().isEmpty()) {
			return true;
		}
		// Organization organization = this.business().organization();
		// List<String> roles =
		// organization.role().listNameWithPerson(effectivePerson.getName());
		if (roles.contains(RoleDefinition.ProcessPlatformManager)) {
			return true;
		}
		// List<String> identities =
		// organization.identity().listNameWithPerson(effectivePerson.getName());
		if (CollectionUtils.containsAny(application.getAvailableIdentityList(), identities)) {
			return true;
		}
		// List<String> departments =
		// organization.department().listNameWithPersonSupNested(effectivePerson.getName());
		if (CollectionUtils.containsAny(application.getAvailableDepartmentList(), departments)) {
			return true;
		}
		// List<String> companies =
		// organization.company().listNameWithPersonSupNested(effectivePerson.getName());
		if (CollectionUtils.containsAny(application.getAvailableCompanyList(), companies)) {
			return true;
		}
		return false;
	}

	/* 获取用户可启动的流程，如果applicationId 为空则取到所有可启动流程 */
	public List<String> listAvailable(EffectivePerson effectivePerson, List<String> roles, List<String> identities,
			List<String> departments, List<String> companies) throws Exception {
		List<String> list = new ArrayList<>();
		EntityManager em = this.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		cq.select(root.get(Application_.id)).distinct(true);
		if (effectivePerson.isNotManager() && (!ListTools.contains(roles, RoleDefinition.ProcessPlatformManager))) {
			Predicate p = cb.and(cb.isEmpty(root.get(Application_.availableIdentityList)),
					cb.isEmpty(root.get(Application_.availableDepartmentList)),
					cb.isEmpty(root.get(Application_.availableCompanyList)));
			p = cb.or(p, cb.isMember(effectivePerson.getName(), root.get(Application_.controllerList)));
			p = cb.or(p, cb.equal(root.get(Application_.creatorPerson), effectivePerson.getName()));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Application_.availableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(departments)) {
				p = cb.or(p, root.get(Application_.availableDepartmentList).in(departments));
			}
			if (ListTools.isNotEmpty(companies)) {
				p = cb.or(p, root.get(Application_.availableCompanyList).in(companies));
			}
			cq.where(p);
		}
		list = em.createQuery(cq).getResultList();
		return list;
	}

	public <T extends JpaObject> String pickName(String id, Class<T> clz, String person) throws Exception {
		Application o = this.pick(id);
		if (null != o) {
			return o.getName();
		} else {
			EntityManager em = this.entityManagerContainer().get(clz);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<T> root = cq.from(clz);
			Predicate p = cb.equal(root.get("application"), id);
			if (StringUtils.isNotEmpty(person)) {
				p = cb.and(p, cb.equal(root.get("person"), person));
			}
			cq.select(root.get("applicationName")).where(p);
			List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
			if (!list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}

}