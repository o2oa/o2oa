package test.o2.collect.assemble;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiDepartmentListRequest;
import com.dingtalk.api.request.OapiUserSimplelistRequest;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse.Department;
import com.dingtalk.api.response.OapiUserSimplelistResponse;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;

public class TestDingding {

	@Test
	public void test() throws Exception {

		String corpId = "ding21016b71e78da961";
		String corpSecret = "d32SCRLwZdKItnXmnvSAfoaZemIKu3vh7wJQuytJfyhXWAFq5J1pOqmjOFDLJ1tJ";

		String accessToken = this.getToken(corpId, corpSecret);

		System.out.println(accessToken);

		List<Department> departments = this.listDepartment(accessToken);
		List<User> users = new ArrayList<>();
		for (Department o : departments) {
			users.addAll(this.listDepartmentPerson(accessToken, o));
		}
		for (User o : users) {
			System.out.println(XGsonBuilder.toJson(o));
		}
	}

	@Test
	public void test1() throws Exception {

		String corpId = "ding21016b71e78da961";
		String corpSecret = "d32SCRLwZdKItnXmnvSAfoaZemIKu3vh7wJQuytJfyhXWAFq5J1pOqmjOFDLJ1tJ";

		String accessToken = this.getToken(corpId, corpSecret);

		System.out.println(accessToken);

		List<Department> departments = this.listDepartment(accessToken);
		System.out.println(XGsonBuilder.toJson(departments));
	}

	private String getToken(String corpId, String corpSecret) throws Exception {
		String address = "https://oapi.dingtalk.com/gettoken?corpid=" + corpId + "&corpsecret=" + corpSecret;
		AccessTokenReq req = HttpConnection.getAsObject(address, null, AccessTokenReq.class);
		return req.getAccess_token();
	}

	public static class AccessTokenReq {

		private String access_token;
		private Integer errcode;
		private String errmsg;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

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
	}

	private List<Department> listDepartment(String corpAccessToken) throws Exception {
		List<Department> os = new ArrayList<>();
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
		OapiDepartmentListRequest request = new OapiDepartmentListRequest();
		request.setId("");
		request.setHttpMethod("GET");
		OapiDepartmentListResponse response = client.execute(request, corpAccessToken);
		for (Department dept : response.getDepartment()) {
			os.addAll(listDepartment(corpAccessToken, dept));
		}
		return os;
	}

	private List<Department> listDepartment(String corpAccessToken, Department department) throws Exception {
		List<Department> os = new ArrayList<>();
		os.add(department);
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
		OapiDepartmentListRequest request = new OapiDepartmentListRequest();
		request.setId(department.getId().toString());
		request.setHttpMethod("GET");
		OapiDepartmentListResponse response = client.execute(request, corpAccessToken);
		for (Department dept : response.getDepartment()) {
			os.addAll(listDepartment(corpAccessToken, dept));
		}
		return os;
	}

	private List<User> listDepartmentPerson(String corpAccessToken, Department department) throws Exception {
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/simplelist");
		OapiUserSimplelistRequest request = new OapiUserSimplelistRequest();
		request.setDepartmentId(department.getId());
		request.setHttpMethod("GET");
		OapiUserSimplelistResponse response = client.execute(request, corpAccessToken);
		System.out.println(response.getBody());
		SimpleListResp resp = XGsonBuilder.instance().fromJson(response.getBody(), SimpleListResp.class);
		return resp.getUserlist();
	}

	public static class SimpleListResp extends GsonPropertyObject {

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

	public static class User extends GsonPropertyObject {

		private String name;

		private String userid;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUserid() {
			return userid;
		}

		public void setUserid(String userid) {
			this.userid = userid;
		}

	}

}
