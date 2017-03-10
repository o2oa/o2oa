package com.x.cms.assemble.control.jaxrs.templateform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.TemplateForm;

import net.sf.ehcache.Element;

class ActionListCategory extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		List<NameValueCountPair> wraps = null;
		LinkedHashMap<String, Long> sort = null;
		Map<String, Long> group = null;
		String cacheKey = ApplicationCache.concreteCacheKey( "category" );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			sort = ( LinkedHashMap<String, Long> ) element.getObjectValue();
			result.setData( wraps );
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				wraps = new ArrayList<>();
				Business business = new Business(emc);
				List<String> ids = business.templateFormFactory().list();
				List<TemplateForm> os = emc.fetchAttribute(ids, TemplateForm.class, "category");
				List<String> names = ListTools.extractProperty(os, "category", String.class, false, false);
				group = names.stream().collect(Collectors.groupingBy(e -> Objects.toString(e, ""), Collectors.counting()));
				sort = group.entrySet().stream().sorted(Map.Entry.<String, Long> comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
								Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
				for (Entry<String, Long> en : sort.entrySet()) {
					NameValueCountPair o = new NameValueCountPair();
					o.setName(en.getKey());
					o.setCount(en.getValue());
					wraps.add(o);
				}
				result.setData(wraps);
			}
		}
		return result;
	}

}
