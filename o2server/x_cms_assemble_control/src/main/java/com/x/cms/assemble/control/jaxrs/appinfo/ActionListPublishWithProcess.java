package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 查询当前用可以发布的分类（分类需绑定流程）
 * @author sword
 */
public class ActionListPublishWithProcess extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListPublishWithProcess.class);

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Boolean isAnonymous = effectivePerson.isAnonymous();
		Business business = new Business(null);
		Boolean isManager = isAnonymous ? false : business.isManager( effectivePerson );
		String personName = effectivePerson.getDistinguishedName();

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName, isManager);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				List<String> unitNames = null;
				List<String> groupNames = null;
				if(!isManager && !isAnonymous) {
					unitNames = userManagerService.listUnitNamesWithPerson(personName);
					groupNames = userManagerService.listGroupNamesByPerson(personName);
				}
				List<String> appIds = new ArrayList<>();
				if(!isManager) {
					appIds = business.getAppInfoFactory().listPeoplePublishAppInfoIds(personName, unitNames, groupNames, false);
				}
				List<String> categoryIds = business.getCategoryInfoFactory().listPeoplePublishCategoryInfoIds(appIds, personName, unitNames, groupNames, isManager, true);
				if(ListTools.isNotEmpty(categoryIds)){
					List<WoCategory> categoryList = emc.fetch(categoryIds, WoCategory.copier2);
					final Map<String, List<WoCategory>> map = categoryList.stream().collect(Collectors.groupingBy(WoCategory::getAppId));
					appIds = ListTools.extractProperty(categoryList, CategoryInfo.appId_FIELDNAME, String.class, true, true);
					wos = emc.fetch(appIds, Wo.copier2);
					wos.stream().forEach(wo -> {
						try {
							wo.setConfig( appInfoServiceAdv.getConfigJson( wo.getId() ) );
						} catch (Exception e) {
							LOGGER.debug(e.getMessage());
						}
						wo.setWrapOutCategoryList(map.get(wo.getId()));
					});
					SortTools.asc( wos, AppInfo.appInfoSeq_FIELDNAME);
					CacheManager.put(cacheCategory, cacheKey, wos);
				}
				result.setData( wos );
			}
		}
		return result;
	}

}
