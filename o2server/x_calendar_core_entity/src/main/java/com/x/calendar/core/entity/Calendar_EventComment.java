package com.x.calendar.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import javax.persistence.*;
import java.util.Date;

/**
 * 日程事件超长LOB信息
 * 如果事件COMMENT超过70个字符时，使用LOB字段进行存储，在COMMENT中使用'CLOB'来标记，在COMMENTID里指定其记录引用
 * 
 * @author O2LEE
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Calendar_EventComment.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Calendar_EventComment.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Calendar_EventComment extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Calendar_EventComment.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME, unique = true)
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

	public static final String lobValue_FIELDNAME = "lobValue";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("COMMENT信息的LOB值")
	@Column(name = "xlobValue", length = JpaObject.length_10M)
	private String lobValue = "";

	public static final String checkTime_FIELDNAME = "checkTime";
	@FieldDescribe("检查时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + checkTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date checkTime = null;

	public Date getCheckTime() { return checkTime; }

	public void setCheckTime(Date checkTime) { this.checkTime = checkTime; }

	public String getLobValue() {
		return lobValue;
	}

	public void setLobValue(String lobValue) {
		this.lobValue = lobValue;
	}
}