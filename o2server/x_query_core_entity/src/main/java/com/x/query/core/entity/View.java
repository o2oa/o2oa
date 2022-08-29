package com.x.query.core.entity;

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

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "View", description = "数据中心视图.")
@Entity
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.View.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.View.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class View extends SliceJpaObject {

	private static final long serialVersionUID = -7520516033901189347L;

	private static final String TABLE = PersistenceProperties.View.table;

	public static final Integer MAX_COUNT = 2000;

	public static final Integer DEFAULT_PAGESIZE = 20;

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
	@CheckRemove(citationNotExists = @CitationNotExist(type = Stat.class, fields = Stat.view_FIELDNAME))
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	@Override
	public void onPersist() throws Exception {

		if ((this.count == null) || (this.count < 1)) {
			this.count = MAX_COUNT;
		}

		if ((this.pageSize == null) || (this.pageSize < 1) || (this.pageSize > MAX_COUNT)) {
			this.pageSize = DEFAULT_PAGESIZE;
		}

	}

	public Integer getCount() {
		if ((this.count == null) || (this.count < 1)) {
			return MAX_COUNT;
		} else {
			return this.count;
		}
	}

	public Integer getPageSize() {
		if ((this.pageSize == null) || (this.pageSize < 1) || (this.pageSize > MAX_COUNT)) {
			return DEFAULT_PAGESIZE;
		} else {
			return this.pageSize;
		}
	}

	/* 更新运行方法 */

	/* Entity 默认字段结束 */

	/* 为了和前台对应 */
	public static final String TYPE_PROCESSPLATFORM = "process";

	public static final String TYPE_CMS = "cms";

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = View.class, equals = @Equal(field = "query", property = "query")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = View.class, equals = @Equal(field = "query", property = "query")))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String query_FIELDNAME = "query";
	@FieldDescribe("所属查询.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + query_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + query_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Query.class) })
	private String query;

	public static final String enableCache_FIELDNAME = "enableCache";
	@FieldDescribe("是否对结果进行缓存.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + enableCache_FIELDNAME)
	private Boolean enableCache;

	public static final String layout_FIELDNAME = "layout";
	@FieldDescribe("显示布局.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + layout_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String layout;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("访问方案.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String afterGridScriptText_FIELDNAME = "afterGridScriptText";
	@FieldDescribe("gird生成后运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterGridScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterGridScriptText;

	public static final String afterGroupGridScriptText_FIELDNAME = "afterGroupGridScriptText";
	@FieldDescribe("gropuGird生成后运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterGroupGridScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterGroupGridScriptText;

	public static final String afterCalculateGridScriptText_FIELDNAME = "afterCalculateGridScriptText";
	@FieldDescribe("calculateGrid生成后运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterCalculateGridScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterCalculateGridScriptText;

	public static final String code_FIELDNAME = "code";
	@FieldDescribe("前台运行脚本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + code_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String code;

	public static final String display_FIELDNAME = "display";
	@FieldDescribe("是否前端可见.")
	@Column(name = ColumnNamePrefix + display_FIELDNAME)
	private Boolean display;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("类型.")
	@Column(length = length_32B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false)
	private String type;

	public static final String cacheAccess_FIELDNAME = "cacheAccess";
	@FieldDescribe("是否缓存访问内容.")
	@Column(name = ColumnNamePrefix + cacheAccess_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean cacheAccess;

	public static final String availableIdentityList_FIELDNAME = "availableIdentityList";
	@FieldDescribe("允许使用的用户.")
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
	@FieldDescribe("允许使用的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableUnitList;

	public static final String availableGroupList_FIELDNAME = "availableGroupList";
	@FieldDescribe("允许使用的群组.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableGroupList;

	public static final String count_FIELDNAME = "count";
	@FieldDescribe("最大返回数量.")
	@Column(name = ColumnNamePrefix + count_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer count;

	public static final String pageSize_FIELDNAME = "pageSize";
	@FieldDescribe("分页单页数量.")
	@Column(name = ColumnNamePrefix + pageSize_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer pageSize;

	public static final String toolbar_FIELDNAME = "toolbar";
	@FieldDescribe("工具条.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + toolbar_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String toolbar;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,为空在最后")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getEnableCache() {
		return enableCache;
	}

	public void setEnableCache(Boolean enableCache) {
		this.enableCache = enableCache;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getDisplay() {
		return display;
	}

	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
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

	public Boolean getCacheAccess() {
		return cacheAccess;
	}

	public void setCacheAccess(Boolean cacheAccess) {
		this.cacheAccess = cacheAccess;
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

	public void setCount(Integer count) {
		this.count = count;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getToolbar() {
		return toolbar;
	}

	public void setToolbar(String toolbar) {
		this.toolbar = toolbar;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public List<String> getAvailableGroupList() {
		return availableGroupList;
	}

	public void setAvailableGroupList(List<String> availableGroupList) {
		this.availableGroupList = availableGroupList;
	}
}
