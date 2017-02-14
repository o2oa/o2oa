package com.x.cms.core.entity.content;

import java.util.Date;

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
import com.x.base.core.entity.item.ItemPrimitiveType;
import com.x.base.core.entity.item.ItemStringValueType;
import com.x.base.core.entity.item.ItemType;
import com.x.base.core.utils.DateTools;
import com.x.cms.core.entity.PersistenceProperties;


@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Content.DataItem.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DataItem extends Item {

	private static final long serialVersionUID = 7182248599724230391L;
	private static final String TABLE = PersistenceProperties.Content.DataItem.table;

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
	@Column(length = JpaObject.length_id, name = JpaObject.IDCOLUMN)
	@Index(name = TABLE + "_id")
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() {
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

	@Column(length = StringValueMaxLength, name = "xstringValue")
	@Index(name = TABLE + "_stringValue")
	private String stringValue;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xstringLobValue")
	private String stringLobValue;

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

	@EntityFieldDescribe("栏目ID")
	@Column(length = JpaObject.length_id, name = "xappId")
	@Index(name = TABLE + "_appId")
	@CheckPersist( allowEmpty = false )
	private String appId;

	@EntityFieldDescribe("栏目名称")
	@Column(name = "xappName", length = JpaObject.length_96B)
	@CheckPersist( allowEmpty = true )
	private String appName;

	@EntityFieldDescribe("分类ID")
	@Column(length = JpaObject.length_id, name = "xcatagoryId")
	@Index(name = TABLE + "_catagoryId")
	@CheckPersist(allowEmpty = false)
	private String catagoryId;

	@EntityFieldDescribe( "分类名称" )
	@Column( name="xcatagoryName", length = JpaObject.length_96B  )
	@CheckPersist( allowEmpty = true )
	private String catagoryName;
	
	@EntityFieldDescribe("文档ID")
	@Column(length = JpaObject.length_id, name = "xdocId")
	@Index(name = TABLE + "_docId")
	@CheckPersist(allowEmpty = false)
	private String docId;

	@EntityFieldDescribe("文档状态: published | draft")
	@Column(name="xdocStatus", length = JpaObject.length_16B )
	@Index(name = TABLE + "_docStatus")
	@CheckPersist(allowEmpty = true)
	private String docStatus;
	
	@EntityFieldDescribe("文档发布时间")
	@Column(name="xpublishTime" )
	@Index(name = TABLE + "_publishTime")
	@CheckPersist( allowEmpty = true )
	private Date publishTime;
	
	@EntityFieldDescribe("标题")
	@Column(length = AbstractPersistenceProperties.processPlatform_title_length, name = "xtitle")
	@Index(name = TABLE + "_title")
	@CheckPersist(allowEmpty = true)
	private String title;
	
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

	public String getStringLobValue() {
		return stringLobValue;
	}

	public void setStringLobValue(String stringLobValue) {
		this.stringLobValue = stringLobValue;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getCatagoryId() {
		return catagoryId;
	}

	public void setCatagoryId(String catagoryId) {
		this.catagoryId = catagoryId;
	}

	public String getCatagoryName() {
		return catagoryName;
	}

	public void setCatagoryName(String catagoryName) {
		this.catagoryName = catagoryName;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}	
}