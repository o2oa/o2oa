package com.x.file.assemble.control.jaxrs.folder;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder;

class ActionUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionFolderNameEmpty();
			}
			Folder folder = emc.find(id, Folder.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!StringUtils.equalsIgnoreCase(effectivePerson.getDistinguishedName(), folder.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			this.exist(business, effectivePerson, wi.getName(), folder.getSuperior(), folder.getId());
			emc.beginTransaction(Folder.class);
			folder.setName(wi.getName());
			emc.check(folder, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			result.setData(wo);
			return result;

		}
	}

	public static class Wi extends Folder {

		private static final long serialVersionUID = 3965042303681243568L;

		static WrapCopier<Wi, Folder> copier = WrapCopierFactory.wi(Wi.class, Folder.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WrapBoolean {

	}

}
