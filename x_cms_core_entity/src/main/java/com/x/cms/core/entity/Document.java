package com.x.cms.core.entity;

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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Document.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Document extends SliceJpaObject {

	private static final long serialVersionUID = 7668822947307502058L;
	private static final String TABLE = PersistenceProperties.Document.table;

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

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime")
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
	private String sequence;

	/* 以上为 JpaObject 默认字段 */
	private void onPersist() {
		
	}

	/* 更新运行方法 */
	@EntityFieldDescribe("文档标题")
	@Column(name="xtitle", length = JpaObject.length_255B )
	@Index(name = TABLE + "_title")
	@CheckPersist(allowEmpty = true)
	private String title;
	
	@EntityFieldDescribe("应用ID")
	@Column(name="xappId", length = JpaObject.length_id )
	@Index(name = TABLE + "_appId")
	@CheckPersist(allowEmpty = true)
	private String appId;

	@EntityFieldDescribe("分类ID")
	@Column(name="xcatagoryId", length = AbstractPersistenceProperties.processPlatform_title_length )
	@Index(name = TABLE + "_catagoryId")
	@CheckPersist(allowEmpty = true)
	private String catagoryId;

	@EntityFieldDescribe( "绑定的表单模板ID" )
	@Column( name="xform", length = JpaObject.length_id )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String form;
	
	@EntityFieldDescribe( "绑定的表单模板名称" )
	@Column( name="xformName", length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String formName;
	
	@EntityFieldDescribe( "绑定的阅读表单模板ID" )
	@Column( name="xreadFormId", length = JpaObject.length_id )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String readFormId;
	
	@EntityFieldDescribe( "绑定的阅读表单模板名称" )
	@Column( name="xreadFormName", length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String readFormName;
	
	@EntityFieldDescribe("创建人，可能为空，如果由系统创建。")
	@Column(name="xcreatorPerson", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorPerson")
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	@EntityFieldDescribe("创建人Identity，可能为空，如果由系统创建。")
	@Column(name="xcreatorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorIdentity")
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	@EntityFieldDescribe("创建人部门，可能为空，如果由系统创建。")
	@Column(name="xcreatorDepartment", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorDepartment")
	@CheckPersist(allowEmpty = true)
	private String creatorDepartment;

	@EntityFieldDescribe("创建人公司，可能为空，如果由系统创建。")
	@Column(name="xcreatorCompany", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorCompany")
	@CheckPersist(allowEmpty = true)
	private String creatorCompany;

	@EntityFieldDescribe("文档状态: published | draft")
	@Column(name="xdocStatus", length = JpaObject.length_16B )
	@Index(name = TABLE + "_docStatus")
	@CheckPersist(allowEmpty = true)
	private String docStatus;
	
	@EntityFieldDescribe("文档发布时间")
	@Column(name="xpublishTime" )
	@Index(name = TABLE + "_publishTime")
	@CheckPersist( allowEmpty = true )
	private Date publishTime;

	@EntityFieldDescribe("附件列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join") )
	@ElementColumn(length = JpaObject.length_id)
	@ElementIndex(name = TABLE + "_attachmentList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCatagoryId() {
		return catagoryId;
	}

	public void setCatagoryId(String catagoryId) {
		this.catagoryId = catagoryId;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public String getCreatorDepartment() {
		return creatorDepartment;
	}

	public void setCreatorDepartment(String creatorDepartment) {
		this.creatorDepartment = creatorDepartment;
	}

	public String getCreatorCompany() {
		return creatorCompany;
	}

	public void setCreatorCompany(String creatorCompany) {
		this.creatorCompany = creatorCompany;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public List<String> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getReadFormId() {
		return readFormId;
	}

	public void setReadFormId(String readFormId) {
		this.readFormId = readFormId;
	}

	public String getReadFormName() {
		return readFormName;
	}

	public void setReadFormName(String readFormName) {
		this.readFormName = readFormName;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

}