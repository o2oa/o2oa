package com.x.program.init.jaxrs.secret;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;
import org.h2.tools.RunScript;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.init.MissionSetSecret;
import com.x.program.init.ThisApplication;

class ActionSet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSet.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		String oldPassword = Token.initPassword;
		this.changeInternalDataServerPassword(oldPassword, wi.getSecret());
		Config.token().setPassword(wi.getSecret());
		Config.token().save();
		LOGGER.print("The initial manager password has been modified.");
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		MissionSetSecret missionSetSecret = new MissionSetSecret();
		missionSetSecret.setSecret(wi.getSecret());
		ThisApplication.setMissionSetSecret(missionSetSecret);
		return result;
	}

	private void changeInternalDataServerPassword(String oldPassword, String newPassword) throws Exception {
		org.h2.Driver.load();
		for (Entry<String, DataServer> en : Config.nodes().dataServers().entrySet()) {
			DataServer o = en.getValue();
			if (BooleanUtils.isTrue(o.getEnable()) && (!Config.externalDataSources().enable())) {
				try (Connection conn = DriverManager.getConnection(
						"jdbc:h2:tcp://" + en.getKey() + ":" + o.getTcpPort() + "/X", "sa", oldPassword)) {
					RunScript.execute(conn, new StringReader("ALTER USER SA SET PASSWORD '" + newPassword + "'"));
				} catch (Exception e) {
					throw new IllegalStateException("Verify that the dataServer:" + en.getKey()
							+ " is started and that the dataServer password is updated synchronously.", e);
				}
			}
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7892218945591687635L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -5726130517002102825L;

		private String secret;

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

	}

}