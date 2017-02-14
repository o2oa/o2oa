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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Process.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Process extends SliceJpaObject {

	private static final long serialVersionUID = 3241184900530625402L;
	private static final String TABLE = PersistenceProperties.Element.Process.table;

	@PrePersist
	public void prePersist() {
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
	public void preUpdate() {
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

	private void onPersist() {

	}

	/* 更新运行方法 */

	public static String[] FLAGS = new String[] { "id", "alias" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@EntityFieldDescribe("名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xname")
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application")))
	private String name;

	@EntityFieldDescribe("代理节点别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xalias")
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application")))
	private String alias;

	@EntityFieldDescribe("描述.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xdescription")
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("流程创建者.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorPerson")
	@CheckPersist(allowEmpty = false)
	private String creatorPerson;

	@EntityFieldDescribe("最后的编辑者.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xlastUpdatePerson")
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	@EntityFieldDescribe("最后的编辑时间.")
	@Column(name = "xlastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	@EntityFieldDescribe("流程所属应用.")
	@Column(length = JpaObject.length_id, name = "xapplication")
	@Index(name = TABLE + "_application")
	@CheckPersist(allowEmpty = false, citationExists = @CitationExist(type = Application.class))
	private String application;

	@EntityFieldDescribe("流程管理者。")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_controllerList", joinIndex = @Index(name = TABLE + "_controllerList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xcontrollerList")
	@ElementIndex(name = TABLE + "_controllerList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList;

	@EntityFieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = "xicon")
	@CheckPersist(allowEmpty = true)
	private String icon;

	@EntityFieldDescribe("work管理人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_reviewIdentityList", joinIndex = @Index(name = TABLE + "_reviewIdentityList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreviewIdentityList")
	@ElementIndex(name = TABLE + "_reviewIdentityList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> reviewIdentityList;

	@EntityFieldDescribe("流程启动前事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xbeforeBeginScript")
	@CheckPersist(allowEmpty = true)
	private String beforeBeginScript;

	@EntityFieldDescribe("流程启动前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xbeforeBeginScriptText")
	@CheckPersist(allowEmpty = true)
	private String beforeBeginScriptText;

	@EntityFieldDescribe("流程启动前事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xafterBeginScript")
	@CheckPersist(allowEmpty = true)
	private String afterBeginScript;

	@EntityFieldDescribe("流程启动前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterBeginScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterBeginScriptText;

	@EntityFieldDescribe("流程结束后事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xbeforeEndScript")
	@CheckPersist(allowEmpty = true)
	private String beforeEndScript;

	@EntityFieldDescribe("流程结束后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xbeforeEndScriptText")
	@CheckPersist(allowEmpty = true)
	private String beforeEndScriptText;

	@EntityFieldDescribe("流程结束后事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xafterEndScript")
	@CheckPersist(allowEmpty = true)
	private String afterEndScript;

	@EntityFieldDescribe("流程结束后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterEndScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterEndScriptText;

	@EntityFieldDescribe("在指定启动时候,允许新建Work的用户.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_startableIdentityList", joinIndex = @Index(name = TABLE + "_startableIdentityList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xstartableIdentityList")
	@ElementIndex(name = TABLE + "_startableIdentityList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> startableIdentityList;

	@EntityFieldDescribe("在指定启动时候,允许新建Work的部门.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_startableDepartmentList", joinIndex = @Index(name = TABLE + "_startableDepartmentList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xstartableDepartmentList")
	@ElementIndex(name = TABLE + "_startableDepartmentList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> startableDepartmentList;

	@EntityFieldDescribe("在指定启动时候,允许新建Work的公司.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_startableCompanyList", joinIndex = @Index(name = TABLE + "_startableCompanyList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xstartableCompanyList")
	@ElementIndex(name = TABLE + "_startableCompanyList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> startableCompanyList;

	@EntityFieldDescribe("编号定义.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xserialTexture")
	@CheckPersist(allowEmpty = true)
	private String serialTexture;

	@EntityFieldDescribe("编号活动ID.")
	@Column(length = JpaObject.length_id, name = "xserialActivity")
	@CheckPersist(allowEmpty = true)
	private String serialActivity;

	@EntityFieldDescribe("过期方式.可选值never,appoint,script")
	@Enumerated(EnumType.STRING)
	@Column(length = ExpireType.length, name = "xexpireType")
	@Index(name = TABLE + "_expireType")
	@CheckPersist(allowEmpty = false)
	private ExpireType expireType = ExpireType.never;

	@EntityFieldDescribe("过期日期.")
	@Column(name = "xexpireDay")
	@CheckPersist(allowEmpty = true)
	private Integer expireDay;

	@EntityFieldDescribe("过期小时.")
	@Column(name = "xexpireHour")
	@CheckPersist(allowEmpty = true)
	private Integer expireHour;

	@EntityFieldDescribe("过期是否是工作时间.")
	@Column(name = "xexpireWorkTime")
	@CheckPersist(allowEmpty = true)
	private Boolean expireWorkTime;

	@EntityFieldDescribe("过期时间设定脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xexpireScript")
	@CheckPersist(allowEmpty = true)
	private String expireScript;

	@EntityFieldDescribe("过期时间设定脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xexpireScriptText")
	@CheckPersist(allowEmpty = true)
	private String expireScriptText;

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

	public List<String> getStartableDepartmentList() {
		return startableDepartmentList;
	}

	public void setStartableDepartmentList(List<String> startableDepartmentList) {
		this.startableDepartmentList = startableDepartmentList;
	}

	public List<String> getStartableCompanyList() {
		return startableCompanyList;
	}

	public void setStartableCompanyList(List<String> startableCompanyList) {
		this.startableCompanyList = startableCompanyList;
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

	public List<String> getReviewIdentityList() {
		return reviewIdentityList;
	}

	public void setReviewIdentityList(List<String> reviewIdentityList) {
		this.reviewIdentityList = reviewIdentityList;
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

}