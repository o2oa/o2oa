package com.x.portal.assemble.surface.jaxrs.page;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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
import com.x.portal.assemble.surface.ThisApplication;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.PageProperties;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;

class V2Get extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Get.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

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
		wo.setPage(new RelatedPage(page, page.getDataOrMobileData()));
		final List<String> list = new CopyOnWriteArrayList<>();
		CompletableFuture<Map<String, RelatedWidget>> relatedWidget = CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedWidget> map = new TreeMap<>();
			if (ListTools.isNotEmpty(properties.getRelatedWidgetList())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					Widget w;
					for (String wid : properties.getRelatedWidgetList()) {
						w = bus.widget().pick(wid);
						if (null != w) {
							map.put(wid, new RelatedWidget(w, w.getDataOrMobileData()));
							list.add(w.getId() + w.getUpdateTime().getTime());
						}
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return map;
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Map<String, RelatedScript>> relatedScript = CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedScript> map = new TreeMap<>();
			if ((null != properties.getRelatedScriptMap()) && (properties.getRelatedScriptMap().size() > 0)) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					for (Entry<String, String> entry : properties.getRelatedScriptMap().entrySet()) {
						switch (entry.getValue()) {
						case RelatedScript.TYPE_PROCESSPLATFORM:
							com.x.processplatform.core.entity.element.Script pp = bus.process().script()
									.pick(entry.getKey());
							if (null != pp) {
								map.put(entry.getKey(), new RelatedScript(pp.getId(), pp.getName(), pp.getAlias(),
										pp.getText(), entry.getValue()));
								list.add(pp.getId() + pp.getUpdateTime().getTime());
							}
							break;
						case RelatedScript.TYPE_CMS:
							com.x.cms.core.entity.element.Script cms = bus.cms().script().pick(entry.getKey());
							if (null != cms) {
								map.put(entry.getKey(), new RelatedScript(cms.getId(), cms.getName(), cms.getAlias(),
										cms.getText(), entry.getValue()));
								list.add(cms.getId() + cms.getUpdateTime().getTime());
							}
							break;
						case RelatedScript.TYPE_SERVICE:
							com.x.program.center.core.entity.Script cs = bus.centerService().script().pick(entry.getKey());
							if (null != cs) {
								map.put(entry.getKey(), new RelatedScript(cs.getId(), cs.getName(), cs.getAlias(),
										cs.getText(), entry.getValue()));
								list.add(cs.getId() + cs.getUpdateTime().getTime());
							}
							break;
						case RelatedScript.TYPE_PORTAL:
							Script p = bus.script().pick(entry.getKey());
							if (null != p) {
								map.put(entry.getKey(), new RelatedScript(p.getId(), p.getName(), p.getAlias(),
										p.getText(), entry.getValue()));
								list.add(p.getId() + p.getUpdateTime().getTime());
							}
							break;
						default:
							break;
						}
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return map;
		}, ThisApplication.forkJoinPool());
		wo.setRelatedWidgetMap(relatedWidget.get(300, TimeUnit.SECONDS));
		wo.setRelatedScriptMap(relatedScript.get(300, TimeUnit.SECONDS));
		list.add(page.getId() + page.getUpdateTime().getTime());
		List<String> sortList = list.stream().sorted().collect(Collectors.toList());
		wo.setFastETag(StringUtils.join(sortList, "#"));
		return wo;
	}

	public static class Wo extends AbstractWo {

		private static final long serialVersionUID = -2548081336433917490L;

	}

}
