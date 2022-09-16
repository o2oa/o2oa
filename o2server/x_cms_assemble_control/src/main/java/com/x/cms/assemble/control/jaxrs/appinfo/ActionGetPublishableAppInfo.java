package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfoConfig;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;

import javax.servlet.http.HttpServletRequest;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author sword
 */
public class ActionGetPublishableAppInfo extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGetPublishableAppInfo.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String appId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Business business = new Business(null);
		List<Wo> wos = new ArrayList<>();
		Boolean isXAdmin = business.isManager(effectivePerson);
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName, appId, isXAdmin );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((Wo)optional.get());
		} else {
			if ( isXAdmin ) {
				wos = listPublishAbleAppInfoByPermission( personName, isAnonymous, null, "all", "全部", isXAdmin, 1000 );
			} else {
				List<Wo> wos_out = listPublishAbleAppInfoByPermission( personName, isAnonymous, null,  "all", "全部", isXAdmin, 1000 );
				for( Wo wo : wos_out ) {
					if( ListTools.isNotEmpty( wo.getWrapOutCategoryList() )) {
						wos.add( wo );
					}
				}
			}
			if(ListTools.isNotEmpty(wos)) {
				for( Wo wo : wos ) {
					if( wo.getId().equalsIgnoreCase( appId )) {
						AppInfoConfig appInfoConfig = appInfoServiceAdv.getConfigObject(appId);
						if( appInfoConfig != null ){
							wo.setConfig( appInfoConfig.getConfig() );
						}else{
							wo.setConfig( "{}" );
						}
						CacheManager.put(cacheCategory, cacheKey, wo);
						result.setData( wo );
						break;
					}
				}
			}
		}
		return result;
	}

}
