package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

public class ExcuteUpdate extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( EffectivePerson effectivePerson, WrapInAppDict wrapIn, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppDict dict = emc.find( id, AppDict.class);
			if (null == dict) {
				throw new Exception("appDict{id:" + id + "} not existed.");
			}
			AppInfo appInfo = emc.find( wrapIn.getAppId(), AppInfo.class );
			if (null == appInfo) {
				throw new Exception("appInfo{id:" + wrapIn.getAppId() + "} not existed.");
			}
			emc.beginTransaction(AppDict.class);
			emc.beginTransaction(AppDictItem.class);
			wrapIn.copyTo(dict, JpaObject.ID, JpaObject.DISTRIBUTEFACTOR, "application");
			emc.check(dict, CheckPersistType.all);
			ItemConverter<AppDictItem> converter = new ItemConverter<>(AppDictItem.class);
			List<AppDictItem> exists = business.getAppDictItemFactory().listEntityWithAppDict( id );
			List<AppDictItem> currents = converter.disassemble(wrapIn.getData());
			List<AppDictItem> removes = converter.subtract(exists, currents);
			List<AppDictItem> adds = converter.subtract(currents, exists);
			for (AppDictItem o : removes) {
				emc.remove(o);
			}
			for (AppDictItem o : adds) {
				o.setAppDictId( dict.getId() );
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();

			logService.log( emc, effectivePerson.getName(), dict.getName(), dict.getAppId(), "", "", dict.getId(), "DICT", "更新" );

			wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
}