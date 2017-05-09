package com.x.portal.core.entity;

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

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Script.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Script extends SliceJpaObject {

	private static final long serialVersionUID = 8877822163007579542L;
	private static final String TABLE = PersistenceProperties.Script.table;

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

	public static String[] FLAGS = new String[] { "id" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@EntityFieldDescribe("名称.")
	@Column(length = PersistenceProperties.portal_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 检查在同一应用下不能重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Script.class, equals = @Equal(property = "portal", field = "portal")))
	private String name;

	@EntityFieldDescribe("别名.")
	@Column(length = PersistenceProperties.portal_name_length, name = "xalias")
	@Index(name = TABLE + "_alias")
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 检查在同一应用下不能重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Script.class, equals = @Equal(property = "portal", field = "portal")))
	private String alias;

	@EntityFieldDescribe("描述.")
	@Column(length = PersistenceProperties.portal_name_length, name = "xdescription")
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("代码格式是否正确.")
	@Column(name = "xvalidated")
	@CheckPersist(allowEmpty = false)
	private Boolean validated;

	@EntityFieldDescribe("脚本所属应用.")
	@Column(length = JpaObject.length_id, name = "xportal")
	@Index(name = TABLE + "_portal")
	@CheckPersist(allowEmpty = false, citationExists = @CitationExist(type = Portal.class))
	private String portal;

	@EntityFieldDescribe("脚本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xtext")
	@CheckPersist(allowEmpty = true)
	private String text;

	@EntityFieldDescribe("依赖的函数列表.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_dependScriptList", joinIndex = @Index(name = TABLE + "_dependScriptList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = PersistenceProperties.portal_name_length, name = "xdependScriptList")
	@ElementIndex(name = TABLE + "_dependScriptList_element")
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Script.class, fields = { "name", "alias",
			"id" }, equals = @Equal(property = "portal", field = "portal")) })
	private List<String> dependScriptList;

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

	public String getPortal() {
		return portal;
	}

	public void setPortal(String portal) {
		this.portal = portal;
	}

}
