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

public class MQ extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;
	
	@FieldDescribe("消息服务类型")
	private String mq;
	
	@FieldDescribe("Kafka服务器配置")
	private MQKafka kafka;
	
	@FieldDescribe("ActiveMQ服务器配置")
	private MQActive activeMQ;
	
	public static MQ defaultInstance() {
		return new MQ();
	}

	public static final Boolean default_enable = false;
	public static final String default_mq = "kafka";
	
	public MQ() {
		this.enable = default_enable;
		this.mq = default_mq;
		
	}
	
	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getMq() {
		return mq;
	}

	public void setMq(String mq) {
		this.mq = mq;
	}

	public MQKafka getKafka() {
		return kafka;
	}

	public void setKafka(MQKafka kafka) {
		this.kafka = kafka;
	}

	public MQActive getActiveMQ() {
		return activeMQ;
	}

	public void setActiveMQ(MQActive activeMQ) {
		this.activeMQ = activeMQ;
	}



	
	
}
