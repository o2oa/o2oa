package com.x.base.core.project.config;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class MQActive extends ConfigObject {

	@FieldDescribe("服务器地址")
	private String url;
	
	@FieldDescribe("消息队列名")
	private String queueName;
	
	public static MQActive defaultInstance() {
		return new MQActive();
	}

	public static final String default_url = "tcp://127.0.0.1:61616";
	public static final String default_queueName = "queue-test";
	
	public MQActive() {
		this.url = default_url;
		this.queueName = default_queueName;

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	

	
	
}
