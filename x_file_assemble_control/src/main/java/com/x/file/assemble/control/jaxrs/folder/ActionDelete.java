package com.x.file.assemble.control.jaxrs.folder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.StorageMapping;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Folder;

public class ActionDelete {

	public WrapOutId execute(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Folder folder = emc.find(id, Folder.class, ExceptionWhen.not_found);
		if (!StringUtils.equalsIgnoreCase(effectivePerson.getName(), folder.getPerson())) {
			throw new Exception(
					"person{name:" + effectivePerson.getName() + "} can not update folder{id:" + folder.getId() + "}");
		}
		List<String> ids = new ArrayList<>();
		ids.add(folder.getId());
		ids.addAll(business.folder().listSubNested(folder.getId()));
		for (int i = ids.size() - 1; i >= 0; i--) {
			List<Attachment> attachments = emc.list(Attachment.class, business.attachment().listWithFolder(ids.get(i)));
			for (Attachment att : attachments) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, att.getStorage());
				att.deleteContent(mapping);
				EntityManager em = emc.beginTransaction(Attachment.class);
				em.remove(att);
				em.getTransaction().commit();
			}
			EntityManager m = emc.beginTransaction(Folder.class);
			emc.delete(Folder.class, ids.get(i));
			m.getTransaction().commit();
		}
		return new WrapOutId(folder.getId());
	}

}