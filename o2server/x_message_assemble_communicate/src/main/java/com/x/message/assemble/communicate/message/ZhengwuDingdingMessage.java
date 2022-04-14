package com.x.message.assemble.communicate.message;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ZhengwuDingdingMessage extends GsonPropertyObject {

	private static final long serialVersionUID = -1369430177900592062L;
	/**
	 * agent_id Number 必须 1234 企业开发者可在应用设置页面获取。ISV开发者可通过企业基本信息接口获取 userid_list
	 * String 可选(userid_list,dept_id_list, to_all_user必须有一个不能为空) zhangsan,lisi
	 * 接收者的用户userid列表，最大列表长度：20 dept_id_list String 可选 123,456 接收者的部门id列表，最大列表长度：20,
	 * 接收者是部门id下(包括子部门下的所有用户) to_all_user Boolean 可选 false 是否发送给企业全部用户(ISV不能设置true)
	 * msg Json 必须 {"msgtype":"text","text":{"content":"消息内容"}}
	 * 消息内容，具体见“消息类型与数据格式”。最长不超过2048个字节。重复消息内容当日只能接收一次。
	 * 
	 * @author ray
	 *
	 */

	private Long agentId = 0L;
	private String touser = "";
	private Msg msg = new Msg();

	public static class Msg {
		/**
		 * { "msgtype": "text", "text": { "content": "张三的请假申请" } }
		 */
		private String msgtype = "text";
		private Text text = new Text();

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

	public Msg getMsg() {
		return msg;
	}

	public void setMsg(Msg msg) {
		this.msg = msg;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

}