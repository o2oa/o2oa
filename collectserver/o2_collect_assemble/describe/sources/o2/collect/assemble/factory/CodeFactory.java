package o2.collect.assemble.factory;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.StringTools;

import o2.collect.assemble.AbstractFactory;
import o2.collect.assemble.Business;
import o2.collect.assemble.sms.SmsMessage;
import o2.collect.assemble.sms.SmsMessageType;
import o2.collect.assemble.sms.SmsSender;
import o2.collect.core.entity.Code;
import o2.collect.core.entity.Code_;

public class CodeFactory extends AbstractFactory {

	public CodeFactory(Business business) throws Exception {
		super(business);
	}

	public String create(String mobile, String answer, String meta) throws Exception {
		Code code = new Code();
		code.setMobile(mobile);
		code.setMeta(meta);
		if (StringUtils.isEmpty(answer)) {
			code.setAnswer(StringTools.randomNumber4());
		} else {
			code.setAnswer(answer);
		}
		code.setExpiredTime(DateUtils.addMinutes(new Date(), 30));
		this.entityManagerContainer().beginTransaction(Code.class);
		this.entityManagerContainer().persist(code, CheckPersistType.all);
		this.entityManagerContainer().commit();
		SmsMessage smsMessage = new SmsMessage();
		smsMessage.setSmsMessageType(SmsMessageType.code);
		smsMessage.setReference(code.getId());
		smsMessage.setMobile(code.getMobile());
		smsMessage.setMessage(code.getAnswer());
		SmsSender.send(smsMessage);
		return code.getAnswer();
	}

}