package com.x.program.center.dingding;

import java.util.LinkedHashMap;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class User extends GsonPropertyObject {

	// "userid": "zhangsan",
	// "unionid": "PiiiPyQqBNBii0HnCJ3zljcuAiEiE",
	// "mobile": "13122222222",
	// "tel" : "010-123333",
	// "workPlace" :"",
	// "remark" : "",
	// "order" : 1,
	// "isAdmin": true,
	// "isBoss": false,
	// "isHide": true,
	// "isLeader": true,
	// "name": "张三",
	// "active": true,
	// "department": [1, 2],
	// "position": "工程师",
	// "email": "zhangsan@alibaba-inc.com",
	// "avatar": "./dingtalk/abc.jpg",
	// "jobnumber": "111111",
	// "extattr": {
	// "爱好":"旅游",
	// "年龄":"24"
	// }
	private String userid;
	private String unionid;
	private String mobile;
	private String tel;
	private String workPlace;
	private String remark;
	private String orderInDepts;
	private Boolean isAdmin;
	private Boolean isBoss;
	private Boolean isHide;
	private Boolean isLeader;
	private String name;
	private Boolean active;
	private List<Long> department;
	private String position;
	private String email;
	private String avatar;
	private String jobnumber;
	private Extattr extattr;

	public static class Extattr extends LinkedHashMap<String, String> {

		private static final long serialVersionUID = -3728149764760442683L;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Boolean getIsBoss() {
		return isBoss;
	}

	public void setIsBoss(Boolean isBoss) {
		this.isBoss = isBoss;
	}

	public Boolean getIsHide() {
		return isHide;
	}

	public void setIsHide(Boolean isHide) {
		this.isHide = isHide;
	}

	public Boolean getIsLeader() {
		return isLeader;
	}

	public void setIsLeader(Boolean isLeader) {
		this.isLeader = isLeader;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<Long> getDepartment() {
		return department;
	}

	public void setDepartment(List<Long> department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getJobnumber() {
		return jobnumber;
	}

	public void setJobnumber(String jobnumber) {
		this.jobnumber = jobnumber;
	}

	public Extattr getExtattr() {
		return extattr;
	}

	public void setExtattr(Extattr extattr) {
		this.extattr = extattr;
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

	public String getOrderInDepts() {
		return orderInDepts;
	}

	public void setOrderInDepts(String orderInDepts) {
		this.orderInDepts = orderInDepts;
	}

}