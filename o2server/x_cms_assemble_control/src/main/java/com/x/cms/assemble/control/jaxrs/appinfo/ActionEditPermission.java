package com.x.cms.assemble.control.jaxrs.appinfo;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;

/**
 * 修改栏目权限
 * @author sword
 */
public class ActionEditPermission extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionEditPermission.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppInfo appInfo = emc.find(id, AppInfo.class);
			if(appInfo == null){
				throw new ExceptionEntityNotExist(id, AppInfo.class);
			}
			if (!effectivePerson.isSecurityManager() && !business.isAppInfoManager(effectivePerson, appInfo)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			emc.beginTransaction(AppInfo.class);
			Wi.copier.copy(wi, appInfo);
			emc.check(appInfo, CheckPersistType.all);
			emc.commit();
			new LogService().log(null, effectivePerson.getDistinguishedName(), appInfo.getAppName(), appInfo.getId(), "", "", "", "APPINFO", "修改权限");
		}
		CacheManager.notify(AppInfo.class);

		return result;
	}

	public static class Wi extends AppInfo {

		private static final long serialVersionUID = -3930163149493805939L;

		public static WrapCopier<Wi, AppInfo> copier = WrapCopierFactory.wi( Wi.class, AppInfo.class,
				ListTools.toList(AppInfo.viewablePersonList_FIELDNAME, AppInfo.viewableUnitList_FIELDNAME,
						AppInfo.viewableGroupList_FIELDNAME, AppInfo.publishablePersonList_FIELDNAME,
						AppInfo.publishableUnitList_FIELDNAME, AppInfo.publishableGroupList_FIELDNAME,
						AppInfo.manageablePersonList_FIELDNAME, AppInfo.manageableUnitList_FIELDNAME,
						AppInfo.manageableGroupList_FIELDNAME),
				null );
	}

	public static class Wo extends WoId {

	}

}
