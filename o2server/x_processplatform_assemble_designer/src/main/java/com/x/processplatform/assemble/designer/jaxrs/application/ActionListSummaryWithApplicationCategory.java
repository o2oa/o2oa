package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.ArrayList;
import java.util.List;

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
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

class ActionListSummaryWithApplicationCategory extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationCategory) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			List<Application> os = this.list(business, effectivePerson, applicationCategory);
			for (Application o : os) {
				Wo wo = Wo.copier.copy(o);
				wo.setProcessList(business.process().sort(this.listProcess(business, o)));
				wo.setFormList(business.form().sort(this.listForm(business, o)));
				wos.add(wo);
			}
			wos = business.application().sort(wos);
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
				JpaObject.singularAttributeField(Process.class, true, true), null);
	}

	public static class WoForm extends Form {

		private static final long serialVersionUID = 1513668573527819003L;

		static WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo(Form.class, WoForm.class,
				JpaObject.singularAttributeField(Form.class, true, true), null);

	}

	private List<Application> list(Business business, EffectivePerson effectivePerson, String applicationCategory)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Application> cq = cb.createQuery(Application.class);
		Root<Application> root = cq.from(Application.class);
		cq.select(root);
		Predicate p = cb.equal(root.get(Application_.applicationCategory), applicationCategory);
		if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.ProcessPlatformManager))) {
			p = cb.and(p,
					cb.or(cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList)),
							cb.equal(root.get(Application_.creatorPerson), effectivePerson.getDistinguishedName())));
		}
		cq.where(p);
		return em.createQuery(cq).getResultList();
	}

	private List<WoProcess> listProcess(Business business, Application application) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application.getId());
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)), cb.isNull(root.get(Process_.editionEnable))));
		return business.entityManagerContainer().fetch(Process.class, WoProcess.copier, p);
	}

	private List<WoForm> listForm(Business business, Application application) throws Exception {
//		EntityManager em = business.entityManagerContainer().get(Form.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Form> cq = cb.createQuery(Form.class);
//		Root<Form> root = cq.from(Form.class);
//		Predicate p = cb.equal(root.get(Form_.application), application.getId());
//		cq.select(root).where(p);
//		return em.createQuery(cq).getResultList();
		return business.entityManagerContainer().fetchEqual(Form.class, WoForm.copier, Form.application_FIELDNAME,
				application.getId());
	}
}