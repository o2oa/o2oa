package com.x.cms.assemble.control.jaxrs.templateform;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.utils.StringTools;
import com.x.cms.assemble.control.jaxrs.templateform.exception.EmptyNameException;
import com.x.cms.assemble.control.jaxrs.templateform.exception.InvalidCategoryException;
import com.x.cms.assemble.control.jaxrs.templateform.exception.InvalidNameException;
import com.x.cms.core.entity.element.TemplateForm;

class ActionCreate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInTemplateForm wrapIn = this.convertToWrapIn(jsonElement, WrapInTemplateForm.class);
			ActionResult<WrapOutId> result = new ActionResult<>();
			if (!StringTools.isSimply(wrapIn.getCategory())) {
				throw new InvalidCategoryException(wrapIn.getCategory());
			}
			if (StringUtils.isEmpty(wrapIn.getName())) {
				throw new EmptyNameException();
			}
			if (!StringTools.isSimply(wrapIn.getName())) {
				throw new InvalidNameException(wrapIn.getName());
			}
			TemplateForm o = emc.find(wrapIn.getId(), TemplateForm.class);
			emc.beginTransaction(TemplateForm.class);
			/** 设计端的ID都是由前台提供的,这里的拷贝需要拷贝id */
			if (null != o) {
				inCopier.copy(wrapIn, o);
				emc.check(o, CheckPersistType.all);
			} else {
				o = new TemplateForm();
				inCopier.copy(wrapIn, o);
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			
			ApplicationCache.notify(TemplateForm.class);
			WrapOutId wrap = new WrapOutId(o.getId());
			result.setData(wrap);
			return result;
		}
	}
}
