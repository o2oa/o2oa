package com.x.okr.assemble.control.jaxrs.okrworkchat;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkChat;
@Wrap(OkrWorkChat.class)
public class WrapInFilter extends GsonPropertyObject {
	
	private String workId;
	
	private String sequenceField = "sequence";
	
	private String key;
	
	private String order = "DESC";

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getSequenceField() {
		return sequenceField;
	}

	public void setSequenceField(String sequenceField) {
		this.sequenceField = sequenceField;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}
