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
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 客户信息，基本信息
 * 
 * @author WUSHUTAO
 */

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Customer.CustomerBaseInfo.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CustomerBaseInfo extends SliceJpaObject {
	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Customer.CustomerBaseInfo.table;

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(name = "xid", length = JpaObject.length_id)
	//private String id = createId();
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
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
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

	private void onPersist() throws Exception {
	}
	/*=============================以上为 JpaObject 默认字段============================================*/

	/*============================以下为具体不同的业务及数据表字段要求====================================*/

	@EntityFieldDescribe("客户名称")
	@Column(name = "xcustomername", length = JpaObject.length_255B)
	@Index(name = TABLE + "_customername")
	@CheckPersist(allowEmpty = false)
	private String customername;

	@EntityFieldDescribe("客户类型")
	@Column(name = "xcustomertype", length = JpaObject.length_255B)
	@Index(name = TABLE + "_customertype")
	@CheckPersist(allowEmpty = true)
	private String customertype;

	@EntityFieldDescribe("客户级别")
	@Column(name = "xlevel", length = JpaObject.length_96B)
	@Index(name = TABLE + "_level")
	@CheckPersist(allowEmpty = true)
	private String level;

	@EntityFieldDescribe("客户来源")
	@Column(name = "xsource", length = JpaObject.length_255B)
	@Index(name = TABLE + "_source")
	@CheckPersist(allowEmpty = true)
	private String source;

	@EntityFieldDescribe("一级行业")
	@Column(name = "xindustryfirst", length = JpaObject.length_255B)
	@Index(name = TABLE + "_industryfirst")
	@CheckPersist(allowEmpty = true)
	private String industryfirst;

	@EntityFieldDescribe("二级行业")
	@Column(name = "xindustrysecond", length = JpaObject.length_255B)
	@Index(name = TABLE + "_industrysecond")
	@CheckPersist(allowEmpty = true)
	private String industrysecond;

	@EntityFieldDescribe("行业总称")
	@Column(name = "xindustry", length = JpaObject.length_255B)
	@Index(name = TABLE + "_industry")
	@CheckPersist(allowEmpty = true)
	private String industry;

	@EntityFieldDescribe("国家")
	@Column(name = "xcountry", length = JpaObject.length_255B)
	@Index(name = TABLE + "_country")
	@CheckPersist(allowEmpty = true)
	private String country;

	@EntityFieldDescribe("省份")
	@Column(name = "xprovince", length = JpaObject.length_255B)
	@Index(name = TABLE + "_province")
	@CheckPersist(allowEmpty = true)
	private String province;

	@EntityFieldDescribe("市")
	@Column(name = "xcity", length = JpaObject.length_255B)
	@Index(name = TABLE + "_city")
	@CheckPersist(allowEmpty = true)
	private String city;

	@EntityFieldDescribe("区或县")
	@Column(name = "xcounty", length = JpaObject.length_255B)
	@Index(name = TABLE + "_county")
	@CheckPersist(allowEmpty = true)
	private String county;

	@EntityFieldDescribe("地区全称")
	@Column(name = "xarea", length = JpaObject.length_255B)
	@Index(name = TABLE + "_area")
	@CheckPersist(allowEmpty = true)
	private String area;

	@EntityFieldDescribe("详细地址门牌号")
	@Column(name = "xhouseno", length = JpaObject.length_255B)
	@Index(name = TABLE + "_houseno")
	@CheckPersist(allowEmpty = true)
	private String houseno;

	@EntityFieldDescribe("经度")
	@Column(name = "xaddresslongitude", length = JpaObject.length_255B)
	@Index(name = TABLE + "_addresslongitude")
	@CheckPersist(allowEmpty = true)
	private String addresslongitude;

	@EntityFieldDescribe("纬度")
	@Column(name = "xaddresslatitude", length = JpaObject.length_255B)
	@Index(name = TABLE + "_addresslatitude")
	@CheckPersist(allowEmpty = true)
	private String addresslatitude;

	@EntityFieldDescribe("电话号码")
	@Column(name = "xtelno", length = JpaObject.length_128B)
	@Index(name = TABLE + "_telno")
	@CheckPersist(allowEmpty = true)
	private String telno;

	@EntityFieldDescribe("传真")
	@Column(name = "xcustomerfax", length = JpaObject.length_255B)
	@Index(name = TABLE + "_customerfax")
	@CheckPersist(allowEmpty = true)
	private String customerfax;

	@EntityFieldDescribe("网址")
	@Column(name = "xurl", length = JpaObject.length_255B)
	@Index(name = TABLE + "_url")
	@CheckPersist(allowEmpty = true)
	private String url;

	@EntityFieldDescribe("备注")
	@Column(name = "xremark", length = JpaObject.length_255B)
	@Index(name = TABLE + "_remark")
	@CheckPersist(allowEmpty = true)
	private String remark;

	@EntityFieldDescribe("EMail")
	@Column(name = "xemail", length = JpaObject.length_255B)
	@Index(name = TABLE + "_email")
	@CheckPersist(allowEmpty = true)
	private String email;

	@EntityFieldDescribe("状态")
	@Column(name = "xstate", length = JpaObject.length_255B)
	@Index(name = TABLE + "_state")
	@CheckPersist(allowEmpty = true)
	private String state;

	@EntityFieldDescribe("客户等级")
	@Column(name = "xcustomerrank", length = JpaObject.length_255B)
	@Index(name = TABLE + "_customerrank")
	@CheckPersist(allowEmpty = true)
	private String customerrank;

	@EntityFieldDescribe("QQ号码")
	@Column(name = "xqqno", length = JpaObject.length_128B)
	@Index(name = TABLE + "_qqno")
	@CheckPersist(allowEmpty = true)
	private String qqno;

	@EntityFieldDescribe("微信")
	@Column(name = "xwebchat", length = JpaObject.length_255B)
	@Index(name = TABLE + "_webchat")
	@CheckPersist(allowEmpty = true)
	private String webchat;

	@EntityFieldDescribe("客户序号")
	@Column(name = "xcustomersequence", length = JpaObject.length_255B)
	@Index(name = TABLE + "_customersequence")
	@CheckPersist(allowEmpty = true)
	private String customersequence;

	@EntityFieldDescribe("线索的创建者姓名")
	@Column(name = "xcreatorname", length = JpaObject.length_255B)
	@Index(name = TABLE + "_creatorname")
	@CheckPersist(allowEmpty = true)
	private String creatorname;

	@EntityFieldDescribe("线索的创建者身份")
	@Column(name = "xcreatoridentity", length = JpaObject.length_255B)
	@Index(name = TABLE + "_creatoridentity")
	@CheckPersist(allowEmpty = true)
	private String creatoridentity;

	@EntityFieldDescribe("线索负责人、所有者姓名")
	@Column(name = "xcustomerownername", length = JpaObject.length_255B)
	@Index(name = TABLE + "_customerownername")
	@CheckPersist(allowEmpty = true)
	private String customerownername;

	@EntityFieldDescribe("线索负责人、所有者身份")
	@Column(name = "xcustomerowneridentity", length = JpaObject.length_255B)
	@Index(name = TABLE + "_customerowneridentity")
	@CheckPersist(allowEmpty = true)
	private String customerowneridentity;

	//如果从线索转化为客户，那么填写线索id，否则为空。
	@EntityFieldDescribe("线索id")
	@Column(name = "xclueid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_clueid")
	@CheckPersist(allowEmpty = true)
	private String clueid;

	public String getCustomername() {
		return customername;
	}

	public void setCustomername(String customername) {
		this.customername = customername;
	}

	public String getCustomertype() {
		return customertype;
	}

	public void setCustomertype(String customertype) {
		this.customertype = customertype;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getIndustryfirst() {
		return industryfirst;
	}

	public void setIndustryfirst(String industryfirst) {
		this.industryfirst = industryfirst;
	}

	public String getIndustrysecond() {
		return industrysecond;
	}

	public void setIndustrysecond(String industrysecond) {
		this.industrysecond = industrysecond;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getHouseno() {
		return houseno;
	}

	public void setHouseno(String houseno) {
		this.houseno = houseno;
	}

	public String getAddresslongitude() {
		return addresslongitude;
	}

	public void setAddresslongitude(String addresslongitude) {
		this.addresslongitude = addresslongitude;
	}

	public String getAddresslatitude() {
		return addresslatitude;
	}

	public void setAddresslatitude(String addresslatitude) {
		this.addresslatitude = addresslatitude;
	}

	public String getTelno() {
		return telno;
	}

	public void setTelno(String telno) {
		this.telno = telno;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCustomerrank() {
		return customerrank;
	}

	public void setCustomerrank(String customerrank) {
		this.customerrank = customerrank;
	}

	public String getClueid() {
		return clueid;
	}

	public void setClueid(String clueid) {
		this.clueid = clueid;
	}

	public String getQqno() {
		return qqno;
	}

	public void setQqno(String qqno) {
		this.qqno = qqno;
	}

	public String getWebchat() {
		return webchat;
	}

	public void setWebchat(String webchat) {
		this.webchat = webchat;
	}

	public String getCustomerfax() {
		return customerfax;
	}

	public void setCustomerfax(String customerfax) {
		this.customerfax = customerfax;
	}

	public String getCreatorname() {
		return creatorname;
	}

	public void setCreatorname(String creatorname) {
		this.creatorname = creatorname;
	}

	public String getCreatoridentity() {
		return creatoridentity;
	}

	public void setCreatoridentity(String creatoridentity) {
		this.creatoridentity = creatoridentity;
	}

	public String getCustomerownername() {
		return customerownername;
	}

	public void setCustomerownername(String customerownername) {
		this.customerownername = customerownername;
	}

	public String getCustomerowneridentity() {
		return customerowneridentity;
	}

	public void setCustomerowneridentity(String customerowneridentity) {
		this.customerowneridentity = customerowneridentity;
	}

	public String getCustomersequence() {
		return customersequence;
	}

	public void setCustomersequence(String customersequence) {
		this.customersequence = customersequence;
	}

}
