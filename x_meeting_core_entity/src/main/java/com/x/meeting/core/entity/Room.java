package com.x.meeting.core.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Room.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Room extends SliceJpaObject {

	private static final long serialVersionUID = -4115399782853963899L;
	private static final String TABLE = PersistenceProperties.Room.table;

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
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("name拼音.")
	@Index(name = TABLE + "_pinyin")
	@Column(length = JpaObject.length_255B, name = "xpinyin")
	@CheckPersist(allowEmpty = true)
	private String pinyin;

	@EntityFieldDescribe("name拼音首字母.")
	@Column(length = JpaObject.length_255B, name = "xpinyinInitial")
	@Index(name = TABLE + "_pinyinInitial")
	@CheckPersist(allowEmpty = true)
	private String pinyinInitial;

	@EntityFieldDescribe("名称,不可重名.")
	@Column(length = JpaObject.length_255B, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "id" }, type = Room.class) )
	private String name;

	@EntityFieldDescribe("所属building.")
	@Column(length = JpaObject.length_id, name = "xbuilding")
	@Index(name = TABLE + "_building")
	@CheckPersist(allowEmpty = false)
	private String building;

	@EntityFieldDescribe("所属楼层.")
	@Column(name = "xfloor")
	@CheckPersist(allowEmpty = false)
	private Integer floor;

	@EntityFieldDescribe("房号.")
	@Column(length = JpaObject.length_255B, name = "xroomNumber")
	@Index(name = TABLE + "_roomNumber")
	@CheckPersist(allowEmpty = true)
	private String roomNumber;

	@EntityFieldDescribe("分机号.")
	@Column(length = JpaObject.length_255B, name = "xphoneNumber")
	@Index(name = TABLE + "_phoneNumber")
	@CheckPersist(allowEmpty = true)
	private String phoneNumber;

	@EntityFieldDescribe("设备内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xdevice")
	@CheckPersist(allowEmpty = true)
	private String device;

	@EntityFieldDescribe("照片.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xphoto")
	@CheckPersist(allowEmpty = true)
	private String photo;

	@EntityFieldDescribe("会议室容量")
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xcapacity")
	@CheckPersist(allowEmpty = true)
	private Integer capacity;

	@EntityFieldDescribe("会议室申请审核人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xauditor")
	@Index(name = TABLE + "_auditor")
	@CheckPersist(allowEmpty = true)
	private String auditor;

	@EntityFieldDescribe("会议室状态,可用,不可用")
	@Column(name = "xavailable")
	@Index(name = TABLE + "_available")
	@CheckPersist(allowEmpty = false)
	private Boolean available;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPinyinInitial() {
		return pinyinInitial;
	}

	public void setPinyinInitial(String pinyinInitial) {
		this.pinyinInitial = pinyinInitial;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}