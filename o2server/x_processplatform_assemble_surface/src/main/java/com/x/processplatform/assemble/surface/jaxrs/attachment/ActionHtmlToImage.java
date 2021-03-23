package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.google.gson.JsonElement;
import com.microsoft.playwright.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.FileTools;
import com.x.general.core.entity.GeneralFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import gui.ava.html.image.generator.HtmlImageGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class ActionHtmlToImage extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wo wo = new Wo();
			String id = saveImage(wi, effectivePerson, business);
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	private String saveImage(Wi wi, EffectivePerson effectivePerson, Business business) throws Exception{
		Work work = null;
		EntityManagerContainer emc = business.entityManagerContainer();
		if(StringUtils.isNotBlank(wi.getWorkId())) {
			/* 后面要重新保存 */
			work = emc.find(wi.getWorkId(), Work.class);
			/* 判断work是否存在 */
			if (null == work) {
				throw new ExceptionEntityNotExist(wi.getWorkId(), Work.class);
			}
			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
		}

		String workHtml = wi.getWorkHtml();
		if (StringUtils.isEmpty(workHtml)) {
			workHtml = "无内容";
		}
		if (workHtml.toLowerCase().indexOf("<html") == -1) {
			workHtml = "<html><head></head><body>" + workHtml + "</body></html>";
		}
		String name = StringUtils.split(effectivePerson.getDistinguishedName(),"@")[0] + DateTools.compact(new Date()) + ".jpg";
		if (StringUtils.isNotEmpty(wi.getTitle())) {
			name = wi.getTitle() + ".jpg";
		}
		byte[] bytes = null;
		try (Playwright playwright = Playwright.create()) {
			List<BrowserType> browserTypes = Arrays.asList(
					playwright.chromium(),
					playwright.firefox(),
					playwright.webkit()
			);
			for (BrowserType browserType : browserTypes) {
				logger.print("Playwright user browser:"+browserType.name());
				BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
				options.setHeadless(true);
				try (Browser browser = browserType.launch(options)) {
					BrowserContext context = browser.newContext();
					Page page = context.newPage();
					//page.navigate("file:///Users/chengjian/dev/tmp/html2Image.html");
					page.setContent(workHtml);
					Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions();
					screenshotOptions.setFullPage(true);
					File tempDir = Config.dir_local_temp();
					FileTools.forceMkdir(tempDir);
					File file = new File(tempDir, name);
					//screenshotOptions.setPath(Paths.get("/Users/chengjian/dev/tmp/screenshot-" + browserType.name() + ".png"));
					screenshotOptions.setPath(file.toPath());
					if(wi.getQuality()!=null && wi.getQuality()>20){
						screenshotOptions.setQuality(wi.getQuality());
					}else {
						screenshotOptions.setQuality(80);
					}
					page.screenshot(screenshotOptions);
					bytes = FileUtils.readFileToByteArray(file);
					break;
				} catch (Exception e) {
					logger.warn("Playwright user browser:{} error:{}",browserType.name(), e.getMessage());
				}
			}
		}
		if(bytes==null){
			logger.warn("Playwright screenshot fail!!!");
			return "";
		}
		String key = "";
		if(work!=null){
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			Attachment attachment = this.concreteAttachment(work, effectivePerson, wi.getSite());
			attachment.saveContent(mapping, bytes, name);
			attachment.setType((new Tika()).detect(bytes, name));
			emc.beginTransaction(Attachment.class);
			emc.persist(attachment, CheckPersistType.all);
			emc.commit();

			key = attachment.getId();
		}else{
			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
			GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name, effectivePerson.getDistinguishedName());
			generalFile.saveContent(gfMapping, bytes, name);
			emc.beginTransaction(GeneralFile.class);
			emc.persist(generalFile, CheckPersistType.all);
			emc.commit();

			key = generalFile.getId();
		}

		return key;
	}

	private Attachment concreteAttachment(Work work, EffectivePerson effectivePerson, String site) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(false);
		attachment.setPerson(effectivePerson.getDistinguishedName());
		attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		attachment.setSite(site);
		/** 用于判断目录的值 */
		attachment.setWorkCreateTime(work.getCreateTime());
		attachment.setApplication(work.getApplication());
		attachment.setProcess(work.getProcess());
		attachment.setJob(work.getJob());
		attachment.setActivity(work.getActivity());
		attachment.setActivityName(work.getActivityName());
		attachment.setActivityToken(work.getActivityToken());
		attachment.setActivityType(work.getActivityType());
		return attachment;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("*待转换html.")
		private String workHtml;
		@FieldDescribe("图片标题")
		private String title;
		@FieldDescribe("图片质量，默认80，值越大越清晰")
		private Integer quality;
		@FieldDescribe("工作标识，把图片保存到工单的附件中，非必填")
		private String workId;
		@FieldDescribe("位置，工作标识不为空的时候必填")
		private String site;

		public String getWorkHtml() {
			return workHtml;
		}

		public void setWorkHtml(String workHtml) {
			this.workHtml = workHtml;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

		public Integer getQuality() {
			return quality;
		}

		public void setQuality(Integer quality) {
			this.quality = quality;
		}
	}

	public static class WoControl extends WorkControl {
	}

	public static void main(String[] args) throws Exception{
		System.out.println(10);
		System.out.println("!!!!!!!!");
		try (Playwright playwright = Playwright.create()) {
			List<BrowserType> browserTypes = Arrays.asList(
					playwright.chromium(),
					playwright.firefox(),
					playwright.webkit()
			);
			for (BrowserType browserType : browserTypes) {
				System.out.println(browserType.name());
				BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
				options.setHeadless(true);
				try (Browser browser = browserType.launch(options)) {
					BrowserContext context = browser.newContext();
					Page page = context.newPage();
					page.navigate("file:///Users/chengjian/dev/tmp/html2Image.html");
					//page.setContent("<html><body>测试一下</body></html>");
					//Thread.sleep(1000);
					Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions();
					screenshotOptions.setFullPage(true);
					screenshotOptions.setQuality(80);
					//screenshotOptions.setQuality(2);
					screenshotOptions.setPath(Paths.get("/Users/chengjian/dev/tmp/screenshot-" + browserType.name() + ".jpg"));
					page.screenshot(screenshotOptions);
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println(11);
	}
}
