package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageMq extends LinkedHashMap<String, MessageMq.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageMq defaultInstance() {
		return new MessageMq();
	}

	public static class Item {

		public static final String TYPE_KAFKA = "kafka";
		public static final String TYPE_ACTIVEMQ = "activeMQ";

		@FieldDescribe("类型,kafka或者activeMQ")
		private String type;

		@FieldDescribe("服务器地址")
		private String kafkaBootstrapServers;

		@FieldDescribe("主题")
		private String kafkaTopic;

		@FieldDescribe("指定必须有多少个分区副本接收消息，生产者才认为消息写入成功")
		private String kafkaAcks;

		@FieldDescribe("错误的次数")
		private Integer kafkaRetries;

		@FieldDescribe("批次可以使用的内存大小")
		private Integer kafkaBatchSize;

		@FieldDescribe("等待更多消息加入批次的时间")
		private Integer kafkaLingerMs;

		@FieldDescribe("生产者内存缓冲区的大小")
		private Integer kafkaBufferMemory;

		@FieldDescribe("key值的序列化类")
		private String kafkaKeyDeserializer;

		@FieldDescribe("value的序列化类")
		private String kafkaValueDeserializer;

		@FieldDescribe("用户名")
		private String activeMQUsername;

		@FieldDescribe("密码")
		private String activeMQPassword;

		@FieldDescribe("服务器地址")
		private String activeMQUrl;

		@FieldDescribe("消息队列名")
		private String activeMQQueueName;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getActiveMQUsername() {
			return activeMQUsername;
		}

		public void setActiveMQUsername(String activeMQUsername) {
			this.activeMQUsername = activeMQUsername;
		}

		public String getActiveMQPassword() {
			return activeMQPassword;
		}

		public void setActiveMQPassword(String activeMQPassword) {
			this.activeMQPassword = activeMQPassword;
		}

		public String getKafkaBootstrapServers() {
			return kafkaBootstrapServers;
		}

		public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
			this.kafkaBootstrapServers = kafkaBootstrapServers;
		}

		public String getKafkaTopic() {
			return kafkaTopic;
		}

		public void setKafkaTopic(String kafkaTopic) {
			this.kafkaTopic = kafkaTopic;
		}

		public String getKafkaAcks() {
			return kafkaAcks;
		}

		public void setKafkaAcks(String kafkaAcks) {
			this.kafkaAcks = kafkaAcks;
		}

		public Integer getKafkaRetries() {
			return kafkaRetries;
		}

		public void setKafkaRetries(Integer kafkaRetries) {
			this.kafkaRetries = kafkaRetries;
		}

		public Integer getKafkaBatchSize() {
			return kafkaBatchSize;
		}

		public void setKafkaBatchSize(Integer kafkaBatchSize) {
			this.kafkaBatchSize = kafkaBatchSize;
		}

		public Integer getKafkaLingerMs() {
			return kafkaLingerMs;
		}

		public void setKafkaLingerMs(Integer kafkaLingerMs) {
			this.kafkaLingerMs = kafkaLingerMs;
		}

		public Integer getKafkaBufferMemory() {
			return kafkaBufferMemory;
		}

		public void setKafkaBufferMemory(Integer kafkaBufferMemory) {
			this.kafkaBufferMemory = kafkaBufferMemory;
		}

		public String getKafkaKeyDeserializer() {
			return kafkaKeyDeserializer;
		}

		public void setKafkaKeyDeserializer(String kafkaKeyDeserializer) {
			this.kafkaKeyDeserializer = kafkaKeyDeserializer;
		}

		public String getKafkaValueDeserializer() {
			return kafkaValueDeserializer;
		}

		public void setKafkaValueDeserializer(String kafkaValueDeserializer) {
			this.kafkaValueDeserializer = kafkaValueDeserializer;
		}

		public String getActiveMQUrl() {
			return activeMQUrl;
		}

		public void setActiveMQUrl(String activeMQUrl) {
			this.activeMQUrl = activeMQUrl;
		}

		public String getActiveMQQueueName() {
			return activeMQQueueName;
		}

		public void setActiveMQQueueName(String activeMQQueueName) {
			this.activeMQQueueName = activeMQQueueName;
		}

	}

}
