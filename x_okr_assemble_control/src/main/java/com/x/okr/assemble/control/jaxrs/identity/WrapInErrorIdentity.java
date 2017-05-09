package com.x.okr.assemble.control.jaxrs.identity;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapInErrorIdentity.class)
public class WrapInErrorIdentity {
	
	@EntityFieldDescribe( "需要变更的无效的身份名称." )
	private String oldIdentity = "";
	
	@EntityFieldDescribe( "用户更新的新的身份名称." )
	private String newIdentity = "";
	
	@EntityFieldDescribe( "需要进行变更的数据类型：工作信息|工作汇报|待办已办|交流动态|系统配置|数据统计." )
	private String recordType = "";
	
	@EntityFieldDescribe( "需要进行变更的数据所涉及的数据表中具体的数据的ID值" )
	private String recordId = "";
	
	@EntityFieldDescribe( "需要进行变更的数据所涉及的数据表." )
	private String tableName = "";

	public String getOldIdentity() {
		return oldIdentity;
	}

	public String getNewIdentity() {
		return newIdentity;
	}

	public void setOldIdentity(String oldIdentity) {
		this.oldIdentity = oldIdentity;
	}

	public void setNewIdentity(String newIdentity) {
		this.newIdentity = newIdentity;
	}

	public String getRecordType() {
		return recordType;
	}

	public String getTableName() {
		return tableName;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}	

}