package com.x.processplatform.assemble.surface.jaxrs.attachment;

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
import com.x.general.core.entity.Invoice;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionDownloadInvoice extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDownloadInvoice.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String jobOrWorkOrWorkCompleted, String fileName) throws Exception {

		LOGGER.debug("execute:{}, id:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jobOrWorkOrWorkCompleted);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Invoice invoice = emc.find(id, Invoice.class);
			if (null == invoice) {
				throw new ExceptionEntityNotExist(id, Invoice.class);
			}

			Control control = new JobControlBuilder(effectivePerson, business,
					jobOrWorkOrWorkCompleted).enableAllowVisit()
					.build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit()) || !invoice.getPerson()
					.equals(control.getCreatorPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, jobOrWorkOrWorkCompleted);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings()
					.get(Invoice.class, invoice.getStorage());
			if (StringUtils.isBlank(fileName)) {
				fileName = OrganizationDefinition.name(invoice.getPerson()) + "_" + invoice.getName();
			} else {
				String extension = FilenameUtils.getExtension(fileName);
				if (StringUtils.isEmpty(extension)) {
					fileName = fileName + "." + invoice.getExtension();
				}
			}
			byte[] bytes = invoice.readContent(mapping);
			Wo wo = new Wo(bytes, this.contentType(false, fileName),
					this.contentDisposition(false, fileName));
			result.setData(wo);
			return result;
		}
	}



	public static class Wo extends WoFile {

		private static final long serialVersionUID = 8958654624399659293L;

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}


}
