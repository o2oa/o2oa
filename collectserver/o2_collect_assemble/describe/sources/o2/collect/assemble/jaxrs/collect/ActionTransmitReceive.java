package o2.collect.assemble.jaxrs.collect;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import net.sf.ehcache.Element;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Unit;

class ActionTransmitReceive extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTransmitReceive.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wo wo = new Wo();
			wo.setValue(false);
			Business business = new Business(emc);
			Unit unit = business.validateUnit(wi.getName(), wi.getPassword());
			if (null == unit) {
				throw new ExceptionValidateUnitError(wi.getName());
			}
			Boolean needUpdate = true;
			String sha = DigestUtils.sha256Hex(wi.toString());
			Element element = business.queueTransmitReceiveCache().get(wi.getName());
			if ((null != element) && StringUtils.equals(sha, (String) element.getObjectValue())) {
				needUpdate = false;
			} else {
				QueueTransmitReceive.send(wi);
			}
			business.queueTransmitReceiveCache().put(new Element(wi.getName(), sha));
			logger.print("接收到同步内容数据,源地址:{},名称:{},内容是否需要更新:{}.", request.getRemoteAddr(), unit.getName(), needUpdate);
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends QueueTransmitReceive.WiTransmitReceive {

	}

	public static class Wo extends WrapBoolean {
	}
}
