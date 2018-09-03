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
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Script.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Script.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Script extends SliceJpaObject {

	private static final long serialVersionUID = 8877822163007579542L;
	private static final String TABLE = PersistenceProperties.Element.Script.table;

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

	public void onPersist() throws Exception {
	}

	/* 以上为 JpaObject 默认字段 */

	/* 更新运行方法 */

	@FieldDescribe("名称.")
	@Column(name = "xname", length = AbstractPersistenceProperties.processPlatform_name_length)
	@CheckPersist(citationNotExists =
	/* 检查在同一应用下不能重名 */
	@CitationNotExist(fields = { "name", "id", "alias" }, type = Script.class), allowEmpty = true)
	private String name;

	@FieldDescribe("别名.")
	@Column(name = "xalias", length = AbstractPersistenceProperties.processPlatform_name_length)
	@CheckPersist(citationNotExists =
	/* 检查在同一应用下不能重名 */
	@CitationNotExist(fields = { "name", "id", "alias" }, type = Script.class), allowEmpty = true)
	private String alias;

	@FieldDescribe("描述.")
	@Column(name = "xdescription", length = AbstractPersistenceProperties.processPlatform_name_length)
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("代码格式是否正确.")
	@Column(name = "xvalidated")
	@CheckPersist(allowEmpty = true)
	private Boolean validated;

	@FieldDescribe("脚本所属应用.")
	@Column(name = "xappId", length = JpaObject.length_id)
	@Index(name = TABLE + "_appId")
	@CheckPersist(citationExists = @CitationExist(type = AppInfo.class), allowEmpty = true)
	private String appId;

	@FieldDescribe("脚本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "xtext", length = JpaObject.length_1M)
	@CheckPersist(allowEmpty = true)
	private String text;

	@FieldDescribe("依赖的函数列表.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_dependScriptList", joinIndex = @Index(name = TABLE + "_dependScriptList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.processPlatform_name_length)
	@ElementIndex(name = TABLE + "_dependScriptList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> dependScriptList;

	@FieldDescribe("创建者.")
	@Column(name = "xcreatorPerson", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	@FieldDescribe("最后的编辑者.")
	@Column(name = "xlastUpdatePerson", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String lastUpdatePerson;

	@FieldDescribe("最后的编辑时间.")
	@Column(name = "xlastUpdateTime")
	@CheckPersist(allowEmpty = true)
	private Date lastUpdateTime;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public List<String> getDependScriptList() {
		return dependScriptList;
	}

	public void setDependScriptList(List<String> dependScriptList) {
		this.dependScriptList = dependScriptList;
	}

	public Boolean getValidated() {
		return validated;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
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

}
