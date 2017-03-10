package com.x.cms.core.entity.element;

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
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.entity.item.ItemPrimitiveType;
import com.x.base.core.entity.item.ItemStringValueType;
import com.x.base.core.entity.item.ItemType;
import com.x.base.core.utils.DateTools;
import com.x.cms.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.AppDictItem.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AppDictItem extends Item {

	private static final long serialVersionUID = 3004565672415351544L;
	private static final String TABLE = PersistenceProperties.Element.AppDictItem.table;

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
	public void preUpdate() throws Exception{
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

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime")
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
	private String sequence;

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() throws Exception{
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

	@EntityFieldDescribe("application标识.")
	@Column(name="xappDictId", length = JpaObject.length_id)
	@Index(name = TABLE + "_appDictId")
	private String appDictId;

	@Column(name="xpath0", length = Item.pathLength)
	@Index(name = TABLE + "_path0")
	private String path0 = "";

	@Column(name="xpath1", length = Item.pathLength)
	@Index(name = TABLE + "_path1")
	private String path1 = "";

	@Column(name="xpath2", length = Item.pathLength)
	@Index(name = TABLE + "_path2")
	private String path2 = "";

	@Column(name="xpath3", length = Item.pathLength)
	@Index(name = TABLE + "_path3")
	private String path3 = "";

	@Column(name="xpath4", length = Item.pathLength)
	@Index(name = TABLE + "_path4")
	private String path4 = "";

	@Column(name="xpath5", length = Item.pathLength)
	@Index(name = TABLE + "_path5")
	private String path5 = "";

	@Column(name="xpath6", length = Item.pathLength)
	@Index(name = TABLE + "_path6")
	private String path6 = "";

	@Column(name="xpath7", length = Item.pathLength)
	@Index(name = TABLE + "_path7")
	private String path7 = "";

	@Column(name="xpath0Location")
	@Index(name = TABLE + "_path0Location")
	private Integer path0Location;

	@Column(name="xpath1Location")
	@Index(name = TABLE + "_path1Location")
	private Integer path1Location;

	@Column(name="xpath2Location")
	@Index(name = TABLE + "_path2Location")
	private Integer path2Location;

	@Column(name="xpath3Location")
	@Index(name = TABLE + "_path3Location")
	private Integer path3Location;

	@Column(name="xpath4Location")
	@Index(name = TABLE + "_path4Location")
	private Integer path4Location;

	@Column(name="xpath5Location")
	@Index(name = TABLE + "_path5Location")
	private Integer path5Location;

	@Column(name="xpath6Location")
	@Index(name = TABLE + "_path6Location")
	private Integer path6Location;

	@Column(name="xpath7Location")
	@Index(name = TABLE + "_path7Location")
	private Integer path7Location;

	@Enumerated(EnumType.STRING)
	@Column( name="xitemType", length = ItemType.length )
	@Index(name = TABLE + "_itemType")
	private ItemType itemType;

	@Enumerated(EnumType.STRING)
	@Column(name="xitemPrimitiveType", length = ItemPrimitiveType.length )
	@Index(name = TABLE + "_itemPrimitiveType")
	private ItemPrimitiveType itemPrimitiveType;

	@Enumerated(EnumType.STRING)
	@Column(name="xitemStringValueType", length = ItemStringValueType.length )
	@Index(name = TABLE + "_itemStringValueType")
	private ItemStringValueType itemStringValueType;

	@Column(name="xstringValue", length = ItemConverter.STRING_VALUE_MAX_LENGTH )
	@Index(name = TABLE + "_stringValue")
	private String stringValue = null;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name="xstringLobValue", length = JpaObject.length_10M)
	private String stringLobValue;

	@Column(name="xnumberValue")
	@Index(name = TABLE + "_numberValue")
	private Double numberValue = null;

	@Column(name="xdateTimeValue")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + "_dateTimeValue")
	private Date dateTimeValue = null;

	@Column(name="xdateValue")
	@Temporal(TemporalType.DATE)
	@Index(name = TABLE + "_dateValue")
	private Date dateValue = null;

	@Column(name="xtimeValue")
	@Temporal(TemporalType.TIME)
	@Index(name = TABLE + "_timeValue")
	private Date timeValue = null;

	@Column(name="xbooleanValue")
	@Index(name = TABLE + "_booleanValue")
	private Boolean booleanValue = null;
	
	@EntityFieldDescribe("lobItem连接Id.")
	@Column(length = JpaObject.length_id, name = "xlobItem")
	@Index(name = TABLE + "_lobItem")
	@CheckPersist(allowEmpty = false)
	private String lobItem;
	
	public String getAppDictId() {
		return appDictId;
	}

	public void setAppDictId(String appDictId) {
		this.appDictId = appDictId;
	}

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

	public String getLobItem() {
		return lobItem;
	}

	public void setLobItem(String lobItem) {
		this.lobItem = lobItem;
	}

}