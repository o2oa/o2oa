package com.x.cms.assemble.control.jaxrs.appdictitem;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.AppDictLobItem;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {
	
	protected ActionResult<JsonElement> execute( EffectivePerson effectivePerson, String appId, String uniqueName, String... paths ) throws Exception {
		ActionResult<JsonElement> result = new ActionResult<>();
		
		JsonElement jsonElement = null;
		AppDictLobItem lob = null;
		AppDict dict = null;
		String cacheKey = null;
		Element element = null;
		List<AppDictItem> list = null;
		ItemConverter<AppDictItem> converter = null;
		Business business = null;
		
		try {
			dict = getAppDict( appId, uniqueName );
			if (null == dict) {
				throw new Exception("appDictId unique name :" + uniqueName + " not existed with appId{id:" + appId + "}.");
			}
			
			cacheKey = appId + "." + uniqueName + ".path." + StringUtils.join(paths, ".");
			element = cache.get( cacheKey );
			
			if (null != element) {
				jsonElement = (JsonElement) element.getObjectValue();
			} else {
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					business = new Business(emc);
					list = business.getAppDictItemFactory().listWithAppDictWithPath(dict.getId(), paths);
					if( list != null && !list.isEmpty() ){
						for ( AppDictItem o : list ) {
							if ( o.isLobItem() ) {
								lob = emc.find( o.getLobItem(), AppDictLobItem.class );
								if (null != lob) {
									o.setStringLobValue(lob.getData());
								}
							}
						}
					}
					
					converter = new ItemConverter<>( AppDictItem.class );
					jsonElement = converter.assemble(list, paths.length);
					
					cache.put( new Element( cacheKey, jsonElement ) );
				}
			}
			result.setData( jsonElement );
		} catch (Exception e) {
			throw new Exception("getWithAppDictItem error.", e);
		}
		return result;
	}
	
}