package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.List;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( EffectivePerson effectivePerson, WrapInAppDict wrapIn) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find(wrapIn.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new Exception("application{id:" + wrapIn.getAppId() + "} not existed.");
			}
			logger.debug("[post]system try to save new appdict......");
			emc.beginTransaction(AppDict.class);
			emc.beginTransaction(AppDictItem.class);
			AppDict appDict = new AppDict();
			wrapIn.copyTo(appDict, JpaObject.ID_DISTRIBUTEFACTOR);
			emc.persist(appDict, CheckPersistType.all);
			ItemConverter<AppDictItem> converter = new ItemConverter<>(AppDictItem.class);
			List<AppDictItem> list = converter.disassemble(wrapIn.getData());
			for (AppDictItem o : list) {
				o.setAppDictId(appDict.getId());
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();

			logService.log( emc, effectivePerson.getName(), appDict.getName(), appDict.getAppId(), "", "", appDict.getId(), "DICT", "新增" );
			
			wrap = new WrapOutId(appDict.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
}