package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.CRC32;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.StoreForm;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

class V2LookupWorkOrWorkCompletedMobile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2LookupWorkOrWorkCompletedMobile.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();

		CompletableFuture<Wo> _wo = CompletableFuture.supplyAsync(() -> {
			Wo wo = new Wo();
			try {
				Work work = null;
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					work = emc.fetch(workOrWorkCompleted, Work.class, ListTools.toList(JpaObject.id_FIELDNAME,
							Work.form_FIELDNAME, Work.activity_FIELDNAME, Work.activityType_FIELDNAME));
				}
				if (null != work) {
					wo = this.work(work);
				} else {
					WorkCompleted workCompleted = null;
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
					}
					if (null != workCompleted) {
						wo = this.workCompleted(workCompleted);
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
			return wo;
		});

		CompletableFuture<Boolean> _control = CompletableFuture.supplyAsync(() -> {
			Boolean value = false;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				value = business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
						new ExceptionEntityNotExist(workOrWorkCompleted));
			} catch (Exception e) {
				logger.error(e);
			}
			return value;
		});

		if (BooleanUtils.isFalse(_control.get())) {
			throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
		}
		result.setData(_wo.get());
		return result;
	}

	private Wo work(Work work) throws Exception {
		Form form = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			form = business.form().pick(work.getForm());
			if (null == form) {
				Activity activity = business.getActivity(work);
				if (null != activity) {
					form = business.form().pick(activity.getForm());
				}
			}
		}
		if (null != form) {
			return this.get(form);
		}
		return new Wo();
	}

	private Wo get(Form form) throws Exception {
		CacheKey cacheKey = new CacheKey(this.getClass(), form.getId());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			return (Wo) optional.get();
		} else {
			List<String> list = new CopyOnWriteArrayList<>();
			CompletableFuture<Void> _relatedForm = CompletableFuture.runAsync(() -> {
				if (ListTools.isNotEmpty(form.getProperties().getMobileRelatedFormList())) {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						Business business = new Business(emc);
						Form _f;
						for (String _id : form.getProperties().getMobileRelatedFormList()) {
							_f = business.form().pick(_id);
							if (null != _f) {
								list.add(_f.getId() + _f.getUpdateTime().getTime());
							}
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			});
			CompletableFuture<Void> _relatedScript = CompletableFuture.runAsync(() -> {
				if ((null != form.getProperties().getMobileRelatedScriptMap())
						&& (form.getProperties().getMobileRelatedScriptMap().size() > 0)) {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						Business business = new Business(emc);
						for (Entry<String, String> entry : form.getProperties().getMobileRelatedScriptMap()
								.entrySet()) {
							switch (entry.getValue()) {
							case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
								Script _pp = business.script().pick(entry.getKey());
								if (null != _pp) {
									list.add(_pp.getId() + _pp.getUpdateTime().getTime());
								}
								break;
							case WorkCompletedProperties.RelatedScript.TYPE_CMS:
								com.x.cms.core.entity.element.Script _cms = business.cms().script()
										.pick(entry.getKey());
								if (null != _cms) {
									list.add(_cms.getId() + _cms.getUpdateTime().getTime());
								}
								break;
							case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
								com.x.portal.core.entity.Script _p = business.portal().script().pick(entry.getKey());
								if (null != _p) {
									list.add(_p.getId() + _p.getUpdateTime().getTime());
								}
								break;
							default:
								break;
							}
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			});
			_relatedForm.get();
			_relatedScript.get();
			list.add(form.getId() + form.getUpdateTime().getTime());
			Wo wo = new Wo();
			wo.setId(form.getId());
			CRC32 crc = new CRC32();
			crc.update(StringUtils.join(list, "#").getBytes());
			wo.setCacheTag(crc.getValue() + "");
			return wo;
		}
	}

	private Wo workCompleted(WorkCompleted workCompleted) throws Exception {
		// 先使用当前库的表单,如果不存在使用储存的表单.
		Wo wo = new Wo();
		Form form = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			form = business.form().pick(workCompleted.getForm());
		}
		if (null != form) {
			return this.get(form);
		} else if (null != workCompleted.getProperties().getStoreFormMobile()) {
			StoreForm storeForm = workCompleted.getProperties().getStoreFormMobile();
			wo = XGsonBuilder.convert(storeForm, Wo.class);
		}
		return wo;
	}

	public static class Wo extends AbstractWo {

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