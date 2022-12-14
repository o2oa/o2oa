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
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 列示有权限看到文档的应用
 * @author sword
 */
public class ActionListHasDocument extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListHasDocument.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		String personName = effectivePerson.getDistinguishedName();

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Boolean isManager = business.isManager( effectivePerson );
				List<String> appIds;
				if(isManager){
					appIds = business.getDocumentFactory().listApp();
				}else{
					appIds = business.reviewFactory().listApp(personName);
				}
				if(ListTools.isNotEmpty(appIds)){
					wos = emc.fetch(appIds, Wo.copier2);
					wos.stream().forEach(wo -> {
						try {
							wo.setConfig( appInfoServiceAdv.getConfigJson( wo.getId() ) );
						} catch (Exception e) {
							LOGGER.debug(e.getMessage());
						}
						if(StringUtils.isBlank(wo.getAppType())){
							wo.setAppType("未分类");
						}
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
