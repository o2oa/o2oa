package com.x.processplatform.assemble.surface.jaxrs.file;

import java.util.ArrayList;
import java.util.List;

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
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.File;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithApplication.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {

		LOGGER.debug("execute:{}, flag:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName,
				() -> applicationFlag);

		List<Wo> wos = new ArrayList<>();
		ActionResult<List<Wo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			wos = emc.fetchEqual(File.class, Wo.copier, File.application_FIELDNAME, application.getId());
			wos = business.file().sort(wos);
		}
		result.setData(wos);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.file.ActionListWithApplication$Wo")
	public static class Wo extends File {

		private static final long serialVersionUID = 3121411589636528551L;
		static WrapCopier<File, Wo> copier = WrapCopierFactory.wo(File.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, File.data_FIELDNAME));

	}

}
