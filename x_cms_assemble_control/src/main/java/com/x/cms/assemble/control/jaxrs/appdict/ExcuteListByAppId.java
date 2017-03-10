package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;

public class ExcuteListByAppId extends ExcuteBase {
	
	protected ActionResult<List<WrapOutAppDict>> execute( EffectivePerson effectivePerson, String appId ) throws Exception {
		ActionResult<List<WrapOutAppDict>> result = new ActionResult<>();
		List<WrapOutAppDict> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);			
			AppInfo appInfo = emc.find(appId, AppInfo.class);
			if (null == appInfo) {
				throw new Exception("appInfo{id:" + appId + "} not existed.");
			}
			List<String> ids = business.getAppDictFactory().listWithAppInfo(appId);
			for (AppDict o : emc.list(AppDict.class, ids)) {
				wraps.add(new WrapOutAppDict(o));
			}
			Collections.sort(wraps, new Comparator<WrapOutAppDict>() {
				public int compare(WrapOutAppDict o1, WrapOutAppDict o2) {
					return ObjectUtils.compare(o1.getName(), o2.getName(), true);
				}
			});
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
}