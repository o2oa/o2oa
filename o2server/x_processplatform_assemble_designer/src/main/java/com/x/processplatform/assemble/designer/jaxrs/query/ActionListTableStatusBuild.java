package com.x.processplatform.assemble.designer.jaxrs.query;

import java.util.List;

import com.x.base.core.project.Applications;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.designer.ThisApplication;

class ActionListTableStatusBuild extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListTableStatusBuild.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> wos = ThisApplication.context().applications()
				.getQuery(x_query_service_processing.class, Applications.joinQueryUri("list", "status", "build"))
				.getDataAsList(Wo.class);

		result.setData(wos);
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		
		private static final long serialVersionUID = -5440962359854767814L;
		
		@FieldDescribe("标识")
		private String id;
		@FieldDescribe("名称")
		private String name;
		@FieldDescribe("说明")
		private String description;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}

	}

}