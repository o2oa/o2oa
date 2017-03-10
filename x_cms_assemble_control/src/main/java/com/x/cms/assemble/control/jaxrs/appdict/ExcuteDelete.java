package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

public class ExcuteDelete extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppDict dict = emc.find( id, AppDict.class);
			if (null == dict) {
				throw new Exception("appDict{id:" + id + "} not existed.");
			}
			AppInfo appInfo = emc.find(dict.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new Exception("appInfo{id:" + dict.getAppId() + "} not existed.");
			}
			emc.beginTransaction(AppDict.class);
			emc.beginTransaction(AppDictItem.class);
			List<String> ids = business.getAppDictItemFactory().listWithAppDict( id );
			emc.delete( AppDictItem.class, ids );
			emc.remove( dict, CheckRemoveType.all );
			emc.commit();
			
			//记录日志
			logService.log( emc, effectivePerson.getName(), dict.getName(), dict.getAppId(), "", "", dict.getId(), "DICT", "删除" );
			wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

}