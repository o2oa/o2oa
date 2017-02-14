package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.entity.BBSReplyInfo;

@Wrap( BBSReplyInfo.class)
public class WrapInReplyInfo extends BBSReplyInfo{

	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();
	
	private String replyMachineName = "PC";
	private String replySystemName = "Windows";
	private String userHostIp = "";
	
	
	public String getReplyMachineName() {
		return replyMachineName;
	}
	public void setReplyMachineName(String replyMachineName) {
		this.replyMachineName = replyMachineName;
	}
	public String getReplySystemName() {
		return replySystemName;
	}
	public void setReplySystemName(String replySystemName) {
		this.replySystemName = replySystemName;
	}
	public String getUserHostIp() {
		return userHostIp;
	}
	public void setUserHostIp(String userHostIp) {
		this.userHostIp = userHostIp;
	}
}
