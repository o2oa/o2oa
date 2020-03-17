package com.x.portal.assemble.designer.jaxrs.templatepage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.TemplatePage;

class ActionListCategory extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = business.templatePage().listEditable(effectivePerson);
			List<TemplatePage> os = emc.fetch(ids, TemplatePage.class,
					ListTools.toList(TemplatePage.category_FIELDNAME));
			List<String> categories = ListTools.extractProperty(os, TemplatePage.category_FIELDNAME, String.class,
					false, false);
			Map<String, Long> group = categories.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
			LinkedHashMap<String, Long> sort = group.entrySet().stream()
					.sorted(Map.Entry.<String, Long>comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
							Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			for (Entry<String, Long> en : sort.entrySet()) {
				Wo wo = new Wo();
				wo.setName(en.getKey());
				wo.setCount(en.getValue());
				wos.add(wo);
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("类别")
		private String name;
		@FieldDescribe("数量")
		private Long count;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}
	}

}