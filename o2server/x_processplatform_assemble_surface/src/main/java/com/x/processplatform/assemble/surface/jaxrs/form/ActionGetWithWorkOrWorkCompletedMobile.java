package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

class ActionGetWithWorkOrWorkCompletedMobile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetWithWorkOrWorkCompletedMobile.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();

			Business business = new Business(emc);

			CompletableFuture<Wo> _wo = CompletableFuture.supplyAsync(() -> {
				Wo wo = null;
				try {
					Work work = emc.find(workOrWorkCompleted, Work.class);
					if (null != work) {
						wo = this.work(business, work);
					} else {
						wo = this.workCompleted(business, emc.flag(workOrWorkCompleted, WorkCompleted.class));
					}
				} catch (Exception e) {
					logger.error(e);
				}
				return wo;
			});

			CompletableFuture<Boolean> _control = CompletableFuture.supplyAsync(() -> {
				Boolean value = false;
				try {
					value = business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
							new ExceptionEntityNotExist(workOrWorkCompleted));
				} catch (Exception e) {
					logger.error(e);
				}
				return value;
			});

			if (BooleanUtils.isFalse(_control.get())) {
				throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
			}

			result.setData(_wo.get());
			return result;
		}
	}

	private Wo work(Business business, Work work) throws Exception {
		Wo wo = new Wo();
		String id = work.getForm();
		if (StringUtils.isEmpty(id)) {
			Activity activity = business.getActivity(work);
			id = PropertyTools.getOrElse(activity, Activity.form_FIELDNAME, String.class, "");
		}
		if (StringUtils.isNotEmpty(id)) {
			Form form = business.form().pick(id);
			if (null != form) {
				wo.setForm(toWoFormMobileDataOrData(form));
				related(business, wo, form);
			}
		}
		return wo;
	}

	private Wo workCompleted(Business business, WorkCompleted workCompleted) throws Exception {
		Wo wo = new Wo();
		// 先使用当前库的表单,如果不存在使用储存的表单.
		if (StringUtils.isNotEmpty(workCompleted.getForm())) {
			Form form = business.form().pick(workCompleted.getForm());
			if (null != form) {
				wo.setForm(toWoFormMobileDataOrData(form));
				related(business, wo, form);
			}
		} else if (null != workCompleted.getProperties().getForm()) {
			wo.setForm(toWoFormMobileDataOrData(workCompleted.getProperties().getForm()));
			if (StringUtils.isNotBlank(workCompleted.getProperties().getForm().getMobileData())) {
				workCompleted.getProperties().getMobileRelatedFormList()
						.forEach(o -> wo.getRelatedFormMap().put(o.getId(), toWoFormMobileDataOrData(o)));
			} else {
				workCompleted.getProperties().getRelatedFormList()
						.forEach(o -> wo.getRelatedFormMap().put(o.getId(), toWoFormDataOrMobileData(o)));
			}
		}
		workCompleted.getProperties().getRelatedScriptList().stream()
				.forEach(o -> wo.getRelatedScriptMap().put(o.getId(), toWoScript(o)));
		return wo;
	}

	private void related(Business business, Wo wo, Form form) throws Exception {
		if (StringUtils.isNotBlank(form.getMobileData())) {
			for (String mobileRelatedFormId : form.getProperties().getMobileRelatedFormList()) {
				Form relatedForm = business.form().pick(mobileRelatedFormId);
				if (null != relatedForm) {
					wo.getRelatedFormMap().put(mobileRelatedFormId, toWoFormMobileDataOrData(relatedForm));
				}
			}
		} else {
			for (String relatedFormId : form.getProperties().getRelatedFormList()) {
				Form relatedForm = business.form().pick(relatedFormId);
				if (null != relatedForm) {
					wo.getRelatedFormMap().put(relatedFormId, toWoFormDataOrMobileData(relatedForm));
				}
			}
		}
		relatedScript(business, wo, form);
	}

	protected void relatedScript(Business business, AbstractWo wo, Form form) throws Exception {
		for (Entry<String, String> entry : form.getProperties().getMobileRelatedScriptMap().entrySet()) {
			switch (entry.getValue()) {
			case WorkCompletedProperties.Script.TYPE_PROCESSPLATFORM:
				Script relatedScript = business.script().pick(entry.getKey());
				if (null != relatedScript) {
					wo.getRelatedScriptMap().put(entry.getKey(), toWoScript(relatedScript));
				}
				break;
			case WorkCompletedProperties.Script.TYPE_CMS:
				com.x.cms.core.entity.element.Script relatedCmsScript = business.cms().script().pick(entry.getKey());
				if (null != relatedCmsScript) {
					wo.getRelatedScriptMap().put(entry.getKey(), toWoScript(relatedCmsScript));
				}
				break;
			case WorkCompletedProperties.Script.TYPE_PORTAL:
				com.x.portal.core.entity.Script relatedPortalScript = business.portal().script().pick(entry.getKey());
				if (null != relatedPortalScript) {
					wo.getRelatedScriptMap().put(entry.getKey(), toWoScript(relatedPortalScript));
				}
				break;
			default:
				break;
			}
		}
	}

	public static class Wo extends AbstractWo {

	}

}