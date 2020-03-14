package com.x.message.core.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Org.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Org.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Org extends SliceJpaObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5626959472585823064L;
	private static final String TABLE = PersistenceProperties.Org.table;

	
	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();


	@Override
	public void onPersist() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	/* 以上为 JpaObject 默认字段 */
	
	public static final String operType_FIELDNAME = "operType";
	@FieldDescribe("操作类型(add|modify|delete)")
	@Column(length = length_32B, name = ColumnNamePrefix + operType_FIELDNAME)
	//@Index(name = TABLE + IndexNameMiddle + operType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String operType;
	
	public static final String orgType_FIELDNAME = "orgType";
	@FieldDescribe("数据类型(person(个人)|unit(部门)|group(群组)|identity(身份)|role(角色)|duty(职务))")
	@Column(length = length_32B, name = ColumnNamePrefix + orgType_FIELDNAME)
	//@Index(name = TABLE + IndexNameMiddle + orgType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String orgType;	
	
	public static final String operUerId_FIELDNAME = "operUerId";
	@FieldDescribe("数据操作者")
	@Column(length = length_96B, name = ColumnNamePrefix + operUerId_FIELDNAME)
	//@Index(name = TABLE + IndexNameMiddle + operUerId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String operUerId;	
	
	public static final String operDataId_FIELDNAME = "operDataId";
	@FieldDescribe("数据id")
	@Column(length = length_96B, name = ColumnNamePrefix + operDataId_FIELDNAME)
	//@Index(name = TABLE + IndexNameMiddle + operDataId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String operDataId;
	
	public static final String receiveSystem_FIELDNAME = "receiveSystem";
	@FieldDescribe("接收系统(authority(权限)|third(第三方系统))")
	@Column(length = length_255B, name = ColumnNamePrefix + receiveSystem_FIELDNAME)
	//@Index(name = TABLE + IndexNameMiddle + receiveSystem_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String receiveSystem;
	
	public static final String consumed_FIELDNAME = "consumed";
	@FieldDescribe("是否消费(true|false)")
	@Column( name = ColumnNamePrefix + consumed_FIELDNAME)
	//@Index(name = TABLE + IndexNameMiddle + consumed_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean consumed;
	
	
	public static final String consumedModule_FIELDNAME = "consumedModule";
	@FieldDescribe("已消费模块[ CMS|Teamwork|各模块名]")
	@Column(length = length_255B, name = ColumnNamePrefix + consumedModule_FIELDNAME)
	//@Index(name = TABLE + IndexNameMiddle + consumedModule_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String consumedModule;
	
	public static final String body_FIELDNAME = "body";
	@FieldDescribe("内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = length_10M, name = ColumnNamePrefix + body_FIELDNAME)
	private String body;

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getOperUerId() {
		return operUerId;
	}

	public void setOperUerId(String operUerId) {
		this.operUerId = operUerId;
	}

	public String getOperDataId() {
		return operDataId;
	}

	public void setOperDataId(String operDataId) {
		this.operDataId = operDataId;
	}

	public String getReceiveSystem() {
		return receiveSystem;
	}

	public void setReceiveSystem(String receiveSystem) {
		this.receiveSystem = receiveSystem;
	}

	public Boolean getConsumed() {
		return consumed;
	}

	public void setConsumed(Boolean consumed) {
		this.consumed = consumed;
	}

	public String getConsumedModule() {
		return consumedModule;
	}

	public void setConsumedModule(String consumedModule) {
		this.consumedModule = consumedModule;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	
	
}
