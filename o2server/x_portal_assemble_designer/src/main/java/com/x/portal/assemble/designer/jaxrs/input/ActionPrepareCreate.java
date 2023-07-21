package com.x.portal.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapPair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.wrap.WrapApplicationDict;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.File;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;
import com.x.portal.core.entity.wrap.WrapFile;
import com.x.portal.core.entity.wrap.WrapPage;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.portal.core.entity.wrap.WrapScript;
import com.x.portal.core.entity.wrap.WrapWidget;

class ActionPrepareCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPrepareCreate.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		// logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<Wo> wos = this.adjustForCreate(business, wi);
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> adjustForCreate(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		Portal exist_portal = business.entityManagerContainer().find(wi.getId(), Portal.class);
		if (null != exist_portal) {
			wos.add(new Wo(wi.getId(), JpaObject.createId()));
		}
		for (WrapWidget wrap : wi.getWidgetList()) {
			Widget exist_widget = business.entityManagerContainer().find(wrap.getId(), Widget.class);
			if (null != exist_widget) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapPage wrap : wi.getPageList()) {
			Page exist_page = business.entityManagerContainer().find(wrap.getId(), Page.class);
			if (null != exist_page) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapScript wrap : wi.getScriptList()) {
			Script exist_script = business.entityManagerContainer().find(wrap.getId(), Script.class);
			if (null != exist_script) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapFile wrap : wi.getFileList()) {
			File exist_file = business.entityManagerContainer().find(wrap.getId(), File.class);
			if (null != exist_file) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapApplicationDict wrap : wi.getApplicationDictList()) {
			ApplicationDict exist_applicationDict = business.entityManagerContainer().find(wrap.getId(),
					ApplicationDict.class);
			if (null != exist_applicationDict) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		return wos;
	}

	public static class Wi extends WrapPortal {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WrapPair {

		public Wo(String value, String replaceValue) {
			this.setFirst(value);
			this.setSecond(replaceValue);
		}

	}

}
