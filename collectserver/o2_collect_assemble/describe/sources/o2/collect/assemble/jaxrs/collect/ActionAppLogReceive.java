package o2.collect.assemble.jaxrs.collect;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import o2.collect.assemble.ThisServletContextListener;
import o2.collect.core.entity.log.AppLog;

class ActionAppLogReceive extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionAppLogReceive.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (null != wi) {
			QueueAppLogReceive.WiAppLogReceive wiAppLogReceive = new QueueAppLogReceive.WiAppLogReceive();
			wiAppLogReceive.setAppLog(wi);
			wiAppLogReceive.setAddress(request.getRemoteAddr());
			logger.print("接收到源地址:{}, 名称:{}, 发送的App日志.", wiAppLogReceive.getAddress());
			ThisServletContextListener.queueAppLogReceive.send(wiAppLogReceive);
		} else {
			throw new ExceptionEmptyAppLog();
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

	public static class Wi extends AppLog {

		private static final long serialVersionUID = -1584642595334127222L;

	}

}
