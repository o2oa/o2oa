package com.x.base.core.project.config;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class EmailNotification extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;
	@FieldDescribe("SMTP服务器.")
	private String server;
	@FieldDescribe("SMTP端口.")
	private String port;
	@FieldDescribe("用户名")
	private String user;
	@FieldDescribe("密码")
	private String password;
	@FieldDescribe("发送者显示名称")
	private String displayName;
	@FieldDescribe("标题前缀")
	private String subjectPrefix;
	@FieldDescribe("邮件内容(HTML)")
	private String contentHTML;

	public static EmailNotification defaultInstance() {
		return new EmailNotification();
	}

	public static final Boolean default_enable = false;
	public static final String default_server = "";
	public static final String default_port = "";
	public static final String default_user = "";
	public static final String default_password = "";
	public static final String default_displayName = "";
	public static final String default_subjectPrefix = "[OA] ";
	public static final String default_contentHTML = "请前往 OA 系统操作。";

	public EmailNotification() {
		this.enable = default_enable;
		this.server = default_server;
		this.port = default_port;
		this.user = default_user;
		this.password = default_password;
		this.displayName = default_displayName;
		this.subjectPrefix = default_subjectPrefix;
		this.contentHTML = default_contentHTML;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getServer() {
		return StringUtils.isEmpty(this.server) ? default_server : this.server;
	}

	public String getPort() {
		return StringUtils.isEmpty(this.port) ? default_port : this.port;
	}

	public String getUser() {
		return StringUtils.isEmpty(this.user) ? default_user : this.user;
	}

	public String getPassword() {
		return StringUtils.isEmpty(this.password) ? default_password : this.password;
	}

	public String getDisplayName() {
		return StringUtils.isEmpty(this.displayName) ? default_displayName : this.displayName;
	}

	public String getSubjectPrefix() {
		return StringUtils.isEmpty(this.subjectPrefix) ? default_subjectPrefix : this.subjectPrefix;
	}

	public String getContentHTML() {
		return StringUtils.isEmpty(this.contentHTML) ? default_contentHTML : this.contentHTML;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setSubjectPrefix(String subjectPrefix) {
		this.subjectPrefix = subjectPrefix;
	}

	public void setContentHTML(String contentHTML) {
		this.contentHTML = contentHTML;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_EMAILNOTIFICATION);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}
}
