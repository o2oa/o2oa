package com.x.cms.assemble.control.jaxrs.templateform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.TemplateForm;

class ActionListCategory extends BaseAction {

	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			List<NameValueCountPair> wraps = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = business.templateFormFactory().list();
			List<TemplateForm> os = emc.fetch(ids, TemplateForm.class, ListTools.toList(TemplateForm.category_FIELDNAME));
			List<String> names = ListTools.extractProperty(os, "category", String.class, false, false);
			Map<String, Long> group = names.stream()
					.collect(Collectors.groupingBy(e -> Objects.toString(e, ""), Collectors.counting()));
			LinkedHashMap<String, Long> sort = group.entrySet().stream()
					.sorted(Map.Entry.<String, Long> comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
							Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			for (Entry<String, Long> en : sort.entrySet()) {
				NameValueCountPair o = new NameValueCountPair();
				o.setName(en.getKey());
				o.setCount(en.getValue());
				wraps.add(o);
			}
			result.setData(wraps);
			return result;
		}
	}

}
