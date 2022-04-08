package com.x.base.core.project.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.message.MessageConnector;

public class Message extends GsonPropertyObject {

	private static final long serialVersionUID = 2536141863287117519L;

	public Message() {

	}

	public Message(List<String> list) {
		this.consumers.addAll(list);
	}

	public Message(List<String> list, Map<String, String> map) {
		this.consumers.addAll(list);
		if (map != null) {
			this.consumersV2.putAll(map);
		}
	}

	public Message(String... args) {
		if (args != null) {
			for (String arg : args) {
				this.consumersV2.put(arg, "");
				this.consumersV3.add(new Consumer(arg));
			}
		}
	}

	public Message(Map<String, String> map) {
		if (map != null) {
			this.consumersV2.putAll(map);
		}
	}

	public static Message defaultInstance() {
		return new Message();
	}

	private List<String> consumers = new ArrayList<>();

	private Map<String, String> consumersV2 = new HashMap<>();

	private List<Consumer> consumersV3 = new ArrayList<>();

	public List<Consumer> getConsumersV3() {
		return consumersV3;
	}

	public List<String> getConsumers() {
		return consumers;
	}

	public void setConsumers(List<String> consumers) {
		this.consumers = consumers;
	}

	public Map<String, String> getConsumersV2() {
		return consumersV2;
	}

	public void setConsumersV2(Map<String, String> consumersV2) {
		this.consumersV2 = consumersV2;
	}

	public static class Consumer implements Serializable {

		private static final long serialVersionUID = 392932139617988800L;

		private static final String[] V3HASITEMCONSUMER = new String[] { MessageConnector.CONSUME_API,
				MessageConnector.CONSUME_MAIL, MessageConnector.CONSUME_MQ, MessageConnector.CONSUME_RESTFUL };

		private static final Boolean DEFAULT_ENABLE = true;
		private static final String DEFAULT_TYPE = "";
		private static final String DEFAULT_LOADER = "";
		private static final String DEFAULT_FILTER = "";
		private static final String DEFAULT_ITEM = "";

		public Consumer() {
			this.type = DEFAULT_TYPE;
			this.enable = DEFAULT_ENABLE;
			this.loader = DEFAULT_LOADER;
			this.filter = DEFAULT_FILTER;
			this.item = DEFAULT_ITEM;
		}

		public Consumer(String type) {
			this.type = type;
			this.enable = DEFAULT_ENABLE;
			this.loader = DEFAULT_LOADER;
			this.filter = DEFAULT_FILTER;
			if (StringUtils.containsAny(type, V3HASITEMCONSUMER)) {
				this.item = DEFAULT_ITEM;
			} else {
				this.item = null;
			}
		}

		@FieldDescribe("消费者名称")
		private String type;
		@FieldDescribe("是否启用")
		private Boolean enable;
		@FieldDescribe("装载器")
		private String loader;
		@FieldDescribe("过滤器")
		private String filter;
		@FieldDescribe("配置条目")
		private String item;

		public Boolean getEnable() {
			return (null == this.enable) ? DEFAULT_ENABLE : this.enable;
		}

		public String getFilter() {
			return StringUtils.isBlank(this.filter) ? DEFAULT_FILTER : this.filter;
		}

		public String getType() {
			return StringUtils.isBlank(this.type) ? DEFAULT_TYPE : this.type;
		}

		public String getLoader() {
			return StringUtils.isBlank(this.loader) ? DEFAULT_LOADER : this.loader;
		}

		public String getItem() {
			return StringUtils.isBlank(this.item) ? DEFAULT_ITEM : this.item;
		}

	}
}
