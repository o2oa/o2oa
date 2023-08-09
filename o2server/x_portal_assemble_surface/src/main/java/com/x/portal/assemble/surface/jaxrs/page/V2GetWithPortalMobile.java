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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.ThisApplication;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.PageProperties;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;

class V2GetWithPortalMobile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2GetWithPortalMobile.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String portalFlag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		CacheKey cacheKey = new CacheKey(this.getClass(), flag, portalFlag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			wo = (Wo) optional.get();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Portal portal = business.portal().pick(portalFlag);
				if (null == portal) {
					throw new ExceptionPortalNotExist(portalFlag);
				}
				if (isNotLoginPage(flag) && (!business.portal().visible(effectivePerson, portal))) {
					throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
							portal.getId());
				}
			}
		} else {
			wo = this.get(flag, portalFlag, effectivePerson);
			CacheManager.put(cacheCategory, cacheKey, wo);
		}
		result.setData(wo);
		return result;
	}

	private Wo get(String flag, String portalFlag, EffectivePerson effectivePerson) throws Exception {
		Page page;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Portal portal = business.portal().pick(portalFlag);
			if (null == portal) {
				throw new ExceptionPortalNotExist(portalFlag);
			}
			if (isNotLoginPage(flag) && (!business.portal().visible(effectivePerson, portal))) {
				throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
						portal.getId());
			}
			page = business.page().pick(portal, flag);
			if (null == page) {
				throw new ExceptionPageNotExist(flag);
			}
		}
		Wo wo = new Wo();
		final PageProperties properties = page.getProperties();
		wo.setPage(new RelatedPage(page, page.getMobileDataOrData()));
		final List<String> list = new CopyOnWriteArrayList<>();
		CompletableFuture<Map<String, RelatedWidget>> relatedWidget = CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedWidget> map = new TreeMap<>();
			if (ListTools.isNotEmpty(properties.getMobileRelatedWidgetList())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					Widget w;
					for (String wid : properties.getMobileRelatedWidgetList()) {
						w = bus.widget().pick(wid);
						if (null != w) {
							map.put(wid, new RelatedWidget(w, w.getMobileDataOrData()));
							list.add(w.getId() + w.getUpdateTime().getTime());
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			return map;
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Map<String, RelatedScript>> relatedScript = CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedScript> map = new TreeMap<>();
			if ((null != properties.getMobileRelatedScriptMap())
					&& (properties.getMobileRelatedScriptMap().size() > 0)) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					for (Entry<String, String> entry : properties.getMobileRelatedScriptMap().entrySet()) {
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
					logger.error(e);
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

		private static final long serialVersionUID = 2957315641879916891L;

	}

}
