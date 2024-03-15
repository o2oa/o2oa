package com.x.program.center.andfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

public class AndFxFactory {

	private static Logger logger = LoggerFactory.getLogger(AndFxFactory.class);

	private List<Department> orgs = new ArrayList<>();

	private List<User> users = new ArrayList<>();

	private List<String> admins = new ArrayList<>();

	public AndFxFactory() throws Exception {
		for (Department o : this.orgs()) {
			orgs.add(o);
			for (User u : this.users(o)) {
				if(admins.contains(u.getUid())){
					u.setIsAdmin(true);
				}
				if(StringUtils.isBlank(u.getMobile())){
					u.setMobile(u.getUid());
				}
				users.add(u);
			}
		}
		orgs = ListTools.trim(orgs, true, true);
		users = ListTools.trim(users, true, true);
		logger.info("and fx sync org num:{}, user num:{}", orgs.size(), users.size());
	}

	public List<Department> roots() {
		List<Department> roots = orgs.stream().filter(o -> 0L == o.getDepartmentId()).collect(Collectors.toList());
		roots.stream().forEach(o -> { o.setParentId(null); });
		return roots;
	}

	private List<Department> orgs() throws Exception {
		String address = Config.andFx().getAddressApi() + "/v1/origin/addrBookExternal/api/zhxc/getOrgInfos";
		Long enId = Long.valueOf(Config.andFx().getEnterId());
		Map<String, List<Long>> map = new HashMap<>();
		map.put("orgIds", Collections.singletonList(enId));
		String requestBody = XGsonBuilder.toJson(map);
		String uuid = StringTools.uniqueToken();
		StringBuffer buffer = new StringBuffer();
		String adBookAppKey = Config.andFx().getAddressAppKey();
		String adBookClientId = Config.andFx().getClientId();
		String adBookAppSecret = Config.andFx().getAddressAppSecret();
		String uMobile = "13800000000";
		buffer.append("appKey" + "=" + adBookAppKey)
				.append("body" + "=" + requestBody)
				.append("client_id" + "=" + adBookClientId)
				.append("uMobile" + "=" + uMobile)
				.append("uuid" + "=" + uuid);
		String signStr = DigestUtils.sha1Hex(buffer.toString() + adBookAppSecret);
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair("appKey", adBookAppKey));
		heads.add(new NameValuePair("client_id", adBookClientId));
		heads.add(new NameValuePair("uMobile", uMobile));
		heads.add(new NameValuePair("uuid", uuid));
		heads.add(new NameValuePair("sign", signStr));

