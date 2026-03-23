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
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.ZonePermission;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

class ActionUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isBlank(wi.getName()) && StringUtils.isBlank(wi.getDescription())) {
				result.setData(wo);
				return result;
			}
			Folder3 folder = emc.find(id, Folder3.class);
			if (null == folder || !Business.TOP_FOLD.equals(folder.getSuperior())) {
				throw new ExceptionFolderNotExist(id);
			}
			boolean isManager = business.controlAble(effectivePerson);
			if(!isManager){
				boolean isZoneAdmin = business.folder3().isZoneAdmin(folder.getZoneId(), effectivePerson.getDistinguishedName());
				if(!isZoneAdmin){
					throw new ExceptionAccessDenied(effectivePerson.getName());
				}
			}
			if(StringUtils.isNotBlank(wi.getName()) && !wi.getName().equals(folder.getName())) {
				if (business.folder3().exist(wi.getName(), Business.TOP_FOLD, null, FileStatusEnum.VALID.getName(), id)) {
					throw new ExceptionFolderNameExist(wi.getName());
				}
				folder.setName(wi.getName());
			}
			if(StringUtils.isNotBlank(wi.getDescription())){
				folder.setDescription(wi.getDescription());
			}
			if(wi.getCapacity() != null){
				folder.setCapacity(wi.getCapacity());
			}
			emc.beginTransaction(Folder3.class);
			folder.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			folder.setLastUpdateTime(new Date());
			emc.check(folder, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(ZonePermission.class);
			wo.setValue(true);
			result.setData(wo);
			return result;

		}
	}

	public static class Wi extends Folder3 {

		static WrapCopier<Wi, Folder3> copier = WrapCopierFactory.wi(Wi.class, Folder3.class,
				ListTools.toList(Folder3.name_FIELDNAME, Folder3.description_FIELDNAME, Folder3.capacity_FIELDNAME),null);
	}

	public static class Wo extends WrapBoolean {

	}

}
