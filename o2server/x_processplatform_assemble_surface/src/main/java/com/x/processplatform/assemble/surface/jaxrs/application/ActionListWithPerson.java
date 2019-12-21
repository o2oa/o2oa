package com.x.processplatform.assemble.surface.jaxrs.application;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

class ActionListWithPerson extends BaseAction {

	/**
	 * 1.身份在可使用列表中<br/>
	 * 2.组织在可使用组织中 <br/>
	 * 4.没有限定身份和组织 <br/>
	 * 5.个人在应用管理员中 <br/>
	 * 6.应用的创建人员 <br/>
	 * 7.个人有Manage权限或者ProcessPlatformManager身份
	 */
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			/** 去除部门以及上级部门,如果设置了一级部门可用,那么一级部门下属的二级部门也可用 */
			List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
			List<String> roles = business.organization().role().listWithPerson(effectivePerson);
			List<String> ids = this.list(business, effectivePerson, roles, identities, units);
			for (String id : ids) {
				Application o = business.application().pick(id);
				wos.add(Wo.copier.copy(o));
			}
			wos = business.application().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Application {

		private static final long serialVersionUID = -4862564047240738097L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	/**
	 * 从可见的application中获取一份ids<br/>
	 * 从可启动的process中获取一份ids <br/>
	 * 两份ids的交集,这样避免列示只有application没有可以启动process的应用
	 */
	private List<String> list(Business business, EffectivePerson effectivePerson, List<String> roles,
			List<String> identities, List<String> units) throws Exception {
		List<String> ids = this.listFromApplication(business, effectivePerson, roles, identities, units);
		return ids;
		// List<String> fromProcessIds = this.listFromProcess(business, effectivePerson,
		// roles, identities, units);
//		return ListUtils.intersection(ids, fromProcessIds);
	}

	private List<String> listFromApplication(Business business, EffectivePerson effectivePerson, List<String> roles,
			List<String> identities, List<String> units) throws Exception {
		List<String> list = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
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
		list = em.createQuery(cq.select(root.get(Application_.id)).distinct(true)).getResultList();
		return list;
	}

	/**
	 * 
	 * 从Process中获取可以启动的Process的application.
	 */
	private List<String> listFromProcess(Business business, EffectivePerson effectivePerson, List<String> roles,
			List<String> identities, List<String> units) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
			p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
					cb.isEmpty(root.get(Process_.startableUnitList)));
			p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Process_.controllerList)));
			p = cb.or(p, cb.equal(root.get(Process_.creatorPerson), effectivePerson.getDistinguishedName()));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(units)) {
				p = cb.or(p, root.get(Process_.startableUnitList).in(units));
			}
		}
		cq.select(root.get(Process_.application)).distinct(true).where(p);
		return em.createQuery(cq).getResultList();
	}
}