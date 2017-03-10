package com.x.okr.assemble.control.jaxrs.okrworkchat;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapInFilterWorkChat.class)
public class WrapInFilterWorkChat extends GsonPropertyObject {
	
	@EntityFieldDescribe( "用于查询的具体工作项ID." )
	private String workId;
	
	@EntityFieldDescribe( "用于列表排序的属性." )
	private String sequenceField = "sequence";
	
	@EntityFieldDescribe( "用于列表排序的方式." )
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

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}
