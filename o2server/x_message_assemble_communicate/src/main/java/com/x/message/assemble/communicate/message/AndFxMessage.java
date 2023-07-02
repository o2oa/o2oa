package com.x.message.assemble.communicate.message;

import com.google.gson.Gson;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.SSLHttpClientUtil;
import com.x.base.core.project.tools.SslTools;
import com.x.base.core.project.tools.StringTools;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 */
public class AndFxMessage extends GsonPropertyObject {

	private static final long serialVersionUID = 7918866243690822067L;

	/**
	 * 消息格式：
	 * {
	 *     "appKey": "替换行业消息appkey",
	 *     "client_id": "5d3fdc22dbdd7a668be5fbff",
	 *     "content": "{"extra":{"color":"","action":"native://openurl?data=%7B%22noDefaultMenu%22%3A1%2C%22url%22%3A%22http%3A%2F%2F122.9.45.143%3A80%2Fseeyon%2FaccessMessageDetail.do%3Fmethod%3Dmessage%26ticket%3DMTM3MTAyNTAxNDQ%40%26url%3DaXV1cTswMDIzMy86LzU2LzI1NDs5MTB0ZmZ6cG8wSTYwZHBtbWJjcHNidWpwbzBuZnR0YmhmSm9lZnkvaXVubUBpdW5tPiYzR3RmZnpwbyYzR240JjNHYnFxdCYzR3c2JjNHZHBtbWJjcHNidWpwbyYzR2l1bm0mM0dlZnViam10JjNHdHZubmJzei9pdW5tJjRHYmdnYmpzSmUmNEUuNTM1NjgxNTY2Mjo5NjE6OjI1OCYzN3BxZm9Hc3BuJjRFbWp0dVFmb2Vqb2gmMzd0dm5uYnN6SmUmNEUuMiYzN3FzcHl6SmUmNEUxJjM3eGZqeWpvTmZ0dGJoZiY0RXVzdmY%253D%22%7D","title":"流程中心","content":"开发1发起事务:","sendTime":"1655104600226"},"message":"流程中心","type":"替换分配的行业消息type值","userName":"画押盖流程中心章"}",
	 *     "content_type": "xmoa",
	 *     "receiver": [
	 *         "13710250144"
	 *     ],
	 *     "sender": "替换分配的行业消息sender",
	 *     "sign": "cc044b9667f67e567733eafc1b5af49d8b5ae0ea",
	 *     "uuid": "b7bd9941061e4ca4a9721e738ad04515"
	 * }
	 */

	private String appKey;

	private String content_type = "xmoa";

	private String client_id;

	private String sender;

	private String[] receiver;

	private String uuid;

	private String content;

	private String sign;

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String[] getReceiver() {
		return receiver;
	}

	public void setReceiver(String[] receiver) {
		this.receiver = receiver;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public static ContentData getContentInst(String boxTitle,String title, String content, String action) throws Exception {
		ContentData contentData = new ContentData();
		contentData.setMessage(title);
		contentData.setType(Config.andFx().getMsgType());
		contentData.setExtra(getExtraInst(title, content, action));
		contentData.setUserName(boxTitle);
		return contentData;
	}

	public static class ContentData {
		private ExtraData extra;
		private String message;
		private String type;
		private String userName;

		public ExtraData getExtra() {
			return extra;
		}

		public void setExtra(ExtraData extra) {
			this.extra = extra;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
	}

	public static ExtraData getExtraInst(String title, String content, String action) throws Exception {
		ExtraData extraData = new ExtraData();
		extraData.setAction(action);
		extraData.setTitle(title);
		extraData.setContent(content);
		extraData.setSendTime(System.currentTimeMillis()+"");
		extraData.setOrgId(Config.andFx().getEnterId());
		return extraData;
	}

	public static class ExtraData {
		private String icon;
		private String action;
		private String title;
		private String content;
		private String orgId;
		private String sendTime;
		private String appId;
		private String webAction;

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getOrgId() {
			return orgId;
		}

		public void setOrgId(String orgId) {
			this.orgId = orgId;
		}

		public String getSendTime() {
			return sendTime;
		}

		public void setSendTime(String sendTime) {
			this.sendTime = sendTime;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getWebAction() {
			return webAction;
		}

		public void setWebAction(String webAction) {
			this.webAction = webAction;
		}
	}

	public static void main(String[] args) throws Exception{
		Gson gson = new Gson();
		AndFxMessage m = new AndFxMessage();
		m.setReceiver(new String[]{"13819191930"});
		m.setAppKey("182461ai23etc63595d26jhsb7e0dcgs");
		m.setClient_id("5d3fdc22dbdd7a668be5fbff");
		m.setSender("10086102021");
		m.setUuid(StringUtils.replace(StringTools.uniqueToken(), "-", ""));
		m.setContent(gson.toJson(AndFxMessage.getContentInst("智慧办公消息","O2OA测试标题1", "O2OA测试消息内容1", null)));
		StringBuffer buffer = new StringBuffer();
		buffer.append("appKey=" + m.getAppKey())
				.append("client_id=" + m.getClient_id())
				.append("sender=" + m.getSender())
				.append("uuid=" + m.getUuid());
		String sign = DigestUtils.sha1Hex(buffer.toString() + "ie19u678by5a8uhj684679tcbkbn853");
		m.setSign(sign);
		String address = "https://test.yd.cmzq-office.com/v1/origin/corporate/api/corporateApi/corporateMessages";
//		String res = HttpConnection.postAsString(address, null, m.toString());
		String sslPassword = "u24Ufe4%.ca@,{]";
		SSLHttpClientUtil httpClient = new SSLHttpClientUtil(sslPassword, "/Users/chengjian/Downloads/test.yd.cmzq-office.com/client.p12");
		String res = httpClient.post(address, m.toString(), null);
		System.out.println(res);
	}
}
