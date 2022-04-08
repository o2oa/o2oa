package com.x.base.core.project.config;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageMq extends LinkedHashMap<String, MessageMq.Item> {

	private static final long serialVersionUID = 2536141863287117519L;

	public static MessageMq defaultInstance() {
		MessageMq messageMq = new MessageMq();
		messageMq.put("o2oa", new Item());
		return messageMq;
	}

	public static class Item {

		public Item() {
			this.kafkaBootstrapServers = DEFAULT_KAFKABOOTSTRAPSERVERS;
			this.kafkaTopic = DEFAULT_KAFKATOPIC;
			this.activeMQUsername = DEFAULT_ACTIVEMQUSERNAME;
			this.activeMQPassword = DEFAULT_ACTIVEMQPASSWORD;
			this.activeMQUrl = DEFAULT_ACTIVEMQURL;
			this.activeMQQueueName = DEFAULT_ACTIVEMQQUEUENAME;
		}

		public static final String TYPE_KAFKA = "kafka";
		public static final String TYPE_ACTIVEMQ = "activeMQ";

		public static final String DEFAULT_TYPE = TYPE_KAFKA;

		public static final String DEFAULT_KAFKABOOTSTRAPSERVERS = "";

		public static final String DEFAULT_KAFKATOPIC = "";

		public static final String DEFAULT_KAFKAACKS = "";

		public static final Integer DEFAULT_KAFKARETRIES = 3;

		public static final String DEFAULT_KAFKABATCHSIZE = "";

		public static final Integer DEFAULT_KAFKALINGERMS = 5000;

		public static final String DEFAULT_KAFKABUFFERMEMORY = "";

		public static final String DEFAULT_ACTIVEMQUSERNAME = "";

		public static final String DEFAULT_ACTIVEMQPASSWORD = "";

		public static final String DEFAULT_ACTIVEMQURL = "";

		public static final String DEFAULT_ACTIVEMQQUEUENAME = "";

		@FieldDescribe("类型,kafka或者activeMQ")
		private String type;

		@FieldDescribe("服务器地址")
		private String kafkaBootstrapServers;

		@FieldDescribe("主题")
		private String kafkaTopic;

		@FieldDescribe("用户名")
		private String activeMQUsername;

		@FieldDescribe("密码")
		private String activeMQPassword;

		@FieldDescribe("服务器地址")
		private String activeMQUrl;

		@FieldDescribe("消息队列名")
		private String activeMQQueueName;

		public String getType() {
			return StringUtils.isBlank(this.type) ? DEFAULT_TYPE : this.type;
		}

		public String getActiveMQUsername() {
			return StringUtils.isBlank(this.activeMQUsername) ? DEFAULT_ACTIVEMQUSERNAME : this.activeMQUsername;
		}

		public String getActiveMQPassword() {
			return StringUtils.isBlank(this.activeMQPassword) ? DEFAULT_ACTIVEMQPASSWORD : this.activeMQPassword;
		}

		public String getKafkaBootstrapServers() {
			return StringUtils.isBlank(this.kafkaBootstrapServers) ? DEFAULT_KAFKABOOTSTRAPSERVERS
					: this.kafkaBootstrapServers;
		}

		public String getKafkaTopic() {
			return StringUtils.isBlank(this.kafkaTopic) ? DEFAULT_KAFKATOPIC : this.kafkaTopic;
		}

		public String getActiveMQUrl() {
			return StringUtils.isBlank(this.activeMQUrl) ? DEFAULT_ACTIVEMQURL : this.activeMQUrl;
		}

		public String getActiveMQQueueName() {
			return StringUtils.isBlank(this.activeMQQueueName) ? DEFAULT_ACTIVEMQQUEUENAME : this.activeMQQueueName;
		}

	}

}
