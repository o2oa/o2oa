package o2.collect.assemble.sms.code;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import o2.collect.assemble.sms.SmsMessage;

public class SmsCodeSenderTencent extends SmsCodeSender {

	private static Logger logger = LoggerFactory.getLogger(SmsCodeSenderTencent.class);

	private Pattern PATTERN_MOBILE_HK_MC = Pattern.compile("^\\+?0{0,2}(852|853)(\\d{8})$");
	private Pattern PATTERN_MOBILE_CN = Pattern.compile("^\\+?0{0,2}(86)?(1(3|4|5|7|8)\\d{9})$");

	private String appid = "1400080897"; // sdkappid 对应的 appkey，需要业务方高度保密
	private String appKey = "54d58114cee48987c1a89f58a62cc8f6"; // sdkappid 对应的 appkey，需要业务方高度保密

	public String send(SmsMessage message) throws Exception {
		String random = (new Random()).nextInt(10000) + "";
		String time = ((new Date()).getTime() / 1000) + "";
		Matcher matcher = null;
		String mobile = "";
		String nationcode = "";
		matcher = PATTERN_MOBILE_CN.matcher(message.getMobile());
		if (matcher.find()) {
			nationcode = "86";
			mobile = matcher.group(2);
		} else {
			matcher = PATTERN_MOBILE_HK_MC.matcher(message.getMobile());
			if (matcher.find()) {
				nationcode = matcher.group(1);
				mobile = matcher.group(2);
			}
		}
		if (StringUtils.isEmpty(mobile)) {
			logger.info("无效的消息:{}.", message);
			return null;
		} else {
			String value = "appkey=" + appKey + "&random=" + random + "&time=" + time + "&mobile=" + mobile;
			String msg = "验证码:" + message.getMessage() + ",有效时间15分钟.如非本人操作,请忽略本短信.";
			String sig = DigestUtils.sha256Hex(value);
			String body = "{\"ext\":\"\",\"extend\":\"\",\"msg\":\"" + msg + "\",\"sig\":\"" + sig
					+ "\",\"tel\":{\"mobile\":\"" + mobile + "\",\"nationcode\":\"" + nationcode + "\"},\"time\":\""
					+ time + "\",\"type\":0}";
			logger.info(body);
			String addr = "https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid=" + appid + "&random=" + random;
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair("Content-Type", "application/json;charset=UTF-8"));
			String resp = HttpConnection.postAsString(addr, heads, body);
			logger.info(resp);
			return resp;
		}
	}

	@Test
	public void test1() {

		Matcher matcher = PATTERN_MOBILE_HK_MC.matcher("+0085312345678");
		System.out.println(matcher.find() + ":" + matcher.group(1) + "@" + matcher.group(2));
		matcher = PATTERN_MOBILE_HK_MC.matcher("+085212345678");
		System.out.println(matcher.find() + ":" + matcher.group(1));
		matcher = PATTERN_MOBILE_HK_MC.matcher("+85212345678");
		System.out.println(matcher.find() + ":" + matcher.group(1));
		matcher = PATTERN_MOBILE_HK_MC.matcher("85212345678");
		System.out.println(matcher.find() + ":" + matcher.group(1));
		matcher = PATTERN_MOBILE_HK_MC.matcher("0085212345678");
		System.out.println(matcher.find() + ":" + matcher.group(1));
		matcher = PATTERN_MOBILE_HK_MC.matcher("085212345678");
		System.out.println(matcher.find() + ":" + matcher.group(1));
		matcher = PATTERN_MOBILE_HK_MC.matcher("12345678");
		System.out.println(matcher.find() + ":" + matcher.group(1));
	}

	@Test
	public void test2() {
		Matcher matcher = PATTERN_MOBILE_CN.matcher("+0085312345678");
		System.out.println(matcher.find());
		matcher = PATTERN_MOBILE_CN.matcher("+08613336173316");
		System.out.println(matcher.find() + ":" + matcher.group(2));
		matcher = PATTERN_MOBILE_CN.matcher("13336173316");
		System.out.println(matcher.find() + ":" + matcher.group(2));
		matcher = PATTERN_MOBILE_CN.matcher("1333333333333333");
		System.out.println(matcher.find() + ":" + matcher.group(2));
	}
}