package com.x.program.center.qiyeweixin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class QiyeweixinFactory {

	private static Logger logger = LoggerFactory.getLogger(QiyeweixinFactory.class);

	private String accessToken;

	private List<Department> orgs = new ArrayList<>();

	private List<User> users = new ArrayList<>();

	public QiyeweixinFactory(String accessToken) throws Exception {
		this.accessToken = accessToken;
		for (Department o : this.orgs()) {
			orgs.add(o);
			users.addAll(this.users(o));
		}
		users = ListTools.trim(users, true, true);
	}

	public List<User> getUsers() {
		return this.users;
	}

	// 2023-03-14 新的api根据应用的权限来同步组织，组织不一定是根节点，根节点id==1的判断已经失效
	public List<Department> roots() {
//		return orgs.stream().filter(o -> 1L == o.getId()).collect(Collectors.toList());
		Set<Long> ids = orgs.stream().map(Department::getId).collect(Collectors.toSet());
		return orgs.stream()
				.filter(dept -> !ids.contains(dept.getParentid()))
				.collect(Collectors.toList());
	}

	private List<Department> orgs() throws Exception {
		String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/department/list?access_token="
				+ this.accessToken + "&id=";
		OrgListResp resp = HttpConnection.getAsObject(address, null, OrgListResp.class);
		logger.debug("orgs response:{}.", resp);
		if (resp.getErrcode() != 0) {
			throw new ExceptionListOrg(resp.getErrcode(), resp.getErrmsg());
		}
		return resp.getDepartment();
	}

	private List<User> users(Department department) throws Exception {
		String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/user/list?access_token=" + this.accessToken
				+ "&department_id=" + department.getId();
		UserListResp resp = HttpConnection.getAsObject(address, null, UserListResp.class);
		logger.debug("users response:{}.", resp);
		if (resp.getErrcode() != 0) {
			throw new ExceptionListUser(resp.getErrcode(), resp.getErrmsg());
		}
		// @Date 2022-09-29 因为企业微信api权限调整，无法读取用户手机号码，这里开始填充一个假的值
		List<User> userList = resp.getUserlist();
		if (userList != null && !userList.isEmpty()) {
			return userList.stream().peek(this::setDefaultMobileStr).collect(Collectors.toList());

		}
		return userList;
	}

	private void setDefaultMobileStr(User user) {
		if (user != null && StringUtils.isNotEmpty(user.getUserid()) && StringUtils.isEmpty(user.getMobile())) {
			String userId = user.getUserid();
			if (userId.length() < 11) { // 用户名太短的情况
				userId = String.format("%11s", userId).replace(" ","0");
			}
			if (userId.length() > 11) { // 用户名超长的情况
				userId = userId.substring(userId.length() - 11); // 截取最后的11位
			}
			logger.debug("这里是补全11位，{}.", userId);
			user.setMobile(userId);
		}
	}

	public List<User> listUser(Department org) throws Exception {
		return users.stream().filter(o -> ListTools.contains(o.getDepartment(), org.getId()))
				.collect(Collectors.toList());
	}

	public List<Department> listSub(Department org) throws Exception {
		return orgs.stream().filter(o -> {
			return Objects.equals(o.getParentid(), org.getId()) ? true : false;
		}).sorted(Comparator.comparing(Department::getOrder, Comparator.nullsLast(Long::compareTo)))
				.collect(Collectors.toList());
	}

	public static class OrgListResp extends GsonPropertyObject {

		// {
		// "retCode": 0,
		// "retMessage": "success",
		// "retData": {
		// "orgNumber": 100016000,
		// "name": "互联网+事业部",
		// "type": 1,
		// "parentId": 1,
		// "fullName": "",
		// "orgCode": "",
		// "postCode": "",
		// "deptHiding": false,
		// "deptPerimits": [],
		// "outerDept": false,
		// "outerPermitDepts": [],
		// "createDeptGroup": false,
		// "orgDeptGroupOwner": null,
		// "deptManagerUseridList": [],
		// "order": 0
		// }
		// }

		private Integer errcode;
		private String errmsg;
		private List<Department> department;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public List<Department> getDepartment() {
			return department;
		}

		public void setDepartment(List<Department> department) {
			this.department = department;
		}

	}

//	public static class OrgResp extends GsonPropertyObject {
//
//		private Integer retCode;
//		private String retMessage;
//		private Department retData;
//
//		public Integer getRetCode() {
//			return retCode;
//		}
//
//		public void setRetCode(Integer retCode) {
//			this.retCode = retCode;
//		}
//
//		public String getRetMessage() {
//			return retMessage;
//		}
//
//		public void setRetMessage(String retMessage) {
//			this.retMessage = retMessage;
//		}
//
//		public Department getRetData() {
//			return retData;
//		}
//
//		public void setRetData(Department retData) {
//			this.retData = retData;
//		}
//	}

	public static class UserListResp extends GsonPropertyObject {
		private Integer errcode;
		private String errmsg;
		private List<User> userlist;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public List<User> getUserlist() {
			return userlist;
		}

		public void setUserlist(List<User> userlist) {
			this.userlist = userlist;
		}
	}

}