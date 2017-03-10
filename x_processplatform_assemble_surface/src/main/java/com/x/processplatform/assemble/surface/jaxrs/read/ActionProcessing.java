package com.x.processplatform.assemble.surface.jaxrs.read;

import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInRead;
import com.x.processplatform.core.entity.content.Read;

class ActionProcessing extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInRead wrapIn = this.convertToWrapIn(jsonElement, WrapInRead.class);
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ReadNotExistedException(id);
			}
			if (!business.read().allowProcessing(effectivePerson, read)) {
				throw new ReadAccessDeniedException(effectivePerson.getName(), read.getId());
			}
			emc.beginTransaction(Read.class);
			/* 如果有新的流程意见那么覆盖原有流程意见 */
			if (StringUtils.isNotEmpty(wrapIn.getOpinion())) {
				read.setOpinion(wrapIn.getOpinion());
			}
			emc.commit();
			/* processing read */
			WrapOutId wrap = ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
					"read/" + URLEncoder.encode(read.getId(), "UTF-8") + "/processing", null, WrapOutId.class);
			result.setData(wrap);
			return result;
		}
	}
}
