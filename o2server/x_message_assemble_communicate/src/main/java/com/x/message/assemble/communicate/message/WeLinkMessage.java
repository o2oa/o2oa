package com.x.message.assemble.communicate.message;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WeLinkMessage extends GsonPropertyObject {

	private static final long serialVersionUID = -5678682987495247923L;
	/**
	 * { "msgRange": 0,0：按用户推送 必填 "toUserList": ["john@welink", "john@1234"], 用户列表
	 * 必填 "msgTitle": "{"EN": "hello world", "CN": "你好"}", //标题 必填
	 * 只有中文就直接写入标题字符串就行不需要json "msgContent": "{"CN": "欢迎使用", "EN":"Welcome"}",
	 * //消息内容 必填 只有中文就直接写入标题字符串就行不需要json "urlType": "html", //这两个参数是消息超链接用的
	 * 但是目前只支持小程序 不支持轻应用 "urlPath": "h5://demo.com", "msgOwner": "流程平台", 消息所有者 必填
	 * "createTime": "1487289600000" 创建时间 }
	 */

	private String msgRange;
	private List<String> toUserList;
	private String msgTitle;
	private String msgContent;
	private String urlType;
	private String urlPath;
	private String msgOwner;
	private String createTime;

	public String getMsgRange() {
		return msgRange;
	}

	public void setMsgRange(String msgRange) {
		this.msgRange = msgRange;
	}

	public List<String> getToUserList() {
		return toUserList;
	}

	public void setToUserList(List<String> toUserList) {
		this.toUserList = toUserList;
	}

	public String getMsgTitle() {
		return msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getUrlType() {
		return urlType;
	}

	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getMsgOwner() {
		return msgOwner;
	}

	public void setMsgOwner(String msgOwner) {
		this.msgOwner = msgOwner;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}