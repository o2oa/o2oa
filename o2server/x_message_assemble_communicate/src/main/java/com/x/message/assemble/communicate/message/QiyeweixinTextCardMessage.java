package com.x.message.assemble.communicate.message;


/**
 * 文本卡片消息
 * 用于可以打开的工作消息发送
 */
public class QiyeweixinTextCardMessage extends QiyeweixinBaseMessage {

	private static final long serialVersionUID = 3587675274646612547L;
	/**
	 * {
	 *    "touser" : "UserID1|UserID2|UserID3",
	 *    "toparty" : "PartyID1 | PartyID2",
	 *    "totag" : "TagID1 | TagID2",
	 *    "msgtype" : "textcard",
	 *    "agentid" : 1,
	 *    "textcard" : {
	 *             "title" : "领奖通知",
	 *             "description" : "<div class=\"gray\">2016年9月26日</div> <div class=\"normal\">恭喜你抽中iPhone 7一台，领奖码：xxxx</div><div class=\"highlight\">请于2016年10月10日前联系行政同事领取</div>",
	 *             "url" : "URL",
	 *                         "btntxt":"更多"
	 *    },
	 *    "enable_id_trans": 0,
	 *    "enable_duplicate_check": 0,
	 *    "duplicate_check_interval": 1800
	 * }
	 */

	public QiyeweixinTextCardMessage() {
		this.setMsgtype("textcard");
	}


	private TextCard textcard = new TextCard();
	private int enable_id_trans = 0;
	private int enable_duplicate_check = 0;
	private int duplicate_check_interval = 0;


	public TextCard getTextcard() {
		return textcard;
	}

	public void setTextcard(TextCard textcard) {
		this.textcard = textcard;
	}

	public int getEnable_id_trans() {
		return enable_id_trans;
	}

	public void setEnable_id_trans(int enable_id_trans) {
		this.enable_id_trans = enable_id_trans;
	}

	public int getEnable_duplicate_check() {
		return enable_duplicate_check;
	}

	public void setEnable_duplicate_check(int enable_duplicate_check) {
		this.enable_duplicate_check = enable_duplicate_check;
	}

	public int getDuplicate_check_interval() {
		return duplicate_check_interval;
	}

	public void setDuplicate_check_interval(int duplicate_check_interval) {
		this.duplicate_check_interval = duplicate_check_interval;
	}






	public static class TextCard {

		private String title = "";
		private String description = "";
		private String url = "";
		private String btntxt = "详情";

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getBtntxt() {
			return btntxt;
		}

		public void setBtntxt(String btntxt) {
			this.btntxt = btntxt;
		}
	}

}
