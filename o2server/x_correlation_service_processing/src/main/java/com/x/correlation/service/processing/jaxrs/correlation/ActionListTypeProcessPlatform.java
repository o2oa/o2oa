package com.x.correlation.service.processing.jaxrs.correlation;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.correlation.core.entity.content.Correlation;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionListTypeProcessPlatformWo;

class ActionListTypeProcessPlatform extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListTypeProcessPlatform.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> wos = new ArrayList<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<Correlation> os = emc.listEqualAndEqual(Correlation.class, Correlation.FROMTYPE_FIELDNAME,
					Correlation.TYPE_PROCESSPLATFORM, Correlation.FROMBUNDLE_FIELDNAME, job);
			wos = Wo.copier.copy(os);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends ActionListTypeProcessPlatformWo {

		private static final long serialVersionUID = -2178467386812682246L;

		static WrapCopier<Correlation, Wo> copier = WrapCopierFactory.wo(Correlation.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}