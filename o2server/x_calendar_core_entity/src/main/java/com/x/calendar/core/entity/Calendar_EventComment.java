package com.x.calendar.core.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 日程事件超长LOB信息
 * 如果事件COMMENT超过70个字符时，使用LOB字段进行存储，在COMMENT中使用'CLOB'来标记，在COMMENTID里指定其记录引用
 * 
 * @author O2LEE
 *
 */
@Schema(name = "Calendar_EventComment", description = "日程事件长信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
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

	public static final String lobValue_FIELDNAME = "lobValue";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("COMMENT信息的LOB值")
	@Column(name = ColumnNamePrefix + lobValue_FIELDNAME, length = JpaObject.length_10M)
	private String lobValue = "";

	public static final String checkTime_FIELDNAME = "checkTime";
	@FieldDescribe("检查时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + checkTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date checkTime = null;

	public Date getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public String getLobValue() {
		return lobValue;
	}

	public void setLobValue(String lobValue) {
		this.lobValue = lobValue;
	}
}