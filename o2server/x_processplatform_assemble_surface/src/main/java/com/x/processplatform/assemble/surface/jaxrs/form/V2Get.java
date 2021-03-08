package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedForm;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedScript;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormProperties;
import com.x.processplatform.core.entity.element.Script;

class V2Get extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2Get.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CacheKey cacheKey = new CacheKey(this.getClass(), id);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			result.setData((Wo) optional.get());
		} else {
			Form form = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				form = business.form().pick(id);
			}
			if (null == form) {
				throw new ExceptionEntityNotExist(id, Form.class);
			}
			Wo wo = new Wo();
			final FormProperties properties = form.getProperties();
			wo.setFastETag(form.getId() + form.getUpdateTime().getTime());
			wo.setForm(new RelatedForm(form, form.getDataOrMobileData()));
			CompletableFuture<Map<String, RelatedForm>> getRelatedFormFuture = this.getRelatedFormFuture(properties);
			CompletableFuture<Map<String, RelatedScript>> getRelatedScriptFuture = this
					.getRelatedScriptFuture(properties);
			wo.setRelatedFormMap(
					getRelatedFormFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setRelatedScriptMap(
					getRelatedScriptFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setMaxAge(3600 * 24);
			CacheManager.put(cacheCategory, cacheKey, wo);
			result.setData(wo);
		}
		return result;
	}

	private CompletableFuture<Map<String, RelatedForm>> getRelatedFormFuture(FormProperties properties) {
		return CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedForm> map = new TreeMap<>();
			if (ListTools.isNotEmpty(properties.getRelatedFormList())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					Form f;
					for (String id : properties.getRelatedFormList()) {
						f = bus.form().pick(id);
						if (null != f) {
							map.put(id, new RelatedForm(f, f.getDataOrMobileData()));
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			return map;
		});
	}

	private CompletableFuture<Map<String, RelatedScript>> getRelatedScriptFuture(FormProperties properties) {
		return CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedScript> map = new TreeMap<>();
			if ((null != properties.getRelatedScriptMap()) && (properties.getRelatedScriptMap().size() > 0)) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					map = convertScript(business, properties);
				} catch (Exception e) {
					logger.error(e);
				}
			}
			return map;
		});
	}

	private Map<String, RelatedScript> convertScript(Business bus, FormProperties properties) throws Exception {
		Map<String, RelatedScript> map = new TreeMap<>();
		for (Entry<String, String> entry : properties.getRelatedScriptMap().entrySet()) {
			switch (entry.getValue()) {
			case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
				Script pp = bus.script().pick(entry.getKey());
				if (null != pp) {
					map.put(entry.getKey(),
							new RelatedScript(pp.getId(), pp.getName(), pp.getAlias(), pp.getText(), entry.getValue()));
				}
				break;
			case WorkCompletedProperties.RelatedScript.TYPE_CMS:
				com.x.cms.core.entity.element.Script cms = bus.cms().script().pick(entry.getKey());
				if (null != cms) {
					map.put(entry.getKey(), new RelatedScript(cms.getId(), cms.getName(), cms.getAlias(), cms.getText(),
							entry.getValue()));
				}
				break;
			case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
				com.x.portal.core.entity.Script p = bus.portal().script().pick(entry.getKey());
				if (null != p) {
					map.put(entry.getKey(),
							new RelatedScript(p.getId(), p.getName(), p.getAlias(), p.getText(), entry.getValue()));
				}
				break;
			default:
				break;
			}
		}
		return map;
	}

	public static class Wo extends AbstractWo {

		private static final long serialVersionUID = 2776033956637839042L;

	}

}