package com.x.processplatform.core.entity.element;

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
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.entity.annotation.NotEqual;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Process", description = "流程平台流程.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Element.Process.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Process.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Process extends SliceJpaObject {

	private static final long serialVersionUID = 3241184900530625402L;
	private static final String TABLE = PersistenceProperties.Element.Process.table;

	public static final String SERIALPHASE_ARRIVE = "arrive";
	public static final String SERIALPHASE_INQUIRE = "inquire";

	public static final String STARTMODE_DRAFT = "draft";
	public static final String STARTMODE_INSTANCE = "instance";

	public static final String STARTABLETERMINAL_CLIENT = "client";
	public static final String STARTABLETERMINAL_MOBILE = "mobile";
	public static final String STARTABLETERMINAL_ALL = "all";
	public static final String STARTABLETERMINAL_NONE = "none";

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
		/* 默认流程名称作为意见为'是' */
		if (this.routeNameAsOpinion == null) {
			this.routeNameAsOpinion = true;
		}
		if (StringUtils.isEmpty(this.edition)) {
			this.edition = this.id;
			this.editionEnable = true;
			this.editionNumber = 1.0;
			this.editionName = this.name + "_V" + this.editionNumber;
		}
	}

	public static final String PERMISSIONWRITESCRIPT_FIELDNAME = "permissionWriteScript";
	@FieldDescribe("可编辑权限脚本,返回组织专用标识.")
	@Transient
	private String permissionWriteScript;

	public String getPermissionWriteScript() {
		if ((null == permissionWriteScript) && (null != properties)) {
			this.permissionWriteScript = this.properties.getPermissionWriteScript();
		}
		return permissionWriteScript;
	}

	public void setPermissionWriteScript(String permissionWriteScript) {
		this.permissionWriteScript = permissionWriteScript;
		this.getProperties().setPermissionWriteScript(permissionWriteScript);
	}

	public static final String PERMISSIONWRITESCRIPTTEXT_FIELDNAME = "permissionWriteScriptText";
	@FieldDescribe("可编辑权限脚本文本,返回组织专用标识.")
	@Transient
	private String permissionWriteScriptText;

	public String getPermissionWriteScriptText() {
		if ((null == permissionWriteScriptText) && (null != properties)) {
			this.permissionWriteScriptText = this.properties.getPermissionWriteScriptText();
		}
		return permissionWriteScriptText;
	}

	public void setPermissionWriteScriptText(String permissionWriteScriptText) {
		this.permissionWriteScriptText = permissionWriteScriptText;
		this.getProperties().setPermissionWriteScriptText(permissionWriteScriptText);
	}
	public static final String MAINTENANCEIDENTITY_FIELDNAME = "maintenanceIdentity";
	@FieldDescribe("流程维护身份,如果无法找到处理身份默认的流程处理身份.")
	@Transient
	private String maintenanceIdentity;

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void setMaintenanceIdentity(String maintenanceIdentity) {
		this.maintenanceIdentity = maintenanceIdentity;
		this.getProperties().setMaintenanceIdentity(maintenanceIdentity);
	}

	public static final String UPDATETABLEENABLE_FIELDNAME = "updateTableEnable";
	@FieldDescribe("启用同步到自建表.")
	@Transient
	private Boolean updateTableEnable;

	public Boolean getUpdateTableEnable() {
		return updateTableEnable;
	}

	public void setUpdateTableEnable(Boolean updateTableEnable) {
		this.updateTableEnable = updateTableEnable;
		this.getProperties().setUpdateTableEnable(updateTableEnable);
	}

	public static final String UPDATETABLELIST_FIELDNAME = "updateTableList";
	@FieldDescribe("同步到自建表.")
	@Transient
	private List<String> updateTableList;

	public List<String> getUpdateTableList() {
		return updateTableList;
	}

	public void setUpdateTableList(List<String> updateTableList) {
		this.updateTableList = updateTableList;
		this.getProperties().setUpdateTableList(updateTableList);
	}

	public static final String dataTraceFieldType_FIELDNAME = "dataTraceFieldType";
	@FieldDescribe("需要记录数据变化的字段配置方式：all|所有、custom|依据dataTraceFieldList配置的字段.")
	@Transient
	private String dataTraceFieldType;

	public String getDataTraceFieldType() {
		return dataTraceFieldType;
	}

	public void setDataTraceFieldType(String dataTraceFieldType) {
		this.dataTraceFieldType = dataTraceFieldType;
		this.getProperties().setDataTraceFieldType(dataTraceFieldType);
	}

	public static final String dataTraceFieldList_FIELDNAME = "dataTraceFieldList";
	@FieldDescribe("需要记录数据变化的字段.")
	@Transient
	private List<String> dataTraceFieldList;

	public List<String> getDataTraceFieldList() {
		return dataTraceFieldList;
	}

	public void setDataTraceFieldList(List<String> dataTraceFieldList) {
		this.dataTraceFieldList = dataTraceFieldList;
		this.getProperties().setDataTraceFieldList(dataTraceFieldList);
	}

	public Boolean getProjectionFully() {
		return BooleanUtils.isTrue(this.projectionFully);
	}

	public String getBeforeArriveScript() {
		return (null == beforeArriveScript) ? "" : this.beforeArriveScript;
	}

	public String getBeforeArriveScriptText() {
		return (null == beforeArriveScriptText) ? "" : this.beforeArriveScriptText;
	}

	public String getAfterArriveScript() {
		return (null == afterArriveScript) ? "" : this.afterArriveScript;
	}

	public String getAfterArriveScriptText() {
		return (null == afterArriveScriptText) ? "" : this.afterArriveScriptText;
	}

	public String getBeforeExecuteScript() {
		return (null == beforeExecuteScript) ? "" : this.beforeExecuteScript;
	}

	public String getBeforeExecuteScriptText() {
		return (null == beforeExecuteScriptText) ? "" : this.beforeExecuteScriptText;
	}

	public String getAfterExecuteScript() {
		return (null == afterExecuteScript) ? "" : this.afterExecuteScript;
	}

	public String getAfterExecuteScriptText() {
		return (null == afterExecuteScriptText) ? "" : this.afterExecuteScriptText;
	}

	public String getBeforeInquireScript() {
		return (null == beforeInquireScript) ? "" : this.beforeInquireScript;
	}

	public String getBeforeInquireScriptText() {
		return (null == beforeInquireScriptText) ? "" : this.beforeInquireScriptText;
	}

	public String getAfterInquireScript() {
		return (null == afterInquireScript) ? "" : this.afterInquireScript;
	}

	public String getAfterInquireScriptText() {
		return (null == afterInquireScriptText) ? "" : this.afterInquireScriptText;
	}

	public Boolean getRouteNameAsOpinion() {
		return BooleanUtils.isNotFalse(routeNameAsOpinion);
	}

	public ProcessProperties getProperties() {
		if (null == this.properties) {
			this.properties = new ProcessProperties();
		}
		return this.properties;
	}

	public void setProperties(ProcessProperties properties) {
		this.properties = properties;
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.manualBeforeTaskScript = this.getProperties().getManualBeforeTaskScript();
			this.manualBeforeTaskScriptText = this.getProperties().getManualBeforeTaskScriptText();
			this.manualAfterTaskScript = this.getProperties().getManualAfterTaskScript();
			this.manualAfterTaskScriptText = this.getProperties().getManualAfterTaskScriptText();
			this.manualStayScript = this.getProperties().getManualStayScript();
			this.manualStayScriptText = this.getProperties().getManualStayScriptText();
			this.updateTableEnable = this.getProperties().getUpdateTableEnable();
			this.updateTableList = this.getProperties().getUpdateTableList();
			this.maintenanceIdentity = this.getProperties().getMaintenanceIdentity();
			this.targetAssignDataScript = this.getProperties().getTargetAssignDataScript();
			this.targetAssignDataScriptText = this.getProperties().getTargetAssignDataScriptText();
			this.manualAfterProcessingScript = this.getProperties().getManualAfterProcessingScript();
			this.manualAfterProcessingScriptText = this.getProperties().getManualAfterProcessingScriptText();
			this.dataTraceFieldType = this.getProperties().getDataTraceFieldType();
			this.dataTraceFieldList = this.getProperties().getDataTraceFieldList();
			this.permissionWriteScript = this.getProperties().getPermissionWriteScript();
			this.permissionWriteScriptText = this.getProperties().getPermissionWriteScriptText();
		}
	}

	public static final String name_FIELDNAME = "name";
	@RestrictFlag
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application"), notEquals = @NotEqual(property = "edition", field = "edition")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application"), notEquals = @NotEqual(property = "edition", field = "edition")))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("流程创建者.")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorPerson;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("最后的编辑者.")
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后的编辑时间.")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String application_FIELDNAME = "application";
	@IdReference(Application.class)
	@FieldDescribe("流程所属应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = @CitationExist(type = Application.class))
	private String application;

	public static final String controllerList_FIELDNAME = "controllerList";
	@FieldDescribe("流程管理者.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ controllerList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon;

	public static final String afterBeginScript_FIELDNAME = "afterBeginScript";
	@IdReference(Script.class)
	/** 脚本可能使用名称,所以长度为255 */
	@FieldDescribe("流程启动前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterBeginScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterBeginScript;

	public static final String afterBeginScriptText_FIELDNAME = "afterBeginScriptText";
	@FieldDescribe("流程启动前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterBeginScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterBeginScriptText;

	public static final String afterEndScript_FIELDNAME = "afterEndScript";
	@IdReference(Script.class)
	/** 脚本可能使用名称,所以长度为255 */
	@FieldDescribe("流程结束后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterEndScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterEndScript;

	public static final String afterEndScriptText_FIELDNAME = "afterEndScriptText";
	@FieldDescribe("流程结束后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterEndScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterEndScriptText;

	public static final String startableIdentityList_FIELDNAME = "startableIdentityList";
	@FieldDescribe("在指定启动时候,允许新建Work的用户.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ startableIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ startableIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> startableIdentityList;

	public static final String startableUnitList_FIELDNAME = "startableUnitList";
	@FieldDescribe("在指定启动时候,允许新建Work的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ startableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ startableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> startableUnitList;

	public static final String startableGroupList_FIELDNAME = "startableGroupList";
	@FieldDescribe("在指定启动时候,允许新建Work的群组.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ startableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ startableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> startableGroupList;

	public static final String serialTexture_FIELDNAME = "serialTexture";
	@FieldDescribe("编号定义.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + serialTexture_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialTexture;

	public static final String serialActivity_FIELDNAME = "serialActivity";
	@IdReference({ Agent.class, Begin.class, Cancel.class, Choice.class, Choice.class, Delay.class, Embed.class,
			End.class, Invoke.class, Manual.class, Merge.class, Parallel.class, Service.class, Split.class })
	@FieldDescribe("编号活动ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + serialActivity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialActivity;

	public static final String serialPhase_FIELDNAME = "serialPhase";
	@FieldDescribe("编号活动阶段可以选择arrive或者inquire,默认情况下为空为arrive")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + serialPhase_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialPhase;

	public static final String expireType_FIELDNAME = "expireType";
	@FieldDescribe("过期方式.可选值never,appoint,script")
	@Enumerated(EnumType.STRING)
	@Column(length = ExpireType.length, name = ColumnNamePrefix + expireType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ExpireType expireType = ExpireType.never;

	public static final String expireDay_FIELDNAME = "expireDay";
	@FieldDescribe("过期日期.")
	@Column(name = ColumnNamePrefix + expireDay_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer expireDay;

	public static final String expireHour_FIELDNAME = "expireHour";
	@FieldDescribe("过期小时.")
	@Column(name = ColumnNamePrefix + expireHour_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer expireHour;

	public static final String expireWorkTime_FIELDNAME = "expireWorkTime";
	@FieldDescribe("过期是否是工作时间.")
	@Column(name = ColumnNamePrefix + expireWorkTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean expireWorkTime;

	public static final String checkDraft_FIELDNAME = "checkDraft";
	@FieldDescribe("是否进行无内容的草稿删除校验.")
	@Column(name = ColumnNamePrefix + checkDraft_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean checkDraft;

	public static final String startableTerminal_FIELDNAME = "startableTerminal";
	@FieldDescribe("可启动流程终端类型,可选值 client,mobile,all,none")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + startableTerminal_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String startableTerminal;

	public static final String projection_FIELDNAME = "projection";
	@FieldDescribe("字段映射配置.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + projection_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projection;

	public static final String projectionFully_FIELDNAME = "projectionFully";
	@FieldDescribe("执行完全映射,在每次流转时会将所有的工作,待办,已办,待阅,已阅,参阅执行全部字段映射,默认false")
	@Column(name = ColumnNamePrefix + projectionFully_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean projectionFully;

	public static final String routeNameAsOpinion_FIELDNAME = "routeNameAsOpinion";
	@FieldDescribe("如果没有默认意见那么将路由名称作为默认意见.")
	@Column(name = ColumnNamePrefix + routeNameAsOpinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean routeNameAsOpinion;

	public static final String beforeArriveScript_FIELDNAME = "beforeArriveScript";
	@IdReference(Script.class)
	@FieldDescribe("统一活动到达前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScript;

	public static final String beforeArriveScriptText_FIELDNAME = "beforeArriveScriptText";
	@FieldDescribe("统一活动到达前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScriptText;

	public static final String afterArriveScript_FIELDNAME = "afterArriveScript";
	@IdReference(Script.class)
	@FieldDescribe("统一活动到达后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScript;

	public static final String afterArriveScriptText_FIELDNAME = "afterArriveScriptText";
	@FieldDescribe("统一活动到达后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScriptText;

	public static final String beforeExecuteScript_FIELDNAME = "beforeExecuteScript";
	@IdReference(Script.class)
	@FieldDescribe("统一活动执行前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScript;

	public static final String beforeExecuteScriptText_FIELDNAME = "beforeExecuteScriptText";
	@FieldDescribe("统一活动执行前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScriptText;

	public static final String afterExecuteScript_FIELDNAME = "afterExecuteScript";
	@IdReference(Script.class)
	@FieldDescribe("统一活动执行后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScript;

	public static final String afterExecuteScriptText_FIELDNAME = "afterExecuteScriptText";
	@FieldDescribe("统一活动执行后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScriptText;

	public static final String beforeInquireScript_FIELDNAME = "beforeInquireScript";
	@IdReference(Script.class)
	@FieldDescribe("统一路由查询前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScript;

	public static final String beforeInquireScriptText_FIELDNAME = "beforeInquireScriptText";
	@FieldDescribe("统一路由查询前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScriptText;

	public static final String afterInquireScript_FIELDNAME = "afterInquireScript";
	@IdReference(Script.class)
	@FieldDescribe("统一路由查询后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScript;

	public static final String afterInquireScriptText_FIELDNAME = "afterInquireScriptText";
	@FieldDescribe("统一路由查询后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScriptText;

	public static final String edition_FIELDNAME = "edition";
	@FieldDescribe("版本编码,不同版本的流程编码需相同.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + edition_FIELDNAME)
	private String edition;

	public static final String editionName_FIELDNAME = "editionName";
	@FieldDescribe("版本名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + editionName_FIELDNAME)
	private String editionName;

	public static final String editionEnable_FIELDNAME = "editionEnable";
	@FieldDescribe("启用版本")
	@Column(name = ColumnNamePrefix + editionEnable_FIELDNAME)
	private Boolean editionEnable;

	public static final String editionNumber_FIELDNAME = "editionNumber";
	@FieldDescribe("版本号")
	@Column(name = ColumnNamePrefix + editionNumber_FIELDNAME)
	private Double editionNumber;

	public static final String editionDes_FIELDNAME = "editionDes";
	@FieldDescribe("版本描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + editionDes_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String editionDes;

	public static final String defaultStartMode_FIELDNAME = "defaultStartMode";
	@FieldDescribe("默认启动方式,draft,instance")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + defaultStartMode_FIELDNAME)
	private String defaultStartMode;

	public static final String CATEGORY_FIELDNAME = "category";
	@FieldDescribe("流程分类")
	@Column(length = length_255B, name = ColumnNamePrefix + CATEGORY_FIELDNAME)
	private String category;

	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ProcessProperties properties;

	public String getManualBeforeTaskScript() {
		return manualBeforeTaskScript;
	}

	public void setManualBeforeTaskScript(String manualBeforeTaskScript) {
		this.getProperties().setManualBeforeTaskScript(manualBeforeTaskScript);
		this.manualBeforeTaskScript = manualBeforeTaskScript;
	}

	public void setManualBeforeTaskScriptText(String manualBeforeTaskScriptText) {
		this.getProperties().setManualBeforeTaskScriptText(manualBeforeTaskScriptText);
		this.manualBeforeTaskScriptText = manualBeforeTaskScriptText;
	}

	public void setManualAfterTaskScript(String manualAfterTaskScript) {
		this.getProperties().setManualAfterTaskScript(manualAfterTaskScript);
		this.manualAfterTaskScript = manualAfterTaskScript;
	}

	public void setManualAfterTaskScriptText(String manualAfterTaskScriptText) {
		this.getProperties().setManualAfterTaskScriptText(manualAfterTaskScriptText);
		this.manualAfterTaskScriptText = manualAfterTaskScriptText;
	}

	public void setManualStayScript(String manualStayScript) {
		this.getProperties().setManualStayScript(manualStayScript);
		this.manualStayScript = manualStayScript;
	}

	public void setManualStayScriptText(String manualStayScriptText) {
		this.getProperties().setManualStayScriptText(manualStayScriptText);
		this.manualStayScriptText = manualStayScriptText;
	}

	public String getTargetAssignDataScript() {
		return targetAssignDataScript;
	}

	public void setTargetAssignDataScript(String targetAssignDataScript) {
		this.getProperties().setTargetAssignDataScript(targetAssignDataScript);
		this.targetAssignDataScript = targetAssignDataScript;
	}

	public String getTargetAssignDataScriptText() {
		return targetAssignDataScriptText;
	}

	public void setTargetAssignDataScriptText(String targetAssignDataScriptText) {
		this.getProperties().setTargetAssignDataScriptText(targetAssignDataScriptText);
		this.targetAssignDataScriptText = targetAssignDataScriptText;
	}

	public String getManualBeforeTaskScriptText() {
		return manualBeforeTaskScriptText;
	}

	public String getManualAfterTaskScript() {
		return manualAfterTaskScript;
	}

	public String getManualAfterTaskScriptText() {
		return manualAfterTaskScriptText;
	}

	public String getManualStayScript() {
		return manualStayScript;
	}

	public String getManualStayScriptText() {
		return manualStayScriptText;
	}

	public static final String MANUALBEFORETASKSCRIPT_FIELDNAME = "manualBeforeTaskScript";
	@FieldDescribe("待办执行前脚本.")
	@Transient
	private String manualBeforeTaskScript;

	public static final String MANUALBEFORETASKSCRIPTTEXT_FIELDNAME = "manualBeforeTaskScriptText";
	@FieldDescribe("待办执行前脚本文本.")
	@Transient
	private String manualBeforeTaskScriptText;

	public static final String MANUALAFTERTASKSCRIPT_FIELDNAME = "manualAfterTaskScript";
	@FieldDescribe("待办执行后脚本.")
	@Transient
	private String manualAfterTaskScript;

	public static final String MANUALAFTERTASKSCRIPTTEXT_FIELDNAME = "manualAfterTaskScriptText";
	@FieldDescribe("待办执行后脚本文本.")
	@Transient
	private String manualAfterTaskScriptText;

	public static final String MANUALSTAYSCRIPT_FIELDNAME = "manualStayScript";
	@FieldDescribe("人工活动有停留脚本.")
	@Transient
	private String manualStayScript;

	public static final String MANUALSTAYSCRIPTTEXT_FIELDNAME = "manualStayScriptText";
	@FieldDescribe("人工活动有停留脚本文本.")
	@Transient
	private String manualStayScriptText;

	public static final String MANUALAFTERPROCESSINGSCRIPT_FIELDNAME = "manualAfterProcessingScript";
	@Transient
	@FieldDescribe("人工环节工作流转后执行脚本.")
	// @since 8.1.0
	private String manualAfterProcessingScript;

	public String getManualAfterProcessingScript() {
		if (null != this.manualAfterProcessingScript) {
			return this.manualAfterProcessingScript;
		} else {
			return this.getProperties().getManualAfterProcessingScript();
		}
	}

	public void setManualAfterProcessingScript(String manualAfterProcessingScript) {
		this.manualAfterProcessingScript = manualAfterProcessingScript;
		this.getProperties().setManualAfterProcessingScript(manualAfterProcessingScript);
	}

	public static final String MANUALAFTERPROCESSINGSCRIPTTEXT_FIELDNAME = "manualAfterProcessingScriptText";
	@Transient
	@FieldDescribe("人工环节工作流转后执行脚本文本.")
	// @since 8.1.0
	private String manualAfterProcessingScriptText;

	public String getManualAfterProcessingScriptText() {
		if (null != this.manualAfterProcessingScriptText) {
			return this.manualAfterProcessingScriptText;
		} else {
			return this.getProperties().getManualAfterProcessingScriptText();
		}
	}

	public void setManualAfterProcessingScriptText(String manualAfterProcessingScriptText) {
		this.manualAfterProcessingScriptText = manualAfterProcessingScriptText;
		this.getProperties().setManualAfterProcessingScriptText(manualAfterProcessingScriptText);
	}

	public static final String TARGETASSIGNDATASCRIPT_FIELDNAME = "targetAssignDataScript";
	@FieldDescribe("数据执行前脚本.")
	@Transient
	private String targetAssignDataScript;

	public static final String TARGETASSIGNDATASCRIPTTEXT_FIELDNAME = "targetAssignDataScriptText";
	@FieldDescribe("数据执行前脚本文本.")
	@Transient
	private String targetAssignDataScriptText;

	/* flag标志位 */

	public String getName() {
		return name;
	}

	public void setProjectionFully(Boolean projectionFully) {
		this.projectionFully = projectionFully;
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

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getAfterEndScript() {
		return afterEndScript;
	}

	public void setAfterEndScript(String afterEndScript) {
		this.afterEndScript = afterEndScript;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public List<String> getStartableIdentityList() {
		return startableIdentityList;
	}

	public void setStartableIdentityList(List<String> startableIdentityList) {
		this.startableIdentityList = startableIdentityList;
	}

	public List<String> getStartableUnitList() {
		return startableUnitList;
	}

	public void setStartableUnitList(List<String> startableUnitList) {
		this.startableUnitList = startableUnitList;
	}

	public String getAfterBeginScript() {
		return afterBeginScript;
	}

	public void setAfterBeginScript(String afterBeginScript) {
		this.afterBeginScript = afterBeginScript;
	}

	public String getAfterBeginScriptText() {
		return afterBeginScriptText;
	}

	public void setAfterBeginScriptText(String afterBeginScriptText) {
		this.afterBeginScriptText = afterBeginScriptText;
	}

	public String getAfterEndScriptText() {
		return afterEndScriptText;
	}

	public void setAfterEndScriptText(String afterEndScriptText) {
		this.afterEndScriptText = afterEndScriptText;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public String getSerialTexture() {
		return serialTexture;
	}

	public void setSerialTexture(String serialTexture) {
		this.serialTexture = serialTexture;
	}

	public String getSerialActivity() {
		return serialActivity;
	}

	public void setSerialActivity(String serialActivity) {
		this.serialActivity = serialActivity;
	}

	public ExpireType getExpireType() {
		return expireType;
	}

	public void setExpireType(ExpireType expireType) {
		this.expireType = expireType;
	}

	public Integer getExpireDay() {
		return expireDay;
	}

	public void setExpireDay(Integer expireDay) {
		this.expireDay = expireDay;
	}

	public Integer getExpireHour() {
		return expireHour;
	}

	public void setExpireHour(Integer expireHour) {
		this.expireHour = expireHour;
	}

	public Boolean getExpireWorkTime() {
		return expireWorkTime;
	}

	public void setExpireWorkTime(Boolean expireWorkTime) {
		this.expireWorkTime = expireWorkTime;
	}

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getCheckDraft() {
		return checkDraft;
	}

	public void setCheckDraft(Boolean checkDraft) {
		this.checkDraft = checkDraft;
	}

	public String getSerialPhase() {
		return serialPhase;
	}

	public void setSerialPhase(String serialPhase) {
		this.serialPhase = serialPhase;
	}

	public void setRouteNameAsOpinion(Boolean routeNameAsOpinion) {
		this.routeNameAsOpinion = routeNameAsOpinion;
	}

	public void setBeforeArriveScript(String beforeArriveScript) {
		this.beforeArriveScript = beforeArriveScript;
	}

	public void setBeforeArriveScriptText(String beforeArriveScriptText) {
		this.beforeArriveScriptText = beforeArriveScriptText;
	}

	public void setAfterArriveScript(String afterArriveScript) {
		this.afterArriveScript = afterArriveScript;
	}

	public void setAfterArriveScriptText(String afterArriveScriptText) {
		this.afterArriveScriptText = afterArriveScriptText;
	}

	public void setBeforeExecuteScript(String beforeExecuteScript) {
		this.beforeExecuteScript = beforeExecuteScript;
	}

	public void setBeforeExecuteScriptText(String beforeExecuteScriptText) {
		this.beforeExecuteScriptText = beforeExecuteScriptText;
	}

	public void setAfterExecuteScript(String afterExecuteScript) {
		this.afterExecuteScript = afterExecuteScript;
	}

	public void setAfterExecuteScriptText(String afterExecuteScriptText) {
		this.afterExecuteScriptText = afterExecuteScriptText;
	}

	public void setBeforeInquireScript(String beforeInquireScript) {
		this.beforeInquireScript = beforeInquireScript;
	}

	public void setBeforeInquireScriptText(String beforeInquireScriptText) {
		this.beforeInquireScriptText = beforeInquireScriptText;
	}

	public void setAfterInquireScript(String afterInquireScript) {
		this.afterInquireScript = afterInquireScript;
	}

	public void setAfterInquireScriptText(String afterInquireScriptText) {
		this.afterInquireScriptText = afterInquireScriptText;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getEditionName() {
		return editionName;
	}

	public void setEditionName(String editionName) {
		this.editionName = editionName;
	}

	public Boolean getEditionEnable() {
		return editionEnable;
	}

	public void setEditionEnable(Boolean editionEnable) {
		this.editionEnable = editionEnable;
	}

	public Double getEditionNumber() {
		return editionNumber;
	}

	public void setEditionNumber(Double editionNumber) {
		this.editionNumber = editionNumber;
	}

	public String getEditionDes() {
		return editionDes;
	}

	public void setEditionDes(String editionDes) {
		this.editionDes = editionDes;
	}

	public String getDefaultStartMode() {
		return defaultStartMode;
	}

	public void setDefaultStartMode(String defaultStartMode) {
		this.defaultStartMode = defaultStartMode;
	}

	public String getStartableTerminal() {
		return startableTerminal;
	}

	public void setStartableTerminal(String startableTerminal) {
		this.startableTerminal = startableTerminal;
	}

	public List<String> getStartableGroupList() {
		return startableGroupList;
	}

	public void setStartableGroupList(List<String> startableGroupList) {
		this.startableGroupList = startableGroupList;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
