package com.x.portal.assemble.surface.jaxrs.page;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.PageProperties;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

class V2Get extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2Get.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CacheKey cacheKey = new CacheKey(this.getClass(), id);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			result.setData((Wo) optional.get());
		} else {
			Wo wo = this.get(id);
			CacheManager.put(cacheCategory, cacheKey, wo);
			result.setData(wo);
		}
		return result;
	}

	private Wo get(String id) throws Exception {
		Page page;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			page = business.page().pick(id);
		}
		if (null == page) {
			throw new ExceptionEntityNotExist(id, Page.class);
		}
		Wo wo = new Wo();
		final PageProperties properties = page.getProperties();
		wo.setFastETag(page.getId() + page.getUpdateTime().getTime());
		wo.setPage(new RelatedPage(page, page.getDataOrMobileData()));
		CompletableFuture<Map<String, RelatedWidget>> _relatedWidget = CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedWidget> map = new TreeMap<>();
			if (ListTools.isNotEmpty(properties.getRelatedWidgetList())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					Widget _f;
					for (String _id : properties.getRelatedWidgetList()) {
						_f = bus.widget().pick(_id);
						if (null != _f) {
							map.put(_id, new RelatedWidget(_f, _f.getDataOrMobileData()));
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			return map;
		});
		CompletableFuture<Map<String, RelatedScript>> _relatedScript = CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedScript> map = new TreeMap<>();
			if ((null != properties.getRelatedScriptMap())
					&& (properties.getRelatedScriptMap().size() > 0)) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					for (Entry<String, String> entry : properties.getRelatedScriptMap().entrySet()) {
						switch (entry.getValue()) {
							case RelatedScript.TYPE_PROCESSPLATFORM:
								com.x.processplatform.core.entity.element.Script _pp = bus.process().script().pick(entry.getKey());
								if (null != _pp) {
									map.put(entry.getKey(), new RelatedScript(_pp.getId(), _pp.getName(), _pp.getAlias(),
											_pp.getText(), entry.getValue()));
								}
								break;
							case RelatedScript.TYPE_CMS:
								com.x.cms.core.entity.element.Script _cms = bus.cms().script().pick(entry.getKey());
								if (null != _cms) {
									map.put(entry.getKey(), new RelatedScript(_cms.getId(), _cms.getName(), _cms.getAlias(),
											_cms.getText(), entry.getValue()));
								}
								break;
							case RelatedScript.TYPE_PORTAL:
								Script _p = bus.script().pick(entry.getKey());
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
			}
			return map;
		});
		wo.setRelatedWidgetMap(_relatedWidget.get());
		wo.setRelatedScriptMap(_relatedScript.get());
		wo.setMaxAge(3600 * 24);
		return wo;
	}

	public static class Wo extends AbstractWo {

	}

}