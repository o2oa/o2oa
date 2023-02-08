package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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
import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 */
public class ActionListWhatICanViewAllDocType_WithAppType extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWhatICanViewAllDocType_WithAppType.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String appType )
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Business business = new Business(null);
		Boolean isManager = business.isManager( effectivePerson );
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName, appType, isAnonymous, isManager );
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
				List<String> appIds = business.getAppInfoFactory().listPeopleViewAppInfoIds(personName, unitNames, groupNames, appType, isManager);
				if(ListTools.isNotEmpty(appIds)){
					List<AppInfo> apps = emc.list(AppInfo.class, appIds);
					for(AppInfo appInfo : apps){
						Wo wo = Wo.copier2.copy(appInfo);
						try {
							wo.setConfig(appInfoServiceAdv.getConfigJson(wo.getId()));
						} catch (Exception e) {
							LOGGER.debug(e.getMessage());
						}
						boolean isAppManager = isManager || this.isAppInfoManager(personName, unitNames, groupNames, appInfo);
						List<String> cateIds = business.getCategoryInfoFactory().listPeopleViewIds(personName,
								unitNames, groupNames, wo.getId(), isAppManager);
						if(ListTools.isNotEmpty(cateIds)){
							wo.setWrapOutCategoryList(emc.fetch(cateIds, WoCategory.copier2));
						}
						if (StringUtils.isBlank(wo.getAppType())) {
							wo.setAppType("未分类");
						}
						wos.add(wo);
					}
					SortTools.asc( wos, AppInfo.appInfoSeq_FIELDNAME);
					CacheManager.put(cacheCategory, cacheKey, wos);
				}
				result.setData( wos );
			}
		}

		return result;
	}
}
