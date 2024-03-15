package com.x.processplatform.core.entity.content;

import static com.x.base.core.entity.StorageType.processPlatform;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.ActivityType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Attachment", description = "流程平台附件.")
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Content.Attachment.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Attachment.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = processPlatform)
public class Attachment extends StorageObject {

	private static final long serialVersionUID = -6564254194819206271L;
	private static final String TABLE = PersistenceProperties.Content.Attachment.table;

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
	public void onPersist() throws Exception {
		this.extension = StringUtils.trimToEmpty(this.extension);
		this.site = StringUtils.trimToEmpty(this.site);
		if (StringTools.utf8Length(this.getProperties().getName()) > length_255B) {
			this.name = StringTools.utf8FileNameSubString(this.getProperties().getName(), length_255B);
		}
	}

	@PostLoad
	public void postLoad() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.getProperties().getName())) {
			this.name = this.getProperties().getName();
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;
		this.getProperties().setName(name);
	}

	@Override
	public String getName() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.properties.getName())) {
			return this.properties.getName();
		} else {
			return this.name;
		}
	}

	public Attachment() {

	}

	public Attachment(Work work, String person, String site) {
		this.setCompleted(false);
		this.setWork(work.getId());
		this.setPerson(person);
		this.setLastUpdatePerson(person);
		this.setSite(site);
		/** 用于判断目录的值 */
		this.setWorkCreateTime(work.getCreateTime() != null ? work.getCreateTime() : new Date());
		this.setApplication(work.getApplication());
		this.setProcess(work.getProcess());
		this.setJob(work.getJob());
		this.setActivity(work.getActivity());
		this.setActivityName(work.getActivityName());
		this.setActivityToken(work.getActivityToken());
		this.setActivityType(work.getActivityType());
	}

	public Attachment(WorkCompleted work, String person, String site) {
		this.setCompleted(true);
		this.setWork(work.getWork());
		this.setWorkCompleted(work.getId());
		this.setPerson(person);
		this.setLastUpdatePerson(person);
		this.setSite(site);
		/** 用于判断目录的值 */
		this.setWorkCreateTime(work.getStartTime() != null ? work.getStartTime() : new Date());
		this.setApplication(work.getApplication());
		this.setProcess(work.getProcess());
		this.setJob(work.getJob());
		this.setActivity(work.getActivity());
		this.setActivityName(work.getActivityName());
	}

	/** 更新运行方法 */

	@Override
	public String path() {
		if (StringUtils.isNotEmpty(fromPath)) {
			return fromPath;
		}
		if (null == this.workCreateTime) {
			throw new IllegalStateException("workCreateTime can not be null.");
		}
		if (StringUtils.isEmpty(job)) {
			throw new IllegalStateException("job can not be empty.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new IllegalStateException("id can not be empty.");
		}
		String str = DateTools.format(workCreateTime, DateTools.formatCompact_yyyyMMdd);
		if (BooleanUtils.isTrue(this.getDeepPath())) {
			str += PATHSEPARATOR;
			str += StringUtils.substring(this.job, 0, 2);
			str += PATHSEPARATOR;
			str += StringUtils.substring(this.job, 2, 4);
		}
		str += PATHSEPARATOR;
		str += this.job;
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}

	@Override
	public String path(String operate) {
		if (StringUtils.isNotEmpty(fromPath) && !DELETE_OPERATE.equals(operate)) {
			return fromPath;
		}
		if (null == this.workCreateTime) {
			throw new IllegalStateException("workCreateTime can not be null.");
		}
		if (StringUtils.isEmpty(job)) {
			throw new IllegalStateException("job can not be empty.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new IllegalStateException("id can not be empty.");
		}
		String str = DateTools.format(workCreateTime, DateTools.formatCompact_yyyyMMdd);
		if (BooleanUtils.isTrue(this.getDeepPath())) {
			str += PATHSEPARATOR;
			str += StringUtils.substring(this.job, 0, 2);
			str += PATHSEPARATOR;
			str += StringUtils.substring(this.job, 2, 4);
		}
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

	@Override
	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	@Override
	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

	public AttachmentProperties getProperties() {
		if (null == this.properties) {
			this.properties = new AttachmentProperties();
		}
		return this.properties;
	}

	public void setProperties(AttachmentProperties properties) {
		this.properties = properties;
	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("文件名称,带扩展名的文件名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String name;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名。")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(allowEmpty = true, fileNameString = true)
	private String extension;

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
	private String storage;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + length_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long length;

	public static final String workCreateTime_FIELDNAME = "workCreateTime";
	@FieldDescribe("关联的Work创建时间，用于分类目录。")
	@Column(name = ColumnNamePrefix + workCreateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workCreateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date workCreateTime;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("任务.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("文件所有者")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("最后更新人员")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ lastUpdatePerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public static final String activity_FIELDNAME = "activity";
	@FieldDescribe("活动标识,可以为空,在给已完成工作添加附件时值为空.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activity;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("活动名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ activityName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName;

	public static final String activityType_FIELDNAME = "activityType";
	@FieldDescribe("活动类型.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + activityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ActivityType activityType;

	public static final String activityToken_FIELDNAME = "activityToken";
	@FieldDescribe("活动Token.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activityToken_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityToken;

	public static final String completed_FIELDNAME = "completed";
	@FieldDescribe("整个job是否已经完成.")
	@Column(name = ColumnNamePrefix + completed_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean completed;

	public static final String workCompleted_FIELDNAME = "workCompleted";
	@FieldDescribe("工作ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workCompleted_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workCompleted;

	public static final String work_FIELDNAME = "work";
	@FieldDescribe("已完成工作ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + work_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + work_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String work;

	public static final String site_FIELDNAME = "site";
	@FieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + site_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String site;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("根据流文件判断的文件类型.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String type;

	public static final String text_FIELDNAME = "text";
	@FieldDescribe("文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_100M, name = ColumnNamePrefix + text_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String text;

	public static final String readIdentityList_FIELDNAME = "readIdentityList";
	@FieldDescribe("可以访问的身份.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readIdentityList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readIdentityList;

	public static final String readUnitList_FIELDNAME = "readUnitList";
	@FieldDescribe("可以访问的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readUnitList;

	public static final String editIdentityList_FIELDNAME = "editIdentityList";
	@FieldDescribe("可以修改的用户.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ editIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + editIdentityList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + editIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + editIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> editIdentityList;

	public static final String editUnitList_FIELDNAME = "editUnitList";
	@FieldDescribe("可以修改的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ editUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + editUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + editUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + editUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> editUnitList;

	public static final String controllerIdentityList_FIELDNAME = "controllerIdentityList";
	@FieldDescribe("可以管理的用户.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ controllerIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ controllerIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerIdentityList;

	public static final String controllerUnitList_FIELDNAME = "controllerUnitList";
	@FieldDescribe("可以管理的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ controllerUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ controllerUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerUnitList;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	private Boolean deepPath;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public static final String divisionList_FIELDNAME = "divisionList";
	@FieldDescribe("分组.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ divisionList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + divisionList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + divisionList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + divisionList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> divisionList;

	public static final String fromJob_FIELDNAME = "fromJob";
	@FieldDescribe("附件拷贝来源任务，仅soft拷贝模式存储.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fromJob_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromJob;

	public static final String fromId_FIELDNAME = "fromId";
	@FieldDescribe("附件拷贝来源附件ID，仅soft拷贝模式存储.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fromId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fromId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromId;

	public static final String fromPath_FIELDNAME = "fromPath";
	@FieldDescribe("附件拷贝来源存储路径，仅soft拷贝模式存储.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + fromPath_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromPath;

	public static final String PROPERTIES_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private AttachmentProperties properties;

	public static final String stringValue01_FIELDNAME = "stringValue01";
	@FieldDescribe("预留数据String值01.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Deprecated(since = "8.0.3")
	private String stringValue01;

	public static final String stringValue02_FIELDNAME = "stringValue02";
	@FieldDescribe("预留数据String值02.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Deprecated(since = "8.0.3")
	private String stringValue02;

	public static final String stringValue03_FIELDNAME = "stringValue03";
	@FieldDescribe("预留数据String值03.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Deprecated(since = "8.0.3")
	private String stringValue03;

	public static final String OBJECTSECURITYCLEARANCE_FIELDNAME = "objectSecurityClearance";
	@FieldDescribe("客体密级标识.")
	@Column(name = ColumnNamePrefix + OBJECTSECURITYCLEARANCE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + OBJECTSECURITYCLEARANCE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer objectSecurityClearance;

	public Integer getObjectSecurityClearance() {
		return objectSecurityClearance;
	}

	public void setObjectSecurityClearance(Integer objectSecurityClearance) {
		this.objectSecurityClearance = objectSecurityClearance;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
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

	public String getWorkCompleted() {
		return workCompleted;
	}

	public void setWorkCompleted(String workCompleted) {
		this.workCompleted = workCompleted;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getReadIdentityList() {
		return readIdentityList;
	}

	public void setReadIdentityList(List<String> readIdentityList) {
		this.readIdentityList = readIdentityList;
	}

	public List<String> getReadUnitList() {
		return readUnitList;
	}

	public void setReadUnitList(List<String> readUnitList) {
		this.readUnitList = readUnitList;
	}

	public List<String> getEditIdentityList() {
		return editIdentityList;
	}

	public void setEditIdentityList(List<String> editIdentityList) {
		this.editIdentityList = editIdentityList;
	}

	public List<String> getEditUnitList() {
		return editUnitList;
	}

	public void setEditUnitList(List<String> editUnitList) {
		this.editUnitList = editUnitList;
	}

	public List<String> getControllerIdentityList() {
		return controllerIdentityList;
	}

	public void setControllerIdentityList(List<String> controllerIdentityList) {
		this.controllerIdentityList = controllerIdentityList;
	}

	public List<String> getControllerUnitList() {
		return controllerUnitList;
	}

	public void setControllerUnitList(List<String> controllerUnitList) {
		this.controllerUnitList = controllerUnitList;
	}

	public List<String> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<String> divisionList) {
		this.divisionList = divisionList;
	}

	public String getFromJob() {
		return fromJob;
	}

	public void setFromJob(String fromJob) {
		this.fromJob = fromJob;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getFromPath() {
		return fromPath;
	}

	public void setFromPath(String fromPath) {
		this.fromPath = fromPath;
	}

	public String getStringValue01() {
		return stringValue01;
	}

	public void setStringValue01(String stringValue01) {
		this.stringValue01 = stringValue01;
	}

	public String getStringValue02() {
		return stringValue02;
	}

	public void setStringValue02(String stringValue02) {
		this.stringValue02 = stringValue02;
	}

	public String getStringValue03() {
		return stringValue03;
	}

	public void setStringValue03(String stringValue03) {
		this.stringValue03 = stringValue03;
	}
}
