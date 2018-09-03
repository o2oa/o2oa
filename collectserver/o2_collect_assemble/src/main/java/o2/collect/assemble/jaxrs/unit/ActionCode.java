package o2.collect.assemble.jaxrs.unit;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.StringTools;

import o2.collect.assemble.Business;
import o2.collect.assemble.sms.SmsMessage;
import o2.collect.assemble.sms.SmsMessageType;
import o2.collect.assemble.sms.SmsSender;
import o2.collect.core.entity.Code;

class ActionCode extends BaseAction {

	ActionResult<Wo> execute(String mobile) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Code code = new Code();
			code.setMeta(ActionCode.class.getName());
			code.setMobile(mobile);
			code.setAnswer(StringTools.randomNumber4());
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
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
