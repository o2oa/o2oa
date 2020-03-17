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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class ActionListWithFolder extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder2 folder = emc.find(id, Folder2.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!StringUtils.equals(folder.getPerson(), effectivePerson.getDistinguishedName())) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			List<String> ids = business.folder2().listSubDirect(folder.getId(),"正常");
			List<Wo> wos = emc.fetch(ids, Wo.copier);
			wos.stream().forEach(o -> {
				try {
					setCount(business, o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			wos = wos.stream().sorted(Comparator.comparing(Folder2::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends AbstractWoFolder {

		private static final long serialVersionUID = 6721942171341743439L;

		protected static WrapCopier<Folder2, Wo> copier = WrapCopierFactory.wo(Folder2.class, Wo.class,
				JpaObject.singularAttributeField(Folder2.class, true, true), null);

	}

}