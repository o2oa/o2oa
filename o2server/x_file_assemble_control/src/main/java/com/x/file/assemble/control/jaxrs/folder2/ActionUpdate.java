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
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

class ActionUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionFolderNameEmpty();
			}
			Folder2 folder = emc.find(id, Folder2.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!StringUtils.equalsIgnoreCase(effectivePerson.getDistinguishedName(), folder.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			wi.setSuperior(StringUtils.trimToEmpty(wi.getSuperior()));
			if(StringUtils.isNotBlank(wi.getSuperior())) {
				Folder2 supFolder = emc.find(wi.getSuperior(), Folder2.class);
				if (null == supFolder) {
					throw new ExceptionFolderNotExist(wi.getSuperior());
				}
				if (!StringUtils.equalsIgnoreCase(effectivePerson.getDistinguishedName(), supFolder.getPerson())) {
					throw new ExceptionAccessDenied(effectivePerson.getName());
				}
				List<String> ids = new ArrayList<>();
				ids.add(folder.getId());
				ids.addAll(business.folder2().listSubNested(folder.getId(),"正常"));
				if(ids.contains(folder.getSuperior())){
					throw new Exception("superior can not be sub folder.");
				}
			}

			this.exist(business, effectivePerson, wi.getName(), wi.getSuperior(), folder.getId());
			emc.beginTransaction(Folder2.class);
			folder.setName(wi.getName());
			folder.setSuperior(wi.getSuperior());
			emc.check(folder, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			result.setData(wo);
			return result;

		}
	}

	public static class Wi extends Folder2 {

		private static final long serialVersionUID = 3965042303681243568L;

		static WrapCopier<Wi, Folder2> copier = WrapCopierFactory.wi(Wi.class, Folder2.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WrapBoolean {

	}

}