		OrgResp resp = HttpConnection.postAsObject(address, heads, requestBody, OrgResp.class);
		logger.debug("orgs response:{}.", resp);
		if (resp.getCode() != 0) {
			throw new ExceptionListOrg(resp.getCode(), resp.getMsg());
		}
		if(ListTools.isNotEmpty(resp.getData())){
			logger.info("and fx sync system admin :{}", XGsonBuilder.toJson(resp.getData().get(0).getAdminInfos()));
			this.admins.addAll(resp.getData().get(0).getAdminInfos().stream().map(User::getUid).collect(Collectors.toList()));
			return resp.getData().get(0).getDeptInfos();
		}else{
			return Collections.EMPTY_LIST;
		}
	}

	private List<User> users(Department department) throws Exception {
		String address = Config.andFx().getAddressApi() + "/v1/origin/addrBookExternal/api/zhxc/getDeptUserInfos";
		Long enId = Long.valueOf(Config.andFx().getEnterId());
		Map<String, Object> map = new HashMap<>();
		map.put("orgId", enId);
		map.put("departmentList", Collections.singletonList(department.getDepartmentId()));

		String requestBody = XGsonBuilder.toJson(map);
		String uuid = StringTools.uniqueToken();
		StringBuffer buffer = new StringBuffer();
		String adBookAppKey = Config.andFx().getAddressAppKey();
		String adBookClientId = Config.andFx().getClientId();
		String adBookAppSecret = Config.andFx().getAddressAppSecret();
		String uMobile = "13800000000";
		buffer.append("appKey" + "=" + adBookAppKey)
				.append("body" + "=" + requestBody)
				.append("client_id" + "=" + adBookClientId)
				.append("uMobile" + "=" + uMobile)
				.append("uuid" + "=" + uuid);
		String signStr = DigestUtils.sha1Hex(buffer.toString() + adBookAppSecret);
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair("appKey", adBookAppKey));
		heads.add(new NameValuePair("client_id", adBookClientId));
		heads.add(new NameValuePair("uMobile", uMobile));
		heads.add(new NameValuePair("uuid", uuid));
		heads.add(new NameValuePair("sign", signStr));

		UserResp resp = HttpConnection.postAsObject(address, heads, requestBody, UserResp.class);
		logger.debug("users response:{}.", resp);
		if (resp.getCode() != 0) {
			throw new ExceptionListUser(resp.getCode(), resp.getMsg());
		}
		List<DeptUserDataResp> list = resp.getData().getDeptUserInfos();
		return ListTools.isNotEmpty(list) ? list.get(0).getList() : Collections.EMPTY_LIST;
	}

	public List<User> listUser(Department org) {
		return users.stream().filter(o -> Objects.equals(o.getDepartmentId(), org.getDepartmentId()))
				.collect(Collectors.toList());
	}

	public List<Department> listSub(Department org) {
		return orgs.stream().filter(o -> {
			return org.getDepartmentId().equals(o.getParentId());
		}).sorted(Comparator.comparing(Department::getSequence, Comparator.nullsLast(Long::compareTo)))
				.collect(Collectors.toList());
	}

	public static class OrgResp extends GsonPropertyObject {

		private Integer code;
		private String msg;
		private List<OrgDataResp> data;

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public List<OrgDataResp> getData() {
			return data;
		}

		public void setData(List<OrgDataResp> data) {
			this.data = data;
		}
	}

	public static class OrgDataResp extends GsonPropertyObject {
		private Long orgId;
		private String orgName;
		private String logoAddress;
		private Integer version;
		private List<User> adminInfos;
		private List<Department> deptInfos;

		public Long getOrgId() {
			return orgId;
		}

		public void setOrgId(Long orgId) {
			this.orgId = orgId;
		}

		public String getOrgName() {
			return orgName;
		}

		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}

		public String getLogoAddress() {
			return logoAddress;
		}

		public void setLogoAddress(String logoAddress) {
			this.logoAddress = logoAddress;
		}

		public Integer getVersion() {
			return version;
		}

		public void setVersion(Integer version) {
			this.version = version;
		}

		public List<Department> getDeptInfos() {
			return deptInfos;
		}

		public void setDeptInfos(List<Department> deptInfos) {
			this.deptInfos = deptInfos;
		}

		public List<User> getAdminInfos() {
			return adminInfos == null ? new ArrayList<>() : adminInfos;
		}

		public void setAdminInfos(List<User> adminInfos) {
			this.adminInfos = adminInfos;
		}
	}

	public static class UserResp extends GsonPropertyObject {

		private Integer code;
		private String msg;
		private UserDataResp data;

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public UserDataResp getData() {
			return data;
		}

		public void setData(UserDataResp data) {
			this.data = data;
		}
	}

	public static class UserDataResp extends GsonPropertyObject {
		private Integer orgVersion;
		private List<DeptUserDataResp> deptUserInfos;

		public Integer getOrgVersion() {
			return orgVersion;
		}

		public void setOrgVersion(Integer orgVersion) {
			this.orgVersion = orgVersion;
		}

		public List<DeptUserDataResp> getDeptUserInfos() {
			return deptUserInfos;
		}

		public void setDeptUserInfos(List<DeptUserDataResp> deptUserInfos) {
			this.deptUserInfos = deptUserInfos;
		}
	}

	public static class DeptUserDataResp extends GsonPropertyObject {
		private Long departmentId;
		private Integer version;
		private List<User> list;

		public Long getDepartmentId() {
			return departmentId;
		}

		public void setDepartmentId(Long departmentId) {
			this.departmentId = departmentId;
		}

		public Integer getVersion() {
			return version;
		}

		public void setVersion(Integer version) {
			this.version = version;
		}

		public List<User> getList() {
			return list;
		}

		public void setList(List<User> list) {
			this.list = list;
		}
	}

}
