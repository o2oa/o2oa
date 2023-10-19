package com.x.program.center.dingding;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;

public class User extends GsonPropertyObject {

private static final long serialVersionUID = 4198572763092545607L;
	// 	{
// 		"leader":"true",
// 		"extension":"{\"爱好\":\"旅游\",\"年龄\":\"24\"}",
// 		"unionid":"z21HjQliSzpw0YWCNxmii6u2Os62cZ62iSZ",
// 		"boss":"true",
// 		"exclusive_account":"false",
// 		"admin":"true",
// 		"remark":"备注备注",
// 		"title":"技术总监",
// 		"hired_date":"1597573616828",
// 		"userid":"zhangsan",
// 		"work_place":"未来park",
// 		"dept_id_list":"[2,3,4]",
// 		"job_number":"4",
// 		"email":"test@xxx.com",
// 		"dept_order":"1",
// 		"mobile":"18513027676",
// 		"active":"true",
// 		"telephone":"010-86123456-2345",
// 		"avatar":"xxx",
// 		"hide_mobile":"false",
// 		"org_email":"test@xxx.com",
// 		"name":"张三",
// 		"state_code":"86"
// }
	private String userid;
	private String unionid;
	private String state_code; // 
	private String mobile;
	private String telephone;
	private String work_place;
	private String remark;
	private String dept_order;
	private Boolean admin;
	private Boolean boss;
	private Boolean hide_mobile;
	private Boolean leader;
	private String name;
	private Boolean active;
	private List<Long> dept_id_list; // 所属部门id列表。v2 版本 api
	private String email;
	private String avatar;
	private String job_number;
	private String extension;

	public static class Extattr extends LinkedHashMap<String, String> {

		private static final long serialVersionUID = -3728149764760442683L;
	}

	public Extattr getExtattr() {
		if (StringUtils.isNotEmpty(getExtension())) {
			Extattr extattr = XGsonBuilder.instance().fromJson(getExtension(), Extattr.class);
			return extattr;
		}
		return null;
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
 
	public List<Long> getDept_id_list() {
		return dept_id_list;
	}

	public void setDept_id_list(List<Long> dept_id_list) {
		this.dept_id_list = dept_id_list;
	}

	public String getState_code() {
		return state_code;
	}

	public void setState_code(String state_code) {
		this.state_code = state_code;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getWork_place() {
		return work_place;
	}

	public void setWork_place(String work_place) {
		this.work_place = work_place;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDept_order() {
		return dept_order;
	}

	public void setDept_order(String dept_order) {
		this.dept_order = dept_order;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public Boolean getBoss() {
		return boss;
	}

	public void setBoss(Boolean boss) {
		this.boss = boss;
	}

	public Boolean getHide_mobile() {
		return hide_mobile;
	}

	public void setHide_mobile(Boolean hide_mobile) {
		this.hide_mobile = hide_mobile;
	}

	public Boolean getLeader() {
		return leader;
	}

	public void setLeader(Boolean leader) {
		this.leader = leader;
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

	public String getJob_number() {
		return job_number;
	}

	public void setJob_number(String job_number) {
		this.job_number = job_number;
	}

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }
 
	
	

}