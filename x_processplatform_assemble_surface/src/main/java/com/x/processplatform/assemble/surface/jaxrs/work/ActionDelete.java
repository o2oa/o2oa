package com.x.processplatform.assemble.surface.jaxrs.work;

import java.net.URLEncoder;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;

class ActionDelete extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(id);
			}
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowDelete())) {
				throw new WorkAccessDeniedException(effectivePerson.getName(), id);
			}
			WrapOutId wrap = ThisApplication.context().applications()
					.deleteQuery(x_processplatform_service_processing.class,
							"work/" + URLEncoder.encode(work.getId(), DefaultCharset.name))
					.getData(WrapOutId.class);
			result.setData(wrap);
			return result;
		}
	}
}