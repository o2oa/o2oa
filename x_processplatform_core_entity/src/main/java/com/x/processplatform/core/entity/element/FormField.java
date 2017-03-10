package com.x.processplatform.core.entity.element;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.FormField.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FormField extends SliceJpaObject {

	private static final long serialVersionUID = 7769671518373251078L;

	private static final String TABLE = PersistenceProperties.Element.FormField.table;

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
		// this.editor = StringUtils.trimToEmpty(this.editor);
	}

	/* 更新运行方法 */

	public static String[] FLAGS = new String[] { "id", "alias" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@EntityFieldDescribe("名称.")
	@Column(length = JpaObject.length_255B, name = "xname")
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String name;

	@EntityFieldDescribe("字段数据类型.")
	@Column(length = JpaObject.length_32B, name = "xdataType")
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String dataType;

	@EntityFieldDescribe("字段所属应用.")
	@Column(length = JpaObject.length_id, name = "xapplication")
	@Index(name = TABLE + "_application")
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Application.class) })
	private String application;

	@EntityFieldDescribe("字段所属表单.")
	@Column(length = JpaObject.length_id, name = "xfrom")
	@Index(name = TABLE + "_form")
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Form.class) })
	private String form;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

}