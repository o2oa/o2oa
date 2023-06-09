package com.x.file.assemble.control.jaxrs.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

public class ActionManageCleanUnusedReferenceTypeCmsDocument extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageCleanUnusedReferenceTypeCmsDocument.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		if (effectivePerson.isNotManager()) {
			throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
		}
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = clear();
		result.setData(wos);
		result.setCount((long) wos.size());
		return result;
	}

	private List<Wo> clear() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> ids = emc.ids(com.x.cms.core.entity.Document.class);
			TreeSet<String> set = new TreeSet<>(ids);
			List<File> os = emc.listEqual(File.class, File.REFERENCETYPE_FIELDNAME, ReferenceType.cmsDocument);
			List<File> list = os.stream().filter(o -> !set.contains(o.getReference())).collect(Collectors.toList());
			if (!list.isEmpty()) {
				list.stream().map(File::getReference).distinct().forEach(r -> {
					Map<String, String> map = new HashMap<>();
					map.put(FileRemoveQueue.REFERENCETYPE, ReferenceType.cmsDocument.toString());
					map.put(FileRemoveQueue.REFERENCE, r);
					try {
						ThisApplication.fileRemoveQueue.send(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
			return Wo.copier.copy(list);
		}
	}

	public static class Wo extends File {

		private static final long serialVersionUID = -6882645958995525779L;

		static WrapCopier<File, Wo> copier = WrapCopierFactory.wo(File.class, Wo.class,
				JpaObject.singularAttributeField(File.class, true, true), null);

	}
}