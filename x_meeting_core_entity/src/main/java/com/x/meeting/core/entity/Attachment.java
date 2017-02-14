package com.x.meeting.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Attachment.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.meeting)
public class Attachment extends StorageObject {

	private static final long serialVersionUID = -6564254194819206271L;
	private static final String TABLE = PersistenceProperties.Attachment.table;

	@PrePersist
	public void prePersist() {
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		if (null == this.sequence) {
			this.sequence = StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId());
		}
		this.onPersist();
	}

	@PreUpdate
	public void preUpdate() {
		this.updateTime = new Date();
		this.onPersist();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@EntityFieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe("列表序号,由创建时间以及ID组成.在保存时自动生成.")
	@Column(length = AbstractPersistenceProperties.length_sequence, name = "xsequence")
	@Index(name = TABLE + "_sequence")
	private String sequence;

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = JpaObject.length_id, name = "xid")
	@Index(name = TABLE + "_id")
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() {
		this.extension = StringUtils.trimToEmpty(this.extension);
	}

	/* 更新运行方法 */

	@Override
	public String path() throws Exception {
		if (null == this.meeting) {
			throw new Exception("meeting can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = this.meeting;
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}

	@Override
	public String getStorage() {
		return storage;
	}

	@Override
	public void setStorage(String storage) {
		this.storage = storage;
	}

	@Override
	public Long getLength() {
		return length;
	}

	@Override
	public void setLength(Long length) {
		this.length = length;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getExtension() {
		return extension;
	}

	@Override
	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@EntityFieldDescribe("关联的会议.")
	@Column(length = JpaObject.length_id, name = "xmeeting")
	@Index(name = TABLE + "_meeting")
	@CheckPersist(allowEmpty = false)
	private String meeting;

	@EntityFieldDescribe("文件名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String name;

	@EntityFieldDescribe("扩展名。")
	@Column(length = JpaObject.length_16B, name = "xextension")
	@CheckPersist(allowEmpty = true, fileNameString = true)
	private String extension;

	@EntityFieldDescribe("关联类型的名称.")
	@Column(length = JpaObject.length_64B, name = "xstorage")
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + "_storage")
	private String storage;

	@EntityFieldDescribe("文件大小.")
	@Column(name = "xlength")
	@Index(name = TABLE + "_length")
	@CheckPersist(allowEmpty = false)
	private Long length;

	@EntityFieldDescribe("是否是会议纪要附件.")
	@Column(name = "xsummary")
	@Index(name = TABLE + "_summary")
	@CheckPersist(allowEmpty = false)
	private Boolean summary;

	@EntityFieldDescribe("最后更新时间")
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	@EntityFieldDescribe("最后更新人员")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xlastUpdatePerson")
	@Index(name = TABLE + "_lastUpdatePerson")
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public String getMeeting() {
		return meeting;
	}

	public void setMeeting(String meeting) {
		this.meeting = meeting;
	}

	public Boolean getSummary() {
		return summary;
	}

	public void setSummary(Boolean summary) {
		this.summary = summary;
	}

}