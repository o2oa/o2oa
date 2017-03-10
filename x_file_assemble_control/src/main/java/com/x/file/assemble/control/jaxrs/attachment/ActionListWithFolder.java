package com.x.file.assemble.control.jaxrs.attachment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.wrapout.WrapOutAttachment;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Folder;

public class ActionListWithFolder extends ActionBase {

	public ActionResult<List<WrapOutAttachment>> execute(EffectivePerson effectivePerson, String folderId)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutAttachment>> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder folder = emc.find(folderId, Folder.class, ExceptionWhen.not_found);
			if (!StringUtils.equals(folder.getPerson(), effectivePerson.getName())) {
				throw new Exception(
						"person{name:" + effectivePerson.getName() + "} access folder{id:" + folderId + "} denied.");
			}
			List<String> ids = business.attachment().listWithFolder(folder.getId());
			List<WrapOutAttachment> wraps = copier.copy(emc.list(Attachment.class, ids));
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
			return result;
		}
	}
}
