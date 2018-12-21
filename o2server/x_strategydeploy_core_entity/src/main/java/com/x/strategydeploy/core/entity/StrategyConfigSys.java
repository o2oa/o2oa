package com.x.strategydeploy.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 战略平台管理群组配置
 * 
 * @author WUSHUTAO
 **/

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.StrategyConfigSys.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.StrategyConfigSys.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StrategyConfigSys extends SliceJpaObject {
	private static final long serialVersionUID = -1074220919856587656L;
	private static final String TABLE = PersistenceProperties.StrategyConfigSys.table;

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

	public void onPersist() throws Exception {
	}
	/*
	 * =============================以上为 JpaObject
	 * 默认字段============================================
	 */

	@FieldDescribe("战略配置标题")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String title;

	@FieldDescribe("战略配置别名")
	@Column(name = "xalias", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String alias;

	@Lob
	@FieldDescribe("战略配置描述")
	@Column(name = "xdescribe", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String describe;

	@FieldDescribe("战略配置值")
	@Column(name = "xvalue", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String value;

	@FieldDescribe("战略配置使用状态")
	@Column(name = "xstatus", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String status;

	@FieldDescribe("战略配置值的类型")
	@Column(name = "xvaluetype", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String valuetype;

	@FieldDescribe("战略配置是否复选")
	@Column(name = "xmultiple", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String multiple;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getValuetype() {
		return valuetype;
	}

	public void setValuetype(String valuetype) {
		this.valuetype = valuetype;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

}
