package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.Date;

/**
 * 签批涂鸦列表
 * @author sword
 */
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.DocSignScrawl.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.DocSignScrawl.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DocSignScrawl extends SliceJpaObject {

	private static final long serialVersionUID = -8648872600208475747L;

	private static final String TABLE = PersistenceProperties.Content.DocSignScrawl.table;

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

	@Override
	public void onPersist() throws Exception {
	}

	public DocSignScrawl() {
		// nothing
	}

	public DocSignScrawl(DocSign docSign, String data) {
		this.signId = docSign.getId();
		this.job = docSign.getJob();
		this.activity = docSign.getActivity();
		this.activityName = docSign.getActivityName();
		this.person = docSign.getPerson();
		this.commitTime = new Date();
		this.data = data;
	}

	public static final String signId_FIELDNAME = "signId";
	@FieldDescribe("签批ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + signId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + signId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String signId;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("任务.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String activity_FIELDNAME = "activity";
	@FieldDescribe("活动ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activity;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("活动名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("签批人")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String commitTime_FIELDNAME = "commitTime";
	@Temporal(TemporalType.TIME)
	@FieldDescribe("提交时间.")
	@Column(name = ColumnNamePrefix + commitTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + commitTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date commitTime;

	public static final String hasScrawlPic_FIELDNAME = "hasScrawlPic";
	@FieldDescribe("是否有涂鸦图片")
	@Column(name = ColumnNamePrefix + hasScrawlPic_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean hasScrawlPic = false;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("涂鸦信息.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
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

	public Date getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(Date commitTime) {
		this.commitTime = commitTime;
	}

	public String getSignId() {
		return signId;
	}

	public void setSignId(String signId) {
		this.signId = signId;
	}

	public Boolean getHasScrawlPic() {
		return hasScrawlPic;
	}

	public void setHasScrawlPic(Boolean hasScrawlPic) {
		this.hasScrawlPic = hasScrawlPic;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
