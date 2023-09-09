package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.general.core.entity.GeneralFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.ActionHtmlToPdfWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionHtmlToPdf extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionHtmlToPdf.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wo wo = new Wo();
			String id = savePdf(wi, effectivePerson.getDistinguishedName(), business);
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	private String savePdf(Wi wi, String person, Business business) {
		try {
			String workHtml = wi.getWorkHtml();
			if (StringUtils.isEmpty(workHtml)) {
				workHtml = "无内容";
			}
			if (workHtml.toLowerCase().indexOf("<html") == -1) {
				workHtml = "<html><head></head><body>" + workHtml + "</body></html>";
			}
			String name = person + DateTools.now() + ".pdf";
			if (StringUtils.isNotEmpty(wi.getTitle())) {
				name = wi.getTitle() + ".pdf";
			}
			byte[] bytes;
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				ConverterProperties props = new ConverterProperties();
				DefaultFontProvider dfp = new DefaultFontProvider(false, false, false);
				dfp.addDirectory(Config.base() + "/commons/fonts");
				props.setFontProvider(dfp);
				PdfWriter writer = new PdfWriter(out);
				PdfDocument pdf = new PdfDocument(writer);
				float width = PageSize.A4.getWidth();
				if (wi.getPageWidth() != null && wi.getPageWidth() > 100) {
					width = wi.getPageWidth().floatValue();
				}
				pdf.setDefaultPageSize(new PageSize(width, PageSize.A4.getHeight()));
				HtmlConverter.convertToPdf(workHtml, pdf, props);
				bytes = out.toByteArray();
			}
			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
			GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name, person);
			generalFile.saveContent(gfMapping, bytes, name, Config.general().getStorageEncrypt());
			EntityManagerContainer emc = business.entityManagerContainer();
			emc.beginTransaction(GeneralFile.class);
			emc.persist(generalFile, CheckPersistType.all);
			emc.commit();

			return generalFile.getId();
		} catch (Exception e) {
			LOGGER.warn("写work信息异常:" + e.getMessage());
		}
		return "";
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionHtmlToPdf$Wi")
	public static class Wi extends ActionHtmlToPdfWi {

		private static final long serialVersionUID = 339938457947418960L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionHtmlToPdf$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -1487347751947636644L;

	}
}
