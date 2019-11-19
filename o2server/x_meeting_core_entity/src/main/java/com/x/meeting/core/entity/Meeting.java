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
import com.x.base.core.project.tools.ListTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Meeting.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Meeting.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Meeting extends SliceJpaObject {

	private static final long serialVersionUID = 8117315785915863335L;
	private static final String TABLE = PersistenceProperties.Meeting.table;

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

	/* 以上为 JpaObject 默认字段 */

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
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Room.class) })
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

	public static final String invitePersonList_FIELDNAME = "invitePersonList";
	@FieldDescribe("邀请人员,身份,组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ invitePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + invitePersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + invitePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + invitePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> invitePersonList;

	// public static final String expandInvitePersonList_FIELDNAME =
	// "expandInvitePersonList";
	// @FieldDescribe("实际邀请人员,含组织展开.")
	// @PersistentCollection(fetch = FetchType.EAGER)
	// @ContainerTable(name = TABLE + ContainerTableNameMiddle
	// + expandInvitePersonList_FIELDNAME, joinIndex = @Index(name = TABLE +
	// IndexNameMiddle
	// + expandInvitePersonList_FIELDNAME + JoinIndexNameSuffix))
	// @OrderColumn(name = ORDERCOLUMNCOLUMN)
	// @ElementColumn(length = length_255B, name = ColumnNamePrefix +
	// expandInvitePersonList_FIELDNAME)
	// @ElementIndex(name = TABLE + IndexNameMiddle +
	// expandInvitePersonList_FIELDNAME + ElementIndexNameSuffix)
	// @CheckPersist(allowEmpty = true)
	// private List<String> expandInvitePersonList;

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
	@Index(name = TABLE + IndexNameMiddle + confirmStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ConfirmStatus confirmStatus;

	public static final String manualCompleted_FIELDNAME = "manualCompleted";
	@FieldDescribe("会议是否手工结束")
	@Column(name = ColumnNamePrefix + manualCompleted_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + manualCompleted_FIELDNAME)
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

}