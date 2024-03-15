package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Strategy;

import javax.persistence.*;
import java.util.List;

/**
 * 考勤组
 */
@Schema(name = "AttendanceV2Group", description = "考勤组.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2Group.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AttendanceV2Group.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Group extends SliceJpaObject {


	private static final String TABLE = PersistenceProperties.AttendanceV2Group.table;
	private static final long serialVersionUID = -1148943360608366700L;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */
	public static final String groupName_FIELDNAME = "groupName";
	@FieldDescribe("考勤组名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ groupName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String groupName ;

	// 考勤类型 固定班制 自由打卡 排班制  目前只有一种固定班制
	public static final String CHECKTYPE_Fixed = "1"; // 固定班制
	public static final String CHECKTYPE_Free = "2"; // 自由打卡
	public static final String CHECKTYPE_Arrangement = "3"; // 排班制
	public static final String checkType_FIELDNAME = "checkType";
	@FieldDescribe("打卡类型 ")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ checkType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String checkType;


	public static final String shiftId_FIELDNAME = "shiftId";
	@FieldDescribe("节假日调班的工作班次id.")
	@Column(length = length_64B, name = ColumnNamePrefix + shiftId_FIELDNAME)
	private String shiftId;


	public static final String operator_FIELDNAME = "operator";
	@FieldDescribe("最后操作人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ operator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String operator;

	public static final int status_auto = 2; //数据状态 自动存储
	public static final int status_manual = 1; //数据状态 手动存储
	public static final String status_FIELDNAME = "status";
	@FieldDescribe("考勤组数据状态.")
  @Column(  name = ColumnNamePrefix + status_FIELDNAME)
	private Integer status;


	public static final String assistAdminList_FIELDNAME = "assistAdminList";
	@FieldDescribe("协助管理员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ assistAdminList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + assistAdminList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + assistAdminList_FIELDNAME)
	@ElementIndex(name = TABLE + assistAdminList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> assistAdminList;

	public static final String participateList_FIELDNAME = "participateList";
	@FieldDescribe("考勤打卡人员、组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ participateList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + participateList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + participateList_FIELDNAME)
	@ElementIndex(name = TABLE + participateList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> participateList; // 考勤打卡人员、组织

	public static final String unParticipateList_FIELDNAME = "unParticipateList";
	@FieldDescribe("无需考勤打卡的人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ unParticipateList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + unParticipateList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + unParticipateList_FIELDNAME)
	@ElementIndex(name = TABLE + unParticipateList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> unParticipateList; // 无需考勤打卡的人员 只有人员

	public static final String trueParticipantList_FIELDNAME = "trueParticipantList";
	@FieldDescribe("真实的考勤打卡的人员列表，participateList和unParticipateList组合计算的结果.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ trueParticipantList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + trueParticipantList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + trueParticipantList_FIELDNAME)
	@ElementIndex(name = TABLE + trueParticipantList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> trueParticipantList; // 必须考勤打卡的人员 只有人员

	public static final String workDateList_FIELDNAME = "workDateList";
	@FieldDescribe("考勤工作日设置.打卡类型为 自由打卡 的时候需要配置，如 1,2,3,4,5 。 0-6代表周日到周六.")
	@Column(length = length_64B, name = ColumnNamePrefix + workDateList_FIELDNAME)
	private String workDateList; // 打卡的工作日设置  如 1,2,3,4,5 。 0-6代表周日到周六

	public static final String workDateProperties_FIELDNAME = "workDateProperties";
	@FieldDescribe("考勤工作日设置. 打卡类型为 固定班制 的时候需要配置，可以为每个打卡日配置班次")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + workDateProperties_FIELDNAME)
	private AttendanceV2GroupWorkDayProperties workDateProperties;

	public static final String allowFieldWork_FIELDNAME = "allowFieldWork";
	@FieldDescribe("是否允许外勤打卡.")
	@Column(name = ColumnNamePrefix + allowFieldWork_FIELDNAME)
	private Boolean allowFieldWork = true;

	public static final String requiredFieldWorkRemarks_FIELDNAME = "requiredFieldWorkRemarks";
	@FieldDescribe("外勤打卡备注是否必填.")
	@Column(name = ColumnNamePrefix + requiredFieldWorkRemarks_FIELDNAME)
	private Boolean requiredFieldWorkRemarks = true;

	public static final String fieldWorkMarkError_FIELDNAME = "fieldWorkMarkError";
	@FieldDescribe("外勤打卡是否作为异常数据处理.")
	@Column(name = ColumnNamePrefix + fieldWorkMarkError_FIELDNAME)
	private Boolean fieldWorkMarkError = true;

	public static final String workPlaceIdList_FIELDNAME = "workPlaceIdList";
	@FieldDescribe("工作场所列表.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ workPlaceIdList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + workPlaceIdList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + workPlaceIdList_FIELDNAME)
	@ElementIndex(name = TABLE + workPlaceIdList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> workPlaceIdList;


	public static final String requiredCheckInDateList_FIELDNAME = "requiredCheckInDateList";
	@FieldDescribe("必须打卡的日期，包含日期 ｜ 班次id ｜ 是否循环，如 2023-01-01|shiftId|week.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ requiredCheckInDateList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + requiredCheckInDateList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + requiredCheckInDateList_FIELDNAME)
	@ElementIndex(name = TABLE + requiredCheckInDateList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> requiredCheckInDateList;


	public static final String noNeedCheckInDateList_FIELDNAME = "noNeedCheckInDateList";
	@FieldDescribe("无需打卡的日期，包含日期 ｜ 是否循环 如 2023-01-01|week .")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ noNeedCheckInDateList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + noNeedCheckInDateList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + noNeedCheckInDateList_FIELDNAME)
	@ElementIndex(name = TABLE + noNeedCheckInDateList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> noNeedCheckInDateList;


	public AttendanceV2GroupWorkDayProperties getWorkDateProperties() {
		return workDateProperties;
	}

	public void setWorkDateProperties(AttendanceV2GroupWorkDayProperties workDateProperties) {
		this.workDateProperties = workDateProperties;
	}


	public String getShiftId() {
		return shiftId;
	}

	public void setShiftId(String shiftId) {
		this.shiftId = shiftId;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public List<String> getParticipateList() {
		return participateList;
	}

	public void setParticipateList(List<String> participateList) {
		this.participateList = participateList;
	}

	public List<String> getUnParticipateList() {
		return unParticipateList;
	}

	public void setUnParticipateList(List<String> unParticipateList) {
		this.unParticipateList = unParticipateList;
	}

	public String getWorkDateList() {
		return workDateList;
	}

	public void setWorkDateList(String workDateList) {
		this.workDateList = workDateList;
	}

	public Boolean getAllowFieldWork() {
		return allowFieldWork;
	}

	public void setAllowFieldWork(Boolean allowFieldWork) {
		this.allowFieldWork = allowFieldWork;
	}

	public Boolean getRequiredFieldWorkRemarks() {
		return requiredFieldWorkRemarks;
	}

	public void setRequiredFieldWorkRemarks(Boolean requiredFieldWorkRemarks) {
		this.requiredFieldWorkRemarks = requiredFieldWorkRemarks;
	}

	public List<String> getWorkPlaceIdList() {
		return workPlaceIdList;
	}

	public void setWorkPlaceIdList(List<String> workPlaceIdList) {
		this.workPlaceIdList = workPlaceIdList;
	}

	public List<String> getRequiredCheckInDateList() {
		return requiredCheckInDateList;
	}

	public void setRequiredCheckInDateList(List<String> requiredCheckInDateList) {
		this.requiredCheckInDateList = requiredCheckInDateList;
	}

	public List<String> getNoNeedCheckInDateList() {
		return noNeedCheckInDateList;
	}

	public void setNoNeedCheckInDateList(List<String> noNeedCheckInDateList) {
		this.noNeedCheckInDateList = noNeedCheckInDateList;
	}

	public List<String> getTrueParticipantList() {
		return trueParticipantList;
	}

	public void setTrueParticipantList(List<String> trueParticipantList) {
		this.trueParticipantList = trueParticipantList;
	}

	public Boolean getFieldWorkMarkError() {
		return fieldWorkMarkError;
	}

	public void setFieldWorkMarkError(Boolean fieldWorkMarkError) {
		this.fieldWorkMarkError = fieldWorkMarkError;
	}

	public List<String> getAssistAdminList() {
		return assistAdminList;
	}

	public void setAssistAdminList(List<String> assistAdminList) {
		this.assistAdminList = assistAdminList;
	}

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

	
	
}