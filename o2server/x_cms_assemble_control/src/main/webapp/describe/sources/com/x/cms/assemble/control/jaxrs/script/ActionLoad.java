package com.x.cms.assemble.control.jaxrs.script;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Script;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.util.ArrayList;
import java.util.List;

class ActionLoad extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLoad.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String applicationFlag,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppInfo appInfo = business.getAppInfoFactory().pick(applicationFlag);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExists(applicationFlag);
			}
			List<Script> list = new ArrayList<>();
			for (Script o : business.getScriptFactory().listScriptNestedWithAppInfoWithUniqueName(appInfo.getId(), flag)) {
				if ((null != wi) && (null != wi.getImportedList())
						&& (!this.contains(wi.getImportedList(), o.getAlias()))
						&& (!this.contains(wi.getImportedList(), o.getName()))
						&& (!this.contains(wi.getImportedList(), o.getId()))) {
					list.add(o);
				} else {
					list.add(o);
				}
			}
			logger.debug(effectivePerson, "find {} script will import with flag:{}, cms appInfo:{}, imported:{}.", "["
					+ StringUtils.join(ListTools.extractProperty(list, "name", String.class, false, false), ",") + "]",
					flag, applicationFlag, "[" + StringUtils.join(wi.getImportedList(), ",") + "]");
			StringBuffer buffer = new StringBuffer("");
			List<String> imported = new ArrayList<>();
			for (Script o : list) {
				buffer.append(o.getText());
				buffer.append(SystemUtils.LINE_SEPARATOR);
				imported.add(o.getId());
				if (StringUtils.isNotEmpty(o.getName())) {
					imported.add(o.getName());
				}
				if (StringUtils.isNotEmpty(o.getAlias())) {
					imported.add(o.getAlias());
				}
			}
			Wo wo = new Wo();
			wo.setImportedList(imported);
			wo.setText(buffer.toString());
			result.setData(wo);
		}
		return result;
	}

	private boolean contains(List<String> list, String value) {
		if (StringUtils.isEmpty(value)) {
			return false;
		} else {
			return ListTools.contains(list, value);
		}
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("引入的脚本列表")
		private List<String> importedList;

		public List<String> getImportedList() {
			return importedList;
		}

		public void setImportedList(List<String> importedList) {
			this.importedList = importedList;
		}

	}

	public class Wo extends GsonPropertyObject {

		@FieldDescribe("脚本内容")
		private String text;

		@FieldDescribe("应用脚本")
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