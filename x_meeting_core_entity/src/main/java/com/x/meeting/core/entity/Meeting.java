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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Meeting.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Meeting extends SliceJpaObject {

	private static final long serialVersionUID = 8117315785915863335L;
	private static final String TABLE = PersistenceProperties.Meeting.table;

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
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(subject, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(subject));
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("名称")
	@Column(length = JpaObject.length_255B, name = "xsubject")
	@Index(name = TABLE + "_subject")
	@CheckPersist(allowEmpty = false)
	private String subject;

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

	@EntityFieldDescribe("说明")
	@Column(length = JpaObject.length_255B, name = "xdescription")
	@Index(name = TABLE + "_description")
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("所属楼层.")
	@Column(length = JpaObject.length_id, name = "xroom")
	@Index(name = TABLE + "_room")
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Room.class) })
	private String room;

	@EntityFieldDescribe("开始时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xstartTime")
	@Index(name = TABLE + "_startTime")
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	@EntityFieldDescribe("结束时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xcompletedTime")
	@Index(name = TABLE + "_completedTime")
	@CheckPersist(allowEmpty = false)
	private Date completedTime;

	@EntityFieldDescribe("邀请人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_invitePersonList", joinIndex = @Index(name = TABLE + "_invitePersonList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xinvitePersonList")
	@ElementIndex(name = TABLE + "_invitePersonList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> invitePersonList;

	@EntityFieldDescribe("接受人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_acceptPersonList", joinIndex = @Index(name = TABLE + "_acceptPersonList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xacceptPersonList")
	@ElementIndex(name = TABLE + "_acceptPersonList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> acceptPersonList;

	@EntityFieldDescribe("拒绝人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_rejectPersonList", joinIndex = @Index(name = TABLE + "_rejectPersonList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xrejectPersonList")
	@ElementIndex(name = TABLE + "_rejectPersonList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> rejectPersonList;

	@EntityFieldDescribe("会议预定状态")
	@Enumerated(EnumType.STRING)
	@Column(length = ConfirmStatus.length, name = "xconfirmStatus")
	@Index(name = TABLE + "_confirmStatus")
	@CheckPersist(allowEmpty = false)
	private ConfirmStatus confirmStatus;

	@EntityFieldDescribe("会议是否手工结束")
	@Column(name = "xmanualCompleted")
	@Index(name = TABLE + "_manualCompleted")
	@CheckPersist(allowEmpty = true)
	private Boolean manualCompleted;

	@EntityFieldDescribe("实际开始时间.")
	@Temporal(TemporalType.TIME)
	@Column(name = "xactualStartTime")
	@Index(name = TABLE + "_actualStartTime")
	@CheckPersist(allowEmpty = true)
	private Date actualStartTime;

	@EntityFieldDescribe("实际结束时间.")
	@Temporal(TemporalType.TIME)
	@Column(name = "xactualCompletedTime")
	@Index(name = TABLE + "_actualCompletedTime")
	@CheckPersist(allowEmpty = true)
	private Date actualCompletedTime;

	@EntityFieldDescribe("会议申请人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xapplicant")
	@Index(name = TABLE + "_applicant")
	@CheckPersist(allowEmpty = false)
	private String applicant;

	@EntityFieldDescribe("会议审核人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xauditor")
	@Index(name = TABLE + "_auditor")
	@CheckPersist(allowEmpty = true)
	private String auditor;

	@EntityFieldDescribe("备注.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xmemo")
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

}