package com.x.base.core.project.organization;

import java.util.Date;

import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Person extends GsonPropertyObject {

	@FieldDescribe("匹配字段")
	private String matchKey;
	@FieldDescribe("数据库主键")
	private String id;
	@FieldDescribe("个人名称")
	private String name;
	@FieldDescribe("昵称")
	private String nickName;
	@FieldDescribe("性别,m:男,f:女,d:未知")
	private GenderType genderType;
	@FieldDescribe("签名")
	private String signature;
	@FieldDescribe("说明")
	private String description;
	@FieldDescribe("员工号")
	private String employee;
	@FieldDescribe("唯一标识")
	private String unique;
	@FieldDescribe("识别名")
	private String distinguishedName;
	@FieldDescribe("排序号")
	private Integer orderNumber;
	@FieldDescribe("主管")
	private String superior;
	@FieldDescribe("邮件地址")
	private String mail;
	@FieldDescribe("微信号")
	private String weixin;
	@FieldDescribe("qq号")
	private String qq;
	@FieldDescribe("手机号")
	private String mobile;
	@FieldDescribe("办公电话")
	private String officePhone;
	@FieldDescribe("入职时间")
	private Date boardDate;
	@FieldDescribe("生日")
	private Date birthday;
	@FieldDescribe("年龄")
	private Integer age;
	@FieldDescribe("企业微信Id")
	private String qiyeweixinId;
	@FieldDescribe("钉钉Id")
	private String dingdingId;
	@FieldDescribe("政务钉钉Id")
	private String zhengwuDingdingId;
	@FieldDescribe("华为WeLink id")
	private String weLinkId;
	@FieldDescribe("主体密级标识.")
	private Integer subjectSecurityClearance;

	public Integer getSubjectSecurityClearance() {
		return subjectSecurityClearance;
	}

	public void setSubjectSecurityClearance(Integer subjectSecurityClearance) {
		this.subjectSecurityClearance = subjectSecurityClearance;
	}

	public String getMatchKey() {
		return matchKey;
	}

	public void setMatchKey(String matchKey) {
		this.matchKey = matchKey;
	}

	public String getWeLinkId() {
		return weLinkId;
	}

	public void setWeLinkId(String weLinkId) {
		this.weLinkId = weLinkId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GenderType getGenderType() {
		return genderType;
	}

	public void setGenderType(GenderType genderType) {
		this.genderType = genderType;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public Date getBoardDate() {
		return boardDate;
	}

	public void setBoardDate(Date boardDate) {
		this.boardDate = boardDate;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getQiyeweixinId() {
		return qiyeweixinId;
	}

	public void setQiyeweixinId(String qiyeweixinId) {
		this.qiyeweixinId = qiyeweixinId;
	}

	public String getDingdingId() {
		return dingdingId;
	}

	public void setDingdingId(String dingdingId) {
		this.dingdingId = dingdingId;
	}

	public String getZhengwuDingdingId() {
		return zhengwuDingdingId;
	}

	public void setZhengwuDingdingId(String zhengwuDingdingId) {
		this.zhengwuDingdingId = zhengwuDingdingId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
