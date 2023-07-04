package com.x.program.init.jaxrs.h2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.H2Tools;
import com.x.program.init.MissionH2Upgrade;
import com.x.program.init.ThisApplication;

class ActionUpgrade extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpgrade.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson) throws IOException, URISyntaxException {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		MissionH2Upgrade.CheckResult checkResult = MissionH2Upgrade.check();
		if (BooleanUtils.isTrue(checkResult.getNeedUpgrade())) {
			MissionH2Upgrade missionH2Upgrade = new MissionH2Upgrade();
			missionH2Upgrade.setFromVersion(checkResult.getLocalRepositoryDataH2Version());
			missionH2Upgrade.setTargetVersion(checkResult.getJarVersion());
			ThisApplication.setMissionH2Upgrade(missionH2Upgrade);
		} else {
			ThisApplication.setMissionH2Upgrade(null);
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