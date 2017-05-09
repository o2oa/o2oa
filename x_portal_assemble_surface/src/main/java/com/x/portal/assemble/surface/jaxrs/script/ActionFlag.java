package com.x.portal.assemble.surface.jaxrs.script;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.wrapin.WrapInScript;
import com.x.portal.assemble.surface.wrapout.WrapOutScript;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

class ActionFlag extends ActionBase {

	ActionResult<WrapOutScript> execute(EffectivePerson effectivePerson, String flag, String portalId,
			JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WrapInScript wrapIn = this.convertToWrapIn(jsonElement, WrapInScript.class);
			ActionResult<WrapOutScript> result = new ActionResult<>();
			Portal portal = business.portal().pick(portalId);
			if (null == portal) {
				throw new PortalNotExistedException(portalId);
			}
			if (!business.portal().visible(effectivePerson, portal)) {
				throw new PortalAccessDeniedException(effectivePerson.getName(), portal.getName(), portal.getId());
			}
			List<Script> list = new ArrayList<>();
			for (Script o : business.script().listScriptNestedWithPortalWithFlag(portal, flag)) {
				if ((!ListTools.contains(wrapIn.getImportedList(), o.getAlias()))
						&& (!ListTools.contains(wrapIn.getImportedList(), o.getName()))
						&& (!ListTools.contains(wrapIn.getImportedList(), o.getId()))) {
					list.add(o);
				}
			}
			StringBuffer buffer = new StringBuffer();
			List<String> imported = new ArrayList<>();
			for (Script o : list) {
				buffer.append(o.getText());
				buffer.append(SystemUtils.LINE_SEPARATOR);
				imported.add(o.getId());
				imported.add(o.getName());
				imported.add(o.getAlias());
			}
			WrapOutScript wrap = new WrapOutScript();
			wrap.setImportedList(imported);
			wrap.setText(buffer.toString());
			result.setData(wrap);
			return result;
		}
	}
}
// WrapInScript wrapIn = this.convertToWrapIn(jsonElement, WrapInScript.class);
// try (EntityManagerContainer emc =
// EntityManagerContainerFactory.instance().create()) {
// Business business = new Business(emc);
// Application application = business.application().pick(applicationFlag);
// if (null == application) {
// throw new ApplicationNotExistedException(applicationFlag);
// }
// List<Script> list = new ArrayList<>();
// for (Script o :
// business.script().listScriptNestedWithWithApplicationWithUniqueName(application,
// flag)) {
// if ((!wrapIn.getImportedList().contains(o.getAlias()))
// && (!wrapIn.getImportedList().contains(o.getName()))
// && (!wrapIn.getImportedList().contains(o.getId()))) {
// list.add(o);
// }
// }
// StringBuffer buffer = new StringBuffer();
// List<String> imported = new ArrayList<>();
// for (Script o : list) {
// buffer.append(o.getText());
// buffer.append(SystemUtils.LINE_SEPARATOR);
// imported.add(o.getId());
// imported.add(o.getName());
// imported.add(o.getAlias());
// }
// WrapOutScript wrap = new WrapOutScript();
// wrap.setImportedList(imported);
// wrap.setText(buffer.toString());
// result.setData(wrap);
// }
// }
// }