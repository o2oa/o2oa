package com.x.processplatform.assemble.surface.jaxrs.script;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInScript;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutScript;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

public class ActionLoad extends ActionBase {
	ActionResult<WrapOutScript> execute(String flag, String applicationFlag, WrapInScript wrapIn) throws Exception {
		ActionResult<WrapOutScript> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			List<Script> list = new ArrayList<>();
			for (Script o : business.script().listScriptNestedWithWithApplicationWithUniqueName(application, flag)) {
				if ((!wrapIn.getImportedList().contains(o.getAlias()))
						&& (!wrapIn.getImportedList().contains(o.getName()))
						&& (!wrapIn.getImportedList().contains(o.getId()))) {
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
		}
		return result;
	}
}
