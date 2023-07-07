package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionDeleteTypeCmsWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionDeleteTypeCmsWo;

class ActionDeleteTypeCms extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteTypeCms.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, job:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> job,
				() -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<Correlation> os = emc.list(Correlation.class, wi.getIdList());
			if (!os.isEmpty()) {
				emc.beginTransaction(Correlation.class);
				for (Correlation o : os) {
					if (!StringUtils.equalsIgnoreCase(o.getFromType(), Correlation.TYPE_CMS)) {
						throw new ExceptionTypeNotMatch(o.getFromType(), Correlation.TYPE_CMS);
					}
					if (!StringUtils.equalsIgnoreCase(o.getFromBundle(), job)) {
						throw new ExceptionBundleNotMatch(o.getFromBundle(), job);
					}
				}
				for (Correlation o : os) {
					emc.remove(o, CheckRemoveType.all);
				}
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends ActionDeleteTypeCmsWi {

		private static final long serialVersionUID = 7942738146562548865L;

	}

	public static class Wo extends ActionDeleteTypeCmsWo {

		private static final long serialVersionUID = -3610071908279960597L;

	}

}