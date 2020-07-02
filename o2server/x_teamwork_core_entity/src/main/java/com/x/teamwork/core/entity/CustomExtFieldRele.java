package com.x.teamwork.core.entity;

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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.CustomExtFieldRele.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.CustomExtFieldRele.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CustomExtFieldRele extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	
	private static final String TABLE = PersistenceProperties.CustomExtFieldRele.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成（必填）.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
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
	public static final String correlationId_FIELDNAME = "correlationId";
	@FieldDescribe("关联ID（非必填）")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + correlationId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + correlationId_FIELDNAME)
	private String correlationId;

	public static final String extFieldName_FIELDNAME = "extFieldName";
	@FieldDescribe("扩展属性列名（必填）")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + extFieldName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extFieldName_FIELDNAME)
	private String extFieldName;
	
	public static final String displayName_FIELDNAME = "displayName";
	@FieldDescribe("显示属性名称（必填）")
	@Column( length = JpaObject.length_64B, name = ColumnNamePrefix + displayName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + displayName_FIELDNAME)
	private String displayName;
	
	public static final String displayType_FIELDNAME = "displayType";
	@FieldDescribe("类型：TEXT|SELECT|MUTISELECT|RICHTEXT|DATE|DATETIME|PERSON|IDENTITY|UNIT|GROUP|（必填）")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + displayType_FIELDNAME)
	private String displayType="TEXT";
	
	public static final String type_FIELDNAME = "type";
	@FieldDescribe("类型：project|task（必填）")
	@Column( length = JpaObject.length_64B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	private String type;
	
	public static final String optionsData_FIELDNAME = "optionsData";
	@FieldDescribe("选择荐的备选数据，数据Json， displayType=SELECT|MUTISELECT时必须填写，否则无选择项")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_1M, name = ColumnNamePrefix + optionsData_FIELDNAME)
	private String optionsData;
	
	public static final String order_FIELDNAME = "order";
	@FieldDescribe("排序号（非必填）")
	@Column( name = ColumnNamePrefix  + order_FIELDNAME)
	private Integer order= 0 ;
	
	public static final String nullable_FIELDNAME = "nullable";
	@FieldDescribe("是否允许为空（非必填）")
	@Column( name = ColumnNamePrefix  + nullable_FIELDNAME)
	private Boolean nullable = true ;
	
	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明信息（非必填）")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	private String description;
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOptionsData() {
		return optionsData;
	}

	public void setOptionsData(String optionsData) {
		this.optionsData = optionsData;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getExtFieldName() {
		return extFieldName;
	}

	public void setExtFieldName(String extFieldName) {
		this.extFieldName = extFieldName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
}