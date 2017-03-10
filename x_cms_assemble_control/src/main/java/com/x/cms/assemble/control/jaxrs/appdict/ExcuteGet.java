package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

public class ExcuteGet extends ExcuteBase {
	
	protected ActionResult<WrapOutAppDict> execute( EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutAppDict> result = new ActionResult<>();
		WrapOutAppDict wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppDict appDict = emc.find( id, AppDict.class );
			if (null == appDict) {
				throw new Exception("appDict{id:" + id + "} not existed.");
			}
			AppInfo appInfo = emc.find( appDict.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new Exception("appInfo{id:" + appDict.getAppId() + "} not existed with appDictId{id:" + id + "}");
			}
			wrap = new WrapOutAppDict( appDict );
			List<AppDictItem> items = business.getAppDictItemFactory().listEntityWithAppDict( id );
			/* 由于需要排序重新生成可排序List */
			items = new ArrayList<>(items);
			ItemConverter<AppDictItem> converter = new ItemConverter<>(AppDictItem.class);
			JsonElement json = converter.assemble(items);
			wrap.setData(json);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

}