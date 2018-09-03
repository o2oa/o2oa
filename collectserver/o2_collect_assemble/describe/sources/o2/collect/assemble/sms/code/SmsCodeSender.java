package o2.collect.assemble.sms.code;

import o2.collect.assemble.sms.SmsMessage;

public abstract class SmsCodeSender {
	public abstract String send(SmsMessage o) throws Exception;
}
