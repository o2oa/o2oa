//package com.x.processplatform.assemble.surface.jaxrs.workcompleted;
//
//import org.apache.commons.lang3.BooleanUtils;
//
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.project.exception.ExceptionEntityNotExist;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.processplatform.assemble.surface.Business;
//import com.x.processplatform.assemble.surface.WorkCompletedControl;
//import com.x.processplatform.core.entity.content.WorkCompleted;
//import com.x.processplatform.core.entity.element.Form;
//
//class ActionComplexMobile extends BaseAction {
//
//	ActionResult<Wo> execute(String id, EffectivePerson effectivePerson) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			ActionResult<Wo> result = new ActionResult<>();
//			Business business = new Business(emc);
//			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
//			if (null == workCompleted) {
//				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
//			}
//			Wo wo = this.get(business, effectivePerson, workCompleted, Wo.class);
//			wo.setForm(this.getForm(business, workCompleted));
//			WorkCompletedControl control = wo.getControl();
//			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
//				throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(), id);
//			}
//			result.setData(wo);
//			return result;
//		}
//	}
//
//	public static class Wo extends AbstractWo {
//	}
//
//	private WoForm getForm(Business business, WorkCompleted workCompleted) throws Exception {
//		Form form = business.form().pick(workCompleted.getForm());
//		if (null == form) {
//			return null;
//		}
//		WoForm wo = WoForm.copier.copy(form);
//		wo.setData(form.getMobileDataOrData());
//		return wo;
//	}
//
//}
