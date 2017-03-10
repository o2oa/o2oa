package com.x.cms.core.entity.element;

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
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Form.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Form extends SliceJpaObject {

	private static final long serialVersionUID = 3263767038182121907L;
	private static final String TABLE = PersistenceProperties.Element.Form.table;

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
	@Column(name = "xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@EntityFieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe("列表序号, 由创建时间以及ID组成.在保存时自动生成.")
	@Column(name = "xsequence", length = AbstractPersistenceProperties.length_sequence)
	@Index(name = TABLE + "_sequence")
	private String sequence;

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() throws Exception{
		this.editor = StringUtils.trimToEmpty(this.editor);
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("名称.")
	@Column(name = "xname", length = AbstractPersistenceProperties.processPlatform_name_length)
	private String name;

	@EntityFieldDescribe("表单别名.")
	@Column(name = "xalias", length = AbstractPersistenceProperties.processPlatform_name_length)
	private String alias;

	@EntityFieldDescribe("描述.")
	@Column(name = "xdescription", length = AbstractPersistenceProperties.processPlatform_name_length)
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("表单所属应用.")
	@Column(name = "xappId", length = JpaObject.length_id)
	@Index(name = TABLE + "_appId")
	@CheckPersist(citationExists = @CitationExist(type = AppInfo.class) , allowEmpty = true)
	private String appId;

	@EntityFieldDescribe("最后的编辑者.")
	@Column(name = "xeditor", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String editor;

	@EntityFieldDescribe("文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "xdata", length = JpaObject.length_10M)
	private String data;

	@EntityFieldDescribe("移动端文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "xmobileData", length = JpaObject.length_10M)
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

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMobileData() {
		return mobileData;
	}

	public void setMobileData(String mobileData) {
		this.mobileData = mobileData;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}