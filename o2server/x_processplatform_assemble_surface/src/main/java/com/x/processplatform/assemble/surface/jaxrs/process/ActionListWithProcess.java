package com.x.processplatform.assemble.surface.jaxrs.process;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ActionListWithProcess extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(ListTools.isEmpty(wi.getProcessList())){
				result.setData(wos);
				return result;
			}
			wos = Wo.copier.copy(business.process().listObjectWithProcess(wi.getProcessList(), wi.isIncludeEdition()));
			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("流程(多值逗号隔开)")
		private List<String> processList;

		@FieldDescribe("是否同时查询同版本的流程(true|false)")
		private boolean includeEdition;

		public List<String> getProcessList() {
			return processList;
		}

		public void setProcessList(List<String> processList) {
			this.processList = processList;
		}

		public boolean isIncludeEdition() {
			return includeEdition;
		}

		public void setIncludeEdition(boolean includeEdition) {
			this.includeEdition = includeEdition;
		}
	}

	public static class Wo extends Process {

		private static final long serialVersionUID = -4124351386819473248L;

		static WrapCopier<Process, Wo> copier = WrapCopierFactory.wo(Process.class, Wo.class,
				Arrays.asList(Process.id_FIELDNAME, Process.name_FIELDNAME, Process.alias_FIELDNAME,
						Process.edition_FIELDNAME, Process.editionNumber_FIELDNAME), null);
	}

}