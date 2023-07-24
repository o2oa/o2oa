package com.x.cms.assemble.control.jaxrs.templateform;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.TemplateForm;

class ActionCreate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.isManager( effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if (!StringTools.isSimply(wi.getCategory())) {
				throw new ExceptionInvalidCategory(wi.getCategory());
			}
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionEmptyName();
			}
			if (!StringTools.isSimply(wi.getName())) {
				throw new ExceptionInvalidName(wi.getName());
			}
			TemplateForm o = emc.find(wi.getId(), TemplateForm.class);
			emc.beginTransaction(TemplateForm.class);
			/** 设计端的ID都是由前台提供的,这里的拷贝需要拷贝id */
			if (null != o) {
				Wi.copier.copy(wi, o);
				emc.check(o, CheckPersistType.all);
			} else {
				o = new TemplateForm();
				Wi.copier.copy(wi, o);
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			CacheManager.notify(TemplateForm.class);
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends TemplateForm {

		private static final long serialVersionUID = 2091352200751493447L;

		static WrapCopier<Wi, TemplateForm> copier = WrapCopierFactory.wi(Wi.class, TemplateForm.class, null, Arrays
				.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
						JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));

	}
}
