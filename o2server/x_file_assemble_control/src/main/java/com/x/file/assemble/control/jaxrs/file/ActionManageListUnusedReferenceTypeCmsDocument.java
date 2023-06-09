package com.x.file.assemble.control.jaxrs.file;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

public class ActionManageListUnusedReferenceTypeCmsDocument extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListUnusedReferenceTypeCmsDocument.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		if (effectivePerson.isNotManager()) {
			throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
		}
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = list();
		result.setData(wos);
		result.setCount((long) wos.size());
		return result;
	}

	private List<Wo> list() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> ids = emc.ids(com.x.cms.core.entity.Document.class);
			TreeSet<String> set = new TreeSet<>(ids);
			List<File> os = emc.listEqual(File.class, File.REFERENCETYPE_FIELDNAME, ReferenceType.cmsDocument);
			List<File> list = os.stream().filter(o -> set.contains(o.getReference())).collect(Collectors.toList());
			return Wo.copier.copy(list);
		}
	}

	public static class Wo extends File {

		private static final long serialVersionUID = -6882645958995525779L;

		static WrapCopier<File, Wo> copier = WrapCopierFactory.wo(File.class, Wo.class,
				JpaObject.singularAttributeField(File.class, true, true), null);

	}
}