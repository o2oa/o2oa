package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Schema(name = "TaskProcessMode", description = "待办处理方式记录.")
@Entity
@ContainerEntity(dumpSize = 100, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.TaskProcessMode.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.TaskProcessMode.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }),
		@UniqueConstraint(name = PersistenceProperties.Content.TaskProcessMode.table + JpaObject.IndexNameMiddle
		+ TaskProcessMode.person_FIELDNAME, columnNames = { JpaObject.ColumnNamePrefix + TaskProcessMode.person_FIELDNAME,
				JpaObject.ColumnNamePrefix + TaskProcessMode.process_FIELDNAME,
				JpaObject.ColumnNamePrefix + TaskProcessMode.activity_FIELDNAME,
				JpaObject.ColumnNamePrefix + TaskProcessMode.routeId_FIELDNAME }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskProcessMode extends SliceJpaObject {

	private static final long serialVersionUID = 4328178480005277983L;

	private static final String TABLE = PersistenceProperties.Content.TaskProcessMode.table;
	public static final Integer MAX_ORG_RECORD = 50;
	public static final Integer MAX_ITEM = 10;

	public TaskProcessMode(){}

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
			this.taskProcessModeItemList = this.getProperties().getTaskProcessModeItemList();
		}
	}

	public TaskProcessModeProperties getProperties() {
		if (null == this.properties) {
			this.properties = new TaskProcessModeProperties();
		}
		return this.properties;
	}

	public void setProperties(TaskProcessModeProperties properties) {
		this.properties = properties;
	}

	public List<TaskProcessModeItem> getTaskProcessModeItemList() {
		return taskProcessModeItemList;
	}

	public void setTaskProcessModeItemList(List<TaskProcessModeItem> taskProcessModeItemList) {
		this.taskProcessModeItemList = taskProcessModeItemList;
		this.getProperties().setTaskProcessModeItemList(taskProcessModeItemList);
	}

	public static final String process_FIELDNAME = "process";
	@Schema(description = "流程标识.")
	@FieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String processName_FIELDNAME = "processName";
	@Schema(description = "流程名称.")
	@FieldDescribe("流程名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + processName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processName;

	public static final String person_FIELDNAME = "person";
	@Schema(description = "当前处理人.")
	@FieldDescribe("当前处理人")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String activity_FIELDNAME = "activity";
	@Schema(description = "活动标识.")
	@FieldDescribe("活动ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activity;

	public static final String activityName_FIELDNAME = "activityName";
	@Schema(description = "活动名称.")
	@FieldDescribe("活动名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName;

	public static final String activityAlias_FIELDNAME = "activityAlias";
	@Schema(description = "活动别名.")
	@FieldDescribe("活动别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityAlias;

	public static final String routeId_FIELDNAME = "routeId";
	@Schema(description = "路径ID.")
	@FieldDescribe("路径ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + routeId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String routeId;

	public static final String routeName_FIELDNAME = "routeName";
	@Schema(description = "活动名称.")
	@FieldDescribe("活动名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + routeName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String routeName;

	public static final String action_FIELDNAME = "action";
	@FieldDescribe("处理方式")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + action_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String action;

	public static final String hitCount_FIELDNAME = "hitCount";
	@FieldDescribe("次数.")
	@Column(name = ColumnNamePrefix + hitCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer hitCount = 0;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private TaskProcessModeProperties properties;

	public static final String routeGroup_FIELDNAME = "routeGroup";
	@FieldDescribe("决策组.")
	@Transient
	private String routeGroup;

	public static final String keepTask_FIELDNAME = "keepTask";
	@FieldDescribe("是否保留待办.")
	@Transient
	private Boolean keepTask;

	public static final String opinion_FIELDNAME = "opinion";
	@FieldDescribe("意见.")
	@Transient
	private String opinion;

	public static final String organizations_FIELDNAME = "organizations";
	@FieldDescribe("人员组织列表.")
	@Transient
	private Map<String, List<String>> organizations;

	public static final String taskProcessModeItemList_FIELDNAME = "taskProcessModeItemList";
	@Transient
	private List<TaskProcessModeItem> taskProcessModeItemList;

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
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

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getHitCount() {
		return hitCount;
	}

	public void setHitCount(Integer hitCount) {
		this.hitCount = hitCount;
	}

	public String getRouteGroup() {
		return routeGroup;
	}

	public void setRouteGroup(String routeGroup) {
		this.routeGroup = routeGroup;
	}

	public Boolean getKeepTask() {
		return keepTask;
	}

	public void setKeepTask(Boolean keepTask) {
		this.keepTask = keepTask;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public Map<String, List<String>> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Map<String, List<String>> organizations) {
		this.organizations = organizations;
	}

	public String getActivityAlias() {
		return activityAlias;
	}

	public void setActivityAlias(String activityAlias) {
		this.activityAlias = activityAlias;
	}
}
