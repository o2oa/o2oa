package com.x.general.assemble.control.jaxrs.generalfile;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;
import org.apache.commons.codec.binary.Base64;

public class ActionGetBase64 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetBase64.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			GeneralFile generalFile = emc.find(flag, GeneralFile.class);
			if (null == generalFile) {
				throw new ExceptionEntityNotExist(flag);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
					generalFile.getStorage());
			wo.setValue(Base64.encodeBase64String(generalFile.readContent(mapping)));
			result.setData(wo);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = 1445774150055126293L;

	}

}
