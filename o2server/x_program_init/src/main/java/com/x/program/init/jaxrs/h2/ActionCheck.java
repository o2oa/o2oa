package com.x.program.init.jaxrs.h2;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.H2Tools;

class ActionCheck extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheck.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson) {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setNeedUpgrade(false);
		Optional<String> jarVersion = H2Tools.jarVersion();
		Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
		if (jarVersion.isPresent()) {
			wo.setJarVersion(jarVersion.get());
		}
		if (localRepositoryDataH2Version.isPresent()) {
			wo.setLocalRepositoryDataH2Version(localRepositoryDataH2Version.get());
		}
		if (jarVersion.isPresent() && localRepositoryDataH2Version.isPresent()) {
			wo.setNeedUpgrade(StringUtils.equals(jarVersion.get(), localRepositoryDataH2Version.get()));
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -4492633937635601307L;

		private String jarVersion;

		private String localRepositoryDataH2Version;

		private Boolean needUpgrade;

		public String getJarVersion() {
			return jarVersion;
		}

		public void setJarVersion(String jarVersion) {
			this.jarVersion = jarVersion;
		}

		public String getLocalRepositoryDataH2Version() {
			return localRepositoryDataH2Version;
		}

		public void setLocalRepositoryDataH2Version(String localRepositoryDataH2Version) {
			this.localRepositoryDataH2Version = localRepositoryDataH2Version;
		}

		public Boolean getNeedUpgrade() {
			return needUpgrade;
		}

		public void setNeedUpgrade(Boolean needUpgrade) {
			this.needUpgrade = needUpgrade;
		}

	}

}