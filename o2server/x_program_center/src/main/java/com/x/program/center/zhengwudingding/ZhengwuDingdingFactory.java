package com.x.program.center.zhengwudingding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ZhengwuDingdingFactory {

	private static Logger logger = LoggerFactory.getLogger(ZhengwuDingdingFactory.class);

	private String accessToken;

	private List<Org> orgs = new ArrayList<>();

	private List<Org> roots = new ArrayList<>();;

	private List<User> users = new ArrayList<>();

	private int  count = 0;
	    
	public void syncSleep(int time) {
	    	int defaultTime = 2000;
	    	 try {
	    		if(time == 0) {
	    			time =defaultTime;
	    		}
	    		Thread.sleep(time);//延时2秒
	 		} catch (InterruptedException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}    
	 }
	
	  public boolean syncExceptionDeal(Integer retCode, String retMessage) {
	    	boolean exceptionDeal = false;
	    	if((retCode == 90002) ||(retCode == 90018) ||  (retCode == 90006) || (retCode == 90005) || (retCode == 90019) ||
	    	    (retCode == 90010) ||  (retCode == 90008) || (retCode == 90014) ) {
	    		this.syncSleep(0);
	    		exceptionDeal = true;
	    	}
	    	return exceptionDeal;
	    }
	    
	public ZhengwuDingdingFactory(String accessToken) throws Exception {
		this.accessToken = accessToken;
		for (Org o : this.listRootOrg()) {
			o = this.detailOrg(o);
			this.roots.add(o);
		}
		for (Org r : roots) {
			for (Org o : this.listOrg(r)) {
				orgs.add(this.detailOrg(o));
			}
		}
	}

	private List<Org> listRootOrg() throws Exception {
		String address = Config.zhengwuDingding().getOapiAddress() + "/org/getRootOrg?access_token=" + this.accessToken;
		OrgListResp resp = HttpConnection.getAsObject(address, null, OrgListResp.class);
		if (resp.getRetCode() != 0) {
			throw new ExceptionRootOrg(resp.getRetCode(), resp.getRetMessage());
		}
		return resp.getRetData();
	}

	private Org detailOrg(Org org) throws Exception {
		String address = Config.zhengwuDingding().getOapiAddress() + "/org/get?access_token=" + this.accessToken
				+ "&orgNumber=" + org.getOrgNumber();
		OrgResp resp = HttpConnection.getAsObject(address, null, OrgResp.class);
		if (resp.getRetCode() != 0) {
			throw new ExceptionDetailOrg(resp.getRetCode(), resp.getRetMessage());
		}
		return resp.getRetData();
	}

	private List<Org> listOrg(Org org) throws Exception {
		String address = Config.zhengwuDingding().getOapiAddress() + "/org/getAllSubOrg?access_token="
				+ this.accessToken + "&orgNumber=" + org.getOrgNumber();
		OrgListResp resp = HttpConnection.getAsObject(address, null, OrgListResp.class);
		if (resp.getRetCode() != 0) {
			throw new ExceptionListOrg(resp.getRetCode(), resp.getRetMessage());
		}
		return resp.getRetData();
	}

	private User detailUser(User user) throws Exception {
		String address = Config.zhengwuDingding().getOapiAddress() + "/user/get?access_token=" + this.accessToken
				+ "&userId=" + user.getUserId();
		UserResp resp = HttpConnection.getAsObject(address, null, UserResp.class);
		logger.debug("detailUser response:{}.", resp);
		if (resp.getRetCode() != 0) {
			
			if(this.syncExceptionDeal(resp.getRetCode(), resp.getRetMessage())) {
				resp = HttpConnection.getAsObject(address, null, UserResp.class);
			}else {
			 throw new ExceptionDetailUser(resp.getRetCode(), resp.getRetMessage());
			}
			//throw new ExceptionDetailUser(resp.getRetCode(), resp.getRetMessage());
		}
		return resp.getRetData();
	}

	public List<Org> roots() {
		return this.roots;
	}

	public List<Org> listSub(Org org) {
		return orgs.stream().filter(o -> {
			return Objects.equals(o.getParentId(), Objects.toString(org.getOrgNumber())) ? true : false;
		}).sorted(Comparator.comparing(Org::getOrder, Comparator.nullsLast(Long::compareTo)))
				.collect(Collectors.toList());
	}

	public List<User> listUser(Org org) throws Exception {
		String address = Config.zhengwuDingding().getOapiAddress() + "/org/member/get?access_token=" + this.accessToken
				+ "&orgNumber=" + org.getOrgNumber();
		UserListResp resp = HttpConnection.getAsObject(address, null, UserListResp.class);
		if (resp.getRetCode() != 0) {
			throw new ExceptionListUser(resp.getRetCode(), resp.getRetMessage());
		}
		List<User> os = new ArrayList<>();
		next: for (User o : resp.getRetData()) {
			for (User u : users) {
				if (StringUtils.equals(o.getUserId(), u.getUserId())) {
					os.add(u);
					continue next;
				}
			}
			User user = this.detailUser(o);
			users.add(user);
			os.add(user);
		}
		return os;
	}

	public User getUser(String userId) throws Exception {
		for (User u : users) {
			if (StringUtils.equals(u.getUserId(), userId)) {
				return u;
			}
		}
		User user = new User();
		user.setUserId(userId);
		user = this.detailUser(user);
		users.add(user);
		return user;
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

		private Integer retCode;
		private String retMessage;
		private List<Org> retData;

		public Integer getRetCode() {
			return retCode;
		}

		public void setRetCode(Integer retCode) {
			this.retCode = retCode;
		}

		public String getRetMessage() {
			return retMessage;
		}

		public void setRetMessage(String retMessage) {
			this.retMessage = retMessage;
		}

		public List<Org> getRetData() {
			return retData;
		}

		public void setRetData(List<Org> retData) {
			this.retData = retData;
		}

	}

	public static class OrgResp extends GsonPropertyObject {

		private Integer retCode;
		private String retMessage;
		private Org retData;

		public Integer getRetCode() {
			return retCode;
		}

		public void setRetCode(Integer retCode) {
			this.retCode = retCode;
		}

		public String getRetMessage() {
			return retMessage;
		}

		public void setRetMessage(String retMessage) {
			this.retMessage = retMessage;
		}

		public Org getRetData() {
			return retData;
		}

		public void setRetData(Org retData) {
			this.retData = retData;
		}
	}

	public static class UserResp extends GsonPropertyObject {
		private Integer retCode;
		private String retMessage;
		private User retData;

		public Integer getRetCode() {
			return retCode;
		}

		public void setRetCode(Integer retCode) {
			this.retCode = retCode;
		}

		public String getRetMessage() {
			return retMessage;
		}

		public void setRetMessage(String retMessage) {
			this.retMessage = retMessage;
		}

		public User getRetData() {
			return retData;
		}

		public void setRetData(User retData) {
			this.retData = retData;
		}

	}

	public static class UserListResp extends GsonPropertyObject {
		private Integer retCode;
		private String retMessage;
		private List<User> retData;

		public Integer getRetCode() {
			return retCode;
		}

		public void setRetCode(Integer retCode) {
			this.retCode = retCode;
		}

		public String getRetMessage() {
			return retMessage;
		}

		public void setRetMessage(String retMessage) {
			this.retMessage = retMessage;
		}

		public List<User> getRetData() {
			return retData;
		}

		public void setRetData(List<User> retData) {
			this.retData = retData;
		}
	}

}
