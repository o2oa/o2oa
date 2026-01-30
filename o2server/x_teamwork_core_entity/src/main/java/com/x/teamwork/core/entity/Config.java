package com.x.teamwork.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

/**
 * 优先级信息
 * 
 * @author O2LJ
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Config.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Config.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Config extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Config.table;

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
		/** 生成默认排序号 */
		if (null == this.order) {
			this.order = DateTools.timeOrderNumber();
		}
	}
	/*
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */
	public static final String relationId_FIELDNAME = "relationId";
	@FieldDescribe("关联对象id.")
	@Column(length = length_id, name = ColumnNamePrefix + relationId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + relationId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String relationId;
	
	public static final String type_FIELDNAME = "type";
	@FieldDescribe("配置类型")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String type;
	
	public static final String order_FIELDNAME = "order";
	@FieldDescribe("排序号")
	@Column( name = ColumnNamePrefix + order_FIELDNAME )
	private Integer order;
	
	public static final String owner_FIELDNAME = "owner";
	@FieldDescribe("创建者")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + owner_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + owner_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String owner;

	public String getRelationId() {
		return relationId;
	}

	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}