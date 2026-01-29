package com.x.program.init.jaxrs.externaldatasources;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.ExternalDataSources;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.Collections;
import java.util.List;

class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

	public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();
		result.setData(Collections.emptyList());
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 7463810035710030958L;
		@FieldDescribe("数据库类型.")
		private String type;
		@FieldDescribe("数据库名称.")
		private String name;
		@FieldDescribe("数据库连接配置.")
		private ExternalDataSources externalDataSources;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ExternalDataSources getExternalDataSources() {
			return externalDataSources;
		}

		public void setExternalDataSources(ExternalDataSources externalDataSources) {
			this.externalDataSources = externalDataSources;
		}

	}

}
