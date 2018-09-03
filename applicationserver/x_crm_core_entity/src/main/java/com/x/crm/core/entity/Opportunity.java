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

/**
 * 商机，基本信息
 * 
 * @author WUSHUTAO
 */

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceDetailMobile.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceDetailMobile.table + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN, JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))name = PersistenceProperties.Opportunity.OpportunityBaseInfo.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Opportunity extends SliceJpaObject {
	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Opportunity.OpportunityBaseInfo.table;

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

	//客户名称
	//选择现有
	@FieldDescribe("客户名称")
	@Column(name = "xcustomerid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_customerid")
	@CheckPersist(allowEmpty = false)
	private String customerid;

	//销售流程名称
	@FieldDescribe("销售流程名称")
	@Column(name = "xsaleactionid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_saleactionid")
	@CheckPersist(allowEmpty = false)
	private String saleactionid;

	//商机名称
	@FieldDescribe("商机名称")
	@Column(name = "xopportunityname", length = JpaObject.length_255B)
	@Index(name = TABLE + "_opportunityname")
	@CheckPersist(allowEmpty = false)
	private String opportunityname;

	//预计成交时间
	@FieldDescribe("预计成交时间")
	@Column(name = "xexpecteddealtime", length = JpaObject.length_255B)
	@Index(name = TABLE + "_expecteddealtime")
	@CheckPersist(allowEmpty = false)
	private String expecteddealtime;

	//商机金额(元)
	@FieldDescribe("商机金额")
	@Column(name = "xexpectedsalesamount", length = JpaObject.length_255B)
	@Index(name = TABLE + "_expectedsalesamount")
	@CheckPersist(allowEmpty = false)
	private String expectedsalesamount;

	//备注
	@FieldDescribe("备注")
	@Column(name = "xremark", length = JpaObject.length_255B)
	@Index(name = TABLE + "_remark")
	@CheckPersist(allowEmpty = true)
	private String remark;

	//发现日期
	@FieldDescribe("发现日期")
	@Column(name = "xuddate1", length = JpaObject.length_255B)
	@Index(name = TABLE + "_uddate1")
	@CheckPersist(allowEmpty = true)
	private String uddate1;

	//负责人
	@FieldDescribe("负责人")
	@Column(name = "xbelongerid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_belongerid")
	@CheckPersist(allowEmpty = false)
	private String belongerid;

	//需求说明
	@FieldDescribe("需求说明")
	@Column(name = "xudmtext1", length = JpaObject.length_255B)
	@Index(name = TABLE + "_udmtext1")
	@CheckPersist(allowEmpty = true)
	private String udmtext1;

	//描述
	@FieldDescribe("描述")
	@Column(name = "xudmtext2", length = JpaObject.length_255B)
	@Index(name = TABLE + "_udmtext2")
	@CheckPersist(allowEmpty = true)
	private String udmtext2;

	//机会来源
	@FieldDescribe("机会来源")
	@Column(name = "xudssel1", length = JpaObject.length_255B)
	@Index(name = TABLE + "_udssel1")
	@CheckPersist(allowEmpty = true)
	private String udssel1;

	//机会类型
	@FieldDescribe("机会类型")
	@Column(name = "xudssel2", length = JpaObject.length_255B)
	@Index(name = TABLE + "_udssel2")
	@CheckPersist(allowEmpty = true)
	private String udssel2;

	//销售阶段
	@FieldDescribe("销售阶段")
	@Column(name = "xudssel3", length = JpaObject.length_255B)
	@Index(name = TABLE + "_udssel3")
	@CheckPersist(allowEmpty = true)
	private String udssel3;

	//------------状态信息----------------
	//商机状态
	@FieldDescribe("商机状态")
	@Column(name = "xstatus", length = JpaObject.length_128B)
	@Index(name = TABLE + "_status")
	@CheckPersist(allowEmpty = true)
	private String status;

	//售前阶段名称
	@FieldDescribe("售前阶段名称")
	@Column(name = "xpresalesstagename", length = JpaObject.length_128B)
	@Index(name = TABLE + "_presalesstagename")
	@CheckPersist(allowEmpty = true)
	private String preSalesStageName;

	//售后阶段名称
	@FieldDescribe("售后阶段名称")
	@Column(name = "xaftersalesstagename", length = JpaObject.length_128B)
	@Index(name = TABLE + "_aftersalesstagename")
	@CheckPersist(allowEmpty = true)
	private String afterSalesStageName;

	//------------状态信息----------------

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getSaleactionid() {
		return saleactionid;
	}

	public void setSaleactionid(String saleactionid) {
		this.saleactionid = saleactionid;
	}

	public String getOpportunityname() {
		return opportunityname;
	}

	public void setOpportunityname(String opportunityname) {
		this.opportunityname = opportunityname;
	}

	public String getExpecteddealtime() {
		return expecteddealtime;
	}

	public void setExpecteddealtime(String expecteddealtime) {
		this.expecteddealtime = expecteddealtime;
	}

	public String getExpectedsalesamount() {
		return expectedsalesamount;
	}

	public void setExpectedsalesamount(String expectedsalesamount) {
		this.expectedsalesamount = expectedsalesamount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUddate1() {
		return uddate1;
	}

	public void setUddate1(String uddate1) {
		this.uddate1 = uddate1;
	}

	public String getBelongerid() {
		return belongerid;
	}

	public void setBelongerid(String belongerid) {
		this.belongerid = belongerid;
	}

	public String getUdmtext1() {
		return udmtext1;
	}

	public void setUdmtext1(String udmtext1) {
		this.udmtext1 = udmtext1;
	}

	public String getUdmtext2() {
		return udmtext2;
	}

	public void setUdmtext2(String udmtext2) {
		this.udmtext2 = udmtext2;
	}

	public String getUdssel1() {
		return udssel1;
	}

	public void setUdssel1(String udssel1) {
		this.udssel1 = udssel1;
	}

	public String getUdssel2() {
		return udssel2;
	}

	public void setUdssel2(String udssel2) {
		this.udssel2 = udssel2;
	}

	public String getUdssel3() {
		return udssel3;
	}

	public void setUdssel3(String udssel3) {
		this.udssel3 = udssel3;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPreSalesStageName() {
		return preSalesStageName;
	}

	public void setPreSalesStageName(String preSalesStageName) {
		this.preSalesStageName = preSalesStageName;
	}

	public String getAfterSalesStageName() {
		return afterSalesStageName;
	}

	public void setAfterSalesStageName(String afterSalesStageName) {
		this.afterSalesStageName = afterSalesStageName;
	}

}
