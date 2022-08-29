package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationDictFlag, String applicationFlag)
			throws Exception {

		LOGGER.debug("execute:{}, applicationDictFlag:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName,
				() -> applicationDictFlag, () -> applicationFlag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			String id = business.applicationDict().getWithApplicationWithUniqueName(application.getId(),
					applicationDictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionEntityExist(applicationFlag, ApplicationDict.class);
			}
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			Wo wo = Wo.copier.copy(dict);
			wo.setData(this.get(business, dict));
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.applicationdict.ActionGet$Wo")
	public static class Wo extends ApplicationDict {

		private static final long serialVersionUID = 9053835173356135926L;

		static WrapCopier<ApplicationDict, Wo> copier = WrapCopierFactory.wo(ApplicationDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("字典数据.")
		@Schema(description = "字典数据.")
		private JsonElement data;

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

	}

}
