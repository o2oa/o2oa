package com.x.component.assemble.control.jaxrs.component;

import org.apache.commons.codec.binary.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.component.assemble.control.Business;
import com.x.component.core.entity.Component;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.editable(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Component component = emc.flag(flag, Component.class);
			if (null == component) {
				throw new ExceptionEntityNotExist(flag, Component.class);
			}
			if (StringUtils.equals(component.getType(), Component.TYPE_SYSTEM)) {
				throw new ExceptionDeleteSystemComponent();
			}

			emc.beginTransaction(Component.class);
			emc.remove(component, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			CacheManager.notify(Component.class);
			return result;
		}
	}

	@Schema(name = "com.x.component.assemble.control.jaxrs.component.ActionDelete$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 412605394505528213L;

	}
}
