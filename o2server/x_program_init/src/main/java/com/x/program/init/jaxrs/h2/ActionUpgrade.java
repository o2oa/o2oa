package com.x.program.init.jaxrs.h2;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.H2Tools;
import com.x.program.init.MissionUpgradeH2;
import com.x.program.init.ThisApplication;

class ActionUpgrade extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpgrade.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson) {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Optional<String> jarVersion = H2Tools.jarVersion();
		Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
		if (jarVersion.isPresent() && localRepositoryDataH2Version.isPresent()
				&& (!StringUtils.equals(jarVersion.get(), localRepositoryDataH2Version.get()))) {
			MissionUpgradeH2 missionUpgradeH2 = new MissionUpgradeH2();
			missionUpgradeH2.setFromVersion(localRepositoryDataH2Version.get());
			missionUpgradeH2.setTargetVerion(jarVersion.get());
			ThisApplication.setMissionUpgradeH2(missionUpgradeH2);
		} else {
			ThisApplication.setMissionUpgradeH2(null);
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -3641608157367930842L;

	}

}