package o2.collect.assemble.sms.code;

import org.apache.commons.lang3.StringUtils;

import com.smn.client.DefaultSmnClient;
import com.smn.client.SmnClient;
import com.smn.request.sms.SmsPublishRequest;
import com.smn.response.sms.SmsPublishResponse;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.LoggerFactory;

import o2.collect.assemble.sms.SmsMessage;

public class SmsCodeSenderHuawei extends SmsCodeSender {

	private static final String accountUserName = "zoneland";
	private static final String accountDomainName = "zoneland";
	private static final String accountPassword = "zone2018";
	private static final String regionName = "cn-north-1";

	private static SmnClient client;

	public String send(SmsMessage message) throws Exception {
		if (null == client) {
			synchronized (SmsCodeSenderHuawei.class) {
				if (client == null) {
					client = new DefaultSmnClient(accountUserName, accountDomainName, accountPassword, regionName);
				}
			}
		}
		SmsPublishRequest smnRequest = new SmsPublishRequest();
		// 设置参数+8613688807587
		// smnRequest.setEndpoint("13336173316").setMessage("[O2平台]您的验证码为: 12345
		// (15分钟内有效), 为了保证账户安全, 请勿向任何人提供此验证码.")
		// .setSignId("7c37ac1e987246ed8a50edeb37d0c112");
		smnRequest.setEndpoint(message.getMobile())
				.setMessage("(O2平台)您的验证码为: " + message.getMessage() + " (15分钟内有效), 为了保证账户安全, 请勿向任何人提供此验证码.")
				.setSignId("7c37ac1e987246ed8a50edeb37d0c112");
		// 发送短信
		try {
			SmsPublishResponse res = client.sendRequest(smnRequest);
			if (StringUtils.isNotEmpty(res.getMessage())) {
				LoggerFactory.print("短信发送异常:{}.", XGsonBuilder.toJson(res));
				return res.getMessage();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
