package com.x.meeting.core.entity;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Meeting", description = "会议管理会议.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Meeting.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Meeting.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Meeting extends SliceJpaObject {

	private static final long serialVersionUID = 8117315785915863335L;
	private static final String TABLE = PersistenceProperties.Meeting.table;

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
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(subject, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(subject));
	}

	/* 更新运行方法 */

	public static final String subject_FIELDNAME = "subject";
	@FieldDescribe("名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + subject_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + subject_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subject;

	public static final String pinyin_FIELDNAME = "pinyin";
	@FieldDescribe("name拼音.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + pinyin_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyin_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyin;

	public static final String pinyinInitial_FIELDNAME = "pinyinInitial";
	@FieldDescribe("name拼音首字母.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + pinyinInitial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyinInitial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyinInitial;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	/* 换到summary 字段 */
	@Deprecated
	private String description;

	public static final String summary_FIELDNAME = "summary";
	@FieldDescribe("说明")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + summary_FIELDNAME)
	private String summary;

	public static final String room_FIELDNAME = "room";
	@FieldDescribe("所属楼层.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + room_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + room_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Room.class) })
	private String room;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("开始时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String completedTime_FIELDNAME = "completedTime";
	@FieldDescribe("结束时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + completedTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + completedTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date completedTime;

	public static final String inviteMemberList_FIELDNAME = "inviteMemberList";
	@FieldDescribe("邀请人员,身份,组织，群组.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ inviteMemberList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + inviteMemberList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + inviteMemberList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + inviteMemberList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> inviteMemberList;

	public static final String invitePersonList_FIELDNAME = "invitePersonList";
	@FieldDescribe("邀请人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ invitePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + invitePersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + invitePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + invitePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> invitePersonList;

	public static final String inviteDelPersonList_FIELDNAME = "inviteDelPersonList";
	@FieldDescribe("邀请人员,身份,组织已删列表.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ inviteDelPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ inviteDelPersonList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + inviteDelPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + inviteDelPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> inviteDelPersonList;

	public static final String acceptPersonList_FIELDNAME = "acceptPersonList";
	@FieldDescribe("接受人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ acceptPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + acceptPersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + acceptPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + acceptPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> acceptPersonList;

	public static final String rejectPersonList_FIELDNAME = "rejectPersonList";
	@FieldDescribe("拒绝人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ rejectPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + rejectPersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + rejectPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + rejectPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> rejectPersonList;

	public static final String checkinPersonList_FIELDNAME = "checkinPersonList";
	@FieldDescribe("签到人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ checkinPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ checkinPersonList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + checkinPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + checkinPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> checkinPersonList;

	public static final String confirmStatus_FIELDNAME = "confirmStatus";
	@FieldDescribe("会议预定状态")
	@Enumerated(EnumType.STRING)
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + confirmStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	@FieldTypeDescribe(fieldType = "enum", fieldValue = "allow|deny|wait", fieldTypeName = "com.x.meeting.core.entity.ConfirmStatus")
	private ConfirmStatus confirmStatus;

	public static final String manualCompleted_FIELDNAME = "manualCompleted";
	@FieldDescribe("会议是否手工结束")
	@Column(name = ColumnNamePrefix + manualCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean manualCompleted;

	public static final String actualStartTime_FIELDNAME = "actualStartTime";
	@FieldDescribe("实际开始时间.")
	@Temporal(TemporalType.TIME)
	@Column(name = ColumnNamePrefix + actualStartTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + actualStartTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date actualStartTime;

	public static final String actualCompletedTime_FIELDNAME = "actualCompletedTime";
	@FieldDescribe("实际结束时间.")
	@Temporal(TemporalType.TIME)
	@Column(name = ColumnNamePrefix + actualCompletedTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + actualCompletedTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date actualCompletedTime;

	public static final String applicant_FIELDNAME = "applicant";
	@FieldDescribe("会议申请人")
	@Column(length = length_255B, name = ColumnNamePrefix + applicant_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + applicant_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String applicant;

	public static final String auditor_FIELDNAME = "auditor";
	@FieldDescribe("会议审核人")
	@Column(length = length_255B, name = ColumnNamePrefix + auditor_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + auditor_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String auditor;

	public static final String memo_FIELDNAME = "memo";
	@FieldDescribe("备注.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + memo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memo;

	public static final String hostUnit_FIELDNAME = "hostUnit";
	@FieldDescribe("承办部门")
	@Column(length = length_255B, name = ColumnNamePrefix + hostUnit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + hostUnit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String hostUnit;

	public static final String hostPerson_FIELDNAME = "hostPerson";
	@FieldDescribe("主持人")
	@Column(length = length_255B, name = ColumnNamePrefix + hostPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + hostPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String hostPerson;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("会议类型")
	@Column(length = length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String type;

	public static final String mode_FIELDNAME = "mode";
	@FieldDescribe("会议方式：online|线上会议;offline|线下会议")
	@Column(length = length_255B, name = ColumnNamePrefix + mode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mode;

	public static final String roomId_FIELDNAME = "roomId";
	@FieldDescribe("线上会议的会议号")
	@Column(length = length_255B, name = ColumnNamePrefix + roomId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roomId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String roomId;

	public static final String roomLink_FIELDNAME = "roomLink";
	@FieldDescribe("线上会议的链接")
	@Column(length = length_255B, name = ColumnNamePrefix + roomLink_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String roomLink;

	public static final String externalPerson_FIELDNAME = "externalPerson";
	@FieldDescribe("外部参会人员")
	@Column(length = 500, name = ColumnNamePrefix + externalPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String externalPerson;

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public Date getActualCompletedTime() {
		return actualCompletedTime;
	}

	public void setActualCompletedTime(Date actualCompletedTime) {
		this.actualCompletedTime = actualCompletedTime;
	}

	public List<String> getInvitePersonList() {
		return invitePersonList;
	}

	public void setInvitePersonList(List<String> invitePersonList) {
		this.invitePersonList = invitePersonList;
	}

	public List<String> getAcceptPersonList() {
		return acceptPersonList;
	}

	public void setAcceptPersonList(List<String> acceptPersonList) {
		this.acceptPersonList = acceptPersonList;
	}

	public List<String> getRejectPersonList() {
		return rejectPersonList;
	}

	public void setRejectPersonList(List<String> rejectPersonList) {
		this.rejectPersonList = rejectPersonList;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public ConfirmStatus getConfirmStatus() {
		return confirmStatus;
	}

	public void setConfirmStatus(ConfirmStatus confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public Boolean getManualCompleted() {
		return manualCompleted;
	}

	public void setManualCompleted(Boolean manualCompleted) {
		this.manualCompleted = manualCompleted;
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

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<String> getCheckinPersonList() {
		return checkinPersonList;
	}

	public void setCheckinPersonList(List<String> checkinPersonList) {
		this.checkinPersonList = checkinPersonList;
	}

	public String getHostUnit() {
		return hostUnit;
	}

	public void setHostUnit(String hostUnit) {
		this.hostUnit = hostUnit;
	}

	public String getHostPerson() {
		return hostPerson;
	}

	public void setHostPerson(String hostPerson) {
		this.hostPerson = hostPerson;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 添加一个签到人员
	 *
	 * @param distinguishedName
	 */
	public void addCheckinPerson(String distinguishedName) {
		this.checkinPersonList = ListTools.addStringToList(distinguishedName, this.checkinPersonList);
	}

	/**
	 * 删除一个签到人员
	 *
	 * @param distinguishedName
	 */
	public void removeCheckinPerson(String distinguishedName) {
		this.checkinPersonList = ListTools.removeStringFromList(distinguishedName, this.checkinPersonList);
	}

	public List<String> getInviteDelPersonList() {
		return inviteDelPersonList;
	}

	public void setInviteDelPersonList(List<String> inviteDelPersonList) {
		this.inviteDelPersonList = inviteDelPersonList;
	}

	public List<String> getInviteMemberList() {
		return inviteMemberList;
	}

	public void setInviteMemberList(List<String> inviteMemberList) {
		this.inviteMemberList = inviteMemberList;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomLink() {
		return roomLink;
	}

	public void setRoomLink(String roomLink) {
		this.roomLink = roomLink;
	}

	public String getExternalPerson() {
		return externalPerson;
	}

	public void setExternalPerson(String externalPerson) {
		this.externalPerson = externalPerson;
	}
}
