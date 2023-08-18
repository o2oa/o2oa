package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeProcessPlatformWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeProcessPlatformWo;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.TargetWo;
import com.x.correlation.service.processing.Business;

class ActionCreateTypeProcessPlatform extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateTypeProcessPlatform.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, job:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> job,
				() -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			this.checkAllowVisitProcessPlatform(wi.getPerson(), job);
			Pair<List<Correlation>, List<TargetWo>> pair = this.readTarget(wi.getPerson(), business,
					wi.getTargetList());
			Map<String, Correlation> exists = this.exists(business, Correlation.TYPE_PROCESSPLATFORM, job);
			emc.beginTransaction(Correlation.class);
			pair.first().stream().forEach(o -> exists.compute(o.getTargetType() + o.getTargetBundle(), (k, v) -> {
				try {
					if (null == v) {
						o.setFromType(Correlation.TYPE_PROCESSPLATFORM);
						o.setFromBundle(job);
						o.setPerson(wi.getPerson());
						emc.persist(o, CheckPersistType.all);
					} else {
						v.setTargetTitle(o.getTargetTitle());
						v.setSite(o.getSite());
						v.setView(o.getView());
						emc.check(v, CheckPersistType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
				return v;
			}));
			emc.commit();
			Wo wo = new Wo();
			wo.setSuccessList(pair.first());
			wo.setFailureList(pair.second());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends ActionCreateTypeProcessPlatformWi {

		private static final long serialVersionUID = -1782585450737681793L;

	}

	public static class Wo extends ActionCreateTypeProcessPlatformWo {

		private static final long serialVersionUID = 8119049505336942577L;

	}

}