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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Process.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Process.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Process extends SliceJpaObject {

	private static final long serialVersionUID = 3241184900530625402L;
	private static final String TABLE = PersistenceProperties.Element.Process.table;

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

	/* Entity 默认字段结束 */

	public static final String name_FIELDNAME = "name";
	@RestrictFlag
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application")))
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
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + controllerList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + controllerList_FIELDNAME + JoinIndexNameSuffix))
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

	public static final String beforeBeginScript_FIELDNAME = "beforeBeginScript";
	@IdReference(Script.class)
	@FieldDescribe("流程启动前事件脚本.")
	/** 脚本可能使用名称,所以长度为255 */
	@Column(length = length_255B, name = ColumnNamePrefix + beforeBeginScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeBeginScript;

	public static final String beforeBeginScriptText_FIELDNAME = "beforeBeginScriptText";
	@FieldDescribe("流程启动前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeBeginScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeBeginScriptText;

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

	public static final String beforeEndScript_FIELDNAME = "beforeEndScript";
	@IdReference(Script.class)
	/** 脚本可能使用名称,所以长度为255 */
	@FieldDescribe("流程结束后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeEndScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeEndScript;

	public static final String beforeEndScriptText_FIELDNAME = "beforeEndScriptText";
	@FieldDescribe("流程结束后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeEndScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeEndScriptText;

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
					+ startableIdentityList_FIELDNAME))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableIdentityList_FIELDNAME + JoinIndexNameSuffix)
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

	public static final String serialTexture_FIELDNAME = "serialTexture";
	@FieldDescribe("编号定义.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + serialTexture_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialTexture;

	public static final String serialActivity_FIELDNAME = "serialActivity";
	@IdReference({ Agent.class, Begin.class, Cancel.class, Choice.class, Choice.class, Delay.class, Embed.class,
			End.class, Invoke.class, Manual.class, Merge.class, Message.class, Parallel.class, Service.class,
			Split.class })
	@FieldDescribe("编号活动ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + serialActivity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialActivity;

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

	public static final String expireScript_FIELDNAME = "expireScript";
	@IdReference(Script.class)
	/** 脚本可能使用名称,所以长度为255 */
	@FieldDescribe("过期时间设定脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + expireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String expireScript;

	public static final String expireScriptText_FIELDNAME = "expireScriptText";
	@FieldDescribe("过期时间设定脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + expireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String expireScriptText;

	public static final String checkDraft_FIELDNAME = "checkDraft";
	@FieldDescribe("是否进行无内容的草稿删除校验.")
	@Column(name = ColumnNamePrefix + checkDraft_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean checkDraft;

	/* flag标志位 */

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

	public String getBeforeBeginScript() {
		return beforeBeginScript;
	}

	public void setBeforeBeginScript(String beforeBeginScript) {
		this.beforeBeginScript = beforeBeginScript;
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

	public String getBeforeBeginScriptText() {
		return beforeBeginScriptText;
	}

	public void setBeforeBeginScriptText(String beforeBeginScriptText) {
		this.beforeBeginScriptText = beforeBeginScriptText;
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

	public String getBeforeEndScript() {
		return beforeEndScript;
	}

	public void setBeforeEndScript(String beforeEndScript) {
		this.beforeEndScript = beforeEndScript;
	}

	public String getBeforeEndScriptText() {
		return beforeEndScriptText;
	}

	public void setBeforeEndScriptText(String beforeEndScriptText) {
		this.beforeEndScriptText = beforeEndScriptText;
	}

	public String getAfterEndScriptText() {
		return afterEndScriptText;
	}

	public void setAfterEndScriptText(String afterEndScriptText) {
		this.afterEndScriptText = afterEndScriptText;
	}

//	public List<String> getReviewIdentityList() {
//		return reviewIdentityList;
//	}
//
//	public void setReviewIdentityList(List<String> reviewIdentityList) {
//		this.reviewIdentityList = reviewIdentityList;
//	}

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

	public String getExpireScript() {
		return expireScript;
	}

	public void setExpireScript(String expireScript) {
		this.expireScript = expireScript;
	}

	public String getExpireScriptText() {
		return expireScriptText;
	}

	public void setExpireScriptText(String expireScriptText) {
		this.expireScriptText = expireScriptText;
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

}