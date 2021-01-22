package com.x.cms.assemble.control.jaxrs.form;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.FormProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

class V2LookupDocMobile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2LookupDocMobile.class);

	private Form form = null;
	private Wo wo = new Wo();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId, String openMode) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();

		this.getDocForm(docId, openMode);

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
				list.addAll(relatedFormFuture.get(10, TimeUnit.SECONDS));
				list.addAll(relatedScriptFuture.get(10, TimeUnit.SECONDS));
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

	private void getDocForm(String docId, String openMode) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = emc.fetch(docId, Document.class, ListTools.toList(JpaObject.id_FIELDNAME, Document.form_FIELDNAME,
					Document.readFormId_FIELDNAME, Document.categoryId_FIELDNAME));
			if (null != document) {
				String formId = document.getForm();
				if(FORM_OPEN_MODE_READ.equals(openMode)){
					formId = document.getReadFormId();
				}
				this.form = business.getFormFactory().pick(formId);
				if (null == this.form) {
					CategoryInfo categoryInfo = business.getCategoryInfoFactory().pick(document.getCategoryId());
					if (null != categoryInfo) {
						formId = categoryInfo.getFormId();
						if(FORM_OPEN_MODE_READ.equals(openMode)){
							formId = categoryInfo.getReadFormId();
						}
						this.form = business.getFormFactory().pick(formId);
					}
				}
			}
		}
	}

	private CompletableFuture<List<String>> relatedFormFuture(FormProperties properties) {
		return CompletableFuture.supplyAsync(() -> {
			List<String> list = new ArrayList<>();
			if (ListTools.isNotEmpty(properties.getMobileRelatedFormList())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					Form f;
					for (String id : properties.getMobileRelatedFormList()) {
						f = business.getFormFactory().pick(id);
						if (null != f) {
							list.add(f.getId() + f.getUpdateTime().getTime());
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			return list;
		});
	}

	private CompletableFuture<List<String>> relatedScriptFuture(FormProperties properties) {
		return CompletableFuture.supplyAsync(() -> {
			List<String> list = new ArrayList<>();
			if ((null != properties.getMobileRelatedScriptMap()) && (properties.getMobileRelatedScriptMap().size() > 0)) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					list = convertScriptToCacheTag(business, properties.getMobileRelatedScriptMap());
				} catch (Exception e) {
					logger.error(e);
				}
			}
			return list;
		});
	}

	public static class Wo extends AbstractWo {

		private static final long serialVersionUID = -955543425744298907L;

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
