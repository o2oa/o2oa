package com.x.cms.assemble.control.jaxrs.appdictitem;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.entity.item.ItemType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.AppDictLobItem;

public class ExcuteSave extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( EffectivePerson effectivePerson, String appId, String uniqueName, JsonElement jsonElement, String... paths ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		ItemConverter<AppDictItem> converter = null;
		AppDict dict = null;
		AppDictLobItem lob = null;
		Business business = null;
		String[] parentPaths = null;
		String[] cursorPaths = null;
		String[] ps = null;
		AppDictItem parent = null;
		AppDictItem cursor = null;
		List<AppDictItem> adds = null;
		Integer index = null;
		String cacheKey = appId + "." + uniqueName + ".path." + StringUtils.join(paths, ".");
		
		try {
			dict = getAppDict( appId, uniqueName );
			if (null == dict) {
				throw new Exception("appDictId unique name :" + uniqueName + " not existed with appId{id:" + appId + "}.");
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				business = new Business(emc);
				parentPaths = new String[] { "", "", "", "", "", "", "", "" };
				cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
				for (int i = 0; i < paths.length - 1; i++) {
					parentPaths[i] = paths[i];
					cursorPaths[i] = paths[i];
				}
				cursorPaths[paths.length - 1] = paths[paths.length - 1];
				parent = business.getAppDictItemFactory().getWithAppDictWithPath(dict.getId(), parentPaths[0], parentPaths[1], parentPaths[2],
						parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6], parentPaths[7]);
				if (null == parent) {
					throw new Exception("parent not existed.");
				}
				cursor = business.getAppDictItemFactory().getWithAppDictWithPath(dict.getId(), cursorPaths[0], cursorPaths[1], cursorPaths[2],
						cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6], cursorPaths[7]);
				converter = new ItemConverter<>(AppDictItem.class);
				
				emc.beginTransaction(AppDictItem.class);
				emc.beginTransaction(AppDictLobItem.class);
				
				if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
					/* 向数组里面添加一个成员对象 */
					index = business.getAppDictItemFactory().getArrayLastIndexWithAppDictWithPath(dict.getId(), paths);
					/* 新的路径开始 */
					ps = new String[paths.length + 1];
					for (int i = 0; i < paths.length; i++) {
						ps[i] = paths[i];
					}
					ps[paths.length] = Integer.toString(index + 1);
					adds = converter.disassemble(jsonElement, ps);
					for ( AppDictItem o : adds ) {
						if (o.isLobItem()) {
							lob = new AppDictLobItem();
							lob.setData(o.getStringLobValue());
							lob.setDistributeFactor(o.getDistributeFactor());
							o.setLobItem(lob.getId());
							emc.persist(lob);
						}
						o.setAppDictId(dict.getId());
						emc.persist(o);
					}
				} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
					/* 向parent对象添加一个属性值 */
					adds = converter.disassemble(jsonElement, paths);
					for (AppDictItem o : adds) {
						o.setAppDictId(dict.getId());
						if (o.isLobItem()) {
							lob = new AppDictLobItem();
							lob.setData(o.getStringLobValue());
							lob.setDistributeFactor(o.getDistributeFactor());
							o.setLobItem(lob.getId());
							emc.persist(lob);
						}
						emc.persist(o);
					}
				} else {
					throw new Exception("unexpected post with uniqueName{'uniqueName':" + uniqueName + "} path:" + StringUtils.join(paths, ".") + "json:" + jsonElement);
				}
				emc.commit();
				
				ApplicationCache.notify( AppDict.class );
				ApplicationCache.notify( AppDictItem.class, cacheKey );
			}
		} catch (Exception e) {
			throw new Exception("postWithAppDictItem error.", e);
		}
		result.setData( new WrapOutId( uniqueName ) );
		return result;
	}
	
}