package com.x.program.center.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ZhengwuDingdingPullSyncOrganizationInit implements Job {

	private static Logger logger = LoggerFactory.getLogger(ZhengwuDingdingPullSyncOrganizationInit.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (StringUtils.isNotEmpty(Config.zhengwuDingding().getSyncOrganizationCallbackAddress())) {
				/* 注册地址 */
				logger.print("注册政务钉钉注册回调事件地址:{}.", Config.zhengwuDingding().getSyncOrganizationCallbackAddress());
				this.regist();
			} else {
				/* 取消人员组织回调 */
				logger.print("注销政务钉钉注册回调事件地址.");
				this.delete();
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void regist() throws Exception {
		String address = Config.zhengwuDingding().getOapiAddress() + "/call_back/regist?access_token="
				+ Config.zhengwuDingding().appAccessToken();
		RegistBody body = new RegistBody();
		body.setUrl(Config.zhengwuDingding().getSyncOrganizationCallbackAddress());
		RegistRasp resp = HttpConnection.postAsObject(address, null, body.toString(), RegistRasp.class);
		if (resp.getRetCode() != 0) {
			throw new ExceptionZhengwuDingdingRegistCallback(resp.getRetCode(), resp.getRetMessage());
		}
	}

	private void delete() throws Exception {
		String address = Config.zhengwuDingding().getOapiAddress() + "/call_back/delete?access_token="
				+ Config.zhengwuDingding().appAccessToken();
		DeleteRasp resp = HttpConnection.postAsObject(address, null, null, DeleteRasp.class);
		if (resp.getRetCode() != 0) {
			throw new ExceptionZhengwuDingdingDeleteCallback(resp.getRetCode(), resp.getRetMessage());
		}
	}

	// private RegistBody get() throws Exception {
	// String address = Config.zhengwuDingding().getOapiAddress() +
	// "/call_back/get?access_token="
	// + Config.zhengwuDingding().appAccessToken();
	// GetRasp resp = HttpConnection.getAsObject(address, null, GetRasp.class);
	// if (resp.getRetCode() != 0) {
	// throw new ExceptionZhengwuDingdingGetCallback(resp.getRetCode(),
	// resp.getRetMessage());
	// }
	// return resp.getRetData();
	// }

	public static class RegistBody extends GsonPropertyObject {
		private String url;
		private List<String> call_back_tag = new ArrayList<>(ListTools.toList("user_add_org", "user_modify_org",
				"user_leave_org", "org_dept_create", "org_dept_modify", "org_dept_remove"));

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public List<String> getCall_back_tag() {
			return call_back_tag;
		}

		public void setCall_back_tag(List<String> call_back_tag) {
			this.call_back_tag = call_back_tag;
		}

	}

	public static class GetRasp extends GsonPropertyObject {
		private Integer retCode;
		private String retMessage;

		private RegistBody retData;

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

		public RegistBody getRetData() {
			return retData;
		}

		public void setRetData(RegistBody retData) {
			this.retData = retData;
		}
	}

	public static class RegistRasp extends GsonPropertyObject {
		private Integer retCode;
		private String retMessage;

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
	}

	public static class DeleteRasp extends GsonPropertyObject {
		private Integer retCode;
		private String retMessage;

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
	}

}
