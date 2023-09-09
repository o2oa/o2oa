package com.x.program.init.jaxrs.externaldatasources;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.ExternalDataSources;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.init.ExceptionMissionExecute;
import com.x.program.init.MissionExternalDataSources;
import com.x.program.init.ThisApplication;

class ActionSet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSet.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();

		MissionExternalDataSources.CheckResult checkResult = MissionExternalDataSources.check();
		if (BooleanUtils.isTrue(checkResult.getConfigured())) {
			throw new ExceptionMissionExecute("外部数据源已配置.");
		}

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		MissionExternalDataSources missionExternalDataSources = new MissionExternalDataSources();
		missionExternalDataSources.setExternalDataSources(wi.getExternalDataSources());
		ThisApplication.setMissionExternalDataSources(missionExternalDataSources);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -1359701091994600065L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -1714043928097428688L;

		@FieldDescribe("外部数据源.")
		private ExternalDataSources externalDataSources;

		public ExternalDataSources getExternalDataSources() {
			return externalDataSources;
		}

		public void setExternalDataSources(ExternalDataSources externalDataSources) {
			this.externalDataSources = externalDataSources;
		}

	}

}