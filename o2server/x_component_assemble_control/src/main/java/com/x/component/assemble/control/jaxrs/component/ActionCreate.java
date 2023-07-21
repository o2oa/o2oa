package com.x.component.assemble.control.jaxrs.component;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.component.assemble.control.Business;
import com.x.component.core.entity.Component;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.editable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Component component = Wi.copier.copy(wi);
			component.setType(Component.TYPE_CUSTOM);
			emc.beginTransaction(Component.class);
			emc.persist(component, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(component.getId());
			result.setData(wo);
			CacheManager.notify(Component.class);
			return result;
		}
	}

	@Schema(name = "com.x.component.assemble.control.jaxrs.component.ActionCreate$Wi")
	public static class Wi extends Component {

		private static final long serialVersionUID = 8867806242224800105L;
		static WrapCopier<Wi, Component> copier = WrapCopierFactory.wi(Wi.class, Component.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Component.TYPE_FIELDNAME));

	}

	@Schema(name = "com.x.component.assemble.control.jaxrs.component.ActionCreate$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 3532574377163941604L;

	}
}
