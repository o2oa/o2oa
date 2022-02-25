package com.x.general.assemble.control.jaxrs.qrcode;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGetCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Integer width, Integer height, String text)
			throws Exception {
		LOGGER.debug("effectivePerson: {}.", effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		byte[] bytes = this.create(width, height, text);
		Wo wo = new Wo(bytes, this.contentType(false, FILENAME), this.contentDisposition(false, FILENAME));
		result.setData(wo);
		return result;
	}

	public class Wo extends WoFile {
		private static final long serialVersionUID = -6210739068105920249L;

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

}
