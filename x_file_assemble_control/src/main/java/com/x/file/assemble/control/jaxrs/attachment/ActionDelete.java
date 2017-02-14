package com.x.file.assemble.control.jaxrs.attachment;

import static com.x.base.core.entity.StorageType.file;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.StorageMapping;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.Attachment;

public class ActionDelete {

	public ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			if (!StringUtils.equals(effectivePerson.getName(), attachment.getPerson())) {
				throw new Exception(
						"person{name:" + effectivePerson.getName() + "} delete attachment{id:" + id + "} denied.");
			}
			StorageMapping mapping = ThisApplication.storageMappings.get(file, attachment.getStorage());
			attachment.deleteContent(mapping);
			emc.beginTransaction(Attachment.class);
			emc.delete(Attachment.class, attachment.getId());
			emc.commit();
			WrapOutId wrap = new WrapOutId(attachment.getId());
			result.setData(wrap);
			return result;
		}
	}
}
