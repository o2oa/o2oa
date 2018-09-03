package com.x.program.center.jaxrs.code;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.core.entity.Code;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(String mobile) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Code code = new Code();
			code.setMobile(mobile);
			code.setAnswer(StringTools.randomNumber4());
			emc.beginTransaction(Code.class);
			emc.persist(code, CheckPersistType.all);
			wo.setValue(code.getAnswer());
			emc.commit();
			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				logger.warn("短信无法发送,系统没有启用O2云服务.");
			} else {
				Message message = new Message();
				message.setUnit(Config.collect().getName());
				message.setMobile(code.getMobile());
				message.setPassword(Config.collect().getPassword());
				message.setAnswer(code.getAnswer());
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