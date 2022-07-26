package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.service.processing.jaxrs.process.ActionListWithProcessWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithProcess extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithProcess.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (ListTools.isEmpty(wi.getProcessList())) {
				result.setData(wos);
				return result;
			}
			wos = Wo.copier.copy(business.process().listObjectWithProcess(wi.getProcessList(), wi.isIncludeEdition()));
			result.setData(wos);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionListWithProcess$Wi")
	public static class Wi extends ActionListWithProcessWi {

		private static final long serialVersionUID = -4029955861121230518L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionListWithProcess$Wo")
	public static class Wo extends Process {

		private static final long serialVersionUID = -4124351386819473248L;

		static WrapCopier<Process, Wo> copier = WrapCopierFactory.wo(Process.class, Wo.class,
				Arrays.asList(Process.id_FIELDNAME, Process.name_FIELDNAME, Process.alias_FIELDNAME,
						Process.edition_FIELDNAME, Process.editionNumber_FIELDNAME),
				null);
	}

}