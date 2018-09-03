package com.x.program.center.qiyeweixin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.connection.HttpConnection;

public class QiyeweixinHelper {

	public static List<Department> listDepartment(String accessToken) throws Exception {
		// https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN&id=ID;
		String address = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=" + accessToken;
		ListDepartmentResp req = HttpConnection.getAsObject(address, null, ListDepartmentResp.class);
		if (req.getErrcode() != 0) {
			throw new ExceptionListDepartment(req.getErrcode(), req.getErrmsg());
		}
		return req.getDepartment();
	}

	public static List<User> listDepartmentUser(String accessToken, Department department) throws Exception {
		String address = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=" + accessToken + "&department_id="
				+ department.getId() + "&fetch_child=0";

		ListDepartmentUserResp req = HttpConnection.getAsObject(address, null, ListDepartmentUserResp.class);
		if (req.getErrcode() != 0) {
			throw new ExceptionListDepartmentUser(req.getErrcode(), req.getErrmsg());
		}
		return req.getUserlist();
	}

	public static class ListDepartmentUserResp {

		private Integer errcode;

		private String errmsg;

		private List<User> userlist = new ArrayList<>();

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

	public static class ListDepartmentResp {

		private Integer errcode;

		private String errmsg;

		private List<Department> department = new ArrayList<>();

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
}
