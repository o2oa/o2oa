package com.x.program.center.jaxrs.config;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.Dingding;
import com.x.base.core.project.config.Qiyeweixin;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

/**
 * config/token.json配置修改
 * @author sword
 */
public class ActionSetToken extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionSetToken.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (BooleanUtils.isNotTrue(Config.general().getConfigApiEnable())) {
			throw new ExceptionModifyConfig();
		}
		Map<String,Object> map = XGsonBuilder.instance().fromJson(jsonElement, Map.class);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		/** 需要修改数据库密码 */
		if (StringUtils.isNotBlank(wi.getPassword()) && !StringUtils.equals(wi.getPassword(), Config.token().getPassword())) {
			this.changeInternalDataServerPassword(Config.token().getPassword(), wi.getPassword());
		}
		Wi.copier = WrapCopierFactory.wi(Wi.class, Token.class, new ArrayList<>(map.keySet()), ListTools.toList("dingding", "qiyeweixin"));
		Wi.copier.copy(wi, Config.token());
		Config.token().save();
		if (null != wi.getDingding()) {
			WiDingding.copier.copy(wi.getDingding(), Config.dingding());
			Config.dingding().save();
		}
		if (null != wi.getQiyeweixin()) {
			WiQiyeweixin.copier.copy(wi.getQiyeweixin(), Config.qiyeweixin());
			Config.qiyeweixin().save();
		}
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

		static WrapCopier<Wi, Token> copier = WrapCopierFactory.wi(Wi.class, Token.class, null, ListTools.toList("dingding", "qiyeweixin"));

		private WiDingding dingding;

		private WiQiyeweixin qiyeweixin;

		public WiDingding getDingding() {
			return dingding;
		}

		public void setDingding(WiDingding dingding) {
			this.dingding = dingding;
		}

		public WiQiyeweixin getQiyeweixin() {
			return qiyeweixin;
		}

		public void setQiyeweixin(WiQiyeweixin qiyeweixin) {
			this.qiyeweixin = qiyeweixin;
		}

	}

	public static class WiDingding extends Dingding {

		private static final long serialVersionUID = -4605289359327123402L;
		
		static WrapCopier<WiDingding, Dingding> copier = WrapCopierFactory.wi(WiDingding.class, Dingding.class, null,
				null);

	}

	public static class WiQiyeweixin extends Qiyeweixin {

		private static final long serialVersionUID = 3841050331957747237L;
		
		static WrapCopier<WiQiyeweixin, Qiyeweixin> copier = WrapCopierFactory.wi(WiQiyeweixin.class, Qiyeweixin.class,
				null, null);

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 4848892775663119464L;

	}
}
