package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;
import com.x.general.core.entity.Invoice;

class ActionDownload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDownload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, boolean stream) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;

			Invoice invoice = emc.find(flag, Invoice.class);
			if(invoice == null){
				throw new ExceptionEntityNotExist(flag);
			}
			if(effectivePerson.isNotManager() && effectivePerson.isNotPerson(invoice.getPerson())){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			StorageMapping gfMapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
					invoice.getStorage());
			wo = new Wo(invoice.readContent(gfMapping), this.contentType(stream, invoice.getName()),
					this.contentDisposition(stream, invoice.getName()));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		private static final long serialVersionUID = 5914845134907661807L;

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}
