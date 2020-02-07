package com.x.program.center.qiyeweixin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

	public List<Department> getOrgs(){
		return this.orgs;
	}

	public List<Department> roots() {
		return orgs.stream().filter(o -> 1L == o.getId()).collect(Collectors.toList());
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
		return resp.getUserlist();
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