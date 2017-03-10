package com.x.cms.core.entity.element;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.cms.core.entity.PersistenceProperties;


@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.QueryView.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class QueryView extends SliceJpaObject {

	private static final long serialVersionUID = -7520516033901189347L;

	private static final String TABLE = PersistenceProperties.Element.QueryView.table;

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

	}

	/* 更新运行方法 */

	public static String[] FLAGS = new String[] { "name", "id" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@EntityFieldDescribe("名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, simplyString = false)
	private String name;

	@EntityFieldDescribe("别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xalias")
	@CheckPersist(allowEmpty = true, simplyString = false )
	private String alias;

	@EntityFieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = "xdescription")
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("所属应用.")
	@Column(length = JpaObject.length_id, name = "xappId")
	@Index(name = TABLE + "_appId")
	@CheckPersist(allowEmpty = false )
	private String appId;
	
	@EntityFieldDescribe("所属应用.")
	@Column(length = JpaObject.length_id, name = "xappName")
	@Index(name = TABLE + "_appName")
	@CheckPersist(allowEmpty = false )
	private String appName;

	@EntityFieldDescribe("是否是定时任务.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xtimingEnable")
	@Index(name = TABLE + "_timingEnable")
	private Boolean timingEnable;

	@EntityFieldDescribe("上次运行后触发器触发过的次数,用于判断是否要运行,如果需要运行那么重置为0,避免由于时间往后调导致的不运行.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xtimingTouch")
	@Index(name = TABLE + "_timingTouch")
	private Integer timingTouch;

	@EntityFieldDescribe("运行间隔,分钟.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xtimingInterval")
	@Index(name = TABLE + "_timingInterval")
	private Integer timingInterval;

	@EntityFieldDescribe("可使用的人.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_availablePersonList", joinIndex = @Index(name = TABLE + "_availablePersonList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailablePersonList")
	@ElementIndex(name = TABLE + "_availablePersonList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> availablePersonList;

	@EntityFieldDescribe("可使用的身份.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_availableIdentityList", joinIndex = @Index(name = TABLE + "_availableIdentityList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailableIdentityList")
	@ElementIndex(name = TABLE + "_availableIdentityList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> availableIdentityList;

	@EntityFieldDescribe("在指定启动时候,允许新建Work的部门.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_availableDepartmentList", joinIndex = @Index(name = TABLE + "_availableDepartmentList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailableDepartmentList")
	@ElementIndex(name = TABLE + "_availableDepartmentList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> availableDepartmentList;

	@EntityFieldDescribe("在指定启动时候,允许新建Work的公司.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_availableCompanyList", joinIndex = @Index(name = TABLE + "_availableCompanyList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailableCompanyList")
	@ElementIndex(name = TABLE + "_availableCompanyList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> availableCompanyList;

	@EntityFieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = "xicon")
	@CheckPersist(allowEmpty = true)
	private String icon;

	@EntityFieldDescribe("应用管理者。")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_controllerList", joinIndex = @Index(name = TABLE + "_controllerList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xcontrollerList")
	@ElementIndex(name = TABLE + "_controllerList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList;

	@EntityFieldDescribe("应用的创建者。")
	@CheckPersist(allowEmpty = false)
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorPerson")
	@Index(name = TABLE + "_creatorPerson")
	private String creatorPerson;

	@EntityFieldDescribe("应用的最后修改时间。")
	@CheckPersist(allowEmpty = false)
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	private Date lastUpdateTime;

	@EntityFieldDescribe("应用的最后修改者")
	@CheckPersist(allowEmpty = false)
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xlastUpdatePerson")
	@Index(name = TABLE + "_lastUpdatePerson")
	private String lastUpdatePerson;

	@EntityFieldDescribe("显示布局.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xlayout")
	@CheckPersist(allowEmpty = true)
	private String layout;

	@EntityFieldDescribe("方案文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xdata")
	@CheckPersist(allowEmpty = true)
	private String data;

	@EntityFieldDescribe("gird生成后运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterGridScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterGridScriptText;

	@EntityFieldDescribe("gropuGird生成后运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterGroupGridScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterGroupGridScriptText;

	@EntityFieldDescribe("calculateGrid生成后运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterCalculateGridScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterCalculateGridScriptText;

	@EntityFieldDescribe("是否前端可见.")
	@Column(name = "xDisplay")
	@Index(name = TABLE + "_display")
	private Boolean display;

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

	public List<String> getAvailableDepartmentList() {
		return availableDepartmentList;
	}

	public void setAvailableDepartmentList(List<String> availableDepartmentList) {
		this.availableDepartmentList = availableDepartmentList;
	}

	public List<String> getAvailableCompanyList() {
		return availableCompanyList;
	}

	public void setAvailableCompanyList(List<String> availableCompanyList) {
		this.availableCompanyList = availableCompanyList;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getAfterGridScriptText() {
		return afterGridScriptText;
	}

	public void setAfterGridScriptText(String afterGridScriptText) {
		this.afterGridScriptText = afterGridScriptText;
	}

	public String getAfterGroupGridScriptText() {
		return afterGroupGridScriptText;
	}

	public void setAfterGroupGridScriptText(String afterGroupGridScriptText) {
		this.afterGroupGridScriptText = afterGroupGridScriptText;
	}

	public String getAfterCalculateGridScriptText() {
		return afterCalculateGridScriptText;
	}

	public void setAfterCalculateGridScriptText(String afterCalculateGridScriptText) {
		this.afterCalculateGridScriptText = afterCalculateGridScriptText;
	}

	

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Boolean getTimingEnable() {
		return timingEnable;
	}

	public void setTimingEnable(Boolean timingEnable) {
		this.timingEnable = timingEnable;
	}

	public Integer getTimingTouch() {
		return timingTouch;
	}

	public void setTimingTouch(Integer timingTouch) {
		this.timingTouch = timingTouch;
	}

	public Integer getTimingInterval() {
		return timingInterval;
	}

	public void setTimingInterval(Integer timingInterval) {
		this.timingInterval = timingInterval;
	}

	public List<String> getAvailableIdentityList() {
		return availableIdentityList;
	}

	public void setAvailableIdentityList(List<String> availableIdentityList) {
		this.availableIdentityList = availableIdentityList;
	}

	public List<String> getAvailablePersonList() {
		return availablePersonList;
	}

	public void setAvailablePersonList(List<String> availablePersonList) {
		this.availablePersonList = availablePersonList;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Boolean getDisplay() {
		return display;
	}

	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
