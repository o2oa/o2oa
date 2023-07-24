package com.x.processplatform.assemble.surface.jaxrs.snap;

import java.nio.charset.StandardCharsets;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Snap;

class ActionDownload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDownload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Snap snap = emc.find(id, Snap.class);
			if (!allow(effectivePerson, business, snap)) {
				throw new ExceptionAccessDenied(effectivePerson, snap);
			}
			String name = snap.getProcessName() + "-" + snap.getTitle();
			if (null != snap.getStartTime()) {
				name += "-" + DateTools.compact(snap.getStartTime());
			}
			name = FileTools.toFileName(name);
			name = StringTools.utf8SubString(name, JpaObject.length_128B);
			String text = gson.toJson(snap);
			Wo wo = new Wo(text.getBytes(StandardCharsets.UTF_8), this.contentType(false, name),
					this.contentDisposition(false, name));
			result.setData(wo);
		}
		return result;
	}

	private boolean allow(EffectivePerson effectivePerson, Business business, Snap snap) throws Exception {
		return (business.ifPersonCanManageApplicationOrProcess(effectivePerson, snap.getApplication(), snap.getProcess())
				|| effectivePerson.isNotPerson(snap.getPerson()));
	}

	public static class Wo extends WoFile {

		private static final long serialVersionUID = -2577413577740827608L;

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
