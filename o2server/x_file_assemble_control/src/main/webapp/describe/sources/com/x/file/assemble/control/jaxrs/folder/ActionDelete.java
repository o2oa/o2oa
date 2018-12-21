package com.x.file.assemble.control.jaxrs.folder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Folder;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder folder = emc.find(id, Folder.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!StringUtils.equalsIgnoreCase(effectivePerson.getDistinguishedName(), folder.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			List<String> ids = new ArrayList<>();
			ids.add(folder.getId());
			ids.addAll(business.folder().listSubNested(folder.getId()));
			for (int i = ids.size() - 1; i >= 0; i--) {
				List<Attachment> attachments = emc.list(Attachment.class,
						business.attachment().listWithFolder(ids.get(i)));
				for (Attachment att : attachments) {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
							att.getStorage());
					att.deleteContent(mapping);
					EntityManager em = emc.beginTransaction(Attachment.class);
					em.remove(att);
					em.getTransaction().commit();
				}
				EntityManager m = emc.beginTransaction(Folder.class);
				emc.delete(Folder.class, ids.get(i));
				m.getTransaction().commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}