package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.*;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import javax.persistence.OrderColumn;
import java.util.List;

/**
 * @author sword
 */
@Schema(name = "Handover", description = "权限交接.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.Handover.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Handover.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Handover extends SliceJpaObject {

	private static final long serialVersionUID = 5380401110786681261L;

	private static final String TABLE = PersistenceProperties.Content.Handover.table;

	public static final String TYPE_REPLACE = "替换";
	public static final String TYPE_EMPOWER = "授权";

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

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.applicationList = this.getProperties().getApplicationList();
			this.processList = this.getProperties().getProcessList();
			this.jobList = this.getProperties().getJobList();
		}
	}

	public Handover() {
		// nothing
	}

	public HandoverProperties getProperties() {
		if (null == this.properties) {
			this.properties = new HandoverProperties();
		}
		return this.properties;
	}

	public void setProperties(HandoverProperties properties) {
		this.properties = properties;
	}

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("标题.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String creator_FIELDNAME = "creator";
	@FieldDescribe("创建用户.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creator;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("用户.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String targetPerson_FIELDNAME = "targetPerson";
	@FieldDescribe("目标用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + targetPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetPerson;

	public static final String targetIdentity_FIELDNAME = "targetIdentity";
	@FieldDescribe("目标用户身份.")
	@Column(length = length_255B, name = ColumnNamePrefix + targetIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetIdentity;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("权限交接类型：替换|授权(默认).")
	@Column(length = length_64B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String scheme_FIELDNAME = "scheme";
	@FieldDescribe("权限交接方案：all|所有有权限的文档(默认)、application|指定应用下有权限的文档、process|指定流程下有权限的文档、job|指定有权限的文档.")
	@Column(length = length_64B, name = ColumnNamePrefix + scheme_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String scheme;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("状态：wait：待运行|processing：运行中|processed：运行完成|cancel：已取消.")
	@Column(length = length_64B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status;

	public static final String handoverJobList_FIELDNAME = "handoverJobList";
	@FieldDescribe("已交接的文档列表.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ handoverJobList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + handoverJobList_FIELDNAME
			+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + handoverJobList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + handoverJobList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> handoverJobList;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private HandoverProperties properties;

	@FieldDescribe("应用列表.")
	@Transient
	private List<String> applicationList;

	@FieldDescribe("流程列表.")
	@Transient
	private List<String> processList;

	@FieldDescribe("工作列表.")
	@Transient
	private List<String> jobList;

	public List<String> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<String> applicationList) {
		this.applicationList = applicationList;
		this.getProperties().setApplicationList(applicationList);
	}

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
		this.getProperties().setProcessList(processList);
	}

	public List<String> getJobList() {
		return jobList;
	}

	public void setJobList(List<String> jobList) {
		this.jobList = jobList;
		this.getProperties().setJobList(jobList);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getTargetIdentity() {
		return targetIdentity;
	}

	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTargetPerson() {
		return targetPerson;
	}

	public void setTargetPerson(String targetPerson) {
		this.targetPerson = targetPerson;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public List<String> getHandoverJobList() {
		return handoverJobList;
	}

	public void setHandoverJobList(List<String> handoverJobList) {
		this.handoverJobList = handoverJobList;
	}
}
