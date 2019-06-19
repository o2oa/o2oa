package com.x.cms.assemble.control.jaxrs.script;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.SystemUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Script;


class ActionGetScriptNested extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ScriptAction.class);
	
	@SuppressWarnings("deprecation")
	ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String uniqueName, String flag, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				AppInfo appInfo = business.getAppInfoFactory().flag(flag);
				if (null == appInfo) {
					throw new Exception("appInfo{'flag':" + flag + "} not existed.");
				}
				List<Script> list = new ArrayList<>();
				for (Script o : business.getScriptFactory().listScriptNestedWithAppInfoWithUniqueName(appInfo.getId(),
						uniqueName)) {
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
				wrap = new Wo();
				wrap.setImportedList(imported);
				wrap.setText(buffer.toString());
				result.setData( wrap );
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}
	
	public class Wi extends GsonPropertyObject {

		private List<String> importedList;

		public List<String> getImportedList() {
			return importedList;
		}

		public void setImportedList(List<String> importedList) {
			this.importedList = importedList;
		}

	}
	
	public static class Wo extends GsonPropertyObject {

		private String text;

		private List<String> importedList;

		public List<String> getImportedList() {
			return importedList;
		}

		public void setImportedList(List<String> importedList) {
			this.importedList = importedList;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
}
