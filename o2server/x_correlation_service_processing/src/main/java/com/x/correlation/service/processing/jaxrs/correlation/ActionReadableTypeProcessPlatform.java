package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionReadableTypeProcessPlatformWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionReadableTypeProcessPlatformWo;
import com.x.correlation.service.processing.Business;

class ActionReadableTypeProcessPlatform extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReadableTypeProcessPlatform.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<Correlation> os = emc.fetchEqualAndEqual(Correlation.class,
					Arrays.asList(Correlation.FROMTYPE_FIELDNAME, Correlation.FROMBUNDLE_FIELDNAME),
					Correlation.TARGETTYPE_FIELDNAME, Correlation.TYPE_PROCESSPLATFORM,
					Correlation.TARGETBUNDLE_FIELDNAME, wi.getJob());
			Set<String> processPlatformSet = new HashSet<>();
			Set<String> cmsSet = new HashSet<>();
			Business business = new Business(emc);
			os.forEach(o -> {
				if (StringUtils.equalsIgnoreCase(o.getFromType(), Correlation.TYPE_PROCESSPLATFORM)) {
					processPlatformSet.add(o.getFromBundle());
				} else if (StringUtils.equalsIgnoreCase(o.getFromType(), Correlation.TYPE_CMS)) {
					cmsSet.add(o.getFromBundle());
				}
			});

			List<Pair<String, List<String>>> pairs = new ArrayList<>();

			ListTools.batch(new ArrayList<>(processPlatformSet), 10)
					.forEach(o -> pairs.add(Pair.of(Correlation.TYPE_PROCESSPLATFORM, o)));

			ListTools.batch(new ArrayList<>(cmsSet), 10).forEach(o -> pairs.add(Pair.of(Correlation.TYPE_CMS, o)));

			Optional<Pair<String, List<String>>> opt = pairs.stream().filter(p -> {
				try {
					if (StringUtils.equalsIgnoreCase(p.first(), Correlation.TYPE_PROCESSPLATFORM)) {
						return processPlatformHasReview(business, wi.getPerson(), p.second());
					} else if (StringUtils.equalsIgnoreCase(p.first(), Correlation.TYPE_CMS)) {
						return cmsHasReviewOrPermissionAny(business, wi.getPerson(), p.second());
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
				return false;
			}).findFirst();
			Wo wo = new Wo();
			wo.setValue(opt.isPresent());
			result.setData(wo);
			return result;
		}
	}


	public static class Wi extends ActionReadableTypeProcessPlatformWi {

		private static final long serialVersionUID = 6266609364542899147L;

	}

	public static class Wo extends ActionReadableTypeProcessPlatformWo {

		private static final long serialVersionUID = -1905429989738754325L;

	}

}