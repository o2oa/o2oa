package com.x.cms.core.entity;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.OrderColumn;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AppInfo.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AppInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AppInfo.table;

	/**
	 * 获取应用ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置应用ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置信息创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取信息更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 设置信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 获取信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * 设置信息记录排序号
	 */
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

	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
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

	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() throws Exception{
	}
	/*
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */
	@EntityFieldDescribe("应用名称")
	@Column(name = "xappName", length = JpaObject.length_96B)
	@CheckPersist(citationNotExists = {
			/* 验证不可重名 */
			@CitationNotExist(fields = "appName", type = AppInfo.class) }, allowEmpty = true)
	private String appName;

	@EntityFieldDescribe("应用别名")
	@Column(name = "xappAlias", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String appAlias;

	@EntityFieldDescribe("应用信息排序号")
	@Column(name = "xappInfoSeq", length = JpaObject.length_96B)
	private String appInfoSeq;

	@EntityFieldDescribe("应用信息说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("图标icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "xappIcon", length = JpaObject.length_32K)
	@CheckPersist(allowEmpty = true)
	private String appIcon;
	
	@EntityFieldDescribe("图标主色调.")
	@Column(name = "xiconColor", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String iconColor;

	@EntityFieldDescribe("备注信息")
	@Column(name = "xappMemo", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String appMemo;

	@EntityFieldDescribe("创建人，可能为空，如果由系统创建。")
	@Column(name = "xcreatorPerson", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorPerson")
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	@EntityFieldDescribe("创建人Identity，可能为空，如果由系统创建。")
	@Column(name = "xcreatorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorIdentity")
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	@EntityFieldDescribe("创建人部门，可能为空，如果由系统创建。")
	@Column(name = "xcreatorDepartment", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorDepartment")
	@CheckPersist(allowEmpty = true)
	private String creatorDepartment;

	@EntityFieldDescribe("创建人公司，可能为空，如果由系统创建。")
	@Column(name = "xcreatorCompany", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorCompany")
	@CheckPersist(allowEmpty = true)
	private String creatorCompany;

	@EntityFieldDescribe("分类列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_categoryList", joinIndex = @Index(name = TABLE + "_categoryList_join"))
	@ElementColumn(length = JpaObject.length_id)
	@ElementIndex(name = TABLE + "_categoryList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> categoryList;

	/**
	 * 获取应用名称
	 * 
	 * @return
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * 设置应用名称
	 * 
	 * @return
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * 获取应用别名
	 * 
	 * @return
	 */
	public String getAppAlias() {
		return appAlias;
	}

	/**
	 * 设置应用别名
	 * 
	 * @return
	 */
	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}

	/**
	 * 获取应用排序号
	 * 
	 * @return
	 */
	public String getAppInfoSeq() {
		return appInfoSeq;
	}

	/**
	 * 设置应用排序号
	 * 
	 * @return
	 */
	public void setAppInfoSeq(String appInfoSeq) {
		try {
			if (Integer.parseInt(appInfoSeq) < 10) {
				this.appInfoSeq = "0" + Integer.parseInt(appInfoSeq);
			} else {
				this.appInfoSeq = appInfoSeq;
			}
		} catch (Exception e) {
			this.appInfoSeq = "999";
		}
	}

	/**
	 * 获取应用说明
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置应用说明
	 * 
	 * @return
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取应用图标访问路径
	 * 
	 * @return
	 */
	public String getAppIcon() {
		return appIcon;
	}

	/**
	 * 设置应用图标访问路径
	 * 
	 * @return
	 */
	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

	/**
	 * 获取应用信息备注
	 * 
	 * @return
	 */
	public String getAppMemo() {
		return appMemo;
	}

	/**
	 * 设置应用信息备注
	 * 
	 * @return
	 */
	public void setAppMemo(String appMemo) {
		this.appMemo = appMemo;
	}

	public List<String> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<String> categoryList) {
		this.categoryList = categoryList;
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

	public String getIconColor() {
		return iconColor;
	}

	public void setIconColor(String iconColor) {
		this.iconColor = iconColor;
	}	
}