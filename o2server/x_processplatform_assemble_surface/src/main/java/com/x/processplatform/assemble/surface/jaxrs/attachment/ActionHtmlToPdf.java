package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.google.gson.JsonElement;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.UUID;

class ActionHtmlToPdf extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wo wo = new Wo();
			String id = savePdf(wi, effectivePerson.getDistinguishedName());
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	private String savePdf(Wi wi, String person){
		try {
			CacheResultObject ro = new CacheResultObject();
			ro.setPerson(person);

			String workHtml = wi.getWorkHtml();
			if(StringUtils.isEmpty(workHtml)){
				workHtml = "无内容";
			}
			if(workHtml.toLowerCase().indexOf("<html") == -1){
				workHtml = "<html><head></head><body>" + workHtml + "</body></html>";
			}
			String title = person + DateTools.now()+".pdf";
			if(StringUtils.isNotEmpty(wi.getTitle())){
				title = wi.getTitle()+".pdf";
			}
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				ConverterProperties props = new ConverterProperties();
				DefaultFontProvider dfp = new DefaultFontProvider(false, false, false);
				//dfp.addFont(Config.base()+"/commons/fonts/NotoSansCJKsc-Regular.otf");
				dfp.addDirectory(Config.base()+"/commons/fonts");
				props.setFontProvider(dfp);
				PdfWriter writer = new PdfWriter(out);
				PdfDocument pdf = new PdfDocument(writer);
				float width = PageSize.A4.getWidth();
				if(wi.getPageWidth()!=null && wi.getPageWidth()>100){
					width = wi.getPageWidth().floatValue();
				}
				pdf.setDefaultPageSize(new PageSize(width, PageSize.A4.getHeight()));
				HtmlConverter.convertToPdf(workHtml, pdf, props);
				ro.setBytes(out.toByteArray());
				ro.setName(title + ".pdf");
			}

			String cacheKey = ApplicationCache.concreteCacheKey(UUID.randomUUID().toString());
			cache.put(new Element(cacheKey,ro));
			return cacheKey;
		} catch (Exception e) {
			logger.warn("写work信息异常"+e.getMessage());
		}
		return "";
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("待转换html.")
		private String workHtml;

		@FieldDescribe("转pdf页面宽度，默认A4.")
		private Float pageWidth;

		@FieldDescribe("pdf标题")
		private String title;

		public String getWorkHtml() { return workHtml; }

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
