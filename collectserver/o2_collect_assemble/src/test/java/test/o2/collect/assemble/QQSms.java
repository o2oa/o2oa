package test.o2.collect.assemble;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;

public class QQSms {

	@Test
	public void test() throws Exception {
		// {
		// "ext": "",
		// "extend": "",
		// "msg": "你的验证码是1234",
		// "sig": "ecab4881ee80ad3d76bb1da68387428ca752eb885e52621a3129dcf4d9bc4fd4",
		// "tel": {
		// "mobile": "13788888888",
		// "nationcode": "86"
		// },
		// "time": 1457336869,
		// "type": 0
		// }
		String appid = "1400080897"; // sdkappid 对应的 appkey，需要业务方高度保密
		String appKey = "54d58114cee48987c1a89f58a62cc8f6"; // sdkappid 对应的 appkey，需要业务方高度保密
		String random = (new Random()).nextInt(10000) + "";
		String time = ((new Date()).getTime() / 1000) + "";
		System.out.println(time);
		String mobile = "91467007"; // tel 的 mobile 字段的内容
		String nationcode = "852";
		String value = "appkey=" + appKey + "&random=" + random + "&time=" + time + "&mobile=" + mobile;
		String msg = "验证码:12345,有效时间15分钟.如非本人操作,请忽略本短信.";
		String sig = DigestUtils.sha256Hex(value);
		String body = "{\"ext\":\"\",\"extend\":\"\",\"msg\":\"" + msg + "\",\"sig\":\"" + sig
				+ "\",\"tel\":{\"mobile\":\"" + mobile + "\",\"nationcode\":\"" + nationcode + "\"},\"time\":\"" + time
				+ "\",\"type\":0}";
		System.out.println(body);
		String addr = "https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid=" + appid + "&random=" + random;
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair("Content-Type", "application/json;charset=UTF-8"));
		String resp = HttpConnection.postAsString(addr, heads, body);
		System.out.println(resp);
	}

	// @Test
	// public void test2() {
	// try {
	// SmsSingleSender sender = new SmsSingleSender(appid, "replace with key");
	// SmsSingleSenderResult result = sender.send(0, "86", "18326693192",
	// "【腾讯】验证码测试1234", "", "123");
	// System.out.print(result);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}
