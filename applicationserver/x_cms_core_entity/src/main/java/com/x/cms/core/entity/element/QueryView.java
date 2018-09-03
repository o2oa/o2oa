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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.QueryView.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.QueryView.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class QueryView extends SliceJpaObject {

	private static final long serialVersionUID = -7520516033901189347L;

	private static final String TABLE = PersistenceProperties.Element.QueryView.table;

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

	// public static String[] FLA GS = new String[] { "name", "id" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@Flag
	@FieldDescribe("名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false)
	private String name;

	@FieldDescribe("别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xalias")
	@CheckPersist(allowEmpty = true)
	private String alias;

	@FieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = "xdescription")
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("所属应用.")
	@Column(length = JpaObject.length_id, name = "xappId")
	@Index(name = TABLE + "_appId")
	@CheckPersist(allowEmpty = false)
	private String appId;

	@FieldDescribe("所属应用.")
	@Column(length = JpaObject.length_id, name = "xappName")
	@Index(name = TABLE + "_appName")
	@CheckPersist(allowEmpty = false)
	private String appName;

	@FieldDescribe("是否是定时任务.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xtimingEnable")
	@Index(name = TABLE + "_timingEnable")
	private Boolean timingEnable;

	@FieldDescribe("上次运行后触发器触发过的次数,用于判断是否要运行,如果需要运行那么重置为0,避免由于时间往后调导致的不运行.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xtimingTouch")
	@Index(name = TABLE + "_timingTouch")
	private Integer timingTouch;

	@FieldDescribe("运行间隔,分钟.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xtimingInterval")
	@Index(name = TABLE + "_timingInterval")
	private Integer timingInterval;

	@FieldDescribe("可使用的人.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_availablePersonList", joinIndex = @Index(name = TABLE + "_availablePersonList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailablePersonList")
	@ElementIndex(name = TABLE + "_availablePersonList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> availablePersonList;

	@FieldDescribe("可使用的身份.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_availableIdentityList", joinIndex = @Index(name = TABLE + "_availableIdentityList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailableIdentityList")
	@ElementIndex(name = TABLE + "_availableIdentityList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> availableIdentityList;

	@FieldDescribe("允许访问的组织列表.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_availableUnitList", joinIndex = @Index(name = TABLE + "_availableUnitList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailableUnitList")
	@ElementIndex(name = TABLE + "_availableUnitList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> availableUnitList;

	@FieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = "xicon")
	@CheckPersist(allowEmpty = true)
	private String icon;

	@FieldDescribe("应用管理者。")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_controllerList", joinIndex = @Index(name = TABLE + "_controllerList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xcontrollerList")
	@ElementIndex(name = TABLE + "_controllerList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList;

	@FieldDescribe("应用的创建者。")
	@CheckPersist(allowEmpty = false)
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xcreatorPerson")
	@Index(name = TABLE + "_creatorPerson")
	private String creatorPerson;

	@FieldDescribe("应用的最后修改时间。")
	@CheckPersist(allowEmpty = false)
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	private Date lastUpdateTime;

	@FieldDescribe("应用的最后修改者")
	@CheckPersist(allowEmpty = false)
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xlastUpdatePerson")
	@Index(name = TABLE + "_lastUpdatePerson")
	private String lastUpdatePerson;

	@FieldDescribe("显示布局.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xlayout")
	@CheckPersist(allowEmpty = true)
	private String layout;

	@FieldDescribe("方案文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xdata")
	@CheckPersist(allowEmpty = true)
	private String data;

	@FieldDescribe("gird生成后运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterGridScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterGridScriptText;

	@FieldDescribe("gropuGird生成后运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterGroupGridScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterGroupGridScriptText;

	@FieldDescribe("calculateGrid生成后运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterCalculateGridScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterCalculateGridScriptText;

	public static final String display_FIELDNAME = "display";
	@FieldDescribe("是否前端可见.")
	@Column(name = ColumnNamePrefix + display_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + display_FIELDNAME)
	private Boolean display;

	@FieldDescribe("前台运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xcode")
	@CheckPersist(allowEmpty = true)
	private String code;

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

	public List<String> getAvailableUnitList() {
		return availableUnitList;
	}

	public void setAvailableUnitList(List<String> availableUnitList) {
		this.availableUnitList = availableUnitList;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
