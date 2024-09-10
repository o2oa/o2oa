package com.x.program.center.dingding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

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
		recursionOrg(1L);
		orgs = ListTools.trim(orgs, true, true);
		users = ListTools.trim(users, true, true);
		logger.info("ding ding sync org num:{}, user num:{}", orgs.size(), users.size());
	}

	/**
	 * 递归查询
	 * @param parentId 上级组织 id
	 * @throws Exception
	 */
	private void recursionOrg(Long parentId) throws Exception {
		for (Department o : this.orgs(parentId)) {
			Department sub = this.detailOrg(o.getDept_id());
			if (null != sub) {
				if(BooleanUtils.isTrue(sub.getFrom_union_org()) && BooleanUtils.isFalse(Config.dingding().getSyncUnionOrgEnable())){
					continue;
				}
				orgs.add(sub);
				for (User u : this.users(o)) {
					users.add(u);
				}
				recursionOrg(o.getDept_id());
			}
		}
	}

	public List<Department> roots() {
		return orgs.stream().filter(o -> 1L == o.getDept_id()).collect(Collectors.toList());
	}

	private List<Department> orgs(Long parentId) throws Exception {
		// String address = Config.dingding().getOapiAddress() + "/department/list?access_token=" + this.accessToken
		// 		+ "&id=";
		String address = Config.dingding().getOapiAddress() + "/topapi/v2/department/listsub?access_token=" + this.accessToken;
		DingdingDepartmentPost body = new DingdingDepartmentPost();
		body.setDept_id(parentId);
		String reString = HttpConnection.postAsString(address, null, body.toString());
		if (logger.isDebugEnabled()) {
			logger.debug("orgs response:{}.", reString);
		}
		Type type = new TypeToken<DingdingResponse<List<Department>>> () {}.getType();
		DingdingResponse<List<Department>> response = XGsonBuilder.instance().fromJson(reString, type);
		if (response.getErrcode() != 0) {
			throw new ExceptionListOrg(response.getErrcode(), response.getErrmsg());
		}
		return response.getResult();
	}

	private Department detailOrg(Long id) throws Exception {
		this.count = this.count + 1;
		if (this.count > 1000) {
			this.syncSleep(2000);
			this.count = 0;
		}
		// String address = Config.dingding().getOapiAddress() + "/department/get?access_token=" + this.accessToken;
		// if (!Objects.isNull(id)) {
		// 	address += "&id=" + id;
		// }
		DingdingResponse<Department> response = postDetailOrg(id);
		Department resp = response.getResult();
		if (response.getErrcode() != 0) {
			if (this.syncExceptionDeal(response.getErrcode(), response.getErrmsg())) {
				 DingdingResponse<Department> response2 = postDetailOrg(id);
				 resp = response2.getResult();
			} else {
				logger.error(new ExceptionDetailOrg(response.getErrcode(), response.getErrmsg()));
				resp = null;
			}
		}
		return resp;
	}

	private DingdingResponse<Department> postDetailOrg(Long id) throws Exception {
		String address = Config.dingding().getOapiAddress() + "/topapi/v2/department/get?access_token=" + this.accessToken;
		DingdingDepartmentPost body = new DingdingDepartmentPost();
		body.setDept_id(id);
		String reString = HttpConnection.postAsString(address, null, body.toString());
		if (logger.isDebugEnabled()) {
			logger.debug("detailOrg response:{}.", reString);
		}
		Type type = new TypeToken<DingdingResponse<Department>> () {}.getType();
		DingdingResponse<Department> response = XGsonBuilder.instance().fromJson(reString, type);
		return response;
	}

	private List<User> users(Department department) throws Exception {
		this.count = this.count + 1;
		if (this.count > 1000) {
			this.syncSleep(2000);
			this.count = 0;
		}
		// String address = Config.dingding().getOapiAddress() + "/user/list?access_token=" + this.accessToken
		// 		+ "&department_id=" + department.getId();
		String address = Config.dingding().getOapiAddress() + "/topapi/v2/user/list?access_token=" + this.accessToken;
		Type type = new TypeToken<DingdingResponse<DingdingUserPageResult>> () {}.getType();
		List<User> list = new ArrayList<>();
		boolean hasMore = true;
		int cursor = 0;
		while(hasMore) {
			DingdingUserListPost body = new DingdingUserListPost();
			body.setDept_id(department.getDept_id());
			body.setCursor(cursor);
			body.setSize(100);
			String resString = HttpConnection.postAsString(address, null, body.toString());
			if (logger.isDebugEnabled()) {
				logger.debug("分页查询部门用户  response {}", resString);
			}
			DingdingResponse<DingdingUserPageResult> response = XGsonBuilder.instance().fromJson(resString, type);
			if (response.getErrcode() != 0) {
				throw new ExceptionListUser(response.getErrcode(), response.getErrmsg());
			}
			DingdingUserPageResult pageResult = response.getResult();
			if (pageResult != null && BooleanUtils.isTrue( pageResult.getHas_more() )) {
				cursor = pageResult.getNext_cursor();
			} else {
				hasMore = false;
			}
			list.addAll(pageResult.getList());
		}
		return list;
	}

	public List<User> listAllUser() {
		return users;
	}

	public List<User> listUser(Department org) throws Exception {
		return users.stream().filter(o ->  ListTools.contains(o.getDept_id_list(), org.getDept_id()))
				.collect(Collectors.toList());
	}

	public List<Department> listSub(Department org) throws Exception {
		return orgs.stream().filter(o -> {
			return Objects.equals(o.getParent_id(), org.getDept_id()) ? true : false;
		}).sorted(Comparator.comparing(Department::getOrder, Comparator.nullsLast(Long::compareTo)))
				.collect(Collectors.toList());
	}

	/**
	 * 查询钉钉组织的 post 对象
	 */
	public static class DingdingDepartmentPost extends GsonPropertyObject {

		private static final long serialVersionUID = 2344247634146398572L;
		private Long dept_id;

		public Long getDept_id() {
			return dept_id;
		}

		public void setDept_id(Long dept_id) {
			this.dept_id = dept_id;
		}

	}

	/**
	 * 根据组织 id  进行人员查询  post  对象
	 */
	public static class DingdingUserListPost extends GsonPropertyObject {

		private static final long serialVersionUID = -8815089632657236893L;

		private Long dept_id;

		private Integer cursor; // 分页游标 分页查询的游标，最开始传0，后续传返回参数中的next_cursor值。
		private Integer size; // 分页大小 如  100

    public Long getDept_id() {
      return dept_id;
    }
    public void setDept_id(Long dept_id) {
      this.dept_id = dept_id;
    }
    public Integer getCursor() {
      return cursor;
    }
    public void setCursor(Integer cursor) {
      this.cursor = cursor;
    }
    public Integer getSize() {
      return size;
    }
    public void setSize(Integer size) {
      this.size = size;
    }





	}

}
