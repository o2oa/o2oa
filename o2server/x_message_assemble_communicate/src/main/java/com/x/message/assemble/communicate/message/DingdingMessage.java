package com.x.message.assemble.communicate.message;

import com.x.base.core.project.gson.GsonPropertyObject;

public   class DingdingMessage extends GsonPropertyObject {

	private static final long serialVersionUID = 9069169667248347063L;
	/**
	 * agent_id Number 必须 1234 企业开发者可在应用设置页面获取。ISV开发者可通过企业基本信息接口获取 userid_list
	 * String 可选(userid_list,dept_id_list, to_all_user必须有一个不能为空) zhangsan,lisi
	 * 接收者的用户userid列表，最大列表长度：20 dept_id_list String 可选 123,456 接收者的部门id列表，最大列表长度：20,
	 * 接收者是部门id下(包括子部门下的所有用户) to_all_user Boolean 可选 false 是否发送给企业全部用户(ISV不能设置true)
	 * msg Json 必须 {"msgtype":"text","text":{"content":"消息内容"}} msg markdown 消息格式
	 * {"msgtype":"markdown","markdown":{"title":"消息内容",
	 * "text":"[这是一个链接](http://o2oa.net)"}}
	 * 消息内容，具体见“消息类型与数据格式”。最长不超过2048个字节。重复消息内容当日只能接收一次。
	 */

	private Long agent_id = 0L;
	private String userid_list = "";
	private Msg msg = new Msg();

	public DingdingMessage() {

	}

	/**
	 * 设置成 text 消息
	 * @param text
	 */
	public void setTextMsg(String text) {
		getMsg().setMsgtype("text");
		getMsg().getText().setContent(text);
	}

	/**
	 * 设置成markdown消息
	 * @param title
	 * @param markdown
	 */
	public void setMarkdownMsg(String title, String markdown) {
		getMsg().setMsgtype("markdown");
		getMsg().getMarkdown().setTitle(title);
		getMsg().getMarkdown().setText(markdown);
	}

	/**
	 * 设置成卡片消息
	 * @param title
	 * @param markdown
	 * @param url
	 */
	public void setActionCardMsg(String title, String markdown, String url) {
		getMsg().setMsgtype("action_card");
		getMsg().getAction_card().setTitle(title);
		getMsg().getAction_card().setMarkdown(markdown);
		getMsg().getAction_card().setSingle_url(url);
	}


	public Long getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(Long agent_id) {
		this.agent_id = agent_id;
	}

	public String getUserid_list() {
		return userid_list;
	}

	public void setUserid_list(String userid_list) {
		this.userid_list = userid_list;
	}

	public Msg getMsg() {
		return msg;
	}

	public void setMsg(Msg msg) {
		this.msg = msg;
	}




	public static class Msg {
		// {
		// "msgtype": "text",
		// "text": {
		// "content": "张三的请假申请"
		// }
		// }
		private String msgtype = "text"; // text markdown action_card


		private Text text = new Text();
		private Markdown markdown = new Markdown();
		private ActionCard action_card = new ActionCard();

		public String getMsgtype() {
			return msgtype;
		}

		public void setMsgtype(String msgtype) {
			this.msgtype = msgtype;
		}

		public Text getText() {
			return text;
		}

		public void setText(Text text) {
			this.text = text;
		}

		public Markdown getMarkdown() {
			return markdown;
		}

		public void setMarkdown(Markdown markdown) {
			this.markdown = markdown;
		}

		public ActionCard getAction_card() {
			return action_card;
		}

		public void setAction_card(ActionCard action_card) {
			this.action_card = action_card;
		}
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

	public static class Markdown {
		private String title = "";// 消息头 文字
		private String text = "";// markdown格式的消息

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}

	public static class ActionCard {
		private String title = "";// 消息标题
		private String markdown = "";// 消息内容
		private String single_title = "查看详情";// 按钮文字
		private String single_url = "";// 卡片点击打开的url

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getMarkdown() {
			return markdown;
		}

		public void setMarkdown(String markdown) {
			this.markdown = markdown;
		}

		public String getSingle_title() {
			return single_title;
		}

		public void setSingle_title(String single_title) {
			this.single_title = single_title;
		}

		public String getSingle_url() {
			return single_url;
		}

		public void setSingle_url(String single_url) {
			this.single_url = single_url;
		}
	}

}
