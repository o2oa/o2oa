package com.x.cms.core.entity;

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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 内容管理应用目录分类信息
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.CatagoryInfo.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS)
public class CatagoryInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.CatagoryInfo.table;

	/**
	 * 获取分类ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置分类ID
	 */
	public void setId( String id ) {
		this.id = id;
	}	
	/**
	 * 获取分类信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置分类信息创建时间
	 */
	public void setCreateTime( Date createTime ) {
		this.createTime = createTime;
	}
	/**
	 * 获取分类信息更新时间
	 */
	public void setUpdateTime( Date updateTime ) {
		this.updateTime = updateTime;
	}
	/**
	 * 设置分类信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	/**
	 * 获取分类信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * 设置分类信息记录排序号
	 */
	public void setSequence( String sequence ) {
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
	
	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() {
		Date date = new Date();
		if ( null == this.createTime ) {
			this.createTime = date;
		}
		this.updateTime = date;
		if ( null == this.sequence ) {
			this.sequence = StringUtils.join( DateTools.compact( this.getCreateTime() ), this.getId() );
		}
		this.onPersist();
	}
	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() {
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() {
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe( "分类名称" )
	@Column( name="xcatagoryName", length = JpaObject.length_96B  )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String catagoryName;

	@EntityFieldDescribe( "分类所属应用ID" )
	@Column( name="xappId", length = JpaObject.length_id  )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String appId;
	
	@EntityFieldDescribe( "上级分类ID" )
	@Column( name="xparentId", length = JpaObject.length_id )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String parentId;
	
	@EntityFieldDescribe( "分类别名" )
	@Column( name="xcatagoryAlias", length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String catagoryAlias;
	
	@EntityFieldDescribe( "绑定的编辑表单模板ID" )
	@Column( name="xformId", length = JpaObject.length_id )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String formId;
	
	@EntityFieldDescribe( "绑定的编辑表单模板名称" )
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
	
	@EntityFieldDescribe( "分类信息排序号" )
	@Column(name="xcatagorySeq", length = JpaObject.length_96B )
	private String catagorySeq;

	@EntityFieldDescribe( "分类信息说明" )
	@Column( name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String description;
	
	@EntityFieldDescribe("图标icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name="xcatagoryIcon", length = JpaObject.length_32K)
	private String catagoryIcon;
	
	@EntityFieldDescribe( "备注信息" )
	@Column( name="xcatagoryMemo", length = JpaObject.length_255B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String catagoryMemo;
	
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
	
	/**
	 * 获取分类名称
	 * @return
	 */
	public String getCatagoryName() {
		return catagoryName;
	}
	/**
	 * 设置分类名称
	 * @param catagoryName
	 */
	public void setCatagoryName(String catagoryName) {
		this.catagoryName = catagoryName;
	}
	/**
	 * 获取分类所属应用ID
	 * @return
	 */
	public String getAppId() {
		return appId;
	}
	/**
	 * 设置分类所属应用ID
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/**
	 * 获取上级分类ID
	 * @return
	 */
	public String getParentId() {
		return parentId;
	}
	/**
	 * 设置上级分类ID
	 * @param parentId
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	/**
	 * 获取分类别名
	 * @return
	 */
	public String getCatagoryAlias() {
		return catagoryAlias;
	}
	/**
	 * 设置分类别名
	 * @param catagoryAlias
	 */
	public void setCatagoryAlias(String catagoryAlias) {
		this.catagoryAlias = catagoryAlias;
	}
	/**
	 * 获取分类信息排序号
	 * @return
	 */
	public String getCatagorySeq() {
		return catagorySeq;
	}
	/**
	 * 设置分类排序号
	 * @param catagorySeq
	 */
	public void setCatagorySeq(String catagorySeq) {
		try{
			if( Integer.parseInt(catagorySeq) < 10 ){
				this.catagorySeq = "0" + Integer.parseInt(catagorySeq);
			}else{
				this.catagorySeq = catagorySeq;
			}
		}catch(Exception e){
			this.catagorySeq = "999";
		}
	}
	/**
	 * 获取分类说明信息
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置分类说明信息
	 * @param description
	 */
	public void setDescription( String description ) {
		this.description = description;
	}
	/**
	 * 获取分类图标
	 * @return
	 */
	public String getCatagoryIcon() {
		return catagoryIcon;
	}
	/**
	 * 设置分类图标
	 * @param catagoryIcon
	 */
	public void setCatagoryIcon(String catagoryIcon) {
		this.catagoryIcon = catagoryIcon;
	}
	/**
	 * 获取分类备注
	 * @return
	 */
	public String getCatagoryMemo() {
		return catagoryMemo;
	}
	/**
	 * 设置分类备注
	 * @param catagoryMemo
	 */
	public void setCatagoryMemo(String catagoryMemo) {
		this.catagoryMemo = catagoryMemo;
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
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
	
}