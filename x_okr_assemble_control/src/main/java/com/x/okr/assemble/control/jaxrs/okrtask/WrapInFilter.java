package com.x.okr.assemble.control.jaxrs.okrtask;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrTask;
@Wrap(OkrTask.class)
public class WrapInFilter extends GsonPropertyObject {
	String id;
	Integer count;
	String sequenceField = "sequence";	
	boolean andJoin;
	String order = "DESC";
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getSequenceField() {
		return sequenceField;
	}
	public void setSequenceField(String sequenceField) {
		this.sequenceField = sequenceField;
	}
	public boolean isAndJoin() {
		return andJoin;
	}
	public void setAndJoin(boolean andJoin) {
		this.andJoin = andJoin;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
}
