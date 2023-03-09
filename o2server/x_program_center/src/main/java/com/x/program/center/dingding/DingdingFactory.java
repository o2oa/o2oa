package com.x.program.center.dingding;

import java.util.*;
import java.util.stream.Collectors;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Dingding;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import org.apache.commons.lang3.BooleanUtils;

public class DingdingFactory {

	private static Logger logger = LoggerFactory.getLogger(DingdingFactory.class);

	private String accessToken;

	private List<Department> orgs = new ArrayList<>();

	private List<User> users = new ArrayList<>();

	private int count = 0;

	public void syncSleep(int time) {
		int defaultTime = 5000;
		try {
			if (time < 100) {
				time = defaultTime;
			}
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean syncExceptionDeal(Integer retCode, String retMessage) {
		boolean exceptionDeal = false;
		if ((retCode == -1) || (retCode == 90002) || (retCode == 90018) || (retCode == 90006) || (retCode == 90005) || (retCode == 90019)
				|| (retCode == 90010) || (retCode == 90008) || (retCode == 90014)) {
			this.syncSleep(0);
			exceptionDeal = true;
		}
		return exceptionDeal;
	}

	public DingdingFactory(String accessToken) throws Exception {
		this.accessToken = accessToken;
		orgs.add(this.detailOrg(1L));
		for (Department o : this.orgs()) {
			Department sub = this.detailOrg(o.getId());
			if (null != sub) {
				if(BooleanUtils.isTrue(sub.getIsFromUnionOrg()) && BooleanUtils.isFalse(Config.dingding().getSyncUnionOrgEnable())){
					continue;
				}
				orgs.add(sub);
				for (UserSimple u : this.users(o)) {
					users.add(this.detailUser(u));
				}
			}
		}
		orgs = ListTools.trim(orgs, true, true);
		users = ListTools.trim(users, true, true);
		logger.info("ding ding sync org num:{}, user num:{}", orgs.size(), users.size());
	}

	public List<Department> roots() {
		return orgs.stream().filter(o -> 1L == o.getId()).collect(Collectors.toList());
	}

	private List<Department> orgs() throws Exception {
		String address = Config.dingding().getOapiAddress() + "/department/list?access_token=" + this.accessToken
				+ "&id=";
		OrgListResp resp = HttpConnection.getAsObject(address, null, OrgListResp.class);
		logger.debug("orgs response:{}.", resp);
		if (resp.getErrcode() != 0) {
			throw new ExceptionListOrg(resp.getErrcode(), resp.getErrmsg());
		}
		logger.info("ding ding all org num:{}", resp.getDepartment().size());
		return resp.getDepartment();
	}

	private Department detailOrg(Long id) throws Exception {
		this.count = this.count + 1;
		if (this.count > 1000) {
			this.syncSleep(2000);
			this.count = 0;
		}
		String address = Config.dingding().getOapiAddress() + "/department/get?access_token=" + this.accessToken;
		if (!Objects.isNull(id)) {
			address += "&id=" + id;
		}
		OrgResp resp = HttpConnection.getAsObject(address, null, OrgResp.class);
		logger.debug("detailOrg response:{}.", resp);
		if (resp.getErrcode() != 0) {
			if (this.syncExceptionDeal(resp.getErrcode(), resp.getErrmsg())) {
				resp = HttpConnection.getAsObject(address, null, OrgResp.class);
			} else {
				logger.error(new ExceptionDetailOrg(resp.getErrcode(), resp.getErrmsg()));
				resp = null;
			}
		}
		return resp;
	}

	private List<UserSimple> users(Department department) throws Exception {
		this.count = this.count + 1;
		if (this.count > 1000) {
			this.syncSleep(2000);
			this.count = 0;
		}
		String address = Config.dingding().getOapiAddress() + "/user/list?access_token=" + this.accessToken
				+ "&department_id=" + department.getId();
		UserListResp resp = HttpConnection.getAsObject(address, null, UserListResp.class);
		logger.debug("users response:{}.", resp);
		if (resp.getErrcode() != 0) {
			if (this.syncExceptionDeal(resp.getErrcode(), resp.getErrmsg())) {
				resp = HttpConnection.getAsObject(address, null, UserListResp.class);
			} else {
				throw new ExceptionListUser(resp.getErrcode(), resp.getErrmsg());
			}
		}
		return resp.getUserlist();
	}

	private User detailUser(UserSimple simple) throws Exception {
		this.count = this.count + 1;
		if (this.count > 1000) {
			this.syncSleep(2000);
			this.count = 0;
		}
		String address = Config.dingding().getOapiAddress() + "/user/get?access_token=" + this.accessToken + "&userid="
				+ simple.getUserid();
		UserResp resp = HttpConnection.getAsObject(address, null, UserResp.class);
		logger.debug("detailUser response:{}.", resp);

		if (resp.getErrcode() != 0) {
			if (this.syncExceptionDeal(resp.getErrcode(), resp.getErrmsg())) {
				resp = HttpConnection.getAsObject(address, null, UserResp.class);
			} else {
				throw new ExceptionDetailUser(resp.getErrcode(), resp.getErrmsg());
			}
		}
		return resp;

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

	public static class OrgResp extends Department {

		private Integer errcode;
		private String errmsg;

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

	public static class UserListResp extends GsonPropertyObject {

		private Integer errcode;
		private String errmsg;
		private List<UserSimple> userlist;

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

		public List<UserSimple> getUserlist() {
			return userlist;
		}

		public void setUserlist(List<UserSimple> userlist) {
			this.userlist = userlist;
		}

	}

	public static class UserResp extends User {

		private Integer errcode;
		private String errmsg;

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

}
