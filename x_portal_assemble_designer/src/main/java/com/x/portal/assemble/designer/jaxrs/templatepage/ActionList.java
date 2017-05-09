package com.x.portal.assemble.designer.jaxrs.templatepage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutTemplatePage;
import com.x.portal.core.entity.TemplatePage;

class ActionList extends ActionBase {
	ActionResult<Map<String, List<WrapOutTemplatePage>>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Map<String, List<WrapOutTemplatePage>>> result = new ActionResult<>();
			List<String> ids = new ArrayList<>();
			if (business.isPortalManager(effectivePerson)) {
				ids = business.templatePage().list();
			} else {
				ids = business.templatePage().listEditable(effectivePerson);
			}
			List<WrapOutTemplatePage> list = outCopier.copy(emc.list(TemplatePage.class, ids));
			Map<String, List<WrapOutTemplatePage>> group = list.stream()
					.collect(Collectors.groupingBy(o -> Objects.toString(o.getCategory(), "")));
			Map<String, List<WrapOutTemplatePage>> sort = group.entrySet().stream()
					.sorted((e1, e2) -> ObjectUtils.compare(e1.getKey(), e2.getKey())).collect(Collectors
							.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			for (Entry<String, List<WrapOutTemplatePage>> en : sort.entrySet()) {
				SortTools.asc(en.getValue(), "name");
			}
			result.setData(sort);
			return result;
		}
	}

}