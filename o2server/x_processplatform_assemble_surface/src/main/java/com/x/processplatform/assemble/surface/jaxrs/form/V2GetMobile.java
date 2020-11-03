package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedForm;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedScript;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;

class V2GetMobile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2GetMobile.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), id);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.get(business, id);
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	private Wo get(Business business, String id) throws Exception {
		Form form = business.form().pick(id);
		if (null == form) {
			throw new ExceptionEntityNotExist(id, Form.class);
		}
		Wo wo = new Wo();
		wo.setFastETag(form.getId() + form.getLastUpdateTime().getTime());
		wo.setForm(new RelatedForm(form, form.getMobileDataOrData()));
		CompletableFuture<Map<String, RelatedForm>> _relatedForm = CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedForm> map = new TreeMap<String, RelatedForm>();
			try {
				Form _f;
				for (String _id : form.getProperties().getMobileRelatedFormList()) {
					_f = business.form().pick(_id);
					if (null != _f) {
						map.put(_id, new RelatedForm(_f, _f.getMobileDataOrData()));
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
			return map;
		});
		CompletableFuture<Map<String, RelatedScript>> _relatedScript = CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedScript> map = new TreeMap<String, RelatedScript>();
			try {
				for (Entry<String, String> entry : form.getProperties().getMobileRelatedScriptMap().entrySet()) {
					switch (entry.getValue()) {
					case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
						Script _pp = business.script().pick(entry.getKey());
						if (null != _pp) {
							map.put(entry.getKey(), new RelatedScript(_pp.getId(), _pp.getName(), _pp.getAlias(),
									_pp.getText(), entry.getValue()));
						}
						break;
					case WorkCompletedProperties.RelatedScript.TYPE_CMS:
						com.x.cms.core.entity.element.Script _cms = business.cms().script().pick(entry.getKey());
						if (null != _cms) {
							map.put(entry.getKey(), new RelatedScript(_cms.getId(), _cms.getName(), _cms.getAlias(),
									_cms.getText(), entry.getValue()));
						}
						break;
					case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
						com.x.portal.core.entity.Script _p = business.portal().script().pick(entry.getKey());
						if (null != _p) {
							map.put(entry.getKey(), new RelatedScript(_p.getId(), _p.getName(), _p.getAlias(),
									_p.getText(), entry.getValue()));
						}
						break;
					default:
						break;
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
			return map;
		});
		wo.setRelatedFormMap(_relatedForm.get());
		wo.setRelatedScriptMap(_relatedScript.get());
		return wo;
	}

	public static class Wo extends AbstractWo {

	}

}