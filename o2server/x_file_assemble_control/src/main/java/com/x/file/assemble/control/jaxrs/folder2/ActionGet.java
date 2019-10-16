package com.x.file.assemble.control.jaxrs.folder2;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder2;
import org.apache.commons.lang3.StringUtils;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Folder2 folder = emc.find(id, Folder2.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!effectivePerson.isManager() && !StringUtils.equals(folder.getPerson(), effectivePerson.getDistinguishedName())) {
				throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} access folder{id:" + id
						+ "} denied.");
			}
			Wo wo = Wo.copier.copy(folder);
			setCount(business, wo);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends AbstractWoFolder {

		private static final long serialVersionUID = 6721942171341743439L;

		protected static WrapCopier<Folder2, Wo> copier = WrapCopierFactory.wo(Folder2.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
