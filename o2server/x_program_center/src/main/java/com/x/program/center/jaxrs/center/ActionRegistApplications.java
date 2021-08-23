package com.x.program.center.jaxrs.center;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.program.center.CenterQueueRegistApplicationsBody;
import com.x.program.center.ThisApplication;

class ActionRegistApplications extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRegistApplications.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		boolean check = checkServerTime(wi.getServerTime());
		if (check) {
			CenterQueueRegistApplicationsBody body = gson.fromJson(wi.getValue(),
					CenterQueueRegistApplicationsBody.class);
			body.setNode(wi.getNode());
			ThisApplication.centerQueue.send(body);
			wo.setValue(true);
		} else {
			logger.warn("server time is too different, node:{}, time:{}.", wi.getNode(),
					DateTools.format(wi.getServerTime()));
			wo.setValue(false);
		}
		result.setData(wo);
		return result;
	}

	private boolean checkServerTime(Date registServerTime) {
		return !((null == registServerTime)
				|| (Math.abs(registServerTime.getTime() - (new Date()).getTime()) > 30 * 1000));
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 6563977941658372302L;

	}

	public static class Wi extends WrapString {

		private static final long serialVersionUID = -201106956161862479L;

		@FieldDescribe("节点名")
		private String node;

		@FieldDescribe("服务器时间")
		private Date serverTime;

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public Date getServerTime() {
			return serverTime;
		}

		public void setServerTime(Date serverTime) {
			this.serverTime = serverTime;
		}

	}

}