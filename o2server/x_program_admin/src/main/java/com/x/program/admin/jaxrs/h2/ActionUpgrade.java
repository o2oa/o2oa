package com.x.program.admin.jaxrs.h2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.H2Tools;
import com.x.program.admin.MissionH2Upgrade;
import com.x.program.admin.ThisApplication;

class ActionUpgrade extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpgrade.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson) throws IOException, URISyntaxException {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Optional<String> jarVersion = H2Tools.jarVersion();
		Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
		Path path = Config.path_local_repository_data(true).resolve(H2Tools.FILENAME_DATABASE);
		if (Files.exists(path) &&  jarVersion.isPresent() && localRepositoryDataH2Version.isPresent()
				&& (!StringUtils.equals(jarVersion.get(), localRepositoryDataH2Version.get()))) {
			MissionH2Upgrade missionH2Upgrade = new MissionH2Upgrade();
			missionH2Upgrade.setFromVersion(localRepositoryDataH2Version.get());
			missionH2Upgrade.setTargetVerion(jarVersion.get());
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