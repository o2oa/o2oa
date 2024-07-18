package com.x.program.center.jaxrs.code;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(String mobile) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			String customSms = ThisApplication.context().applications().findApplicationName(CUSTOM_SMS_APPLICATION);
			if(StringUtils.isNotBlank(customSms) && Config.customConfig(CUSTOM_SMS_CONFIG_NAME) != null){
				ActionResponse resp = ThisApplication.context().applications()
						.getQuery(customSms, Applications.joinQueryUri("sms", "send", "code", "mobile", mobile, "token", "(0)"));
				RespWi respWi = resp.getData(RespWi.class);
				if (BooleanUtils.isNotTrue(respWi.getValue())) {
					throw new ExceptionTransferCodeError(resp);
				}
			}else if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				throw new ExceptionDisable();
			} else {
				Message message = new Message();
				message.setUnit(Config.collect().getName());
				message.setMobile(mobile);
				message.setPassword(Config.collect().getPassword());
				ActionResponse resp = ConnectionAction
						.put(Config.collect().url() + "/o2_collect_assemble/jaxrs/code/transfer", null, message);
				RespWi respWi = resp.getData(RespWi.class);
				if (BooleanUtils.isNotTrue(respWi.getValue())) {
					throw new ExceptionTransferCodeError(resp);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {
	}

	public static class RespWi extends WrapBoolean {

	}

	public static class Message extends GsonPropertyObject {

		private String mobile;

		private String answer;

		private String unit;

		private String password;

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getAnswer() {
			return answer;
		}

		public void setAnswer(String answer) {
			this.answer = answer;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

	}

}
