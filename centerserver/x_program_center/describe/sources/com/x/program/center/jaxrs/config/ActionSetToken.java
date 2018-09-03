package com.x.program.center.jaxrs.config;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

public class ActionSetToken extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		/** 需要修改数据库密码 */
		if (!StringUtils.equals(wi.getPassword(), Config.token().getPassword())) {
			this.changeInternalDataServerPassword(Config.token().getPassword(), wi.getPassword());
		}
		wi.save();
		this.configFlush(effectivePerson);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private void changeInternalDataServerPassword(String oldPassword, String newPassword) throws Exception {
		org.h2.Driver.load();
		for (Entry<String, DataServer> en : Config.nodes().dataServers().entrySet()) {
			DataServer o = en.getValue();
			if (BooleanUtils.isTrue(o.getEnable())) {
				try (Connection conn = DriverManager.getConnection(
						"jdbc:h2:tcp://" + en.getKey() + ":" + o.getTcpPort() + "/X", "sa", oldPassword)) {
					RunScript.execute(conn, new StringReader("ALTER USER SA SET PASSWORD '" + newPassword + "'"));
				} catch (Exception e) {
					throw new Exception("Verify that the dataServer:" + en.getKey()
							+ " is started and that the dataServer password is updated synchronously.", e);
				}
			}
		}
	}

	public static class Wi extends Token {

		static WrapCopier<Wi, Token> copier = WrapCopierFactory.wi(Wi.class, Token.class, null, null);

	}

	public static class Wo extends WrapBoolean {

	}
}