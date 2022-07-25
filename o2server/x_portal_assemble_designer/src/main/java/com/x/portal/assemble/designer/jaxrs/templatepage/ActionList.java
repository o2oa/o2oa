package com.x.portal.assemble.designer.jaxrs.templatepage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.TemplatePage;

class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			List<String> ids = new ArrayList<>();
			if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.PortalManager))) {
				ids = business.templatePage().listEditable(effectivePerson);
			} else {
				ids = business.templatePage().list();
			}
			List<WoTemplatePage> list = WoTemplatePage.copier.copy(emc.list(TemplatePage.class, ids));
			Map<String, List<WoTemplatePage>> group = list.stream()
					.collect(Collectors.groupingBy(o -> Objects.toString(o.getCategory(), "")));
			Map<String, List<WoTemplatePage>> sort = group.entrySet().stream()
					.sorted((e1, e2) -> ObjectUtils.compare(e1.getKey(), e2.getKey())).collect(Collectors
							.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			Wo wo = new Wo();
			for (Entry<String, List<WoTemplatePage>> en : sort.entrySet()) {
				wo.put(en.getKey(),
						en.getValue().stream().sorted(
								Comparator.comparing(WoTemplatePage::getName, Comparator.nullsLast(String::compareTo)))
								.collect(Collectors.toList()));
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends LinkedHashMap<String, List<WoTemplatePage>> {

		private static final long serialVersionUID = 6662363584476991536L;

	}

	public static class WoTemplatePage extends TemplatePage {

		private static final long serialVersionUID = -7592184343034018992L;

		static WrapCopier<TemplatePage, WoTemplatePage> copier = WrapCopierFactory.wo(TemplatePage.class,
				WoTemplatePage.class, JpaObject.singularAttributeField(TemplatePage.class, true, true), null);

	}

}