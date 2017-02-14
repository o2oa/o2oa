package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.entity.BBSSubjectInfo;

@Wrap( BBSSubjectInfo.class)
public class WrapInSubjectInfo extends BBSSubjectInfo{

	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();
	
	private String subjectMachineName = "PC";
	private String subjectSystemName = "Windows";
	private String userHostIp = "";
	private String content = "";
	private String pictureBase64 = "";
	
	public String getSubjectMachineName() {
		return subjectMachineName;
	}
	public void setSubjectMachineName(String subjectMachineName) {
		this.subjectMachineName = subjectMachineName;
	}
	public String getSubjectSystemName() {
		return subjectSystemName;
	}
	public void setSubjectSystemName(String subjectSystemName) {
		this.subjectSystemName = subjectSystemName;
	}
	public String getUserHostIp() {
		return userHostIp;
	}
	public void setUserHostIp(String userHostIp) {
		this.userHostIp = userHostIp;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPictureBase64() {
		return pictureBase64;
	}
	public void setPictureBase64(String pictureBase64) {
		this.pictureBase64 = pictureBase64;
	}
}
