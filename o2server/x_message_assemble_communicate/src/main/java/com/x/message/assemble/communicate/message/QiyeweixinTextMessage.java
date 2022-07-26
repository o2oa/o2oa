package com.x.message.assemble.communicate.message;


public class QiyeweixinTextMessage extends QiyeweixinBaseMessage {

	private static final long serialVersionUID = 8600141025638957869L;
	/**
	 * { "touser" : "UserID1|UserID2|UserID3", "toparty" : "PartyID1|PartyID2",
	 * "totag" : "TagID1 | TagID2", "msgtype" : "text", "agentid" : 1, "text" : {
	 * "content" : "你的快递已到，请携带工卡前往邮件中心领取。\n出发前可查看<a
	 * href=\"http://work.weixin.qq.com\">邮件中心视频实况</a>，聪明避开排队。" }, "safe":0 }
	 */

	public QiyeweixinTextMessage() {
		this.setMsgtype("text");
	}

	private Text text = new Text();
	private Long safe = 0L;


	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	public Long getSafe() {
		return safe;
	}

	public void setSafe(Long safe) {
		this.safe = safe;
	}




	public static class Text {

		private String content = "";

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

}
