package com.x.pan.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * office online协作锁定表
 * @author sword
 */
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.LockInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.LockInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN })})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class LockInfo extends SliceJpaObject {

	private static final long serialVersionUID = 5170199979868369931L;

	private static final String TABLE = PersistenceProperties.LockInfo.table;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	@Override
	public void onPersist() {
	}

	public LockInfo() {
	}

	public LockInfo(String person, String fileId, String lockValue) {
		this.fileId = fileId;
		this.person = person;
		this.lockValue = lockValue;
		this.id = fileId;
		this.lockTime = new Date();
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("锁定用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String person;

	public static final String fileId_FIELDNAME = "fileId";
	@FieldDescribe("文件ID。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fileId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fileId_FIELDNAME, unique = true)
	@CheckPersist(allowEmpty = false)
	private String fileId;

	public static final String lockValue_FIELDNAME = "lockValue";
	@FieldDescribe("锁定值.")
	@Column(length = length_255B, name = ColumnNamePrefix + lockValue_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String lockValue;

	public static final String lockTime_FIELDNAME = "lockTime";
	@FieldDescribe("锁定时间")
	@Column(name = ColumnNamePrefix + lockTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date lockTime;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getLockValue() {
		return lockValue;
	}

	public void setLockValue(String lockValue) {
		this.lockValue = lockValue;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	public boolean isExpired() {
		return DateTools.beforeNowMinutesNullIsTrue(this.getLockTime(), 30);
	}
}
