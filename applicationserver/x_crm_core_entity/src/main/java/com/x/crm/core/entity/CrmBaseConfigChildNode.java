package com.x.crm.core.entity;

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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceDetailMobile.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceDetailMobile.table + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN, JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))name = PersistenceProperties.CrmConfig.CrmBaseConfigChildNode.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CrmBaseConfigChildNode extends SliceJpaObject {

	private static final long serialVersionUID = -1407237029647312852L;

	private static final String TABLE = PersistenceProperties.CrmConfig.CrmBaseConfigChildNode.table;

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(name = "xid", length = JpaObject.length_id)

	//private String id = createId();
	private String id = createId();

	@FieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@FieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@FieldDescribe("列表序号, 由创建时间以及ID组成.在保存时自动生成.")
	@Column(name = "xsequence", length = AbstractPersistenceProperties.length_sequence)
	@Index(name = TABLE + "_sequence")
	private String seq   uence;

	/**
	 * 获取记录ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置记录ID
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

	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersistDDDDDDDDDD 回调方法。
	 */
	@PrePersistDDDDDDDDDD
	public void prePersist() throws Exception {
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		// 序列号信息的组成，与排序有关
		if (null == this.sequence) {
			this.sequence = StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId());
		}
		this.onPersist();
	}

	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() throws Exception {
		this.updateTime = new Date();
		this.onPersist();
	}

	public void onPersist() throws Exception {
	}
	/*=============================以上为 JpaObject 默认字段============================================*/

	/*============================以下为具体不同的业务及数据表字段要求====================================*/
	@FieldDescribe("系统配置名称")
	@Column(name = "xconfigname", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String configname;

	@FieldDescribe("配置值")
	@Column(name = "xconfigvalue", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String configvalue;

	@FieldDescribe("值类型: identity | number | date | text")
	@Column(name = "xvaluetype", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String valuetype;

	@FieldDescribe("排序号")
	@Column(name = "xordernumber")
	@CheckPersist(allowEmpty = true)
	private Integer ordernumber = 1;

	@FieldDescribe("备注说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("所属业务")
	@Column(name = "xbaseconfigtype", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String baseconfigtype;

	@FieldDescribe("关联的上一级配置id")
	@Column(name = "xparentconfigid", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String parentconfigid;

	@FieldDescribe("配置级别，如果子级配置，那么在上一的level上加1")
	@Column(name = "xconfiglevel")
	@CheckPersist(allowEmpty = true)
	private Integer configlevel = 0;

	//@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "CrmBaseConfig")
	//private Set<CrmBaseConfig> childNode = new HashSet<CrmBaseConfig>(0);

	public String getConfigname() {
		return configname;
	}

	public void setConfigname(String configname) {
		this.configname = configname;
	}

	public String getConfigvalue() {
		return configvalue;
	}

	public void setConfigvalue(String configvalue) {
		this.configvalue = configvalue;
	}

	public String getValuetype() {
		return valuetype;
	}

	public void setValuetype(String valuetype) {
		this.valuetype = valuetype;
	}

	public Integer getOrdernumber() {
		return ordernumber;
	}

	public void setOrdernumber(Integer ordernumber) {
		this.ordernumber = ordernumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBaseconfigtype() {
		return baseconfigtype;
	}

	public void setBaseconfigtype(String baseconfigtype) {
		this.baseconfigtype = baseconfigtype;
	}

	public String getParentconfigid() {
		return parentconfigid;
	}

	public void setParentconfigid(String parentconfigid) {
		this.parentconfigid = parentconfigid;
	}

	public Integer getConfiglevel() {
		return configlevel;
	}

	public void setConfiglevel(Integer configlevel) {
		this.configlevel = configlevel;
	}

//	public Set<CrmBaseConfig> getChildNode() {
//		return childNode;
//	}
//
//	public void setChildNode(Set<CrmBaseConfig> childNode) {
//		this.childNode = childNode;
//	}

}
