package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Message extends GsonPropertyObject {

	public Message() {

	}

	public Message(List<String> list) {
		this.consumers.addAll(list);
	}

	public Message(List<String> list, Map<String, String> map) {
		this.consumers.addAll(list);
		if(map!=null) {
			this.consumersV2.putAll(map);
		}
	}

	public Message(String... args) {
		if(args!=null){
			for (String arg : args){
				this.consumersV2.put(arg,"");
			}
		}
		//this.consumers.addAll(ListTools.toList(args));
	}

	public Message(Map<String, String> map){
		if(map!=null) {
			this.consumersV2.putAll(map);
		}
	}

	public static Message defaultInstance() {
		return new Message();
	}

	private List<String> consumers = new ArrayList<>();

	private Map<String,String> consumersV2 = new HashMap<>();

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
}
