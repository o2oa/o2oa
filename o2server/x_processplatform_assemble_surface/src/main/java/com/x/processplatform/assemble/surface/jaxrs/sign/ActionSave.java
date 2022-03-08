package com.x.processplatform.assemble.surface.jaxrs.sign;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.DocSign;
import com.x.processplatform.core.entity.content.DocSignScrawl;
import com.x.processplatform.core.entity.content.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.ExceptionDefPOA;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class ActionSave extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSave.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String taskId, JsonElement jsonElement) throws Exception {
		String job = null;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(wi.getStatus() == null){
			throw new ExceptionFieldEmpty(DocSign.status_FIELDNAME);
		}
		if(wi.getStatus().equals(DocSign.STATUS_3) && StringUtils.isBlank(wi.getHtmlContent())){
			throw new ExceptionFieldEmpty("htmlContent");
		}
		Task task = null;
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			task = emc.fetch(taskId, Task.class);
			if(task == null){
				throw new ExceptionEntityNotExist(taskId, Task.class);
			}
			if(!task.getPerson().equals(effectivePerson.getDistinguishedName())){
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		this.saveDocSign(wi, task);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	private String saveDocSign(Wi wi, Task task) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			boolean flag = false;
			emc.beginTransaction(DocSign.class);
			DocSign docSign = emc.firstEqual(DocSign.class, DocSign.taskId_FIELDNAME, task.getId());
			if(docSign == null) {
				flag = true;
				docSign = new DocSign(task);
				docSign.setStatus(wi.getStatus());
			}
			docSign.getProperties().setInputList(wi.getInputList());
			docSign.getProperties().setScrawlList(wi.getScrawlList());
			if(wi.getStatus().equals(DocSign.STATUS_2)){
				docSign.setCommitTime(new Date());
				emc.beginTransaction(DocSignScrawl.class);
				emc.deleteEqual(DocSignScrawl.class, DocSignScrawl.signId_FIELDNAME, docSign.getId());
				if(ListTools.isNotEmpty(wi.getScrawlList())){
					for (String data : wi.getScrawlList()){
						JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
						DocSignScrawl signScrawl = new DocSignScrawl(docSign, data, null);
						if(jsonObject.has("src")){
							signScrawl.setType(DocSignScrawl.SCRAWL_TYPE_BASE64);
						}
						emc.persist(signScrawl);
					}
				}
			}else if(wi.getStatus().equals(DocSign.STATUS_3)){
				docSign.setCommitTime(new Date());

			}
			if(flag){
				emc.persist(docSign);
			}
			emc.commit();
			return docSign.getId();
		}
	}

	private void html2Img(DocSign docSign, Wi wi, EntityManagerContainer emc) throws Exception{
		String html = wi.getHtmlContent();
		if (html.toLowerCase().indexOf("<html") == -1) {
			html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>" + html + "</body></html>";
		}
		String name = StringUtils.split(docSign.getPerson(),"@")[0] + docSign.getTaskId() + ".png";
		byte[] bytes = null;
		try (Playwright playwright = Playwright.create()) {
			List<BrowserType> browserTypes = Arrays.asList(
					playwright.chromium(),
					playwright.firefox(),
					playwright.webkit()
			);
			for (BrowserType browserType : browserTypes) {
				BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
				options.setHeadless(true);
				try (Browser browser = browserType.launch(options)) {
					BrowserContext context = browser.newContext();
					Page page = context.newPage();
					page.setContent(html);
					Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions();
					screenshotOptions.setFullPage(true);
					if(wi.getHtmlWidth()!=null && wi.getHtmlHeight()!=null){
						screenshotOptions.setClip(0, 0, wi.getHtmlWidth(), wi.getHtmlHeight());
					}
					File tempDir = Config.dir_local_temp();
					FileTools.forceMkdir(tempDir);
					File file = new File(tempDir, name);
					screenshotOptions.setPath(file.toPath());
					page.screenshot(screenshotOptions);
					bytes = FileUtils.readFileToByteArray(file);
					break;
				} catch (Exception e) {
					logger.warn("Playwright user browser:{} error:{}",browserType.name(), e.getMessage());
				}
			}
		}
		if(bytes==null){
			throw new IllegalStateException("签批转图片异常！");
		}

	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("状态：1(暂存)|2(签批正文不可以修改)|3(签批正文可以修改,正文保存为图片).")
		private Integer status;
		@FieldDescribe("包含签批的html正文后内容，状态为3时必传.")
		private String htmlContent;
		@FieldDescribe("html宽度，允许为空.")
		private Double htmlWidth;
		@FieldDescribe("html宽度长度，允许为空.")
		private Double htmlHeight;
		@FieldDescribe("输入框列表.")
		private List<String> inputList = new ArrayList<>();
		@FieldDescribe("涂鸦列表.")
		private List<String> scrawlList = new ArrayList<>();

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public String getHtmlContent() {
			return htmlContent;
		}

		public void setHtmlContent(String htmlContent) {
			this.htmlContent = htmlContent;
		}

		public Double getHtmlWidth() {
			return htmlWidth;
		}

		public void setHtmlWidth(Double htmlWidth) {
			this.htmlWidth = htmlWidth;
		}

		public Double getHtmlHeight() {
			return htmlHeight;
		}

		public void setHtmlHeight(Double htmlHeight) {
			this.htmlHeight = htmlHeight;
		}

		public List<String> getInputList() {
			return inputList;
		}

		public void setInputList(List<String> inputList) {
			this.inputList = inputList;
		}

		public List<String> getScrawlList() {
			return scrawlList;
		}

		public void setScrawlList(List<String> scrawlList) {
			this.scrawlList = scrawlList;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2577413577740827608L;

	}

}
