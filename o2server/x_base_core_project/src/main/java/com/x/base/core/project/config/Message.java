package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class Message extends GsonPropertyObject {

	public Message() {

	}

	public Message(List<String> list) {
		this.consumers.addAll(list);
	}

	public Message(String... args) {
		this.consumers.addAll(ListTools.toList(args));
	}

	public static Message defaultInstance() {
		return new Message();
	}

	private List<String> consumers = new ArrayList<>();

	public List<String> getConsumers() {
		return consumers;
	}

	public void setConsumers(List<String> consumers) {
		this.consumers = consumers;
	}

}
