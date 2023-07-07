package com.x.processplatform.assemble.surface.jaxrs.snap;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Snap;

class ActionUpload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes) throws Exception {

		LOGGER.debug("execute:{} .", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String text = new String(bytes, StandardCharsets.UTF_8);
			Snap snap = gson.fromJson(text, Snap.class);
			if (!allow(effectivePerson, business, snap)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			if (!check(snap)) {
				throw new ExceptionContentConfusion();
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = ThisApplication.context().applications()
					.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
							Applications.joinQueryUri("snap", "upload"), text, snap.getJob())
					.getData(Wo.class);
			result.setData(wo);
			return result;
		}
	}

	private boolean check(Snap snap) {
		if (StringUtils.isBlank(snap.getId())) {
			return false;
		}
		if (StringUtils.isBlank(snap.getJob())) {
			return false;
		}
		return !((null == snap.getProperties().getWorkCompleted()) && snap.getProperties().getWorkList().isEmpty());
	}

	private boolean allow(EffectivePerson effectivePerson, Business business, Snap snap) throws Exception {
		return (business.ifPersonCanManageApplicationOrProcess(effectivePerson, snap.getApplication(),
				snap.getProcess()));
	}

	public static class Wo extends WrapString {
	}
}
