package com.x.organization.core.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PermissionSetting", description = "组织权限设定.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.PermissionSetting.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.PermissionSetting.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PermissionSetting extends SliceJpaObject {

	public static final String HIDDENMOBILESYMBOL = "***********";

	private static final long serialVersionUID = 5733185578089403629L;

	private static final String TABLE = PersistenceProperties.PermissionSetting.table;

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

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
	}

	/* 更新运行方法 */

	/* Entity 默认字段结束 */

	public static final String excludeUnit_FIELDNAME = "excludeUnit";
	@FieldDescribe("不允许被查询单位.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + excludeUnit_FIELDNAME, joinIndex = @Index(name = TABLE
	+ IndexNameMiddle + excludeUnit_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + excludeUnit_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + excludeUnit_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> excludeUnit;

	public static final String excludePerson_FIELDNAME = "excludePerson";
	@FieldDescribe("不允许被查询个人.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + excludePerson_FIELDNAME, joinIndex = @Index(name = TABLE
	+ IndexNameMiddle + excludePerson_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + excludePerson_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + excludePerson_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> excludePerson;
	
	public static final String limitQueryOuter_FIELDNAME = "limitQueryOuter";
	@FieldDescribe("限制查看外部门.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + limitQueryOuter_FIELDNAME, joinIndex = @Index(name = TABLE
	+ IndexNameMiddle + limitQueryOuter_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + limitQueryOuter_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + limitQueryOuter_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> limitQueryOuter;
	
	public static final String limitQueryAll_FIELDNAME = "limitQueryAll";
	@FieldDescribe("限制查看所有人")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + limitQueryAll_FIELDNAME, joinIndex = @Index(name = TABLE
	+ IndexNameMiddle + limitQueryAll_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + limitQueryAll_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + limitQueryAll_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> limitQueryAll;		
	
	public static final String explain_FIELDNAME = "explain";
	@FieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + explain_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + explain_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String explain;
	
	public static final String status_FIELDNAME = "status";
	@FieldDescribe("状态.草稿,发布")
	@Column(length = length_255B, name = ColumnNamePrefix + status_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status;
	
	public static final String extend1_FIELDNAME = "extend1";
	@Flag
	@FieldDescribe("extend1扩展字段.")
	@Column(length = length_255B, name = ColumnNamePrefix + extend1_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extend1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extend1;

	public static final String extend2_FIELDNAME = "extend2";
	@Flag
	@FieldDescribe("extend2扩展字段.")
	@Column(length = length_255B, name = ColumnNamePrefix + extend2_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extend2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extend2;
	
	public static final String extend3_FIELDNAME = "extend3";
	@Flag
	@FieldDescribe("extend3扩展字段.")
	@Column(length = length_255B, name = ColumnNamePrefix + extend3_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extend3_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extend3;
	
	public List<String> getExcludeUnit() {
		return excludeUnit;
	}

	public void setExcludeUnit(List<String> excludeUnit) {
		this.excludeUnit = excludeUnit;
	}

	public List<String> getExcludePerson() {
		return excludePerson;
	}
	public void setExcludePerson(List<String> excludePerson) {
		this.excludePerson = excludePerson;
	}	
	
	public List<String> getLimitQueryOuter() {
		return limitQueryOuter;
	}

	public void setLimitQueryOuter(List<String> limitQueryOuter) {
		this.limitQueryOuter = limitQueryOuter;
	}	
	
	public List<String> getLimitQueryAll() {
		return limitQueryAll;
	}
	public void setLimitQueryAll(List<String> limitQueryAll) {
		this.limitQueryAll = limitQueryAll;
	}
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}
	public String getExtend2() {
		return extend2;
	}
	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}
	public String getExtend3() {
		return extend3;
	}
	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}

}