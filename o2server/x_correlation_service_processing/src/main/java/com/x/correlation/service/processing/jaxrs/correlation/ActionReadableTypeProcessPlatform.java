package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionDeleteTypeProcessPlatformWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionDeleteTypeProcessPlatformWo;

class ActionReadableTypeProcessPlatform extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReadableTypeProcessPlatform.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<Correlation> os = emc.fetchEqualAndEqual(Correlation.class,
					Arrays.asList(Correlation.FROMTYPE_FIELDNAME, Correlation.FROMBUNDLE_FIELDNAME),
					Correlation.TARGETTYPE_FIELDNAME, Correlation.TYPE_PROCESSPLATFORM,
					Correlation.TARGETBUNDLE_FIELDNAME, job);
			os.stream().collect(Collectors.groupingBy(Correlation::getFromType)).entrySet().stream().forEach(o -> {

			});
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends ActionDeleteTypeProcessPlatformWi {

		private static final long serialVersionUID = 6266609364542899147L;

	}

	public static class Wo extends ActionDeleteTypeProcessPlatformWo {

		private static final long serialVersionUID = -1905429989738754325L;

	}

}