//package o2.collect.assemble.sms.code;
//
//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoClient;
//import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
//import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
//
//import o2.collect.assemble.sms.SmsMessage;
//
//public class SmsCodeSenderAliqin extends SmsCodeSender {
//
//	private static final String url = "https://eco.taobao.com/router/rest";
//	private static final String app = "23697087";
//	private static final String secret = "9fed15e7c0eecd7db213ea1d525fc26c";
//	private static final String sign = "O2平台";
//	private static final String template = "SMS_55160021";
//
//	private static TaobaoClient client;
//
//	public String send(SmsMessage message) throws Exception {
//		if (null == client) {
//			synchronized (SmsCodeSenderAliqin.class) {
//				if (client == null) {
//					client = new DefaultTaobaoClient(url, app, secret);
//				}
//			}
//		}
//		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
//		req.setExtend("");
//		req.setSmsType("normal");
//		req.setSmsFreeSignName(sign);
//		req.setSmsParamString("{code:'" + message.getMessage() + "'}");
//		req.setRecNum(message.getMobile());
//		req.setSmsTemplateCode(template);
//		AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
//		return rsp.getBody();
//	}
//
//}
