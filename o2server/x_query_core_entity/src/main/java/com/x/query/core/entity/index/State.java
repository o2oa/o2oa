package com.x.query.core.entity.index;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.query.core.entity.PersistenceProperties;

@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@javax.persistence.Table(name = PersistenceProperties.Index.State.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Index.State.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class State extends SliceJpaObject {

	private static final long serialVersionUID = -5610293696763235753L;

	private static final String TABLE = PersistenceProperties.Index.State.table;

	public static final String TYPE_WORKCOMPLETED = "workCompleted";
	public static final String TYPE_CMS = "cms";

	public static final String FREQUENCY_LOW = "low";
	public static final String FREQUENCY_HIGH = "high";

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

	@Override
	public void onPersist() throws Exception {
		// nothing
	}

	public static final String LATESTID_FIELDNAME = "latestId";
	@FieldDescribe("最后查询标识.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + LATESTID_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String latestId;

	public static final String LATESTCREATETIME_FIELDNAME = "latestCreateTime";
	@FieldDescribe("最后查询创建时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + LATESTSEQUENCE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date latestCreateTime;

	public static final String LATESTSEQUENCE_FIELDNAME = "latestSequence";
	@FieldDescribe("最后查询顺序标识.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + LATESTSEQUENCE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String latestSequence;

	public static final String TYPE_FIELDNAME = "type";
	@Flag
	@FieldDescribe("类型.")
	@Column(length = length_32B, name = ColumnNamePrefix + TYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String NODE_FIELDNAME = "node";
	@Flag
	@FieldDescribe("节点.")
	@Column(length = length_255B, name = ColumnNamePrefix + NODE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String node;

	public static final String FREQUENCY_FIELDNAME = "frequency";
	@FieldDescribe("频率.")
	@Column(length = length_8B, name = ColumnNamePrefix + FREQUENCY_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String frequency;

	public String getLatestId() {
		return latestId;
	}

	public void setLatestId(String latestId) {
		this.latestId = latestId;
	}

	public Date getLatestCreateTime() {
		return latestCreateTime;
	}

	public void setLatestCreateTime(Date latestCreateTime) {
		this.latestCreateTime = latestCreateTime;
	}

	public String getLatestSequence() {
		return latestSequence;
	}

	public void setLatestSequence(String latestSequence) {
		this.latestSequence = latestSequence;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

}
