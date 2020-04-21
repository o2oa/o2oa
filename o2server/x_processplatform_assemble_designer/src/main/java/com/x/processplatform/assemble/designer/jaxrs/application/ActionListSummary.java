package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Form_;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

class ActionListSummary extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = this.list(business, effectivePerson);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Application {

		private static final long serialVersionUID = -7648824521711153693L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("流程对象")
		private List<WoProcess> processList = new ArrayList<>();

		@FieldDescribe("表单对象")
		private List<WoForm> formList = new ArrayList<>();

		public List<WoProcess> getProcessList() {
			return processList;
		}

		public void setProcessList(List<WoProcess> processList) {
			this.processList = processList;
		}

		public List<WoForm> getFormList() {
			return formList;
		}

		public void setFormList(List<WoForm> formList) {
			this.formList = formList;
		}

	}

	public static class WoProcess extends Process {

		private static final long serialVersionUID = 1439909268641168987L;

		static WrapCopier<Process, WoProcess> copier = WrapCopierFactory.wo(Process.class, WoProcess.class,
				ListTools.toList(JpaObject.id_FIELDNAME, "application", "name", "updateTime"), null);

	}

	public static class WoForm extends Form {

		private static final long serialVersionUID = 1513668573527819003L;

		static WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo(Form.class, WoForm.class,
				ListTools.toList(JpaObject.id_FIELDNAME, "application", "name", "updateTime"), null);

	}

	private List<Wo> list(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Application> cq = cb.createQuery(Application.class);
		Root<Application> root = cq.from(Application.class);
		cq.select(root);
		if ((effectivePerson.isNotManager()) && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
			Predicate p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList));
			p = cb.or(p, cb.equal(root.get(Application_.creatorPerson), effectivePerson.getDistinguishedName()));
			cq.where(p);
		}
		List<Application> os = em.createQuery(cq).getResultList();
		List<String> applicationIds = ListTools.extractProperty(os, JpaObject.id_FIELDNAME, String.class, true, true);
		Map<String, List<WoProcess>> processMap = this.mapProcess(business, applicationIds);
		Map<String, List<WoForm>> formMap = this.mapForm(business, applicationIds);
		List<Wo> wos = new ArrayList<>();
		for (Application o : os) {
			Wo wo = Wo.copier.copy(o);
			List<WoProcess> ps = processMap.get(wo.getId());
			if (ListTools.isNotEmpty(ps)) {
				wo.setProcessList(business.process().sort(ps));
			}
			List<WoForm> fs = formMap.get(wo.getId());
			if (ListTools.isNotEmpty(fs)) {
				wo.setFormList(business.form().sort(fs));
			}
			wos.add(wo);
		}
		return business.application().sort(wos);
	}

	private Map<String, List<WoProcess>> mapProcess(Business business, List<String> applicationIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = root.get(Process_.application).in(applicationIds);
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)),
				cb.isNull(root.get(Process_.editionEnable))));
		cq.select(root).where(p);
		List<Process> os = em.createQuery(cq).getResultList();
		List<WoProcess> wos = WoProcess.copier.copy(os);
		Map<String, List<WoProcess>> map = wos.stream().collect(Collectors.groupingBy(Process::getApplication));
		return map;
	}

	/** From 的 data和mobileData 数据量大,如果直接取出,会有秒级的延时 */
	private Map<String, List<WoForm>> mapForm(Business business, List<String> applicationIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Form.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Form> root = cq.from(Form.class);
		Predicate p = root.get(Form_.application).in(applicationIds);
		cq.select(root.get(Form_.id)).where(p);
		List<String> ids = em.createQuery(cq).getResultList();
		List<Form> os = business.entityManagerContainer().fetch(ids, Form.class,
				WoForm.copier.getCopyFields());
		List<WoForm> wos = WoForm.copier.copy(os);
		Map<String, List<WoForm>> map = wos.stream().collect(Collectors.groupingBy(Form::getApplication));
		return map;
	}

}