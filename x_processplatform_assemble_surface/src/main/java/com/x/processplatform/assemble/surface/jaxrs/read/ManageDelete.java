package com.x.processplatform.assemble.surface.jaxrs.read;

import java.net.URLEncoder;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.element.Process;

class ManageDelete extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ReadNotExistedException(id);
			}
			Process process = business.process().pick(read.getProcess());
			// 需要对这个应用的管理权限
			if (!business.process().allowControl(effectivePerson, process)) {
				throw new ReadAccessDeniedException(effectivePerson.getName(), read.getId());
			}
			ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
					"read/" + URLEncoder.encode(read.getId(), "UTF-8"));
			WrapOutId wrap = new WrapOutId(read.getId());
			result.setData(wrap);
			return result;
		}
	}

}