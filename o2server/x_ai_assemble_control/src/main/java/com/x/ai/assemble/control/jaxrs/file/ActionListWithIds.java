package com.x.ai.assemble.control.jaxrs.file;

import com.google.gson.JsonElement;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import java.util.ArrayList;
import java.util.List;

class ActionListWithIds extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionListWithIds.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			List<Wo> wos = new ArrayList<>();
			if(ListTools.isNotEmpty(wi.getIdList())){
				wos = emc.fetch(wi.getIdList(), Wo.copier);
			}

			result.setData(wos);
			result.setCount((long)wos.size());
			return result;
		}
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("文件id列表")
		private List<String> idList;

		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}
	}

	public static class Wo extends File {

		private static final long serialVersionUID = 5050265572359201452L;

		static WrapCopier<File, Wo> copier = WrapCopierFactory.wo(File.class, Wo.class,
				JpaObject.singularAttributeField(File.class, true, true), null);

	}

}
