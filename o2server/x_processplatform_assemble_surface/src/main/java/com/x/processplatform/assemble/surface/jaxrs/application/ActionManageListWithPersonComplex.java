package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
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
import org.apache.commons.collections4.ListUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ActionManageListWithPersonComplex extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);

			if (effectivePerson.isManager()) {
				List<String> identities = business.organization().identity().listWithPerson(person);
				/** 去除部门以及上级部门,如果设置了一级部门可用,那么一级部门下属的二级部门也可用 */
				List<String> units = business.organization().unit().listWithPersonSupNested(person);
				List<String> roles = business.organization().role().listWithPerson(person);
				List<String> ids = this.list(business, effectivePerson, roles, identities, units);
				for (String id : ids) {
					Application o = business.application().pick(id);
					if (null != o) {
						Wo wo = Wo.copier.copy(o);
						wo.setProcessList(this.referenceProcess(business, effectivePerson, identities, units, o));
						wos.add(wo);
					}
				}
				wos = business.application().sort(wos);
				result.setData(wos);
			}

			return result;
		}
	}

	public static class Wo extends Application {

		private static final long serialVersionUID = 1176431364379021779L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("流程对象")
		private List<WoProcess> processList;

		@FieldDescribe("是否可编辑")
		private Boolean allowControl;

		public Boolean getAllowControl() {
			return allowControl;
		}

		public void setAllowControl(Boolean allowControl) {
			this.allowControl = allowControl;
		}

		public List<WoProcess> getProcessList() {
			return processList;
		}

		public void setProcessList(List<WoProcess> processList) {
			this.processList = processList;
		}

	}

	public static class WoProcess extends Process {

		private static final long serialVersionUID = 8239383153729965245L;
		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static WrapCopier<Process, WoProcess> copier = WrapCopierFactory.wo(Process.class, WoProcess.class, null,
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
		List<String> fromProcessIds = this.listFromProcess(business, effectivePerson, roles, identities, units);
		return ListUtils.intersection(ids, fromProcessIds);
	}

	private List<String> listFromApplication(Business business, EffectivePerson effectivePerson, List<String> roles,
			List<String> identities, List<String> units) throws Exception {
		List<String> list = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);

		Predicate p = cb.and(cb.isEmpty(root.get(Application_.availableIdentityList)),
				cb.isEmpty(root.get(Application_.availableUnitList)));

		if (ListTools.isNotEmpty(identities)) {
			p = cb.or(p, root.get(Application_.availableIdentityList).in(identities));
		}
		if (ListTools.isNotEmpty(units)) {
			p = cb.or(p, root.get(Application_.availableUnitList).in(units));
		}
		cq.where(p);

		list = em.createQuery(cq.select(root.get(Application_.id))).getResultList().stream().distinct()
				.collect(Collectors.toList());
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

		p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
				cb.isEmpty(root.get(Process_.startableUnitList)));

		if (ListTools.isNotEmpty(identities)) {
			p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
		}
		if (ListTools.isNotEmpty(units)) {
			p = cb.or(p, root.get(Process_.startableUnitList).in(units));
		}

		cq.select(root.get(Process_.application)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	private List<WoProcess> referenceProcess(Business business, EffectivePerson effectivePerson,
			List<String> identities, List<String> units, Application application) throws Exception {
		List<String> ids = this.listStartableWithApplication(business, effectivePerson, identities, units, application);
		List<WoProcess> wos = new ArrayList<>();
		for (String id : ids) {
			WoProcess o = WoProcess.copier.copy(business.process().pick(id));
			wos.add(o);
		}
		wos = business.process().sort(wos);
		return wos;
	}

	/* 获取用户可启动的流程，如果applicationId 为空则取到所有可启动流程 */
	private List<String> listStartableWithApplication(Business business, EffectivePerson effectivePerson,
			List<String> identities, List<String> units, Application application) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();

		p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
				cb.isEmpty(root.get(Process_.startableUnitList)));

		if (ListTools.isNotEmpty(identities)) {
			p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
		}
		if (ListTools.isNotEmpty(units)) {
			p = cb.or(p, root.get(Process_.startableUnitList).in(units));
		}

		p = cb.and(p, cb.equal(root.get(Process_.application), application.getId()));
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)), cb.isNull(root.get(Process_.editionEnable))));
		cq.select(root.get(Process_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}
}
