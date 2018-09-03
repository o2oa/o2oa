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
@Table(name = PersistenceProperties.AttendanceDetailMobile.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceDetailMobile.table + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN, JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))name = PersistenceProperties.Clue.ClueBaseInfo.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Clue extends SliceJpaObject {
	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Clue.ClueBaseInfo.table;

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

	//销售线索的销售人员姓名
	@FieldDescribe("销售线索的销售人员姓名")
	@Column(name = "xsalescluename", length = JpaObject.length_255B)
	@Index(name = TABLE + "_salescluename")
	@CheckPersist(allowEmpty = false)
	private String salescluename;

	//公司
	@FieldDescribe("公司")
	@Column(name = "xcompany", length = JpaObject.length_255B)
	@Index(name = TABLE + "_company")
	@CheckPersist(allowEmpty = true)
	private String company;

	//销售线索详情
	@FieldDescribe("销售线索详情")
	@Column(name = "xremark", length = JpaObject.length_255B)
	@Index(name = TABLE + "_remark")
	@CheckPersist(allowEmpty = true)
	private String remark;

	//销售线索来源
	//select包括：搜索引擎，客户介绍，会议，广告，电话，网站，其他
	@FieldDescribe("销售线索来源")
	@Column(name = "xsource", length = JpaObject.length_255B)
	@Index(name = TABLE + "_source")
	@CheckPersist(allowEmpty = false)
	private String source;

	//市场活动名称
	//选择现有进行关联
	@FieldDescribe("市场活动名称")
	@Column(name = "xmarketingeventid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_marketingeventid")
	@CheckPersist(allowEmpty = true)
	private String marketingeventid;

	//线索池
	//select 动态列出线索池
	@FieldDescribe("线索池")
	@Column(name = "xsalescluepoolid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_salescluepoolid")
	@CheckPersist(allowEmpty = true)
	private String salescluepoolid;

	//部门
	@FieldDescribe("部门")
	@Column(name = "xdepartment", length = JpaObject.length_255B)
	@Index(name = TABLE + "_department")
	@CheckPersist(allowEmpty = true)
	private String department;

	//职务
	@FieldDescribe("职务")
	@Column(name = "xposition", length = JpaObject.length_255B)
	@Index(name = TABLE + "_position")
	@CheckPersist(allowEmpty = true)
	private String position;

	//电话(不知道为什么分享逍客定义字段为联系方式)
	@FieldDescribe("电话")
	@Column(name = "xcontactway", length = JpaObject.length_255B)
	@Index(name = TABLE + "_contactway")
	@CheckPersist(allowEmpty = true)
	private String contactway;

	//手机
	@FieldDescribe("手机")
	@Column(name = "xmobile", length = JpaObject.length_255B)
	@Index(name = TABLE + "_mobile")
	@CheckPersist(allowEmpty = false)
	private String mobile;

	//网址
	@FieldDescribe("网址")
	@Column(name = "xurl", length = JpaObject.length_255B)
	@Index(name = TABLE + "_url")
	@CheckPersist(allowEmpty = false)
	private String url;

	//邮件
	@FieldDescribe("邮件")
	@Column(name = "xemail", length = JpaObject.length_255B)
	@Index(name = TABLE + "_email")
	@CheckPersist(allowEmpty = false)
	private String email;

	//地址
	@FieldDescribe("地址")
	@Column(name = "xaddress", length = JpaObject.length_255B)
	@Index(name = TABLE + "_address")
	@CheckPersist(allowEmpty = false)
	private String address;

	//状态
	//未分配:1,待处理:2,已转换:3,已处理:4,无效:5,已作废:99
	@FieldDescribe("状态")
	@Column(name = "xstate", length = JpaObject.length_255B)
	@Index(name = TABLE + "_state")
	@CheckPersist(allowEmpty = false)
	private String state;

	//如果线索转化为客户，那么记录客户id
	@FieldDescribe("关联客户的id")
	@Column(name = "xcustomerid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_customerid")
	@CheckPersist(allowEmpty = false)
	private String customerid;

	public String getSalescluename() {
		return salescluename;
	}

	public void setSalescluename(String salescluename) {
		this.salescluename = salescluename;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMarketingeventid() {
		return marketingeventid;
	}

	public void setMarketingeventid(String marketingeventid) {
		this.marketingeventid = marketingeventid;
	}

	public String getSalescluepoolid() {
		return salescluepoolid;
	}

	public void setSalescluepoolid(String salescluepoolid) {
		this.salescluepoolid = salescluepoolid;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getContactway() {
		return contactway;
	}

	public void setContactway(String contactway) {
		this.contactway = contactway;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	
	
}
