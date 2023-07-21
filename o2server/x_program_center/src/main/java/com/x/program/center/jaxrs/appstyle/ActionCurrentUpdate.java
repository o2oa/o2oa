package com.x.program.center.jaxrs.appstyle;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.AppStyle;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.tools.DateTools;
import com.x.portal.core.entity.Portal;
import com.x.program.center.Business;

class ActionCurrentUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			List<Portal> portals = this.listMobilePortal(business);
			wo.setValue(this.hash(Config.appStyle(), portals));
			result.setData(wo);
			return result;
		}
	}

	String hash(AppStyle appStyle, List<Portal> portals) {
		StringBuilder sb = new StringBuilder();
		sb.append(appStyle.toString());
		for (Portal portal : portals) {
			sb.append(null == portal ? "2018-01-01 00:00:00" : DateTools.format(portal.getUpdateTime()));
		}
		return DigestUtils.sha256Hex(sb.toString());
	}

	public static class Wo extends WrapString {

	}

}
