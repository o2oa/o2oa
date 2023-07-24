package com.x.processplatform.assemble.designer.jaxrs.elementtool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormField;
import com.x.processplatform.core.entity.element.FormField_;
import com.x.processplatform.core.entity.element.Form_;

class ActionFormOrphan extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> applicationIds = emc.ids(Application.class);
			List<String> formIds = emc.ids(Form.class);
			Wo wo = new Wo();
			wo.setFormList(emc.fetch(this.listOrphanForm(business, applicationIds), WoForm.copier));
			wo.setFormFieldList(emc.fetch(this.listOrphanFormField(business, formIds), WoFormField.copier));
			result.setData(wo);
			return result;
		}
	}

	private List<String> listOrphanForm(Business business, List<String> applicationIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Form.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Form> root = cq.from(Form.class);
		Predicate p = cb.not(root.get(Form_.application).in(applicationIds));
		cq.select(root.get(Form_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	private List<String> listOrphanFormField(Business business, List<String> formIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(FormField.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<FormField> root = cq.from(FormField.class);
		Predicate p = cb.not(root.get(FormField_.form).in(formIds));
		cq.select(root.get(FormField_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public static class WoForm extends Form {

		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Form, WoForm> copier = WrapCopierFactory.wo(Form.class, WoForm.class, ListTools.toList(
				JpaObject.id_FIELDNAME, Form.name_FIELDNAME, Form.alias_FIELDNAME, Form.application_FIELDNAME), null);
	}

	public static class WoFormField extends FormField {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<FormField, WoFormField> copier = WrapCopierFactory.wo(FormField.class, WoFormField.class,
				ListTools.toList(JpaObject.id_FIELDNAME, WoFormField.name_FIELDNAME, WoFormField.form_FIELDNAME), null);
	}

	public static class Wo extends GsonPropertyObject {

		private List<WoForm> formList = new ArrayList<>();
		private List<WoFormField> formFieldList = new ArrayList<>();

		public List<WoForm> getFormList() {
			return formList;
		}

		public void setFormList(List<WoForm> formList) {
			this.formList = formList;
		}

		public List<WoFormField> getFormFieldList() {
			return formFieldList;
		}

		public void setFormFieldList(List<WoFormField> formFieldList) {
			this.formFieldList = formFieldList;
		}

	}
}