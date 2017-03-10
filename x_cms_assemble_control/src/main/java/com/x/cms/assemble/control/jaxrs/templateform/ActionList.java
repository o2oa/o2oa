package com.x.cms.assemble.control.jaxrs.templateform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.Control;
import com.x.cms.core.entity.element.TemplateForm;

import net.sf.ehcache.Element;

class ActionList extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<Map<String, List<WrapOutTemplateFormSimple>>> execute( EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Map<String, List<WrapOutTemplateFormSimple>>> result = new ActionResult<>();
		List<WrapOutTemplateFormSimple> wraps = null;
		LinkedHashMap<String, List<WrapOutTemplateFormSimple>> sort = null;
		Map<String, List<WrapOutTemplateFormSimple>> group = null;
		String cacheKey = ApplicationCache.concreteCacheKey( "all" );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			sort = ( LinkedHashMap<String, List<WrapOutTemplateFormSimple>> ) element.getObjectValue();
			result.setData( sort );
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				wraps = new ArrayList<>();
				Business business = new Business(emc);
				List<String> ids = business.templateFormFactory().list();
				List<TemplateForm> os = emc.list(TemplateForm.class, ids);
				wraps = simpleOutCopier.copy(os);
				Control control = new Control();
				/** 添加管理员和流程管理员删除的权限 */
				if (effectivePerson.isManager() || business.organization().role().hasAny(effectivePerson.getName(), RoleDefinition.ProcessPlatformManager)) {
					control.setAllowDelete(true);
				} else {
					control.setAllowDelete(false);
				}
				for (WrapOutTemplateFormSimple o : wraps) {
					o.setControl(control);
				}
				SortTools.asc(wraps, "name");
				group = wraps.stream().collect(Collectors.groupingBy(e -> Objects.toString(e.getCategory(), "")));
				sort = group.entrySet().stream().sorted(Map.Entry.<String, List<WrapOutTemplateFormSimple>> comparingByKey()).collect(Collectors
						.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
				result.setData(sort);
			}
		}
		return result;
	}

}
