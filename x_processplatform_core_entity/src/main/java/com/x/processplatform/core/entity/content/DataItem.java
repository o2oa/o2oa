package com.x.processplatform.core.entity.content;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.entity.item.Item;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.entity.item.ItemPrimitiveType;
import com.x.base.core.entity.item.ItemStringValueType;
import com.x.base.core.entity.item.ItemType;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;

/**
 * 需要编写级联删除的地方:<br/>
 * 1.designer 的Applicaiton删除<br/>
 * 2.designer 的Process删除<br/>
 * 3.processing中的脚本运行对象中的WorkDataHelper<br/>
 * 4.processing中的Cancel环节<br/>
 * 5.processing中的End环节<br/>
 * 6.surface中的Data的增,删,改<br/>
 * 7.surface中的管理端Work删除<br/>
 */
@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Content.DataItem.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DataItem extends Item {

	private static final long serialVersionUID = 7182248599724230391L;
	private static final String TABLE = PersistenceProperties.Content.DataItem.table;

	@PrePersist
	public void prePersist() throws Exception {
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
	public void preUpdate() throws Exception {
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
	@Column(length = JpaObject.length_id, name = JpaObject.IDCOLUMN)
	@Index(name = TABLE + "_id")
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() throws Exception {
		this.path0 = StringUtils.trimToEmpty(this.path0);
		this.path1 = StringUtils.trimToEmpty(this.path1);
		this.path2 = StringUtils.trimToEmpty(this.path2);
		this.path3 = StringUtils.trimToEmpty(this.path3);
		this.path4 = StringUtils.trimToEmpty(this.path4);
		this.path5 = StringUtils.trimToEmpty(this.path5);
		this.path6 = StringUtils.trimToEmpty(this.path6);
		this.path7 = StringUtils.trimToEmpty(this.path7);
		this.path0Location = NumberUtils.toInt(this.path0, -1);
		this.path1Location = NumberUtils.toInt(this.path1, -1);
		this.path2Location = NumberUtils.toInt(this.path2, -1);
		this.path3Location = NumberUtils.toInt(this.path3, -1);
		this.path4Location = NumberUtils.toInt(this.path4, -1);
		this.path5Location = NumberUtils.toInt(this.path5, -1);
		this.path6Location = NumberUtils.toInt(this.path6, -1);
		this.path7Location = NumberUtils.toInt(this.path7, -1);
		if (StringUtils.isEmpty(this.startTimeMonth)) {
			this.startTimeMonth = DateTools.format(this.startTime, DateTools.format_yyyyMM);
		}
		if (StringUtils.isEmpty(this.completedTimeMonth) && (null != this.completedTime)) {
			this.completedTimeMonth = DateTools.format(this.completedTime, DateTools.format_yyyyMM);
		}
	}

	/* 更新运行方法 */

	@Column(length = Item.pathLength, name = "xpath0")
	@Index(name = TABLE + "_path0")
	private String path0 = "";

	@Column(length = Item.pathLength, name = "xpath1")
	@Index(name = TABLE + "_path1")
	private String path1 = "";

	@Column(length = Item.pathLength, name = "xpath2")
	@Index(name = TABLE + "_path2")
	private String path2 = "";

	@Column(length = Item.pathLength, name = "xpath3")
	@Index(name = TABLE + "_path3")
	private String path3 = "";

	@Column(length = Item.pathLength, name = "xpath4")
	@Index(name = TABLE + "_path4")
	private String path4 = "";

	@Column(length = Item.pathLength, name = "xpath5")
	@Index(name = TABLE + "_path5")
	private String path5 = "";

	@Column(length = Item.pathLength, name = "xpath6")
	@Index(name = TABLE + "_path6")
	private String path6 = "";

	@Column(length = Item.pathLength, name = "xpath7")
	@Index(name = TABLE + "_path7")
	private String path7 = "";

	@Index(name = TABLE + "_path0Location")
	@Column(name = "xpath0Location")
	private Integer path0Location;

	@Index(name = TABLE + "_path1Location")
	@Column(name = "xpath1Location")
	private Integer path1Location;

	@Index(name = TABLE + "_path2Location")
	@Column(name = "xpath2Location")
	private Integer path2Location;

	@Index(name = TABLE + "_path3Location")
	@Column(name = "xpath3Location")
	private Integer path3Location;

	@Index(name = TABLE + "_path4Location")
	@Column(name = "xpath4Location")
	private Integer path4Location;

	@Index(name = TABLE + "_path5Location")
	@Column(name = "xpath5Location")
	private Integer path5Location;

	@Index(name = TABLE + "_path6Location")
	@Column(name = "xpath6Location")
	private Integer path6Location;

	@Index(name = TABLE + "_path7Location")
	@Column(name = "xpath7Location")
	private Integer path7Location;

	@Enumerated(EnumType.STRING)
	@Column(length = JpaObject.length_1B, name = "xitemType")
	@Index(name = TABLE + "_itemType")
	@CheckPersist(allowEmpty = false)
	private ItemType itemType;

	@Enumerated(EnumType.STRING)
	@Column(length = JpaObject.length_1B, name = "xitemPrimitiveType")
	@Index(name = TABLE + "_itemPrimitiveType")
	@CheckPersist(allowEmpty = false)
	private ItemPrimitiveType itemPrimitiveType;

	@Enumerated(EnumType.STRING)
	@Column(length = JpaObject.length_2B, name = "xitemStringValueType")
	@Index(name = TABLE + "_itemStringValueType")
	@CheckPersist(allowEmpty = false)
	private ItemStringValueType itemStringValueType;

	@Column(length = ItemConverter.STRING_VALUE_MAX_LENGTH, name = "xstringValue")
	@Index(name = TABLE + "_stringValue")
	private String stringValue;

	@EntityFieldDescribe("lobItem连接Id.")
	@Column(length = JpaObject.length_id, name = "xlobItem")
	@Index(name = TABLE + "_lobItem")
	@CheckPersist(allowEmpty = false)
	private String lobItem;

	@Index(name = TABLE + "_numberValue")
	@Column(name = "xnumberValue")
	private Double numberValue;

	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + "_dateTimeValue")
	@Column(name = "xdateTimeValue")
	private Date dateTimeValue;

	@Temporal(TemporalType.DATE)
	@Index(name = TABLE + "_dateValue")
	@Column(name = "xdateValue")
	private Date dateValue;

	@Temporal(TemporalType.TIME)
	@Index(name = TABLE + "_timeValue")
	@Column(name = "xtimeValue")
	private Date timeValue;

	@Index(name = TABLE + "_booleanValue")
	@Column(name = "xbooleanValue")
	private Boolean booleanValue;

	/* Item 字段 */

	@EntityFieldDescribe("Job标识.")
	@Column(length = JpaObject.length_id, name = "xjob")
	@Index(name = TABLE + "_job")
	@CheckPersist(allowEmpty = false)
	private String job;

	@EntityFieldDescribe("标题")
	@Column(length = AbstractPersistenceProperties.processPlatform_title_length, name = "xtitle")
	@Index(name = TABLE + "_title")
	@CheckPersist(allowEmpty = true)
	private String title;

	@EntityFieldDescribe("工作开始时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + "_startTime")
	@Column(name = "xstartTime")
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	@EntityFieldDescribe("用于在Filter中分类使用.")
	@Column(length = JpaObject.length_16B, name = "xstartTimeMonth")
	@Index(name = TABLE + "_startTimeMonth")
	@CheckPersist(allowEmpty = true)
	private String startTimeMonth;

	@EntityFieldDescribe("工作结束时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + "_completedTime")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xcompletedTime")
	private Date completedTime;

	@EntityFieldDescribe("用于在Filter中分类使用.")
	@Column(length = JpaObject.length_16B, name = "xcompletedTimeMonth")
	@Index(name = TABLE + "_completedTimeMonth")
	@CheckPersist(allowEmpty = true)
	private String completedTimeMonth;

	@EntityFieldDescribe("创建人，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorPerson")
	@Index(name = TABLE + "_creatorPerson")
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	@EntityFieldDescribe("创建人Identity，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorIdentity")
	@Index(name = TABLE + "_creatorIdentity")
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	@EntityFieldDescribe("创建人部门，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorDepartment")
	@Index(name = TABLE + "_creatorDepartment")
	@CheckPersist(allowEmpty = true)
	private String creatorDepartment;

	@EntityFieldDescribe("创建人公司，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorCompany")
	@Index(name = TABLE + "_creatorCompany")
	@CheckPersist(allowEmpty = true)
	private String creatorCompany;

	@EntityFieldDescribe("应用ID")
	@Column(length = JpaObject.length_id, name = "xapplication")
	@Index(name = TABLE + "_application")
	@CheckPersist(allowEmpty = false)
	private String application;

	@EntityFieldDescribe("应用名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xapplicationName")
	@Index(name = TABLE + "_applicationName")
	@CheckPersist(allowEmpty = true)
	private String applicationName;

	@EntityFieldDescribe("应用别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xapplicationAlias")
	@Index(name = TABLE + "_applicationAlias")
	@CheckPersist(allowEmpty = true)
	private String applicationAlias;

	@EntityFieldDescribe("流程ID")
	@Column(length = JpaObject.length_id, name = "xprocess")
	@Index(name = TABLE + "_process")
	@CheckPersist(allowEmpty = false)
	private String process;

	@EntityFieldDescribe("流程名称")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xprocessName")
	@Index(name = TABLE + "_processName")
	@CheckPersist(allowEmpty = false)
	private String processName;

	@EntityFieldDescribe("流程别名")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xprocessAlias")
	@Index(name = TABLE + "_processAlias")
	@CheckPersist(allowEmpty = false)
	private String processAlias;

	@EntityFieldDescribe("编号")
	@Column(length = JpaObject.length_128B, name = "xserial")
	@Index(name = TABLE + "_serial")
	@CheckPersist(allowEmpty = true)
	private String serial;

	@EntityFieldDescribe("是否已经完成.")
	@Column(name = "xcompleted")
	@Index(name = TABLE + "_completed")
	@CheckPersist(allowEmpty = false)
	private Boolean completed;

	public String getPath0() {
		return path0;
	}

	public void setPath0(String path0) {
		this.path0 = path0;
	}

	public String getPath1() {
		return path1;
	}

	public void setPath1(String path1) {
		this.path1 = path1;
	}

	public String getPath2() {
		return path2;
	}

	public void setPath2(String path2) {
		this.path2 = path2;
	}

	public String getPath3() {
		return path3;
	}

	public void setPath3(String path3) {
		this.path3 = path3;
	}

	public String getPath4() {
		return path4;
	}

	public void setPath4(String path4) {
		this.path4 = path4;
	}

	public String getPath5() {
		return path5;
	}

	public void setPath5(String path5) {
		this.path5 = path5;
	}

	public String getPath6() {
		return path6;
	}

	public void setPath6(String path6) {
		this.path6 = path6;
	}

	public String getPath7() {
		return path7;
	}

	public void setPath7(String path7) {
		this.path7 = path7;
	}

	public Integer getPath0Location() {
		return path0Location;
	}

	public void setPath0Location(Integer path0Location) {
		this.path0Location = path0Location;
	}

	public Integer getPath1Location() {
		return path1Location;
	}

	public void setPath1Location(Integer path1Location) {
		this.path1Location = path1Location;
	}

	public Integer getPath2Location() {
		return path2Location;
	}

	public void setPath2Location(Integer path2Location) {
		this.path2Location = path2Location;
	}

	public Integer getPath3Location() {
		return path3Location;
	}

	public void setPath3Location(Integer path3Location) {
		this.path3Location = path3Location;
	}

	public Integer getPath4Location() {
		return path4Location;
	}

	public void setPath4Location(Integer path4Location) {
		this.path4Location = path4Location;
	}

	public Integer getPath5Location() {
		return path5Location;
	}

	public void setPath5Location(Integer path5Location) {
		this.path5Location = path5Location;
	}

	public Integer getPath6Location() {
		return path6Location;
	}

	public void setPath6Location(Integer path6Location) {
		this.path6Location = path6Location;
	}

	public Integer getPath7Location() {
		return path7Location;
	}

	public void setPath7Location(Integer path7Location) {
		this.path7Location = path7Location;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public ItemPrimitiveType getItemPrimitiveType() {
		return itemPrimitiveType;
	}

	public void setItemPrimitiveType(ItemPrimitiveType itemPrimitiveType) {
		this.itemPrimitiveType = itemPrimitiveType;
	}

	public ItemStringValueType getItemStringValueType() {
		return itemStringValueType;
	}

	public void setItemStringValueType(ItemStringValueType itemStringValueType) {
		this.itemStringValueType = itemStringValueType;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Double getNumberValue() {
		return numberValue;
	}

	public void setNumberValue(Double numberValue) {
		this.numberValue = numberValue;
	}

	public Date getDateTimeValue() {
		return dateTimeValue;
	}

	public void setDateTimeValue(Date dateTimeValue) {
		this.dateTimeValue = dateTimeValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Date getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getStartTimeMonth() {
		return startTimeMonth;
	}

	public void setStartTimeMonth(String startTimeMonth) {
		this.startTimeMonth = startTimeMonth;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public String getCreatorDepartment() {
		return creatorDepartment;
	}

	public void setCreatorDepartment(String creatorDepartment) {
		this.creatorDepartment = creatorDepartment;
	}

	public String getCreatorCompany() {
		return creatorCompany;
	}

	public void setCreatorCompany(String creatorCompany) {
		this.creatorCompany = creatorCompany;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
	}

	public String getCompletedTimeMonth() {
		return completedTimeMonth;
	}

	public void setCompletedTimeMonth(String completedTimeMonth) {
		this.completedTimeMonth = completedTimeMonth;
	}

	public String getApplicationAlias() {
		return applicationAlias;
	}

	public void setApplicationAlias(String applicationAlias) {
		this.applicationAlias = applicationAlias;
	}

	public String getProcessAlias() {
		return processAlias;
	}

	public void setProcessAlias(String processAlias) {
		this.processAlias = processAlias;
	}

	public String getLobItem() {
		return lobItem;
	}

	public void setLobItem(String lobItem) {
		this.lobItem = lobItem;
	}
}