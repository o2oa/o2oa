package com.x.query.service.processing.jaxrs.test;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ExtractTextTools;

class ActionExtract extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExtract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(ExtractTextTools.extract(bytes, this.fileName(disposition), Config.query().getExtractOffice(),
					Config.query().getExtractPdf(), Config.query().getExtractText(), Config.query().getExtractImage()));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {
	}

}