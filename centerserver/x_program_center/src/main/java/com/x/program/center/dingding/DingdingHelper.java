package com.x.program.center.dingding;

import java.util.List;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiDepartmentListRequest;
import com.dingtalk.api.request.OapiUserListRequest;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.dingtalk.api.response.OapiUserListResponse;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;

public class DingdingHelper {

	// public static Department getDepartment(String accessToken, String
	// departmentId) throws Exception {
	// if (StringUtils.isEmpty(departmentId)) {
	// return null;
	// }
	// DingTalkClient client = new
	// DefaultDingTalkClient("https://oapi.dingtalk.com/department/get");
	// OapiDepartmentGetRequest request = new OapiDepartmentGetRequest();
	// request.setId(departmentId);
	// request.setHttpMethod("GET");
	// OapiDepartmentGetResponse response = client.execute(request, accessToken);
	// if (response.getErrcode() != 0) {
	// return null;
	// }
	// return XGsonBuilder.instance().fromJson(response.getBody(),
	// Department.class);
	// }

	// public static Department createDeparment(String accessToken, Department
	// department) throws Exception {
	// DingTalkClient client = new
	// DefaultDingTalkClient("https://oapi.dingtalk.com/department/create");
	// OapiDepartmentCreateRequest request = new OapiDepartmentCreateRequest();
	// if (null != department.getParentid()) {
	// request.setParentid(Objects.toString(department.getParentid()));
	// }
	// request.setName(department.getName());
	// if (null != department.getOrder()) {
	// request.setOrder(Objects.toString(department.getOrder()));
	// }
	// request.setCreateDeptGroup(false);
	// OapiDepartmentCreateResponse response = client.execute(request, accessToken);
	// if (response.getErrcode() != 0) {
	//
	// }
	// department.setId(response.getId());
	// return department;
	// }

	// public static Department deleteDepartment(String accessToken, Department
	// department) throws Exception {
	// DingTalkClient client = new
	// DefaultDingTalkClient("https://oapi.dingtalk.com/department/delete");
	// OapiDepartmentDeleteRequest request = new OapiDepartmentDeleteRequest();
	// request.setId(Objects.toString(department.getId()));
	// request.setHttpMethod("GET");
	// OapiDepartmentDeleteResponse response = client.execute(request, accessToken);
	// if (response.getErrcode() != 0) {
	//
	// }
	// return department;
	// }

	public static List<Department> listDepartment(String accessToken) throws Exception {
		DingTalkClient client = new DefaultDingTalkClient(Config.dingding().getOapiAddress() + "/department/list");
		OapiDepartmentListRequest request = new OapiDepartmentListRequest();
		request.setHttpMethod("GET");
		OapiDepartmentListResponse response = client.execute(request, accessToken);
		if (response.getErrcode() != 0) {
			throw new ExceptionListDepartment(response.getErrcode(), response.getErrmsg());
		}
		return XGsonBuilder.instance().fromJson(XGsonBuilder.toJson(response.getDepartment()),
				new TypeToken<List<Department>>() {
				}.getType());
	}

	public static List<User> listDepartmentUser(String accessToken, Department department) throws Exception {
		DingTalkClient client = new DefaultDingTalkClient(Config.dingding().getOapiAddress() + "/user/list");
		OapiUserListRequest request = new OapiUserListRequest();
		request.setDepartmentId(department.getId());
		request.setHttpMethod("GET");
		OapiUserListResponse response = client.execute(request, accessToken);
		if (response.getErrcode() != 0) {
			throw new ExceptionListDepartmentUser(response.getErrcode(), response.getErrmsg());
		}
		return XGsonBuilder.instance().fromJson(XGsonBuilder.toJson(response.getUserlist()),
				new TypeToken<List<User>>() {
				}.getType());
	}

	// public static void deleteUserFromDepartment(String accessToken, User user,
	// Department department) throws Exception {
	// DingTalkClient client = new
	// DefaultDingTalkClient("https://oapi.dingtalk.com/user/update");
	// OapiUserUpdateRequest request = new OapiUserUpdateRequest();
	// request.setUserid(user.getUserid());
	// List<Long> ids = user.getDepartment();
	// ids.remove(department.getId());
	// request.setDepartment(ids);
	// OapiUserUpdateResponse response = client.execute(request, accessToken);
	// if (response.getErrcode() != 0) {
	//
	// }
	// }

	// public static List<User> deleteUser(String accessToken, List<User> users)
	// throws Exception {
	// if (ListTools.isEmpty(users)) {
	// return users;
	// }
	// DingTalkClient client = new
	// DefaultDingTalkClient("https://oapi.dingtalk.com/user/batchdelete");
	// OapiUserBatchdeleteRequest request = new OapiUserBatchdeleteRequest();
	// request.setUseridlist(ListTools.extractProperty(users, "userid",
	// String.class, true, true));
	// OapiUserBatchdeleteResponse response = client.execute(request, accessToken);
	// if (response.getErrcode() != 0) {
	//
	// }
	// return users;
	// }
	//
	// public static User getUser(String accessToken, String userid) throws
	// Exception {
	// DingTalkClient client = new
	// DefaultDingTalkClient("https://oapi.dingtalk.com/user/get");
	// OapiUserGetRequest request = new OapiUserGetRequest();
	// request.setUserid(userid);
	// request.setHttpMethod("GET");
	// OapiUserGetResponse response = client.execute(request, accessToken);
	// if (response.getErrcode() != 0) {
	//
	// }
	// return XGsonBuilder.instance().fromJson(response.getBody(), User.class);
	// }

}
