package com.x.processplatform.assemble.surface.jaxrs.data;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;

class ActionFetchWithJob extends BaseAction {

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement)
			throws Exception {
		ActionResult<JsonElement> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if ((!this.manager(business, effectivePerson))
					&& (emc.countEqual(Review.class, Review.person_FIELDNAME,
							effectivePerson.getDistinguishedName()) == 0)
					&& (!this.applicationControl(business, effectivePerson, job))) {
				throw new ExceptionJobAccessDenied(effectivePerson.getName(), job);
			}
			Map<String, Object> map = new ListOrderedMap<>();
			for (String path : wi.getPathList()) {
				map.put(path, this.getData(business, job, StringUtils.split(path, ".")));
			}
			result.setData(gson.toJsonTree(map));
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("查询路径.")
		private List<String> pathList;

		public List<String> getPathList() {
			return pathList;
		}

		public void setPathList(List<String> pathList) {
			this.pathList = pathList;
		}

	}

}