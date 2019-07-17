package com.x.cms.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 数据指处理，比如修改栏目名称引起的所有分类 和文档别名需要修改
 */
@Entity
@ContainerEntity
@Table(name = PersistenceProperties.CmsBatchOperation.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.CmsBatchOperation.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CmsBatchOperation extends SliceJpaObject {

	private static final long serialVersionUID = 7668822947307502058L;
	public static final int STRING_VALUE_MAX_LENGTH = JpaObject.length_255B;
	@SuppressWarnings("unused")
	private static final String TABLE = PersistenceProperties.CmsBatchOperation.table;

	/* 以上为 JpaObject 默认字段 */
	public void onPersist() throws Exception {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	public static final String optType_FIELDNAME = "optType";
	@FieldDescribe("操作类别")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + optType_FIELDNAME)
	@CheckPersist( allowEmpty = false )
	private String optType;
	
	public static final String objType_FIELDNAME = "objType";
	@FieldDescribe("对象类别")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + objType_FIELDNAME)
	@CheckPersist( allowEmpty = false )
	private String objType;

	public static final String bundle_FIELDNAME = "bundle";
	@FieldDescribe("绑定的ID")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + bundle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String bundle;

	public static final String oldInfo_FIELDNAME = "oldInfo";
	@FieldDescribe("旧的信息：修改标题、名称时使用")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + oldInfo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String oldInfo;
	
	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明备注")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;
	
	public static final String errorCount_FIELDNAME = "errorCount";
	@FieldDescribe("执行错误次数")
	@Column( name = ColumnNamePrefix + errorCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer errorCount;
	
	public static final String isRunning_FIELDNAME = "isRunning";
	@FieldDescribe("是否正在执行中")
	@Column( name = ColumnNamePrefix + isRunning_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isRunning;

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOldInfo() {
		return oldInfo;
	}

	public void setOldInfo(String oldInfo) {
		this.oldInfo = oldInfo;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}
	
	public void addErrorCount( Integer count ) {
		if( this.errorCount == null ) {
			this.errorCount = 0;
		}
		this.errorCount = this.errorCount + count;
	}

	public Boolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append( "OBJ_TYPE: " ).append( this.objType );
		sb.append( ", OPT_TYPE: " ).append( this.optType );
		sb.append( ", BUNDLE: " ).append( this.bundle );
		sb.append( ", OLDINFO: " ).append( this.oldInfo );
		sb.append( ", DESCRIPTION: " ).append( this.description );
		return sb.toString();
	}
}