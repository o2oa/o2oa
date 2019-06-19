package com.x.processplatform.core.entity.element;

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
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Manual.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Manual.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Manual extends Activity {

	private static final long serialVersionUID = -6011906779905098667L;
	private static final String TABLE = PersistenceProperties.Element.Manual.table;

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
	}

	/* 更新运行方法 */

	// public static String[] FLA GS = new String[] { "id", "alias" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@FieldDescribe("分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + group_FIELDNAME)
	private String group;

	@FieldDescribe("意见分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + opinionGroup_FIELDNAME)
	private String opinionGroup;

	@FieldDescribe("节点名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String name;

	@Flag
	@FieldDescribe("节点别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String alias;

	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	@IdReference(Process.class)
	@FieldDescribe("流程标识,不为空.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	@FieldDescribe("节点位置.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + position_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String position;

	@FieldDescribe("前端自定内容.")
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + extension_FIELDNAME)
	@Basic(fetch = FetchType.EAGER)
	@Lob
	@CheckPersist(allowEmpty = true)
	private String extension;

	@IdReference(Form.class)
	@FieldDescribe("节点使用的表单.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + form_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + form_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String form;

	@FieldDescribe("待阅人名称,存储 Identity,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readIdentityList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readIdentityList;

	@FieldDescribe("待阅组织名称,存储unit,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + readUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + readUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readUnitList;

	@FieldDescribe("待阅群组名称,存储group,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + readGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + readGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readGroupList;

	@IdReference(Script.class)
	@FieldDescribe("待阅人脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + readScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readScript;

	@FieldDescribe("待阅人脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + readScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readScriptText;

	@FieldDescribe("待阅角色定义内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + readDuty_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readDuty;

	@FieldDescribe("活动待阅人员data数据路径.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readDataPathList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readDataPathList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = JpaObject.length_255B, name = ColumnNamePrefix + readDataPathList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readDataPathList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readDataPathList;

	@FieldDescribe("参与人名称,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reviewIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ reviewIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + reviewIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reviewIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reviewIdentityList;

	@FieldDescribe("参与人组织名称,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + reviewUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + reviewUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + reviewUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reviewUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reviewUnitList;

	@FieldDescribe("参与人群组名称,存储group,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + reviewGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + reviewGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + reviewGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reviewGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reviewGroupList;

	@IdReference(Script.class)
	@FieldDescribe("参与人脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + reviewScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reviewScript;

	@FieldDescribe("参与人脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + reviewScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reviewScriptText;

	@FieldDescribe("参阅角色定义内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + reviewDuty_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reviewDuty;

	@FieldDescribe("参阅人员data数据路径.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reviewDataPathList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ reviewDataPathList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = JpaObject.length_255B, name = ColumnNamePrefix + reviewDataPathList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reviewDataPathList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reviewDataPathList;

	@IdReference(Script.class)
	@FieldDescribe("活动到达前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScript;

	@FieldDescribe("活动到达前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScriptText;

	@IdReference(Script.class)
	@FieldDescribe("活动到达后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScript;

	@FieldDescribe("活动到达后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScriptText;

	@IdReference(Script.class)
	@FieldDescribe("活动执行前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScript;

	@FieldDescribe("活动执行前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScriptText;

	@IdReference(Script.class)
	@FieldDescribe("活动执行后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScript;

	@FieldDescribe("活动执行后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScriptText;

	@IdReference(Script.class)
	@FieldDescribe("路由查询前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScript;

	@FieldDescribe("路由查询前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScriptText;

	@IdReference(Script.class)
	@FieldDescribe("路由查询后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScript;

	@FieldDescribe("路由查询后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScriptText;

	@FieldDescribe("允许调度")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowReroute_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + allowReroute_FIELDNAME)
	private Boolean allowReroute;

	@FieldDescribe("允许调度到此节点")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowRerouteTo_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + allowRerouteTo_FIELDNAME)
	private Boolean allowRerouteTo;

	public static final String routeList_FIELDNAME = "routeList";
	@IdReference(Route.class)
	@FieldDescribe("出口路由,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + routeList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + routeList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + routeList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + routeList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> routeList;

	public static final String taskIdentityList_FIELDNAME = "taskIdentityList";
	@FieldDescribe("人工节点的待办人名称,存储 Identity name,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ taskIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + taskIdentityList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + taskIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + taskIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> taskIdentityList;

	public static final String taskUnitList_FIELDNAME = "taskUnitList";
	@FieldDescribe("人工节点的待办部门名称,存储 Unit name,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + taskUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + taskUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + taskUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + taskUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> taskUnitList;

	public static final String taskGroupList_FIELDNAME = "taskGroupList";
	@FieldDescribe("待办人群组名称,存储group,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + taskGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + taskGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + taskGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + taskGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> taskGroupList;

	public static final String taskScript_FIELDNAME = "taskScript";
	@IdReference(Script.class)
	@FieldDescribe("待办人脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + taskScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskScript;

	public static final String taskScriptText_FIELDNAME = "taskScriptText";
	@FieldDescribe("待办人脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + taskScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskScriptText;

	public static final String taskExpireType_FIELDNAME = "taskExpireType";
	@FieldDescribe("过期方式.")
	@Enumerated(EnumType.STRING)
	@Column(length = ExpireType.length, name = ColumnNamePrefix + taskExpireType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskExpireType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ExpireType taskExpireType = ExpireType.never;

	public static final String taskExpireDay_FIELDNAME = "taskExpireDay";
	@FieldDescribe("过期日期.")
	@Column(name = ColumnNamePrefix + taskExpireDay_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer taskExpireDay;

	public static final String taskExpireHour_FIELDNAME = "taskExpireHour";
	@FieldDescribe("过期小时.")
	@Column(name = ColumnNamePrefix + taskExpireHour_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer taskExpireHour;

	public static final String taskExpireWorkTime_FIELDNAME = "taskExpireWorkTime";
	@FieldDescribe("过期是否是工作时间.")
	@Column(name = ColumnNamePrefix + taskExpireWorkTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean taskExpireWorkTime;

	public static final String taskExpireScript_FIELDNAME = "taskExpireScript";
	@IdReference(Script.class)
	@FieldDescribe("过期时间设定脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + taskExpireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskExpireScript;

	public static final String taskExpireScriptText_FIELDNAME = "taskExpireScriptText";
	@FieldDescribe("过期时间设定脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + taskExpireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskExpireScriptText;

	public static final String taskDuty_FIELDNAME = "taskDuty";
	@FieldDescribe("待办角色定义内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + taskDuty_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskDuty;

	public static final String taskDataPathList_FIELDNAME = "taskDataPathList";
	@FieldDescribe("活动待办人员data数据路径.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + IndexNameMiddle + taskDataPathList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + taskDataPathList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = JpaObject.length_255B, name = ColumnNamePrefix + taskDataPathList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + taskDataPathList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> taskDataPathList;

	public static final String manualMode_FIELDNAME = "manualMode";
	@FieldDescribe("人工节点的处理方式.")
	@Enumerated(EnumType.STRING)
	@Column(length = ManualMode.length, name = ColumnNamePrefix + manualMode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + manualMode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ManualMode manualMode;

	public static final String allowReset_FIELDNAME = "allowReset";
	@FieldDescribe("允许重置待办")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowReset_FIELDNAME)
	private Boolean allowReset;

	public static final String allowRetract_FIELDNAME = "allowRetract";
	@FieldDescribe("允许召回")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowRetract_FIELDNAME)
	private Boolean allowRetract;

	public static final String allowRollback_FIELDNAME = "allowRollback";
	@FieldDescribe("允许回滚")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowRollback_FIELDNAME)
	private Boolean allowRollback;

	public static final String allowDeleteWork_FIELDNAME = "allowDeleteWork";
	@FieldDescribe("允许删除Work")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowDeleteWork_FIELDNAME)
	private Boolean allowDeleteWork;

	public static final String allowRapid_FIELDNAME = "allowRapid";
	@FieldDescribe("允许快速处理。")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowRapid_FIELDNAME)
	private Boolean allowRapid;

	public static final String allowAddSplit_FIELDNAME = "allowAddSplit";
	@FieldDescribe("允许在会签拆分状态下增加会签分支.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowAddSplit_FIELDNAME)
	private Boolean allowAddSplit;

	public static final String allowPress_FIELDNAME = "allowPress";
	@FieldDescribe("允许提醒.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowPress_FIELDNAME)
	private Boolean allowPress;

	public static final String resetRange_FIELDNAME = "resetRange";
	@FieldDescribe("重置范围.")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + resetRange_FIELDNAME)
	private String resetRange;

	public static final String resetCount_FIELDNAME = "resetCount";
	@FieldDescribe("允许最大重置次数")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + resetCount_FIELDNAME)
	private Integer resetCount;

	public static final String manualBeforeTaskScript_FIELDNAME = "manualBeforeTaskScript";
	@IdReference(Script.class)
	@FieldDescribe("待办执行前脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ manualBeforeTaskScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String manualBeforeTaskScript;

	public static final String manualBeforeTaskScriptText_FIELDNAME = "manualBeforeTaskScriptText";
	@FieldDescribe("待办执行前脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + manualBeforeTaskScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String manualBeforeTaskScriptText;

	public static final String manualAfterTaskScript_FIELDNAME = "manualAfterTaskScript";
	@IdReference(Script.class)
	@FieldDescribe("待办执行后脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ manualAfterTaskScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String manualAfterTaskScript;

	public static final String manualAfterTaskScriptText_FIELDNAME = "manualAfterTaskScriptText";
	@FieldDescribe("待办执行后脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + manualAfterTaskScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String manualAfterTaskScriptText;

	public static final String manualStayScript_FIELDNAME = "manualStayScript";
	@IdReference(Script.class)
	@FieldDescribe("人工活动有停留脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ manualStayScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String manualStayScript;

	public static final String manualStayScriptText_FIELDNAME = "manualStayScriptText";
	@FieldDescribe("人工活动有停留脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + manualStayScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String manualStayScriptText;

//	public static final String manualPressScript_FIELDNAME = "manualPressScript";
//	@IdReference(Script.class)
//	@FieldDescribe("提醒内容脚本.")
//	@Column(length = length_255B, name = ColumnNamePrefix + manualPressScript_FIELDNAME)
//	@CheckPersist(allowEmpty = true)
//	private String manualPressScript;
//
//	public static final String manualPressScriptText_FIELDNAME = "manualPressScriptText";
//	@FieldDescribe("提醒内容脚本文本.")
//	@Lob
//	@Basic(fetch = FetchType.EAGER)
//	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + manualPressScriptText_FIELDNAME)
//	@CheckPersist(allowEmpty = true)
//	private String manualPressScriptText;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public List<String> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<String> routeList) {
		this.routeList = routeList;
	}

	public List<String> getTaskIdentityList() {
		return taskIdentityList;
	}

	public void setTaskIdentityList(List<String> taskIdentityList) {
		this.taskIdentityList = taskIdentityList;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getBeforeArriveScript() {
		return beforeArriveScript;
	}

	public void setBeforeArriveScript(String beforeArriveScript) {
		this.beforeArriveScript = beforeArriveScript;
	}

	public String getAfterArriveScript() {
		return afterArriveScript;
	}

	public void setAfterArriveScript(String afterArriveScript) {
		this.afterArriveScript = afterArriveScript;
	}

	public String getBeforeExecuteScript() {
		return beforeExecuteScript;
	}

	public void setBeforeExecuteScript(String beforeExecuteScript) {
		this.beforeExecuteScript = beforeExecuteScript;
	}

	public String getAfterExecuteScript() {
		return afterExecuteScript;
	}

	public void setAfterExecuteScript(String afterExecuteScript) {
		this.afterExecuteScript = afterExecuteScript;
	}

	public List<String> getTaskUnitList() {
		return taskUnitList;
	}

	public void setTaskUnitList(List<String> taskUnitList) {
		this.taskUnitList = taskUnitList;
	}

	public String getTaskScript() {
		return taskScript;
	}

	public void setTaskScript(String taskScript) {
		this.taskScript = taskScript;
	}

	public List<String> getReadIdentityList() {
		return readIdentityList;
	}

	public void setReadIdentityList(List<String> readIdentityList) {
		this.readIdentityList = readIdentityList;
	}

	public List<String> getReadUnitList() {
		return readUnitList;
	}

	public void setReadUnitList(List<String> readUnitList) {
		this.readUnitList = readUnitList;
	}

	public String getReadScript() {
		return readScript;
	}

	public void setReadScript(String readScript) {
		this.readScript = readScript;
	}

	public List<String> getReviewIdentityList() {
		return reviewIdentityList;
	}

	public void setReviewIdentityList(List<String> reviewIdentityList) {
		this.reviewIdentityList = reviewIdentityList;
	}

	public List<String> getReviewUnitList() {
		return reviewUnitList;
	}

	public void setReviewUnitList(List<String> reviewUnitList) {
		this.reviewUnitList = reviewUnitList;
	}

	public String getReviewScript() {
		return reviewScript;
	}

	public void setReviewScript(String reviewScript) {
		this.reviewScript = reviewScript;
	}

	public ManualMode getManualMode() {
		return manualMode;
	}

	public void setManualMode(ManualMode manualMode) {
		this.manualMode = manualMode;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public Integer getResetCount() {
		return resetCount;
	}

	public void setResetCount(Integer resetCount) {
		this.resetCount = resetCount;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getReviewScriptText() {
		return reviewScriptText;
	}

	public void setReviewScriptText(String reviewScriptText) {
		this.reviewScriptText = reviewScriptText;
	}

	public String getBeforeArriveScriptText() {
		return beforeArriveScriptText;
	}

	public void setBeforeArriveScriptText(String beforeArriveScriptText) {
		this.beforeArriveScriptText = beforeArriveScriptText;
	}

	public String getAfterArriveScriptText() {
		return afterArriveScriptText;
	}

	public void setAfterArriveScriptText(String afterArriveScriptText) {
		this.afterArriveScriptText = afterArriveScriptText;
	}

	public String getBeforeExecuteScriptText() {
		return beforeExecuteScriptText;
	}

	public void setBeforeExecuteScriptText(String beforeExecuteScriptText) {
		this.beforeExecuteScriptText = beforeExecuteScriptText;
	}

	public String getAfterExecuteScriptText() {
		return afterExecuteScriptText;
	}

	public void setAfterExecuteScriptText(String afterExecuteScriptText) {
		this.afterExecuteScriptText = afterExecuteScriptText;
	}

	public String getTaskScriptText() {
		return taskScriptText;
	}

	public void setTaskScriptText(String taskScriptText) {
		this.taskScriptText = taskScriptText;
	}

	public String getReadScriptText() {
		return readScriptText;
	}

	public void setReadScriptText(String readScriptText) {
		this.readScriptText = readScriptText;
	}

	public String getBeforeInquireScript() {
		return beforeInquireScript;
	}

	public void setBeforeInquireScript(String beforeInquireScript) {
		this.beforeInquireScript = beforeInquireScript;
	}

	public String getBeforeInquireScriptText() {
		return beforeInquireScriptText;
	}

	public void setBeforeInquireScriptText(String beforeInquireScriptText) {
		this.beforeInquireScriptText = beforeInquireScriptText;
	}

	public String getAfterInquireScript() {
		return afterInquireScript;
	}

	public void setAfterInquireScript(String afterInquireScript) {
		this.afterInquireScript = afterInquireScript;
	}

	public String getAfterInquireScriptText() {
		return afterInquireScriptText;
	}

	public void setAfterInquireScriptText(String afterInquireScriptText) {
		this.afterInquireScriptText = afterInquireScriptText;
	}

	// public String getBeforeArrivedExecuteScript() {
	// return beforeArrivedExecuteScript;
	// }
	//
	// public void setBeforeArrivedExecuteScript(String
	// beforeArrivedExecuteScript) {
	// this.beforeArrivedExecuteScript = beforeArrivedExecuteScript;
	// }
	//
	// public String getBeforeArrivedExecuteScriptText() {
	// return beforeArrivedExecuteScriptText;
	// }
	//
	// public void setBeforeArrivedExecuteScriptText(String
	// beforeArrivedExecuteScriptText) {
	// this.beforeArrivedExecuteScriptText = beforeArrivedExecuteScriptText;
	// }
	//
	// public String getAfterArrivedExecuteScript() {
	// return afterArrivedExecuteScript;
	// }
	//
	// public void setAfterArrivedExecuteScript(String
	// afterArrivedExecuteScript) {
	// this.afterArrivedExecuteScript = afterArrivedExecuteScript;
	// }
	//
	// public String getAfterArrivedExecuteScriptText() {
	// return afterArrivedExecuteScriptText;
	// }
	//
	// public void setAfterArrivedExecuteScriptText(String
	// afterArrivedExecuteScriptText) {
	// this.afterArrivedExecuteScriptText = afterArrivedExecuteScriptText;
	// }

	public Boolean getAllowReset() {
		return allowReset;
	}

	public void setAllowReset(Boolean allowReset) {
		this.allowReset = allowReset;
	}

	public Boolean getAllowReroute() {
		return allowReroute;
	}

	public void setAllowReroute(Boolean allowReroute) {
		this.allowReroute = allowReroute;
	}

	public Boolean getAllowRetract() {
		return allowRetract;
	}

	public void setAllowRetract(Boolean allowRetract) {
		this.allowRetract = allowRetract;
	}

	public Boolean getAllowRollback() {
		return allowRollback;
	}

	public void setAllowRollback(Boolean allowRollback) {
		this.allowRollback = allowRollback;
	}

	public Boolean getAllowDeleteWork() {
		return allowDeleteWork;
	}

	public void setAllowDeleteWork(Boolean allowDeleteWork) {
		this.allowDeleteWork = allowDeleteWork;
	}

	public Boolean getAllowRapid() {
		return allowRapid;
	}

	public void setAllowRapid(Boolean allowRapid) {
		this.allowRapid = allowRapid;
	}

	public Boolean getAllowRerouteTo() {
		return allowRerouteTo;
	}

	public void setAllowRerouteTo(Boolean allowRerouteTo) {
		this.allowRerouteTo = allowRerouteTo;
	}

	public ExpireType getTaskExpireType() {
		return taskExpireType;
	}

	public void setTaskExpireType(ExpireType taskExpireType) {
		this.taskExpireType = taskExpireType;
	}

	public Integer getTaskExpireDay() {
		return taskExpireDay;
	}

	public void setTaskExpireDay(Integer taskExpireDay) {
		this.taskExpireDay = taskExpireDay;
	}

	public Integer getTaskExpireHour() {
		return taskExpireHour;
	}

	public void setTaskExpireHour(Integer taskExpireHour) {
		this.taskExpireHour = taskExpireHour;
	}

	public Boolean getTaskExpireWorkTime() {
		return taskExpireWorkTime;
	}

	public void setTaskExpireWorkTime(Boolean taskExpireWorkTime) {
		this.taskExpireWorkTime = taskExpireWorkTime;
	}

	public String getTaskExpireScript() {
		return taskExpireScript;
	}

	public void setTaskExpireScript(String taskExpireScript) {
		this.taskExpireScript = taskExpireScript;
	}

	public String getTaskExpireScriptText() {
		return taskExpireScriptText;
	}

	public void setTaskExpireScriptText(String taskExpireScriptText) {
		this.taskExpireScriptText = taskExpireScriptText;
	}

	public String getTaskDuty() {
		return taskDuty;
	}

	public void setTaskDuty(String taskDuty) {
		this.taskDuty = taskDuty;
	}

	public String getReadDuty() {
		return readDuty;
	}

	public void setReadDuty(String readDuty) {
		this.readDuty = readDuty;
	}

	public List<String> getReadDataPathList() {
		return readDataPathList;
	}

	public void setReadDataPathList(List<String> readDataPathList) {
		this.readDataPathList = readDataPathList;
	}

	public List<String> getTaskDataPathList() {
		return taskDataPathList;
	}

	public void setTaskDataPathList(List<String> taskDataPathList) {
		this.taskDataPathList = taskDataPathList;
	}

	public String getReviewDuty() {
		return reviewDuty;
	}

	public void setReviewDuty(String reviewDuty) {
		this.reviewDuty = reviewDuty;
	}

	public List<String> getReviewDataPathList() {
		return reviewDataPathList;
	}

	public void setReviewDataPathList(List<String> reviewDataPathList) {
		this.reviewDataPathList = reviewDataPathList;
	}

	public String getResetRange() {
		return resetRange;
	}

	public void setResetRange(String resetRange) {
		this.resetRange = resetRange;
	}

	public String getManualBeforeTaskScript() {
		return manualBeforeTaskScript;
	}

	public void setManualBeforeTaskScript(String manualBeforeTaskScript) {
		this.manualBeforeTaskScript = manualBeforeTaskScript;
	}

	public String getManualBeforeTaskScriptText() {
		return manualBeforeTaskScriptText;
	}

	public void setManualBeforeTaskScriptText(String manualBeforeTaskScriptText) {
		this.manualBeforeTaskScriptText = manualBeforeTaskScriptText;
	}

	public String getManualAfterTaskScript() {
		return manualAfterTaskScript;
	}

	public void setManualAfterTaskScript(String manualAfterTaskScript) {
		this.manualAfterTaskScript = manualAfterTaskScript;
	}

	public String getManualAfterTaskScriptText() {
		return manualAfterTaskScriptText;
	}

	public void setManualAfterTaskScriptText(String manualAfterTaskScriptText) {
		this.manualAfterTaskScriptText = manualAfterTaskScriptText;
	}

	public String getManualStayScript() {
		return manualStayScript;
	}

	public void setManualStayScript(String manualStayScript) {
		this.manualStayScript = manualStayScript;
	}

	public String getManualStayScriptText() {
		return manualStayScriptText;
	}

	public void setManualStayScriptText(String manualStayScriptText) {
		this.manualStayScriptText = manualStayScriptText;
	}

	public List<String> getReadGroupList() {
		return readGroupList;
	}

	public void setReadGroupList(List<String> readGroupList) {
		this.readGroupList = readGroupList;
	}

	public List<String> getReviewGroupList() {
		return reviewGroupList;
	}

	public void setReviewGroupList(List<String> reviewGroupList) {
		this.reviewGroupList = reviewGroupList;
	}

	public List<String> getTaskGroupList() {
		return taskGroupList;
	}

	public void setTaskGroupList(List<String> taskGroupList) {
		this.taskGroupList = taskGroupList;
	}

	public Boolean getAllowAddSplit() {
		return allowAddSplit;
	}

	public void setAllowAddSplit(Boolean allowAddSplit) {
		this.allowAddSplit = allowAddSplit;
	}

	public Boolean getAllowPress() {
		return allowPress;
	}

	public void setAllowPress(Boolean allowPress) {
		this.allowPress = allowPress;
	}

	public String getOpinionGroup() {
		return opinionGroup;
	}

	public void setOpinionGroup(String opinionGroup) {
		this.opinionGroup = opinionGroup;
	}

//	public String getManualPressScript() {
//		return manualPressScript;
//	}
//
//	public void setManualPressScript(String manualPressScript) {
//		this.manualPressScript = manualPressScript;
//	}
//
//	public String getManualPressScriptText() {
//		return manualPressScriptText;
//	}
//
//	public void setManualPressScriptText(String manualPressScriptText) {
//		this.manualPressScriptText = manualPressScriptText;
//	}

}
