package o2.collect.assemble.jaxrs.collect;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import o2.collect.assemble.ThisServletContextListener;

class ActionPromptErrorLogReceive extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPromptErrorLogReceive.class);

	ActionResult<WrapOutBoolean> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		wi.setAddress(request.getRemoteAddr());
		logger.print("接收到源地址:{}, 名称:{}, 发送的提示错误日志.", wi.getAddress(), wi.getName());
		ThisServletContextListener.queuePromptErrorLogReceive.send(wi);
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = WrapOutBoolean.trueInstance();
		result.setData(wrap);
		return result;
	}

	public static class Wi extends QueuePromptErrorLogReceive.WiPromptErrorLogReceive {

	}

}
