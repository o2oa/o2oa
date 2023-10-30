package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.StoreForm;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormProperties;

import io.swagger.v3.oas.annotations.media.Schema;

class V2LookupWorkOrWorkCompletedMobile extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2LookupWorkOrWorkCompletedMobile.class);

	private Form form = null;
	private Wo wo = new Wo();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {

		LOGGER.debug("execute:{}, workOrWorkCompleted:{}.", effectivePerson::getDistinguishedName,
				() -> workOrWorkCompleted);

		ActionResult<Wo> result = new ActionResult<>();

		this.getWorkWorkCompletedForm(workOrWorkCompleted);

		if (null != this.form) {
			CacheKey cacheKey = new CacheKey(this.getClass(), this.form.getId());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				this.wo = (Wo) optional.get();
			} else {
				List<String> list = new ArrayList<>();
				CompletableFuture<List<String>> relatedFormFuture = this.relatedFormFuture(this.form.getProperties());
				CompletableFuture<List<String>> relatedScriptFuture = this
						.relatedScriptFuture(this.form.getProperties());
				list.add(this.form.getId() + this.form.getUpdateTime().getTime());
				list.addAll(relatedFormFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
				list.addAll(
						relatedScriptFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
				list = list.stream().sorted().collect(Collectors.toList());
				this.wo.setId(this.form.getId());
				CRC32 crc = new CRC32();
				crc.update(StringUtils.join(list, "#").getBytes());
				this.wo.setCacheTag(crc.getValue() + "");
				CacheManager.put(cacheCategory, cacheKey, wo);
			}
		}
		result.setData(wo);
		return result;
	}

	private void getWorkWorkCompletedForm(String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCompleted workCompleted = null;
			Work work = emc.fetch(flag, Work.class, ListTools.toList(JpaObject.id_FIELDNAME, Work.form_FIELDNAME,
					Work.activity_FIELDNAME, Work.activityType_FIELDNAME, Work.application_FIELDNAME));
			if (null == work) {
				workCompleted = emc.flag(flag, WorkCompleted.class);
			}
			if (null != work) {
				this.form = getFormWithWork(business, work);
			} else if (null != workCompleted) {
				this.form = business.form().pick(workCompleted.getForm());
				if (null == this.form) {
					StoreForm storeForm = workCompleted.storeForm(true);
					this.wo = XGsonBuilder.convert(storeForm, Wo.class);
				}
			}
		}
	}

	private Form getFormWithWork(Business business, Work work) throws Exception {
		Form o = business.form().pick(work.getForm());
		if (null == o) {
			Activity activity = business.getActivity(work);
			if (null != activity) {
				o = business.form().pick(activity.getForm());
			}
		}
		if (null == o) {
			Application application = business.application().pick(work.getApplication());
			if ((null != application) && StringUtils.isNotEmpty(application.getDefaultForm())) {
				o = business.form().pick(application.getDefaultForm());
			}
		}
		return o;
	}

	private CompletableFuture<List<String>> relatedFormFuture(FormProperties properties) {
		return CompletableFuture.supplyAsync(() -> {
			List<String> list = new ArrayList<>();
			if (ListTools.isNotEmpty(properties.getMobileRelatedFormList())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Form f;
					for (String id : properties.getMobileRelatedFormList()) {
						f = emc.find(id, Form.class);
						if (null != f) {
							list.add(f.getId() + f.getUpdateTime().getTime());
						}
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return list;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<String>> relatedScriptFuture(FormProperties properties) {
		return CompletableFuture.supplyAsync(() -> {
			List<String> list = new ArrayList<>();
			if ((null != properties.getMobileRelatedScriptMap())
					&& (properties.getMobileRelatedScriptMap().size() > 0)) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					list = convertScriptToCacheTag(business, properties.getMobileRelatedScriptMap());
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return list;
		}, ThisApplication.forkJoinPool());
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.form.V2LookupWorkOrWorkCompletedMobile$Wo")
	public static class Wo extends AbstractWo {

		private static final long serialVersionUID = 4034113778768000046L;

		private String id;

		private String cacheTag;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCacheTag() {
			return cacheTag;
		}

		public void setCacheTag(String cacheTag) {
			this.cacheTag = cacheTag;
		}

	}

}