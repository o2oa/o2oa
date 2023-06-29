package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.bean.tuple.Sextuple;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeCmsWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeProcessPlatformWo;
import com.x.correlation.service.processing.Business;

class ActionCreateTypeCms extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateTypeCms.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String document, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, job:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> document,
				() -> jsonElement);

		ActionResult<List<Wo>> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		List<Wo> wos = new ArrayList<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			this.checkFromCms(business, wi.getPerson(), document);
			List<Correlation> targets = this.readTarget(effectivePerson, business, wi.getTargetList());
			Map<String, Correlation> exists = this.exists(business, Correlation.TYPE_CMS, document);
			emc.beginTransaction(Correlation.class);
			targets.stream().forEach(o -> exists.compute(o.getTargetType() + o.getTargetBundle(), (k, v) -> {
				try {
					if (null == v) {
						o.setFromType(Correlation.TYPE_CMS);
						o.setFromBundle(document);
						o.setPerson(wi.getPerson());
						emc.persist(o, CheckPersistType.all);
					} else {
						v.setTargetTitle(o.getTargetTitle());
						v.setSite(o.getSite());
						emc.check(v, CheckPersistType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
				return v;
			}));
			emc.commit();
			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends ActionCreateTypeCmsWi {

		private static final long serialVersionUID = -1782585450737681793L;

	}

	public static class Wo extends ActionCreateTypeProcessPlatformWo {

		private static final long serialVersionUID = 8119049505336942577L;

		static WrapCopier<Correlation, Wo> copier = WrapCopierFactory.wo(Correlation.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}