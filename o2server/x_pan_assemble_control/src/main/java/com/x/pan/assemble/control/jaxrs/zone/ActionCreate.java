package com.x.pan.assemble.control.jaxrs.zone;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.*;
import org.apache.commons.lang3.StringUtils;

/**
 * 创建共享区
 * @author sword
 */
public class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionFolderNameEmpty();
			}
			if (business.folder3().exist(wi.getName(), Business.TOP_FOLD, null, FileStatusEnum.VALID.getName(), null)) {
				throw new ExceptionFolderNameExist(wi.getName());
			}
			FileConfig3 config = business.getSystemConfig();
			if(config.getProperties() != null && ListTools.isNotEmpty(config.getProperties().getZoneAdminList())){
				if(!ListTools.containsAny(config.getProperties().getZoneAdminList(), business.getUserInfo(effectivePerson.getDistinguishedName()))){
					throw new ExceptionAccessDenied(effectivePerson);
				}
			}
			emc.beginTransaction(Folder3.class);
			emc.beginTransaction(ZonePermission.class);
			Folder3 folder = new Folder3(wi.getName(), effectivePerson.getDistinguishedName(), Business.TOP_FOLD, null);
			folder.setDescription(wi.getDescription());
			ZonePermission zonePermission = new ZonePermission(effectivePerson.getDistinguishedName(), ZoneRoleEnum.ADMIN.getValue(),
					folder.getZoneId(), effectivePerson.getDistinguishedName());
			emc.persist(folder, CheckPersistType.all);
			emc.persist(zonePermission, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(ZonePermission.class);
			Wo wo = new Wo();
			wo.setId(folder.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Folder3 {

		static WrapCopier<Wi, Folder3> copier = WrapCopierFactory.wi(Wi.class, Folder3.class, ListTools.toList(Folder3.name_FIELDNAME, Folder3.description_FIELDNAME),
				null);

	}

	public static class Wo extends WoId {

	}

}
