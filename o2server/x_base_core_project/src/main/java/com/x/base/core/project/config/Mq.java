package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import com.x.base.core.project.annotation.FieldDescribe;

public class Mq extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;

	@FieldDescribe("消息服务类型")
	private String mq;

	@FieldDescribe("Kafka服务器配置")
	private MQKafka kafka;

	@FieldDescribe("ActiveMQ服务器配置")
	private MQActive activeMQ;

	public static Mq defaultInstance() {
		return new Mq();
	}

	public static final Boolean default_enable = false;
	public static final String default_mq = "kafka";

	public Mq() {
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
