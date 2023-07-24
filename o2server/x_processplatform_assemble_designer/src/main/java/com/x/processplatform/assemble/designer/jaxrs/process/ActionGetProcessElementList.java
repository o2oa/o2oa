package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;

class ActionGetProcessElementList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetProcessElementList.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			check(effectivePerson, emc, id);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			final Map<String, Set<String>> map = new HashMap<>();
			if (ListTools.isNotEmpty(wi.getElementWiList())) {
				wi.getElementWiList().stream().forEach(elementWi -> {
					if (map.containsKey(elementWi.getElementType())) {
						map.get(elementWi.getElementType()).add(elementWi.getElementId());
					} else {
						Set<String> set = new HashSet<>();
						set.add(elementWi.getElementId());
						map.put(elementWi.getElementType(), set);
					}
				});
			}
			Wo wo = new Wo();
			for (Entry<String, Set<String>> en : map.entrySet()) {
				if ("route".equals(en.getKey())) {
					List<Route> routeList = emc.listEqualAndIn(Route.class, Route.process_FIELDNAME, id,
							JpaObject.id_FIELDNAME, en.getValue());
					wo.setRouteList(routeList);
				} else {
					@SuppressWarnings("unchecked")
					List<Activity> list = (List<Activity>) emc.listEqualAndIn(
							ActivityType.getClassOfActivityType(ActivityType.valueOf(en.getKey())),
							Route.process_FIELDNAME, id, JpaObject.id_FIELDNAME, en.getValue());
					if (wo.getActivityList() == null) {
						wo.setActivityList(list);
					} else {
						wo.getActivityList().addAll(list);
					}
				}
			}

			result.setData(wo);
			return result;
		}
	}

	private void check(EffectivePerson effectivePerson, EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business(emc);
		Process process = emc.fetch(id, Process.class, ListTools.toList(Process.application_FIELDNAME));
		if (null == process) {
			throw new ExceptionProcessNotExisted(id);
		}
		Application application = emc.find(process.getApplication(), Application.class);
		if (null == application) {
			throw new ExceptionApplicationNotExist(process.getApplication());
		}
		if (!business.editable(effectivePerson, application)) {
			throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(), application.getName(),
					application.getId());
		}
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 1178481980432997496L;

		@FieldDescribe("节点信息.")
		private List<Activity> activityList;

		@FieldDescribe("路由信息.")
		private List<Route> routeList;

		public List<Activity> getActivityList() {
			return activityList;
		}

		public void setActivityList(List<Activity> activityList) {
			this.activityList = activityList;
		}

		public List<Route> getRouteList() {
			return routeList;
		}

		public void setRouteList(List<Route> routeList) {
			this.routeList = routeList;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 3133186372114900337L;

		@FieldDescribe("查询元素列表(元素类型:route|begin|agent|...)")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "ElementWi", fieldValue = "{\"elementType\": \"元素类型\", \"elementId\": \"元素ID\"}")
		private List<ElementWi> elementWiList;

		public List<ElementWi> getElementWiList() {
			return elementWiList;
		}

		public void setElementWiList(List<ElementWi> elementWiList) {
			this.elementWiList = elementWiList;
		}
	}

	public static class ElementWi extends GsonPropertyObject {

		private static final long serialVersionUID = 1505830298561352310L;

		@FieldDescribe("元素类型（route|begin|agent|...）.")
		private String elementType;
		@FieldDescribe("元素ID.")
		private String elementId;

		public String getElementType() {
			return elementType;
		}

		public void setElementType(String elementType) {
			this.elementType = elementType;
		}

		public String getElementId() {
			return elementId;
		}

		public void setElementId(String elementId) {
			this.elementId = elementId;
		}
	}
}
