package com.x.processplatform.core.entity.element;

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
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.QueryStat.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class QueryStat extends SliceJpaObject {

	private static final long serialVersionUID = -1926258273469924948L;

	private static final String TABLE = PersistenceProperties.Element.QueryStat.table;

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

	public static String[] FLAGS = new String[] { "id" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@EntityFieldDescribe("名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, simplyString = false, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = QueryStat.class, equals = @Equal(field = "application", property = "application")))
	private String name;

	@EntityFieldDescribe("别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xalias")
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = QueryStat.class, equals = @Equal(field = "application", property = "application")))
	private String alias;

	@EntityFieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = "xdescription")
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("所属应用.")
	@Column(length = JpaObject.length_id, name = "xapplication")
	@Index(name = TABLE + "_application")
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Application.class) })
	private String application;

	@EntityFieldDescribe("所关联的queryView.")
	@Column(length = JpaObject.length_id, name = "xqueryView")
	@Index(name = TABLE + "_queryView")
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = QueryView.class) })
	private String queryView;

	@EntityFieldDescribe("所关联的queryView的name.")
	@Column(length = JpaObject.length_255B, name = "xqueryViewName")
	@Index(name = TABLE + "_queryViewName")
	@CheckPersist(allowEmpty = true)
	private String queryViewName;

	@EntityFieldDescribe("所关联的queryView的alias.")
	@Column(length = JpaObject.length_255B, name = "xqueryViewAlias")
	@Index(name = TABLE + "_queryViewAlias")
	@CheckPersist(allowEmpty = true)
	private String queryViewAlias;

	@EntityFieldDescribe("是否是定时任务.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xtimingEnable")
	@Index(name = TABLE + "_timingEnable")
	private Boolean timingEnable;

	@EntityFieldDescribe("运行间隔,分钟.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xtimingInterval")
	@Index(name = TABLE + "_timingInterval")
	private Integer timingInterval;

	@EntityFieldDescribe("可使用的人.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_availablePersonList", joinIndex = @Index(name = TABLE + "_availablePersonList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailablePersonList")
	@ElementIndex(name = TABLE + "_availablePersonList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> availablePersonList;

	@EntityFieldDescribe("可使用的身份.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_availableIdentityList", joinIndex = @Index(name = TABLE + "_availableIdentityList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailableIdentityList")
	@ElementIndex(name = TABLE + "_availableIdentityList _element")
	@CheckPersist(allowEmpty = true)
	private List<String> availableIdentityList;

	@EntityFieldDescribe("在指定启动时候,允许新建Work的部门.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_availableDepartmentList", joinIndex = @Index(name = TABLE + "_availableDepartmentList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xavailableDepartmentList")
	@ElementIndex(name = TABLE + "_availableDepartmentList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> availableDepartmentList;

	@EntityFieldDescribe("在指定启动时候,允许新建Work的公司.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_availableCompanyList", joinIndex = @Index(name = TABLE + "_availableCompanyList_join"))
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
	@Column(length = JpaObject.length_10M, name = "xcalculate")
	@CheckPersist(allowEmpty = true)
	private String calculate;

	@EntityFieldDescribe("缓存结果.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xdata")
	@CheckPersist(allowEmpty = true)
	private String data;

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

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getQueryView() {
		return queryView;
	}

	public void setQueryView(String queryView) {
		this.queryView = queryView;
	}

	public String getQueryViewName() {
		return queryViewName;
	}

	public void setQueryViewName(String queryViewName) {
		this.queryViewName = queryViewName;
	}

	public String getQueryViewAlias() {
		return queryViewAlias;
	}

	public void setQueryViewAlias(String queryViewAlias) {
		this.queryViewAlias = queryViewAlias;
	}

	public Boolean getTimingEnable() {
		return timingEnable;
	}

	public void setTimingEnable(Boolean timingEnable) {
		this.timingEnable = timingEnable;
	}

	public Integer getTimingInterval() {
		return timingInterval;
	}

	public void setTimingInterval(Integer timingInterval) {
		this.timingInterval = timingInterval;
	}

	public List<String> getAvailablePersonList() {
		return availablePersonList;
	}

	public void setAvailablePersonList(List<String> availablePersonList) {
		this.availablePersonList = availablePersonList;
	}

	public List<String> getAvailableIdentityList() {
		return availableIdentityList;
	}

	public void setAvailableIdentityList(List<String> availableIdentityList) {
		this.availableIdentityList = availableIdentityList;
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

	public String getCalculate() {
		return calculate;
	}

	public void setCalculate(String calculate) {
		this.calculate = calculate;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	// public Boolean getDataSaveGrid() {
	// return dataSaveGrid;
	// }
	//
	// public void setDataSaveGrid(Boolean dataSaveGrid) {
	// this.dataSaveGrid = dataSaveGrid;
	// }
	//
	// public Boolean getDataSaveGroupGrid() {
	// return dataSaveGroupGrid;
	// }
	//
	// public void setDataSaveGroupGrid(Boolean dataSaveGroupGrid) {
	// this.dataSaveGroupGrid = dataSaveGroupGrid;
	// }
	//
	// public Boolean getDataSaveCalculateGrid() {
	// return dataSaveCalculateGrid;
	// }
	//
	// public void setDataSaveCalculateGrid(Boolean dataSaveCalculateGrid) {
	// this.dataSaveCalculateGrid = dataSaveCalculateGrid;
	// }
	//
	// public Boolean getDataSaveColumnGrid() {
	// return dataSaveColumnGrid;
	// }
	//
	// public void setDataSaveColumnGrid(Boolean dataSaveColumnGrid) {
	// this.dataSaveColumnGrid = dataSaveColumnGrid;
	// }

}