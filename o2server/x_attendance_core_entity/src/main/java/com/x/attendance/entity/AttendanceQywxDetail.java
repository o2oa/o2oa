package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceDetail", description = "考勤企业微信信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceQywxDetail.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceQywxDetail.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceQywxDetail extends SliceJpaObject {

	private static final long serialVersionUID = -1447276817950216827L;
	private static final String TABLE = PersistenceProperties.AttendanceQywxDetail.table;

	@Override
	public void onPersist() throws Exception {
	}

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
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	public static final String o2User_FIELDNAME = "o2User";
	@FieldDescribe("O2用户distinguishedName")
	@Column(name = ColumnNamePrefix + o2User_FIELDNAME, length = AbstractPersistenceProperties.organization_name_length)
	private String o2User;

	public static final String o2Unit_FIELDNAME = "o2Unit";
	@FieldDescribe("O2用户所在的组织distinguishedName")
	@Column(name = ColumnNamePrefix + o2Unit_FIELDNAME, length = AbstractPersistenceProperties.organization_name_length)
	private String o2Unit;

	public static final String userid_FIELDNAME = "userid";
	@FieldDescribe("用户id")
	@Column(name = ColumnNamePrefix + userid_FIELDNAME, length = length_96B)
	private String userid;

	public static final String groupname_FIELDNAME = "groupname";
	@FieldDescribe("打卡规则名称")
	@Column(name = ColumnNamePrefix + groupname_FIELDNAME, length = length_128B)
	private String groupname;

	// 打卡类型
	public static final String CHECKIN_TYPE_ON = "上班打卡";
	public static final String CHECKIN_TYPE_OFF = "下班打卡";
	public static final String CHECKIN_TYPE_OUTSIDE = "外出打卡";

	public static final String checkin_type_FIELDNAME = "checkin_type";
	@FieldDescribe("打卡类型。字符串，目前有：上班打卡，下班打卡，外出打卡")
	@Column(name = ColumnNamePrefix + checkin_type_FIELDNAME, length = length_128B)
	private String checkin_type;

	public static final String checkin_time_FIELDNAME = "checkin_time";
	@FieldDescribe("打卡时间。Unix时间戳")
	@Column(name = ColumnNamePrefix + checkin_time_FIELDNAME)
	private long checkin_time;

	public static final String checkin_time_date_FIELDNAME = "checkin_time_date";
	@FieldDescribe("实际打卡时间,  用Date格式存储")
	@Column(name = ColumnNamePrefix + checkin_time_date_FIELDNAME)
	private Date checkin_time_date;

	// 异常类型
	public static final String EXCEPTION_TYPE_TIME = "时间异常";
	public static final String EXCEPTION_TYPE_ADDRESS = "地点异常";
	public static final String EXCEPTION_TYPE_WIFI = "wifi异常";
	public static final String EXCEPTION_TYPE_UNKOWN_DEVICE = "非常用设备";
	public static final String EXCEPTION_TYPE_NOSIGN = "未打卡";
	public static final String EXCEPTION_TYPE_NORMAL = "正常";

	public static final String exception_type_FIELDNAME = "exception_type";
	@FieldDescribe("异常类型，字符串，包括：时间异常，地点异常，未打卡，wifi异常，非常用设备。如果有多个异常，以分号间隔")
	@Column(name = ColumnNamePrefix + exception_type_FIELDNAME, length = length_255B)
	private String exception_type;

	public static final String location_title_FIELDNAME = "location_title";
	@FieldDescribe("打卡地点title")
	@Column(name = ColumnNamePrefix + location_title_FIELDNAME, length = length_255B)
	private String location_title;

	public static final String location_detail_FIELDNAME = "location_detail";
	@FieldDescribe("打卡地点详情")
	@Column(name = ColumnNamePrefix + location_detail_FIELDNAME, length = length_255B)
	private String location_detail;

	public static final String wifiname_FIELDNAME = "wifiname";
	@FieldDescribe("打卡wifi名称")
	@Column(name = ColumnNamePrefix + wifiname_FIELDNAME, length = length_255B)
	private String wifiname;

	public static final String notes_FIELDNAME = "notes";
	@FieldDescribe("打卡备注")
	@Column(name = ColumnNamePrefix + notes_FIELDNAME, length = length_255B)
	private String notes;

	public String getO2User() {
		return o2User;
	}

	public void setO2User(String o2User) {
		this.o2User = o2User;
	}

	public String getO2Unit() {
		return o2Unit;
	}

	public void setO2Unit(String o2Unit) {
		this.o2Unit = o2Unit;
	}

	public Date getCheckin_time_date() {
		return checkin_time_date;
	}

	public void setCheckin_time_date(Date checkin_time_date) {
		this.checkin_time_date = checkin_time_date;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getCheckin_type() {
		return checkin_type;
	}

	public void setCheckin_type(String checkin_type) {
		this.checkin_type = checkin_type;
	}

	public long getCheckin_time() {
		return checkin_time;
	}

	public void setCheckin_time(long checkin_time) {
		this.checkin_time = checkin_time;
	}

	public String getException_type() {
		return exception_type;
	}

	public void setException_type(String exception_type) {
		this.exception_type = exception_type;
	}

	public String getLocation_title() {
		return location_title;
	}

	public void setLocation_title(String location_title) {
		this.location_title = location_title;
	}

	public String getLocation_detail() {
		return location_detail;
	}

	public void setLocation_detail(String location_detail) {
		this.location_detail = location_detail;
	}

	public String getWifiname() {
		return wifiname;
	}

	public void setWifiname(String wifiname) {
		this.wifiname = wifiname;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
