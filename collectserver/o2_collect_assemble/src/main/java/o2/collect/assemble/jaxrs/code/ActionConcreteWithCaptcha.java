package o2.collect.assemble.jaxrs.code;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.StringTools;

import o2.collect.assemble.Business;
import o2.collect.assemble.sms.SmsMessage;
import o2.collect.assemble.sms.SmsMessageType;
import o2.collect.assemble.sms.SmsSender;
import o2.collect.core.entity.Code;

class ActionConcreteWithCaptcha extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String key, String answer, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if (!business.validateCaptcha(key, answer)) {
				throw new ExceptionCaptchaInvalid();
			}
			if (StringUtils.isEmpty(wi.getMobile())) {
				throw new ExceptionMobileEmpty();
			}
			if (!this.accountExist(business, wi.getMobile())) {
				throw new ExceptionNotFindUnitWithMobile(wi.getMobile());
			}
			Code code = new Code();
			code.setMobile(wi.getMobile());
			code.setMeta(wi.getMeta());
			if (StringUtils.isEmpty(wi.getAnswer())) {
				code.setAnswer(StringTools.randomNumber4());
			} else {
				code.setAnswer(wi.getAnswer());
			}
			code.setExpiredTime(DateUtils.addMinutes(new Date(), 30));
			business.entityManagerContainer().beginTransaction(Code.class);
			business.entityManagerContainer().persist(code, CheckPersistType.all);
			business.entityManagerContainer().commit();
			SmsMessage smsMessage = new SmsMessage();
			smsMessage.setSmsMessageType(SmsMessageType.code);
			smsMessage.setReference(code.getId());
			smsMessage.setMobile(code.getMobile());
			smsMessage.setMessage(code.getAnswer());
			SmsSender.send(smsMessage);
			Wo wo = new Wo();
			wo.setAnswer(code.getAnswer());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("后设")
		private String meta;

		@FieldDescribe("值")
		private String answer;

		@FieldDescribe("号码")
		private String mobile;

		public String getMeta() {
			return meta;
		}

		public void setMeta(String meta) {
			this.meta = meta;
		}

		public String getAnswer() {
			return answer;
		}

		public void setAnswer(String answer) {
			this.answer = answer;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("值")
		private String answer;

		public String getAnswer() {
			return answer;
		}

		public void setAnswer(String answer) {
			this.answer = answer;
		}

	}
}