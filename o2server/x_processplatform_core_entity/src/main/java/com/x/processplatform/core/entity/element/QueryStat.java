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
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.QueryStat.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.QueryStat.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class QueryStat extends SliceJpaObject {

	private static final long serialVersionUID = -1926258273469924948L;

	private static final String TABLE = PersistenceProperties.Element.QueryStat.table;

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

	// public static String[] FLA GS = new String[] { "id" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = QueryStat.class, equals = @Equal(field = "application", property = "application")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@FieldDescribe("别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = QueryStat.class, equals = @Equal(field = "application", property = "application")))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("所属应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Application.class) })
	private String application;

	public static final String queryView_FIELDNAME = "queryView";
	@FieldDescribe("所关联的queryView.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + queryView_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + queryView_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = QueryView.class) })
	private String queryView;

	public static final String queryViewName_FIELDNAME = "queryViewName";
	@FieldDescribe("所关联的queryView的name.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + queryViewName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + queryViewName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String queryViewName;

	public static final String queryViewAlias_FIELDNAME = "queryViewAlias";
	@FieldDescribe("所关联的queryView的alias.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + queryViewAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + queryViewAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String queryViewAlias;

	public static final String timingEnable_FIELDNAME = "timingEnable";
	@FieldDescribe("是否是定时任务.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + timingEnable_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timingEnable_FIELDNAME)
	private Boolean timingEnable;

	public static final String timingInterval_FIELDNAME = "timingInterval";
	@FieldDescribe("运行间隔,分钟.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + timingInterval_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timingInterval_FIELDNAME)
	private Integer timingInterval;

	public static final String availablePersonList_FIELDNAME = "availablePersonList";
	@FieldDescribe("可使用的人.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availablePersonList;

	public static final String availableIdentityList_FIELDNAME = "availableIdentityList";
	@FieldDescribe("可使用的身份.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availableIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableIdentityList;

	public static final String availableUnitList_FIELDNAME = "availableUnitList";
	@FieldDescribe("在指定启动时候,允许新建的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableUnitList;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon;

	public static final String controllerList_FIELDNAME = "controllerList";
	@FieldDescribe("应用管理者。")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + controllerList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + controllerList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("应用的创建者。")
	@CheckPersist(allowEmpty = false)
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	private String creatorPerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("应用的最后修改时间。")
	@CheckPersist(allowEmpty = false)
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	private Date lastUpdateTime;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("应用的最后修改者")
	@CheckPersist(allowEmpty = false)
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdatePerson_FIELDNAME)
	private String lastUpdatePerson;

	public static final String layout_FIELDNAME = "layout";
	@FieldDescribe("显示布局.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + layout_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String layout;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("方案文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String result_FIELDNAME = "result";
	@FieldDescribe("缓存结果.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + result_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String result;

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

	public List<String> getAvailableUnitList() {
		return availableUnitList;
	}

	public void setAvailableUnitList(List<String> availableUnitList) {
		this.availableUnitList = availableUnitList;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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