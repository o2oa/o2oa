package com.x.file.assemble.control.jaxrs.folder;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder;

class ActionListWithFolder extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder folder = emc.find(id, Folder.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!StringUtils.equals(folder.getPerson(), effectivePerson.getDistinguishedName())) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			List<String> ids = business.folder().listSubDirect(folder.getId());
			List<Wo> wos = emc.fetch(ids, Wo.copier);
			wos.stream().forEach(o -> {
				try {
					setCount(business, o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			wos = wos.stream().sorted(Comparator.comparing(Folder::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends AbstractWoFolder {

		private static final long serialVersionUID = 6721942171341743439L;

		protected static WrapCopier<Folder, Wo> copier = WrapCopierFactory.wo(Folder.class, Wo.class,
				JpaObject.singularAttributeField(Folder.class, true, true), null);

	}

}