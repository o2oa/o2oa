package com.x.processplatform.core.entity.element;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Route.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Route.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Route extends SliceJpaObject {

	private static final long serialVersionUID = -1151288890276589956L;
	private static final String TABLE = PersistenceProperties.Element.Route.table;

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
		/* 如果脚本为空，添加默认返回true 的脚本 */
		if (StringUtils.isEmpty(script) && StringUtils.isEmpty(scriptText)) {
			this.scriptText = "return true;";
		}
	}

	/* 更新运行方法 */

	// public static String[] FLA GS = new String[] { "id", "alias" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	/* 路由名称可能包含 >= 这样的符号，所以不校验简单字串 */
	@CheckPersist(allowEmpty = true)
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("代理节点别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String process_FIELDNAME = "process";
	@IdReference(Process.class)
	@FieldDescribe("流程标识符.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String activityType_FIELDNAME = "activityType";
	@FieldDescribe("目标类别.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + activityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ActivityType activityType;

	public static final String activity_FIELDNAME = "activity";
	@IdReference({ Agent.class, Begin.class, Cancel.class, Choice.class, Choice.class, Delay.class, Embed.class,
			End.class, Invoke.class, Manual.class, Merge.class, Message.class, Parallel.class, Service.class,
			Split.class })
	@FieldDescribe("目标活动节点标识符.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activity;

	public static final String track_FIELDNAME = "track";
	@FieldDescribe("路由的曲线坐标.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + track_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String track;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后.")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public static final String position_FIELDNAME = "position";
	@FieldDescribe("文本位置.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + position_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String position;

	public static final String script_FIELDNAME = "script";
	@IdReference(Script.class)
	@FieldDescribe("路由脚本")
	@Column(length = length_255B, name = ColumnNamePrefix + script_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String script;

	public static final String scriptText_FIELDNAME = "scriptText";
	@FieldDescribe("路由脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + scriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String scriptText;

	public static final String passSameTarget_FIELDNAME = "passSameTarget";
	@FieldDescribe("如何与前一个环节处理人相同那么自动执行.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + passSameTarget_FIELDNAME)
	private Boolean passSameTarget;

	public static final String passExpired_FIELDNAME = "passExpired";
	@FieldDescribe("超时时候的默认路由.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + passExpired_FIELDNAME)
	private Boolean passExpired;

	public static final String opinion_FIELDNAME = "opinion";
	@FieldDescribe("默认意见.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + opinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String opinion;

	public static final String decisionOpinion_FIELDNAME = "decisionOpinion";
	@FieldDescribe("决策性意见,使用#分割.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + decisionOpinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String decisionOpinion;

	public static final String sole_FIELDNAME = "sole";
	@FieldDescribe("唯一路由,当多人处理时,如果有人选择此路由将通过此路由,一票否决.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + sole_FIELDNAME)
	private Boolean sole;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getScriptText() {
		return scriptText;
	}

	public void setScriptText(String scriptText) {
		this.scriptText = scriptText;
	}

	public Boolean getPassSameTarget() {
		return passSameTarget;
	}

	public void setPassSameTarget(Boolean passSameTarget) {
		this.passSameTarget = passSameTarget;
	}

	public Boolean getPassExpired() {
		return passExpired;
	}

	public void setPassExpired(Boolean passExpired) {
		this.passExpired = passExpired;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getDecisionOpinion() {
		return decisionOpinion;
	}

	public void setDecisionOpinion(String decisionOpinion) {
		this.decisionOpinion = decisionOpinion;
	}

	public Boolean getSole() {
		return sole;
	}

	public void setSole(Boolean sole) {
		this.sole = sole;
	}

}
