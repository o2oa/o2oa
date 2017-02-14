package com.x.file.assemble.control.jaxrs.attachment;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.wrapout.WrapOutAttachment;
import com.x.file.core.entity.Attachment;

public class ActionListWithShare extends ActionBase {

	public ActionResult<List<WrapOutAttachment>> execute(EffectivePerson effectivePerson, String owner)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutAttachment>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = business.attachment().listWithPersonWithShare(owner, effectivePerson.getName());
			List<WrapOutAttachment> wraps = copier.copy(emc.list(Attachment.class, ids));
			for (WrapOutAttachment o : wraps) {
				o.setContentType(Config.mimeTypes().getContentType(o.getName()));
			}
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
			return result;
		}
	}
}
