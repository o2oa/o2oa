package com.x.cms.assemble.control.jaxrs.form;

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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.FormProperties;

class V2LookupDoc extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2LookupDoc.class);

	private Form form = null;
	private Form readForm = null;
	private com.x.processplatform.core.entity.element.Form ppForm;
	private Wo wo = new Wo();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId) throws Exception {

		LOGGER.debug("execute;{}. docId:{}.", effectivePerson::getDistinguishedName, () -> docId);

		ActionResult<Wo> result = new ActionResult<>();

		this.getDocForm(docId);
		String formId = "";
		String readFormId = "";
		String ppFormId = "";
		if (null != this.form) {
			formId = form.getId();
			this.wo.setFormId(formId);
		}
		if (null != this.readForm) {
			readFormId = readForm.getId();
			this.wo.setReadFormId(readFormId);
		}
		if (null != this.ppForm){
			ppFormId = this.ppForm.getId();
			this.wo.setPpFormId(ppFormId);
		}
		if(StringUtils.isNotEmpty(formId) || StringUtils.isNotEmpty(readFormId) || StringUtils.isNotEmpty(ppFormId)){
			CacheKey cacheKey = new CacheKey(this.getClass(), formId, readFormId, ppFormId);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				this.wo = (Wo) optional.get();
			} else {
				List<String> list = new ArrayList<>();
				if (null != this.form) {
					CompletableFuture<List<String>> relatedFormFuture = this
							.relatedFormFuture(this.form.getProperties());
					CompletableFuture<List<String>> relatedScriptFuture = this
							.relatedScriptFuture(this.form.getProperties());
					list.add(this.form.getId() + this.form.getUpdateTime().getTime());
					list.addAll(relatedFormFuture.get(10, TimeUnit.SECONDS));
					list.addAll(relatedScriptFuture.get(10, TimeUnit.SECONDS));
				}
				if (null != this.readForm && !formId.equals(readFormId)) {
					CompletableFuture<List<String>> relatedFormFuture = this
							.relatedFormFuture(this.readForm.getProperties());
					CompletableFuture<List<String>> relatedScriptFuture = this
							.relatedScriptFuture(this.readForm.getProperties());
					list.add(this.readForm.getId() + this.readForm.getUpdateTime().getTime());
					list.addAll(relatedFormFuture.get(10, TimeUnit.SECONDS));
					list.addAll(relatedScriptFuture.get(10, TimeUnit.SECONDS));
				}
				if(this.ppForm != null){
					list.add(this.ppForm.getId() + this.ppForm.getUpdateTime().getTime());
				}
				list = list.stream().sorted().collect(Collectors.toList());
				CRC32 crc = new CRC32();
				crc.update(StringUtils.join(list, "#").getBytes());
				this.wo.setCacheTag(crc.getValue() + "");
				CacheManager.put(cacheCategory, cacheKey, wo);
			}
		}
		result.setData(wo);
		return result;
	}

	private void getDocForm(String docId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = emc.fetch(docId, Document.class, ListTools.toList(JpaObject.id_FIELDNAME, Document.form_FIELDNAME,
					Document.readFormId_FIELDNAME, Document.categoryId_FIELDNAME, Document.ppFormId_FIELDNAME));
			if (null != document) {
				String formId = document.getForm();
				String readFormId = document.getReadFormId();
				if (StringUtils.isNotBlank(formId)) {
					this.form = business.getFormFactory().pick(formId);
				}
				if (null == this.form) {
					CategoryInfo categoryInfo = business.getCategoryInfoFactory().pick(document.getCategoryId());
					if (null != categoryInfo) {
						formId = categoryInfo.getFormId();
						this.form = business.getFormFactory().pick(formId);
					}
				}
				if (StringUtils.isNotBlank(readFormId)) {
					if (readFormId.equals(formId)) {
						this.readForm = this.form;
					} else {
						this.readForm = business.getFormFactory().pick(readFormId);
					}
				}
				if (null == this.readForm) {
					CategoryInfo categoryInfo = business.getCategoryInfoFactory().pick(document.getCategoryId());
					if (null != categoryInfo) {
						readFormId = categoryInfo.getReadFormId();
						this.readForm = business.getFormFactory().pick(readFormId);
					}
				}
				if(StringUtils.isNotBlank(document.getPpFormId())){
					this.ppForm = business.process().form().pick(document.getPpFormId());
					LOGGER.info("通过流程ID：{}获取表单：{}", this.ppForm == null? "" : this.ppForm.getName());
				}
			}
		}
	}

	private CompletableFuture<List<String>> relatedFormFuture(FormProperties properties) {
		return CompletableFuture.supplyAsync(() -> {
			List<String> list = new ArrayList<>();
			if (ListTools.isNotEmpty(properties.getRelatedFormList())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Form f;
					for (String id : properties.getRelatedFormList()) {
						f = emc.fetch(id, Form.class,
								ListTools.toList(JpaObject.id_FIELDNAME, JpaObject.updateTime_FIELDNAME));
						if (null != f) {
							list.add(f.getId() + f.getUpdateTime().getTime());
						}
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return list;
		},ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<String>> relatedScriptFuture(FormProperties properties) {
		return CompletableFuture.supplyAsync(() -> {
			List<String> list = new ArrayList<>();
			if ((null != properties.getRelatedScriptMap()) && (properties.getRelatedScriptMap().size() > 0)) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					list = convertScriptToCacheTag(business, properties.getRelatedScriptMap());
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return list;
		},ThisApplication.forkJoinPool());
	}

	public static class Wo extends AbstractWo {

		private static final long serialVersionUID = -4090679604631097945L;

		private String formId;

		private String readFormId;

		private String ppFormId;

		private String cacheTag;

		public String getFormId() {
			return formId;
		}

		public void setFormId(String formId) {
			this.formId = formId;
		}

		public String getReadFormId() {
			return readFormId;
		}

		public void setReadFormId(String readFormId) {
			this.readFormId = readFormId;
		}

		public String getCacheTag() {
			return cacheTag;
		}

		public void setCacheTag(String cacheTag) {
			this.cacheTag = cacheTag;
		}

		public String getPpFormId() {
			return ppFormId;
		}

		public void setPpFormId(String ppFormId) {
			this.ppFormId = ppFormId;
		}
	}

}
