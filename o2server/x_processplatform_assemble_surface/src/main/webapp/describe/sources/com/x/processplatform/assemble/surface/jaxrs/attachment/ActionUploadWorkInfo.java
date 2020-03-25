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
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import net.sf.ehcache.Element;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.util.UUID;

class ActionUploadWorkInfo extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			String title = "";
			if(work == null){
				WorkCompleted workCompleted = emc.find(workId, WorkCompleted.class);
				if (null == workCompleted) {
					throw new Exception("workId: "+workId+" not exist in work or workCompleted");
				}
				if(!business.readable(effectivePerson, workCompleted)){
					throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
							workCompleted.getTitle(), workCompleted.getId());
				}
				title = workCompleted.getTitle();
			}else{
				if(!business.readable(effectivePerson, work)){
					throw new ExceptionAccessDenied(effectivePerson, work);
				}
				title = work.getTitle();
			}
			String workHtml = wi.getWorkHtml();
			if(workHtml!=null && workHtml.toLowerCase().indexOf("<html") == -1){
				workHtml = "<html><head></head><body>" + workHtml + "</body></html>";
			}
			String id = saveHtml(workId, flag, workHtml, effectivePerson.getDistinguishedName(), title);
			Wo wo = new Wo();
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	private String saveHtml(String workId, String flag, String workHtml, String person, String title){
		try {
			CacheResultObject ro = new CacheResultObject();
			ro.setPerson(person);
			if("word".equals(flag)){
				try (POIFSFileSystem fs = new POIFSFileSystem();
					 InputStream is = new ByteArrayInputStream(workHtml.getBytes("UTF-8"));
					 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					fs.createDocument(is, "WordDocument");
					fs.writeFilesystem(out);
					ro.setBytes(out.toByteArray());
					ro.setName(title + "-表单信息.doc");
				}
			}else if("pdf".equals(flag)){
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    ConverterProperties props = new ConverterProperties();
					DefaultFontProvider dfp = new DefaultFontProvider(false, false, false);
					dfp.addFont(Config.base()+"/commons/fonts/NotoSansCJKsc-Regular.otf");
					props.setFontProvider(dfp);
					PdfWriter writer = new PdfWriter(out);
					PdfDocument pdf = new PdfDocument(writer);
					pdf.setDefaultPageSize(new PageSize(1000, PageSize.A4.getHeight()));
                    HtmlConverter.convertToPdf(workHtml, pdf, props);
                    ro.setBytes(out.toByteArray());
                    ro.setName(title + "-表单信息.pdf");
                }
            }else {
				ro.setBytes(workHtml.getBytes(DefaultCharset.charset));
				ro.setName(title + "-表单信息.html");
			}

			String cacheKey = ApplicationCache.concreteCacheKey(workId, UUID.randomUUID().toString());
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

		public String getWorkHtml() { return workHtml; }

		public void setWorkHtml(String workHtml) {
			this.workHtml = workHtml;
		}

	}

	public static void main(String[] args) throws Exception {
		System.out.println(11);
		String fileName = "测试公司函下载哦哦哦哦哦哦哦-表单信息.html";
		File file = new File("/Users/chengjian/Downloads/",fileName);
		File outfile = new File(file.getAbsolutePath()+"3.pdf");
		FileInputStream in = new FileInputStream(file);
		FileOutputStream out = new FileOutputStream(outfile);
		PdfWriter writer = new PdfWriter(out);
		PdfDocument pdf = new PdfDocument(writer);
		pdf.setDefaultPageSize(new PageSize(1000, PageSize.A4.getHeight()));
		ConverterProperties props = new ConverterProperties();
		DefaultFontProvider dfp = new DefaultFontProvider(false, false, false);
		dfp.addFont("/Users/chengjian/dev/O2/o2oa/o2server/commons/fonts/NotoSansCJKsc-Regular.otf");
		props.setFontProvider(dfp);
		HtmlConverter.convertToPdf(in, pdf, props);
		in.close();
		out.close();
		System.out.println(22);
	}
}
