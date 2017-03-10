package com.x.component.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Component.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Component extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;

	private static final String TABLE = PersistenceProperties.Component.table;

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

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = JpaObject.length_id, name = JpaObject.IDCOLUMN)
	private String id = createId();

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
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() throws Exception{
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("名称,不可重名.")
	@Column(length = AbstractPersistenceProperties.component_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists = {
			/* 验证不可重名 */
			@CitationNotExist(fields = "name", type = Component.class) })
	private String name;

	@EntityFieldDescribe("标题.")
	@Column(length = AbstractPersistenceProperties.component_name_length, name = "xtitle")
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String title;

	@EntityFieldDescribe("是否是可见的应用.")
	@Column(name = "xvisible")
	@CheckPersist(allowEmpty = true)
	private Boolean visible;

	@EntityFieldDescribe("应用排序.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xorder")
	private Integer order;

	@EntityFieldDescribe("应用路径.")
	@Column(length = JpaObject.length_255B, name = "xpath")
	@CheckPersist(allowEmpty = false)
	private String path;

	@EntityFieldDescribe("图标位置.")
	@Column(length = JpaObject.length_255B, name = "iconPath")
	@CheckPersist(allowEmpty = false)
	private String iconPath;

	@EntityFieldDescribe("部件名称.")
	@Column(length = AbstractPersistenceProperties.component_name_length, name = "xwidgetName")
	@CheckPersist(allowEmpty = true)
	private String widgetName;

	@EntityFieldDescribe("部件标题.")
	@Column(length = AbstractPersistenceProperties.component_name_length, name = "xwidgetTitle")
	@CheckPersist(allowEmpty = true)
	private String widgetTitle;

	@EntityFieldDescribe("部件图标位置.")
	@Column(length = JpaObject.length_255B, name = "xwidgetIconPath")
	@CheckPersist(allowEmpty = true)
	private String widgetIconPath;

	@EntityFieldDescribe("是否自动启动.")
	@Column(name = "xwidgetStart")
	@CheckPersist(allowEmpty = true)
	private Boolean widgetStart;

	@EntityFieldDescribe("是否在应用列表中可见.")
	@Column(name = "xwidgetVisible")
	@CheckPersist(allowEmpty = true)
	private Boolean widgetVisible;

	@EntityFieldDescribe("可访问人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_allowList", joinIndex = @Index(name = TABLE + "_allowList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xallowList")
	@ElementIndex(name = TABLE + "_allowList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> allowList = new ArrayList<String>();

	@EntityFieldDescribe("拒绝访问人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_denyList", joinIndex = @Index(name = TABLE + "_denyList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xdenyList")
	@ElementIndex(name = TABLE + "_denyList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> denyList = new ArrayList<String>();

	@EntityFieldDescribe("管理人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_controllerList", joinIndex = @Index(name = TABLE + "_controllerList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xcontrollerList")
	@ElementIndex(name = TABLE + "_controllerList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAllowList() {
		return allowList;
	}

	public void setAllowList(List<String> allowList) {
		this.allowList = allowList;
	}

	public List<String> getDenyList() {
		return denyList;
	}

	public void setDenyList(List<String> denyList) {
		this.denyList = denyList;
	}

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getWidgetName() {
		return widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public String getWidgetTitle() {
		return widgetTitle;
	}

	public void setWidgetTitle(String widgetTitle) {
		this.widgetTitle = widgetTitle;
	}

	public String getWidgetIconPath() {
		return widgetIconPath;
	}

	public void setWidgetIconPath(String widgetIconPath) {
		this.widgetIconPath = widgetIconPath;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Boolean getWidgetStart() {
		return widgetStart;
	}

	public void setWidgetStart(Boolean widgetStart) {
		this.widgetStart = widgetStart;
	}

	public Boolean getWidgetVisible() {
		return widgetVisible;
	}

	public void setWidgetVisible(Boolean widgetVisible) {
		this.widgetVisible = widgetVisible;
	}

}