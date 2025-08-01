package com.x.correlation.service.processing.jaxrs.correlation;

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
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionCreateTypeCmsWo;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionUpdateWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.SiteTargetWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.TargetWo;
import com.x.correlation.service.processing.Business;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

class ActionUpdateTypeCms extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateTypeCms.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String document, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, job:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> document,
				() -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wo wo = new Wo();
			Business business = new Business(emc);
			this.checkPermissionReadFromCms(wi.getPerson(), document);
			for(SiteTargetWi siteTargetWi : wi.getSiteTargetList()) {
				if(StringUtils.isEmpty(siteTargetWi.getSite())){
					continue;
				}
				siteTargetWi.getTargetList().forEach(o -> o.setSite(siteTargetWi.getSite()));
				Pair<List<Correlation>, List<TargetWo>> pair = this.readTarget(wi.getPerson(),
						business,
						siteTargetWi.getTargetList());
				List<Correlation> list = emc .listEqualAndEqualAndEqual(Correlation.class, Correlation.FROMTYPE_FIELDNAME,
						Correlation.TYPE_CMS, Correlation.FROMBUNDLE_FIELDNAME, document, Correlation.SITE_FIELDNAME, siteTargetWi.getSite());
				emc.beginTransaction(Correlation.class);
				for (Correlation o : list){
					emc.remove(o);
				}
				pair.first().forEach( o -> {
					try {
						o.setFromType(Correlation.TYPE_CMS);
						o.setFromBundle(document);
						o.setPerson(wi.getPerson());
						emc.persist(o, CheckPersistType.all);
					} catch (Exception e) {
						LOGGER.error(e);
					}
				});
				emc.commit();
				wo.getSuccessList().addAll(pair.first());
				wo.getFailureList().addAll(pair.second());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends ActionUpdateWi {

		private static final long serialVersionUID = -1782585450737681793L;

	}

	public static class Wo extends ActionCreateTypeCmsWo {

		private static final long serialVersionUID = -3922704779590488369L;

	}

}
