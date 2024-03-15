package com.x.processplatform.assemble.surface.jaxrs.sign;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.DocSign;
import com.x.processplatform.core.entity.content.DocSignScrawl;
import com.x.processplatform.core.entity.content.DocSignStatus;
import com.x.processplatform.core.entity.content.Task;

class ActionSave extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSave.class);

	private static final String SCRAWL_SRC = "src";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String taskId, JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (wi.getStatus() == null) {
			throw new ExceptionFieldEmpty(DocSign.status_FIELDNAME);
		}
		if (wi.getStatus().equals(DocSignStatus.STATUS_3.getValue()) && StringUtils.isBlank(wi.getHtmlContent())) {
			throw new ExceptionFieldEmpty("htmlContent");
		}
		Task task = null;
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			task = emc.fetch(taskId, Task.class);
			if (task == null) {
				throw new ExceptionEntityNotExist(taskId, Task.class);
			}
			if (!task.getPerson().equals(effectivePerson.getDistinguishedName())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		wo.setId(this.saveDocSign(wi, task));
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(wo);
		return result;
	}

	private String saveDocSign(Wi wi, Task task) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			boolean flag = true;
			emc.beginTransaction(DocSign.class);
			DocSign docSign = emc.firstEqual(DocSign.class, DocSign.taskId_FIELDNAME, task.getId());
			if (docSign == null) {
				flag = false;
				docSign = new DocSign(task);
			}
			docSign.setStatus(wi.getStatus());
			docSign.getProperties().setInputList(wi.getInputList());
			docSign.getProperties().setScrawlList(wi.getScrawlList());

			emc.beginTransaction(DocSignScrawl.class);
			if (flag) {
				List<DocSignScrawl> signScrawlList = emc.listEqual(DocSignScrawl.class, DocSignScrawl.signId_FIELDNAME,
						docSign.getId());
				for (DocSignScrawl signScrawl : signScrawlList) {
					if (StringUtils.isNotBlank(signScrawl.getStorage())) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(DocSignScrawl.class,
								signScrawl.getStorage());
						signScrawl.deleteContent(mapping);
					}
					emc.remove(signScrawl);
				}
			}
			if (wi.getStatus().equals(DocSignStatus.STATUS_2.getValue())) {
				List<String> list = new ArrayList<>();
				docSign.setCommitTime(new Date());
				if (ListTools.isNotEmpty(wi.getScrawlList())) {
					for (String data : wi.getScrawlList()) {
						DocSignScrawl signScrawl = this.base642Img(docSign, data);
						list.add(gson.toJson(signScrawl));
						emc.persist(signScrawl, CheckPersistType.all);
					}
				}
				docSign.getProperties().setScrawlList(list);
			} else if (wi.getStatus().equals(DocSignStatus.STATUS_3.getValue())) {
				docSign.setCommitTime(new Date());
				DocSignScrawl signScrawl = this.html2Img(docSign, wi, emc);
				emc.persist(signScrawl, CheckPersistType.all);
				docSign.setSignScrawlId(signScrawl.getId());
			}
			if (!flag) {
				emc.persist(docSign, CheckPersistType.all);
			}
			emc.commit();
			return docSign.getId();
		}
	}

	private DocSignScrawl base642Img(DocSign docSign, String data) throws Exception {
		JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
		DocSignScrawl signScrawl = new DocSignScrawl(docSign, null);
		if (jsonObject.has(SCRAWL_SRC)) {
			String base64String = jsonObject.get(SCRAWL_SRC).getAsString();
			if (StringUtils.isNotBlank(base64String)) {
				byte[] bytes = Base64.decodeBase64(base64String);
				String name = StringUtils.split(docSign.getPerson(), "@")[0] + signScrawl.getId() + ".png";
				signScrawl.setType(DocSignScrawl.SCRAWL_TYPE_BASE64);
				signScrawl.setName(name);
				StorageMapping mapping = ThisApplication.context().storageMappings().random(DocSignScrawl.class);
				signScrawl.saveContent(mapping, bytes, name, Config.general().getStorageEncrypt());
			}
		}
		if (jsonObject.has(DocSignScrawl.width_FIELDNAME)) {
			signScrawl.setWidth(jsonObject.get(DocSignScrawl.width_FIELDNAME).getAsString());
		}
		if (jsonObject.has(DocSignScrawl.height_FIELDNAME)) {
			signScrawl.setHeight(jsonObject.get(DocSignScrawl.height_FIELDNAME).getAsString());
		}
		return signScrawl;
	}

	private DocSignScrawl html2Img(DocSign docSign, Wi wi, EntityManagerContainer emc) throws Exception {
		String html = wi.getHtmlContent();
		if (html.toLowerCase().indexOf("<html") == -1) {
			html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>"
					+ html + "</body></html>";
		}
		String name = StringUtils.split(docSign.getPerson(), "@")[0] + docSign.getTaskId() + ".png";
		byte[] bytes = null;
		try (Playwright playwright = Playwright.create()) {
			List<BrowserType> browserTypes = Arrays.asList(playwright.chromium(), playwright.firefox(),
					playwright.webkit());
			for (BrowserType browserType : browserTypes) {
				BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
				options.setHeadless(true);
				try (Browser browser = browserType.launch(options)) {
					BrowserContext context = browser.newContext();
					Page page = context.newPage();
					page.setContent(html);
					Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions();
					screenshotOptions.setFullPage(true);
					if (wi.getHtmlWidth() != null && wi.getHtmlHeight() != null) {
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
					logger.warn("Playwright user browser:{} error:{}", browserType.name(), e.getMessage());
				}
			}
		}
		if (bytes == null) {
			throw new IllegalStateException("签批转图片异常！");
		}
		DocSignScrawl signScrawl = new DocSignScrawl(docSign, name);
		signScrawl.setType(DocSignScrawl.SCRAWL_TYPE_IMAGE);
		StorageMapping mapping = ThisApplication.context().storageMappings().random(DocSignScrawl.class);
		signScrawl.saveContent(mapping, bytes, name, Config.general().getStorageEncrypt());
		return signScrawl;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 8217095918226150147L;

		@FieldDescribe("状态：1(暂存)|2(签批正文不可以修改)|3(签批正文可以修改,正文保存为图片).")
		private Integer status;
		@FieldDescribe("包含签批的html正文后内容，状态为3时必传.")
		private String htmlContent;
		@FieldDescribe("html正文宽度，允许为空.")
		private Double htmlWidth;
		@FieldDescribe("html正文高度，允许为空.")
		private Double htmlHeight;
		@FieldDescribe("输入框列表.")
		private List<String> inputList = new ArrayList<>();
		@FieldDescribe("涂鸦列表，示例[\"{'src':'base64图片','width':'100','height':'100'}\"].")
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

		private static final long serialVersionUID = 6416229895418700723L;

	}

}
