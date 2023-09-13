package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

import com.google.gson.JsonElement;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.FileTools;
import com.x.general.core.entity.GeneralFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.ActionHtmlToImageWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionHtmlToImage extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionHtmlToImage.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

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

	private String saveImage(Wi wi, EffectivePerson effectivePerson, Business business) throws Exception {
		Work work = null;
		EntityManagerContainer emc = business.entityManagerContainer();
		if (StringUtils.isNotBlank(wi.getWorkId())) {
			// 后面要重新保存
			work = emc.find(wi.getWorkId(), Work.class);
			// 判断work是否存在
			if (null == work) {
				throw new ExceptionEntityNotExist(wi.getWorkId(), Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}
		}

		String workHtml = wi.getWorkHtml();
		if (StringUtils.isEmpty(workHtml)) {
			workHtml = "无内容";
		}
		if (workHtml.toLowerCase().indexOf("<html") == -1) {
			workHtml = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head>"
					+ "<body>" + workHtml + "</body></html>";
		}
		String name = StringUtils.split(effectivePerson.getDistinguishedName(), "@")[0] + DateTools.compact(new Date())
				+ ".png";
		if (StringUtils.isNotEmpty(wi.getTitle())) {
			name = wi.getTitle() + ".png";
		}
		byte[] bytes = playWright(wi, workHtml, name);
		if (bytes == null) {
			LOGGER.warn("Playwright screenshot fail.");
			return "";
		}
		String key = "";
		if (work != null) {
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			Attachment attachment = this.concreteAttachment(work, effectivePerson, wi.getSite());
			attachment.saveContent(mapping, bytes, name, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes, name));
			emc.beginTransaction(Attachment.class);
			emc.persist(attachment, CheckPersistType.all);
			emc.commit();

			key = attachment.getId();
		} else {
			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
			GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name,
					effectivePerson.getDistinguishedName());
			generalFile.saveContent(gfMapping, bytes, name, Config.general().getStorageEncrypt());
			emc.beginTransaction(GeneralFile.class);
			emc.persist(generalFile, CheckPersistType.all);
			emc.commit();

			key = generalFile.getId();
		}

		return key;
	}

	private byte[] playWright(Wi wi, String workHtml, String name) {
		byte[] bytes = null;
		try (Playwright playwright = Playwright.create()) {
			List<BrowserType> browserTypes = Arrays.asList(playwright.chromium(), playwright.firefox(),
					playwright.webkit());
			for (BrowserType browserType : browserTypes) {
				LOGGER.print("Playwright user browser:" + browserType.name());
				BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
				options.setHeadless(true);
				try (Browser browser = browserType.launch(options);
						BrowserContext context = browser.newContext();
						Page page = context.newPage()) {
					page.setContent(workHtml);
					Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions();
					screenshotOptions.setFullPage(true);
					if (wi.getHtmlWidth() != null && wi.getHtmlHeight() != null) {
						screenshotOptions.setClip(wi.getStartX(), wi.getStartY(), wi.getHtmlWidth(),
								wi.getHtmlHeight());
					}
					if (BooleanUtils.isTrue(wi.getOmitBackground())) {
						screenshotOptions.setOmitBackground(wi.getOmitBackground());
					}
					File tempDir = Config.dir_local_temp();
					FileTools.forceMkdir(tempDir);
					File file = new File(tempDir, name);
					screenshotOptions.setPath(file.toPath());
					page.screenshot(screenshotOptions);
					bytes = FileUtils.readFileToByteArray(file);
					break;
				} catch (Exception e) {
					LOGGER.warn("Playwright user browser:{} error:{}", browserType.name(), e.getMessage());
				}
			}
		}
		return bytes;
	}

	private Attachment concreteAttachment(Work work, EffectivePerson effectivePerson, String site) {
		Attachment attachment = new Attachment();
		attachment.setCompleted(false);
		attachment.setPerson(effectivePerson.getDistinguishedName());
		attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		attachment.setSite(site);
		// 用于判断目录的值
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

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionHtmlToImage$Wi")
	public static class Wi extends ActionHtmlToImageWi {

		private static final long serialVersionUID = -4349899902435225796L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionHtmlToImage$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -7747581377738836294L;

	}

}
