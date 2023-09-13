package com.x.cms.assemble.control.jaxrs.fileinfo;

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
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.general.core.entity.GeneralFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

class ActionUploadForm extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUploadForm.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId, String flag, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Document document = emc.find(docId, Document.class);
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
			String title = StringUtils.isNoneBlank(wi.getTitle()) ? wi.getTitle() : document.getTitle();
			String id = saveHtml(flag, workHtml, effectivePerson.getDistinguishedName(), title,
					wi.getPageWidth(), business);
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
					name = title + "-表单.doc";
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
					name = title + "-表单.pdf";
				}
			} else {
				bytes = workHtml.getBytes(DefaultCharset.charset);
				name = title + "-表单.html";
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
			logger.warn("写表单信息异常" + e.getMessage());
		}
		return "";
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("待转换html.")
		private String workHtml;

		@FieldDescribe("文件标题.")
		private String title;

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

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

}
