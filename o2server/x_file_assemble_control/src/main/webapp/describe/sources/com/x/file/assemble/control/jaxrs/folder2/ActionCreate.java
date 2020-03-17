package com.x.file.assemble.control.jaxrs.folder2;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder2;
import org.apache.commons.lang3.StringUtils;

public class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionFolderNameEmpty();
			}
			if (this.exist(business, effectivePerson, wi.getName(), wi.getSuperior(), null)) {
				throw new ExceptionFolderNameExist(effectivePerson.getName(), wi.getName(), wi.getSuperior());
			}
			emc.beginTransaction(Folder2.class);
			Folder2 folder = Wi.copier.copy(wi);
			folder.setPerson(effectivePerson.getDistinguishedName());
			folder.setStatus("正常");
			emc.persist(folder, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(folder.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Folder2 {

		private static final long serialVersionUID = 3965042303681243568L;

		static WrapCopier<Wi, Folder2> copier = WrapCopierFactory.wi(Wi.class, Folder2.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

	}

}
