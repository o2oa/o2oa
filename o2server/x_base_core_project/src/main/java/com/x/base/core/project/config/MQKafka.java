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

public class MQKafka extends ConfigObject {
	
	@FieldDescribe("服务器地址")
	private String bootstrap_servers;
	
	@FieldDescribe("主题")
	private String topic;
	
	@FieldDescribe("指定必须有多少个分区副本接收消息，生产者才认为消息写入成功")
	private String acks;
	
	@FieldDescribe("错误的次数")
	private Integer retries;

	@FieldDescribe("批次可以使用的内存大小")
	private Integer batch_size;
	
	@FieldDescribe("等待更多消息加入批次的时间")
	private Integer linger_ms;
	
	@FieldDescribe("生产者内存缓冲区的大小")
	private Integer buffer_memory;
	
	@FieldDescribe("key值的序列化类")
	private String key_deserializer;
	
	@FieldDescribe("value的序列化类")
	private String value_deserializer;
	
	public static MQKafka defaultInstance() {
		return new MQKafka();
	}

	public static final String default_bootstrap_servers = "localhost:9092";
	public static final String default_topic = "topic-test";
	public static final String default_acks = "all";
	public static final Integer default_retries = 0;
	public static final Integer default_batch_size = 16384;
	public static final Integer default_linger_ms= 1;
	public static final Integer default_buffer_memory = 33554432;
	public static final String default_key_deserializer = "org.apache.kafka.common.serialization.StringDeserializer";
	public static final String default_value_deserializer = "org.apache.kafka.common.serialization.StringDeserializer";
	
	public MQKafka() {
		this.bootstrap_servers = default_bootstrap_servers;
		this.topic = default_topic;
		this.acks = default_acks;
		this.retries = default_retries;
		this.batch_size = default_batch_size;
		this.linger_ms = default_linger_ms;
		this.buffer_memory= default_buffer_memory;
		this.key_deserializer = default_key_deserializer;
		this.value_deserializer = default_value_deserializer;
	}
	

	public String getBootstrap_servers() {
	      return StringUtils.isEmpty(bootstrap_servers) ? default_bootstrap_servers : this.bootstrap_servers; 
	}
	
	public void setBootstrap_servers(String bootstrap_servers) {
		this.bootstrap_servers = bootstrap_servers;
	}

	public String getTopic() {
		   return StringUtils.isEmpty(topic) ? default_topic : this.topic; 
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getAcks() {
	    return StringUtils.isEmpty(acks) ? default_acks : this.acks; 

	}

	public void setAcks(String acks) {
		this.acks = acks;
	}

	public Integer getRetries() {
		return this.retries; 
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public Integer getBatch_size() {
		return  this.batch_size; 
	}

	public void setBatch_size(Integer batch_size) {
		this.batch_size = batch_size;
	}

	public Integer getLinger_ms() {
		return this.linger_ms; 
	}

	public void setLinger_ms(Integer linger_ms) {
		this.linger_ms = linger_ms;
	}

	public Integer getBuffer_memory() {
		return  this.buffer_memory; 
	}

	public void setBuffer_memory(Integer buffer_memory) {
		this.buffer_memory = buffer_memory;
	}

	public String getKey_deserializer() {
		return StringUtils.isEmpty(key_deserializer) ? default_key_deserializer : this.key_deserializer ;
	}

	public void setKey_deserializer(String key_deserializer) {
		this.key_deserializer = key_deserializer;
	}

	public String getValue_deserializer() {
		return StringUtils.isEmpty(value_deserializer) ? default_value_deserializer : this.value_deserializer ;
	}

	public void setValue_deserializer(String value_deserializer) {
		this.value_deserializer = value_deserializer;
	}
	
}
