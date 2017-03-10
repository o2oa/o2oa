package com.x.cms.assemble.control.jaxrs.templateform;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
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

class ActionListWithCategory extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutTemplateFormSimple>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<List<WrapOutTemplateFormSimple>> result = new ActionResult<>();
		List<WrapOutTemplateFormSimple> wraps = null;
		List<String> ids = null;
		List<TemplateForm> os = null;
		Business business = null;
		WrapInTemplateForm wrapIn = null;
		Boolean check = true;
		
		try{
			wrapIn = this.convertToWrapIn( jsonElement, WrapInTemplateForm.class );
		}catch( Exception e){
			check = false;
			throw e;
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( "category", wrapIn.getCategory() );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutTemplateFormSimple> ) element.getObjectValue();
			result.setData( wraps );
		} else {
			if( check ){
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					wraps = new ArrayList<>();
					business = new Business(emc);
					ids = business.templateFormFactory().listWithCategory( wrapIn.getCategory() );
					os = emc.list(TemplateForm.class, ids);
					wraps = simpleOutCopier.copy(os);
					SortTools.asc(wraps, "name");
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
					result.setData(wraps);
					
				}
			}
		}
		return result;
	}

}