package com.x.attendance.entity.v2;

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

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceV2GroupScheduleConfig", description = "考勤组排班配置数据.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2GroupScheduleConfig.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2GroupScheduleConfig.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2GroupScheduleConfig  extends SliceJpaObject {


	
	private static final long serialVersionUID = 8147079789841383897L;
	private static final String TABLE = com.x.attendance.entity.PersistenceProperties.AttendanceV2GroupScheduleConfig.table;

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
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */

	public static final String groupId_FIELDNAME = "groupId";
	@FieldDescribe("考勤组id.")
	@Column(length = length_64B, name = ColumnNamePrefix + groupId_FIELDNAME)
  @CheckPersist(allowEmpty = false)
	private String groupId;
 

  public static final String scheduleConfigJson_FIELDNAME = "scheduleConfigJson";
  @FieldDescribe("配置 json 字符串")
	@Lob
	@Basic(fetch = FetchType.EAGER)
  @Column(length = JpaObject.length_128K, name = ColumnNamePrefix + scheduleConfigJson_FIELDNAME)
  private String scheduleConfigJson;
    

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

  public String getScheduleConfigJson() {
    return scheduleConfigJson;
  }

  public void setScheduleConfigJson(String scheduleConfigJson) {
    this.scheduleConfigJson = scheduleConfigJson;
  }
 

}
