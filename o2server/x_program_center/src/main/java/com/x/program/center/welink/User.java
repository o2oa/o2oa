package com.x.program.center.welink;

import com.x.base.core.project.gson.GsonPropertyObject;

public class User extends GsonPropertyObject {

	//{
	//          "userStatus": "1",                     //状态, 1：未开户，2：开户中，3：已开户，4：已销户
	//          "userId": "zhangsan1@welink",       //用户帐号, Key值
	//          "deptCode": "10001",                   //部门Id, Key值, 必填
	//          "deptNameCn": "72270测试部门",
	//          "deptNameEn": "72270Test Dept",
	//          "mobileNumber": "+86-15811847236",     //绑定手机号码, 必填
	//          "phoneNumber": "+86-15811847236",      //手机号码
	//          "landlineNumber": "0755-88888888",     //电话号码(座机)
	//          "userNameCn": "张三",                  //用户中文名称, 必填
	//          "userNameEn": "zhangshan",            //用户英文名称, 必填
	//          "sex": "M",                           //性别, 仅：M/F, M: 男, F: 女, 必填
	//          "corpUserId": "36188",                //用户工号(集成用的字段，如果在开户时没有维护则为空)
	//          "userEmail": "zhangshan4@126.com",    //用户邮箱, 必填
	//          "secretary": "zhangshan@welink",   //秘书(用户帐号)
	//          "address": "广东省深圳",               //地址
	//          "remark": "欢迎加入WeLink",        //备注
	//          "creationTime": "2018-05-03 13:58:02",  //创建时间
	//          "lastUpdatedTime": "2018-05-03 13:58:02"  //最后更新时间
	//        }

	private String userStatus;
	private String userId;
	private String deptCode;
	private String deptNameCn;
	private String deptNameEn;
	private String mobileNumber;
	private String phoneNumber;
	private String landlineNumber;
	private String userNameCn;
	private String userNameEn;
	private String sex;
	private String corpUserId;
	private String userEmail;
	private String secretary;
	private String address;
	private String remark;
	private String creationTime;
	private String lastUpdatedTime;

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getDeptNameCn() {
		return deptNameCn;
	}

	public void setDeptNameCn(String deptNameCn) {
		this.deptNameCn = deptNameCn;
	}

	public String getDeptNameEn() {
		return deptNameEn;
	}

	public void setDeptNameEn(String deptNameEn) {
		this.deptNameEn = deptNameEn;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}

	public String getUserNameCn() {
		return userNameCn;
	}

	public void setUserNameCn(String userNameCn) {
		this.userNameCn = userNameCn;
	}

	public String getUserNameEn() {
		return userNameEn;
	}

	public void setUserNameEn(String userNameEn) {
		this.userNameEn = userNameEn;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCorpUserId() {
		return corpUserId;
	}

	public void setCorpUserId(String corpUserId) {
		this.corpUserId = corpUserId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getSecretary() {
		return secretary;
	}

	public void setSecretary(String secretary) {
		this.secretary = secretary;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(String lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}