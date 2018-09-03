package com.x.program.center.qiyeweixin;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class User extends GsonPropertyObject {

	// "errcode": 0,
	// "unionid": "PiiiPyQqBNBii0HnCJ3zljcuAiEiE",
	// "openId": "PiiiPyQqBNBii0HnCJ3zljcuAiEiE",
	// "roles": [{
	// "id": 23003585,
	// "name": "engineer",
	// "groupName": "group one"
	// }],
	// "remark": "remark",
	// "userid": "zhangsan",
	// "isLeaderInDepts": "{1:false}",
	// "isBoss": false,
	// "hiredDate": 1520265600000,
	// "isSenior": false,
	// "tel": "010-88996533",
	// "department": [1,2],
	// "workPlace": "beijing",
	// "email": "ceshi@aliyun.com",
	// "orderInDepts": "{1:71738366882504}",
	// "mobile": "15901516821",
	// "errmsg": "ok",
	// "active": false,
	// "avatar": "dingtalk.com/abc.jpg",
	// "isAdmin": false,
	// "isHide": false,
	// "jobnumber": "001",
	// "name": "test1",
	// "extattr": {},
	// "stateCode": "86",
	// "position": "manager"

	private String userid;
	private String name;
	private String avatar;

	private List<Long> department;

	private String email;

	private String mobile;
	private String jobnumber;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public List<Long> getDepartment() {
		return department;
	}

	public void setDepartment(List<Long> department) {
		this.department = department;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getJobnumber() {
		return jobnumber;
	}

	public void setJobnumber(String jobnumber) {
		this.jobnumber = jobnumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userid == null) ? 0 : userid.hashCode());
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
		if (userid == null) {
			if (other.userid != null)
				return false;
		} else if (!userid.equals(other.userid))
			return false;
		return true;
	}

}
