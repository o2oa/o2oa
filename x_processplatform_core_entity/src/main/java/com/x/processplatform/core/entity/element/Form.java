package com.x.processplatform.core.entity.element;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Form.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Form extends SliceJpaObject {

	private static final long serialVersionUID = 3263767038182121907L;
	private static final String TABLE = PersistenceProperties.Element.Form.table;

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
	@CheckRemove(citationNotExists = {
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Agent.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Begin.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Cancel.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Choice.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Condition.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Delay.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Embed.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = End.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Invoke.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Manual.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Merge.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Message.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Parallel.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Service.class, fields = "form"),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Split.class, fields = "form") })
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() {
		// this.editor = StringUtils.trimToEmpty(this.editor);
	}

	/* 更新运行方法 */

	public static String[] FLAGS = new String[] { "id", "alias" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public String getDataOrMobileData() {
		if (StringUtils.isNotEmpty(this.getData())) {
			return this.getData();
		} else if (StringUtils.isNotEmpty(this.getMobileData())) {
			return this.getMobileData();
		}
		return null;
	}

	public String getMobileDataOrData() {
		if (StringUtils.isNotEmpty(this.getMobileData())) {
			return this.getMobileData();
		} else if (StringUtils.isNotEmpty(this.getData())) {
			return this.getData();
		}
		return null;
	}

	@EntityFieldDescribe("名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xname")
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Form.class, equals = @Equal(property = "application", field = "application")))
	private String name;

	@EntityFieldDescribe("表单别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xalias")
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Form.class, equals = @Equal(property = "application", field = "application")))
	private String alias;

	@EntityFieldDescribe("描述.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xdescription")
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("表单所属应用.")
	@Column(length = JpaObject.length_id, name = "xapplication")
	@Index(name = TABLE + "_application")
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Application.class) })
	private String application;

	// @EntityFieldDescribe("最后的编辑者.")
	// @Column(length = AbstractPersistenceProperties.organization_name_length,
	// name = "xeditor")
	// @CheckPersist(allowEmpty = true)
	// private String editor;

	@EntityFieldDescribe("最后的编辑者.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xlastUpdatePerson")
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	@EntityFieldDescribe("最后的编辑时间.")
	@Column(name = "xlastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	@EntityFieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = "xicon")
	@CheckPersist(allowEmpty = true)
	private String icon;

	@EntityFieldDescribe("文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	// @Persistent(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xdata")
	@CheckPersist(allowEmpty = true)
	private String data;

	@EntityFieldDescribe("移动端文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = "xmobileData")
	@CheckPersist(allowEmpty = true)
	private String mobileData;

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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getMobileData() {
		return mobileData;
	}

	public void setMobileData(String mobileData) {
		this.mobileData = mobileData;
	}

	// public String getEditor() {
	// return editor;
	// }
	//
	// public void setEditor(String editor) {
	// this.editor = editor;
	// }

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}