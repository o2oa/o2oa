package com.x.cms.assemble.control.jaxrs.appdictitem;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.AppDictLobItem;

public class ExcuteUpdate extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( EffectivePerson effectivePerson, String appId, String uniqueName, JsonElement jsonElement, String... paths ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		ItemConverter<AppDictItem> converter = null;
		List<AppDictItem> exists = null;
		Business business = null;
		AppDictLobItem lob = null;
		AppDict dict = null;
		List<AppDictItem> currents = null;
		List<AppDictItem> removes = null;
		List<AppDictItem> adds = null;
		String cacheKey = appId + "." + uniqueName + ".path." + StringUtils.join(paths, ".");
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			dict = getAppDict(appId, uniqueName);
			if (null == dict) {
				throw new Exception("appDictId unique name :" + uniqueName + " not existed with appId{id:" + appId + "}.");
			}
			
			business = new Business(emc);
			converter = new ItemConverter<>(AppDictItem.class);
			exists = business.getAppDictItemFactory().listWithAppDictWithPath(dict.getId(), paths);
			if (exists.isEmpty()) {
				throw new Exception("appDict{'uniqueName':" + uniqueName + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
			}
			
			emc.beginTransaction(AppDictItem.class);
			emc.beginTransaction(AppDictLobItem.class);
			
			currents = converter.disassemble(jsonElement, paths);
			removes = converter.subtract(exists, currents);
			adds = converter.subtract(currents, exists);
			
			for (AppDictItem o : removes) {
				if (o.isLobItem()) {
					lob = emc.find( o.getLobItem(), AppDictLobItem.class );
					if (null != lob) {
						emc.remove(lob);
					}
				}
				emc.remove(o);
			}
			for (AppDictItem o : adds) {
				o.setAppDictId(dict.getId());
				if (o.isLobItem()) {
					lob = emc.find( o.getLobItem(), AppDictLobItem.class );
					if (null != lob) {
						emc.remove(lob);
					}
				}
				emc.persist(o);
			}
			emc.commit();
			
			ApplicationCache.notify( AppDict.class );
			ApplicationCache.notify( AppDictItem.class, cacheKey );
			
		} catch (Exception e) {
			throw new Exception("putWithAppDictItemWithPath error.", e);
		}
		result.setData( new WrapOutId( uniqueName ) );
		return result;
	}
	
}