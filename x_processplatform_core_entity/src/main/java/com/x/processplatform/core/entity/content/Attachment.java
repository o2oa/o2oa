package com.x.processplatform.core.entity.content;

import static com.x.base.core.entity.StorageType.processPlatform;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.ActivityType;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Content.Attachment.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = processPlatform)
public class Attachment extends StorageObject {

	private static final long serialVersionUID = -6564254194819206271L;
	private static final String TABLE = PersistenceProperties.Content.Attachment.table;

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
		this.site = StringUtils.trimToEmpty(this.site);
	}

	/* 更新运行方法 */

	@Override
	public String path() throws Exception {
		if (null == this.workCreateTime) {
			throw new Exception("workCreateTime can not be null.");
		}
		if (StringUtils.isEmpty(job)) {
			throw new Exception("job can not be empty.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = DateTools.format(workCreateTime, DateTools.formatCompact_yyyyMMdd);
		str += PATHSEPARATOR;
		str += this.job;
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

	@EntityFieldDescribe("文件名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String name;

	@EntityFieldDescribe("扩展名。")
	@Column(length = JpaObject.length_16B, name = "xextension")
	@CheckPersist(allowEmpty = true, fileNameString = true)
	private String extension;

	@EntityFieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_64B, name = "xstorage")
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + "_storage")
	private String storage;

	@EntityFieldDescribe("文件大小.")
	@Column(name = "xlength")
	@Index(name = TABLE + "_length")
	@CheckPersist(allowEmpty = false)
	private Long length;

	@EntityFieldDescribe("关联的Work创建时间，用于分类目录。")
	@Column(name = "xworkCreateTime")
	@Index(name = TABLE + "_workCreateTime")
	@CheckPersist(allowEmpty = false)
	private Date workCreateTime;

	@EntityFieldDescribe("应用ID.")
	@Column(length = JpaObject.length_id, name = "xapplication")
	@Index(name = TABLE + "_application")
	@CheckPersist(allowEmpty = false)
	private String application;

	@EntityFieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = "xprocess")
	@Index(name = TABLE + "_process")
	@CheckPersist(allowEmpty = false)
	private String process;

	@EntityFieldDescribe("任务.")
	@Column(length = JpaObject.length_id, name = "xjob")
	@Index(name = TABLE + "_job")
	@CheckPersist(allowEmpty = false)
	private String job;

	@EntityFieldDescribe("文件所有者")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xperson")
	@Index(name = TABLE + "_person")
	@CheckPersist(allowEmpty = false)
	private String person;

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

	@EntityFieldDescribe("活动ID.")
	@Column(length = JpaObject.length_id, name = "xactivity")
	@Index(name = TABLE + "_activity")
	@CheckPersist(allowEmpty = false)
	private String activity;

	@EntityFieldDescribe("活动名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xactivityName")
	@Index(name = TABLE + "_activityName")
	@CheckPersist(allowEmpty = true)
	private String activityName;

	@EntityFieldDescribe("活动类型.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = "xactivityType")
	@Index(name = TABLE + "_activityType")
	@CheckPersist(allowEmpty = false)
	private ActivityType activityType;

	@EntityFieldDescribe("活动Token.")
	@Column(length = JpaObject.length_id, name = "xactivityToken")
	@Index(name = TABLE + "_activityToken")
	@CheckPersist(allowEmpty = false)
	private String activityToken;

	@EntityFieldDescribe("工作是否已经完成.")
	@Index(name = TABLE + "_completed")
	@Column(name = "xcompleted")
	@CheckPersist(allowEmpty = false)
	private Boolean completed;

	@EntityFieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_64B, name = "xsite")
	@CheckPersist(allowEmpty = true)
	private String site;

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public Date getWorkCreateTime() {
		return workCreateTime;
	}

	public void setWorkCreateTime(Date workCreateTime) {
		this.workCreateTime = workCreateTime;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

}