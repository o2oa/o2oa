package com.x.pan.assemble.control.jaxrs.folder3;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Folder3;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

class ActionUpdateName extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionFolderNameEmpty();
			}
			Folder3 folder = emc.find(id, Folder3.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if(Business.TOP_FOLD.equals(folder.getSuperior())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			if(!business.zoneEditable(effectivePerson, folder.getId(), folder.getPerson())){
				throw new ExceptionAccessDenied(effectivePerson);
			}

			if(!wi.getName().equals(folder.getName())){
				if (business.folder3().exist(wi.getName(), folder.getSuperior(), folder.getZoneId(), FileStatusEnum.VALID.getName(), folder.getId())) {
					throw new ExceptionFolderNameExist(wi.getName());
				}
				emc.beginTransaction(Folder3.class);
				folder.setName(wi.getName());
				folder.setLastUpdatePerson(effectivePerson.getDistinguishedName());
				folder.setLastUpdateTime(new Date());
				emc.check(folder, CheckPersistType.all);
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;

		}
	}

	public static class Wi extends Folder3 {

		static WrapCopier<Wi, Folder3> copier = WrapCopierFactory.wi(Wi.class, Folder3.class,
				ListTools.toList(Folder3.name_FIELDNAME), null);

	}

	public static class Wo extends WrapBoolean {

	}

}
