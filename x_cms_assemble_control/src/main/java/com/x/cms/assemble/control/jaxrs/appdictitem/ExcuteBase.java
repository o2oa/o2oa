package com.x.cms.assemble.control.jaxrs.appdictitem;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.AppDictLobItem;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( AppDictItem.class );
	
	protected LogService logService = new LogService();
	
	protected void deleteWithAppDictItem( String appId, String appDictId, String... paths ) throws Exception {
		AppDictLobItem lob = null;
		Business business = null;
		List<AppDictItem> exists = null;
		AppDict dict = null;
		String cacheKey = appId + "." + appDictId + ".path." + StringUtils.join(paths, ".");
		
		try {
			dict = getAppDict(appId, appDictId);
			if (null == dict) {
				throw new Exception("appDictId unique name :" + appDictId + " not existed with appId{id:" + appId + "}.");
			}
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				business = new Business(emc);
				exists = business.getAppDictItemFactory().listWithAppDictWithPath(dict.getId(), paths);
				if (exists.isEmpty()) {
					throw new Exception("appDictId{id:" + appDictId + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
				}
				
				emc.beginTransaction( AppDictItem.class );
				emc.beginTransaction( AppDictLobItem.class );
				
				for (AppDictItem o : exists) {
					lob = emc.find( o.getLobItem(), AppDictLobItem.class );
					if (null != lob) {
						emc.remove( lob );
					}
					emc.remove(o);
				}
				if (NumberUtils.isNumber(paths[paths.length - 1])) {
					int position = paths.length - 1;
					for (AppDictItem o : business.getAppDictItemFactory().listWithAppDictWithPathWithAfterLocation(dict.getId(), NumberUtils.toInt(paths[position]),
							paths)) {
						o.path( Integer.toString(o.pathLocation(position) - 1), position );
					}
				}
				emc.commit();
				
				ApplicationCache.notify( AppDict.class );
				ApplicationCache.notify( AppDictItem.class, cacheKey );
			}
		} catch (Exception e) {
			throw new Exception("deleteWithAppDictItem error.", e);
		}
	}

	protected AppDict getAppDict( String appId, String uniqueName ) throws Exception {
		try {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				//获取到AppDict的ID
				String id = business.getAppDictFactory().getWithAppWithUniqueName( appId, uniqueName );
				if (null != id) {
					AppDict dict = business.entityManagerContainer().find( id, AppDict.class );
					if (null != dict) {
						return dict;
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("get appDictId unique name:" + uniqueName + " with appId{id:" + appId + "} error.");
		}
		return null;
	}
}
