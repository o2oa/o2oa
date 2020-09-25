package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

class V2Get extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2Get.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Form form = emc.find(id, Form.class);
			if (Objects.isNull(form)) {
				throw new ExceptionEntityNotExist(id, Form.class);
			}
			Wo wo = new Wo();
			wo.setForm(toWoFormDataOrMobileData(form));
			related(business, wo, form);
			result.setData(wo);
			return result;
		}
	}

	private void related(Business business, Wo wo, Form form) throws Exception {
		if (StringUtils.isNotBlank(form.getData())) {
			for (String relatedFormId : form.getProperties().getRelatedFormList()) {
				Form relatedForm = business.form().pick(relatedFormId);
				if (null != relatedForm) {
					wo.getRelatedFormMap().put(relatedFormId, toWoFormDataOrMobileData(relatedForm));
				}
			}
		} else {
			for (String mobileRelatedFormId : form.getProperties().getMobileRelatedFormList()) {
				Form mobileRelatedForm = business.form().pick(mobileRelatedFormId);
				if (null != mobileRelatedForm) {
					wo.getRelatedFormMap().put(mobileRelatedFormId, toWoFormMobileDataOrData(mobileRelatedForm));
				}
			}
		}
		relatedScript(business, wo, form);
	}

	protected void relatedScript(Business business, AbstractWo wo, Form form) throws Exception {
		for (Entry<String, String> entry : form.getProperties().getRelatedScriptMap().entrySet()) {
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