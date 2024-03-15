package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

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
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.general.core.entity.GeneralFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionUploadWorkInfo extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUploadWorkInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String flag, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			String title = "";
			if (work == null) {
				WorkCompleted workCompleted = emc.find(workId, WorkCompleted.class);
				if (null == workCompleted) {
					throw new Exception("workId: " + workId + " not exist in work or workCompleted");
				}
				Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
						.enableAllowVisit().build();
				if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
					throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
							workCompleted.getTitle(), workCompleted.getId());
				}
				title = workCompleted.getTitle();
			} else {
				Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowVisit().build();
				if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
					throw new ExceptionAccessDenied(effectivePerson, work);
				}
				title = work.getTitle();
			}
			String workHtml = wi.getWorkHtml();
			if (StringUtils.isNotBlank(workHtml)) {
				try {
					workHtml = URLDecoder.decode(workHtml, StandardCharsets.UTF_8.name());
				} catch (Exception e) {
					logger.error(e);
				}
				if (workHtml.toLowerCase().indexOf("<html") == -1) {
					workHtml = "<html><head></head><body>" + workHtml + "</body></html>";
				}
			}
			String id = saveHtml(flag, workHtml, effectivePerson.getDistinguishedName(), title, wi.getPageWidth(),
					business);
			Wo wo = new Wo();
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	private String saveHtml(String flag, String workHtml, String person, String title, Float pageWidth,
			Business business) {
		try {
			String name = "";
			byte[] bytes;
			if (title.length() > 60) {
				title = title.substring(0, 60);
			}
			if ("word".equals(flag)) {
				try (POIFSFileSystem fs = new POIFSFileSystem();
						InputStream is = new ByteArrayInputStream(workHtml.getBytes("UTF-8"));
						ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					fs.createDocument(is, "WordDocument");
					fs.writeFilesystem(out);
					bytes = out.toByteArray();
					name = title + "-处理单.doc";
				}
			} else if ("pdf".equals(flag)) {
				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					ConverterProperties props = new ConverterProperties();
					DefaultFontProvider dfp = new DefaultFontProvider(false, false, false);
					// dfp.addFont(Config.base()+"/commons/fonts/NotoSansCJKsc-Regular.otf");
					dfp.addDirectory(Config.base() + "/commons/fonts");
					props.setFontProvider(dfp);
					PdfWriter writer = new PdfWriter(out);
					PdfDocument pdf = new PdfDocument(writer);
					float width = PageSize.A4.getWidth();
					if (pageWidth != null && pageWidth > 100) {
						width = pageWidth.floatValue();
					}
					pdf.setDefaultPageSize(new PageSize(width, PageSize.A4.getHeight()));
					HtmlConverter.convertToPdf(workHtml, pdf, props);
					bytes = out.toByteArray();
					name = title + "-处理单.pdf";
				}
			} else {
				bytes = workHtml.getBytes(DefaultCharset.charset);
				name = title + "-处理单.html";
			}
			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
			GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name, person);
			generalFile.saveContent(gfMapping, bytes, name, Config.general().getStorageEncrypt());
			EntityManagerContainer emc = business.entityManagerContainer();
			emc.beginTransaction(GeneralFile.class);
			emc.persist(generalFile, CheckPersistType.all);
			emc.commit();

			String key = generalFile.getId();
			return key;
		} catch (Exception e) {
			logger.warn("写work信息异常" + e.getMessage());
		}
		return "";
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("待转换html.")
		private String workHtml;

		@FieldDescribe("转pdf页面宽度，默认A4.")
		private Float pageWidth;

		public String getWorkHtml() {
			return workHtml;
		}

		public void setWorkHtml(String workHtml) {
			this.workHtml = workHtml;
		}

		public Float getPageWidth() {
			return pageWidth;
		}

		public void setPageWidth(Float pageWidth) {
			this.pageWidth = pageWidth;
		}
	}

}
