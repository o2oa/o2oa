package com.x.program.center.jaxrs.script;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Script;

class ActionFlag extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			List<Script> list = new ArrayList<>();
			for (Script o : business.script().listScriptNestedWithFlag(flag)) {
				if ((!this.contains(wi.getImportedList(), o.getAlias()))
						&& (!this.contains(wi.getImportedList(), o.getName()))
						&& (!this.contains(wi.getImportedList(), o.getId()))) {
					list.add(o);
				}
			}
			StringBuffer buffer = new StringBuffer();
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
			return result;
		}
	}

	private boolean contains(List<String> list, String value) {
		if (StringUtils.isEmpty(value)) {
			return false;
		} else {
			return ListTools.contains(list, value);
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("已经导入的脚本")
		private List<String> importedList;

		public List<String> getImportedList() {
			return importedList;
		}

		public void setImportedList(List<String> importedList) {
			this.importedList = importedList;
		}

	}

	public static class Wo extends Script {

		private static final long serialVersionUID = -8067704098385000667L;

		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		private List<String> importedList;

		public List<String> getImportedList() {
			return importedList;
		}

		public void setImportedList(List<String> importedList) {
			this.importedList = importedList;
		}

	}

}
