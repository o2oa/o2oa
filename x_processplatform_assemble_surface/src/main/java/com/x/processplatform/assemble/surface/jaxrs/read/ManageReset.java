package com.x.processplatform.assemble.surface.jaxrs.read;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInRead;
import com.x.processplatform.core.entity.content.Read;

class ManageReset extends ActionBase {

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
			Control control = business.getControlOfRead(effectivePerson, read);
			if (BooleanUtils.isNotTrue(control.getAllowReadReset())) {
				throw new ReadAccessDeniedException(effectivePerson.getName(), read.getId());
			}
			List<String> identites = business.organization().identity().check(wrapIn.getIdentityList());
			if (identites.isEmpty()) {
				throw new IdentityEmptyException();
			}
			wrapIn.setIdentityList(identites);
			emc.beginTransaction(Read.class);
			if (!StringUtils.isEmpty(wrapIn.getOpinion())) {
				read.setOpinion(wrapIn.getOpinion());
			}
			emc.commit();
			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					"read/" + URLEncoder.encode(read.getId(), "UTF-8") + "/reset", wrapIn);
			WrapOutId wrap = new WrapOutId(read.getId());
			result.setData(wrap);
			return result;
		}
	}
}
