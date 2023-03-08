package com.x.portal.assemble.surface.jaxrs.dict;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.general.core.entity.ApplicationDict;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

class ActionListWithApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithApplication.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {

		LOGGER.debug("execute:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName, () -> applicationFlag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Portal application = business.portal().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag);
			}
			List<String> ids = business.applicationDict().listWithApplication(application);
			wos = Wo.copier.copy(emc.list(ApplicationDict.class, ids));
			SortTools.asc(wos, false, "name");
			result.setData(wos);
			return result;
		}
	}

	@Schema(name = "com.x.portal.assemble.surface.jaxrs.dict.ActionListWithApplication$Wo")
	public static class Wo extends ApplicationDict {

		private static final long serialVersionUID = 1366579178082784176L;

		static WrapCopier<ApplicationDict, Wo> copier = WrapCopierFactory.wo(ApplicationDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
