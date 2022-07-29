package com.x.component.assemble.control.jaxrs.component;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.component.assemble.control.Business;
import com.x.component.core.entity.Component;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionEdit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);
	private static final String SYSTEM_SETTING_NAME = "Setting";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.editable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Component component = emc.flag(flag, Component.class);
			if (null == component) {
				throw new ExceptionEntityNotExist(flag, Component.class);
			}
			Wi.copier.copy(wi, component);
			List<String> names = ListTools.extractProperty(Config.components().getSystems(), "name", String.class, true,
					true);
			if (ListTools.contains(names, component.getName())) {
				component.setType(Component.TYPE_SYSTEM);
			} else {
				component.setType(Component.TYPE_CUSTOM);
			}
			if(SYSTEM_SETTING_NAME.equals(component.getName())){
				component.setVisible(true);
			}
			emc.beginTransaction(Component.class);
			emc.persist(component, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			CacheManager.notify(Component.class);
			return result;
		}
	}

	@Schema(name = "com.x.component.assemble.control.jaxrs.component.ActionEdit$Wi")
	public static class Wi extends Component {

		private static final long serialVersionUID = 8867806242224800105L;
		static WrapCopier<Wi, Component> copier = WrapCopierFactory.wi(Wi.class, Component.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Component.TYPE_FIELDNAME));

	}

	@Schema(name = "com.x.component.assemble.control.jaxrs.component.ActionEdit$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 8280946227549787499L;

	}
}
