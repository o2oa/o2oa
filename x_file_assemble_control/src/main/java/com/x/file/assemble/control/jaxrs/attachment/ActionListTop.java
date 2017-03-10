package com.x.file.assemble.control.jaxrs.attachment;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.wrapout.WrapOutAttachment;
import com.x.file.core.entity.personal.Attachment;

public class ActionListTop extends ActionBase {

	public ActionResult<List<WrapOutAttachment>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutAttachment>> result = new ActionResult<>();
			List<String> ids = business.attachment().listTopWithPerson(effectivePerson.getName());
			List<WrapOutAttachment> wraps = copier.copy(emc.list(Attachment.class, ids));
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
			return result;
		}
	}
}
