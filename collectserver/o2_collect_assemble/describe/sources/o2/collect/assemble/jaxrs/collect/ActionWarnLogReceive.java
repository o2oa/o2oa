package o2.collect.assemble.jaxrs.collect;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import o2.collect.assemble.ThisServletContextListener;

class ActionWarnLogReceive extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionWarnLogReceive.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		wi.setAddress(request.getRemoteAddr());
		logger.print("接收到源地址:{}, 名称:{}, 发送的警告日志.", wi.getAddress(), wi.getName());
		ThisServletContextListener.queueWarnLogReceive.send(wi);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends QueueWarnLogReceive.WiWarnLogReceive {

	}

	public static class Wo extends WrapBoolean {
	}
}
