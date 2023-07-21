package com.x.program.center.zhengwudingding;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.x.base.core.project.gson.GsonPropertyObject;

public class User extends GsonPropertyObject {

	// {
	// "userId": 100010000,
	// "userName": "张三",
	// "email": "",
	// "orgList": [
	// 100016013
	// ],
	// "title": "",
	// "position": "",
	// "job": "",
	// "officeTel": "",
	// "tel": "",
	// "workPlace": "",
	// "office": "",
	// "isSenior": false,
	// "jobNumber": "",
	// "orderInOrgs": {
	// "100016013": 99999994
	// },
	// "virtualNet": null
	// }

	private String userId;
	private String userName;
	private String mobile;
	private String email;
	private List<Long> orgList = new ArrayList<>();
	private String title;
	private String position;
	private String job;
	private String officeTel;
	private String tel;
	private String workPlace;
	private String office;
	private Boolean isSenior;
	private String jobNumber;
	private TreeMap<String, Long> orderInOrgs = new TreeMap<>();
	private String virtualNet;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Long> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<Long> orgList) {
		this.orgList = orgList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getOfficeTel() {
		return officeTel;
	}

	public void setOfficeTel(String officeTel) {
		this.officeTel = officeTel;
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

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public Boolean getIsSenior() {
		return isSenior;
	}

	public void setIsSenior(Boolean isSenior) {
		this.isSenior = isSenior;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public TreeMap<String, Long> getOrderInOrgs() {
		return orderInOrgs;
	}

	public void setOrderInOrgs(TreeMap<String, Long> orderInOrgs) {
		this.orderInOrgs = orderInOrgs;
	}

	public String getVirtualNet() {
		return virtualNet;
	}

	public void setVirtualNet(String virtualNet) {
		this.virtualNet = virtualNet;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}