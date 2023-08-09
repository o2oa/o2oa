package com.x.cms.assemble.control.jaxrs.form;

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
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.FormProperties;
import com.x.cms.core.entity.element.Script;

class V2GetMobile extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2GetMobile.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String tag) throws Exception {
		LOGGER.debug("execute:{}, id:{}, tag:{}.", effectivePerson::getDistinguishedName, () -> id, () -> tag);
		ActionResult<Wo> result = new ActionResult<>();
		CacheKey cacheKey = new CacheKey(this.getClass(), id, tag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			result.setData((Wo) optional.get());
		} else {
			Form form = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				form = business.getFormFactory().pick(id);
			}
			if (null == form) {
				throw new ExceptionEntityNotExist(id, Form.class);
			}
			Wo wo = new Wo();
			final FormProperties properties = form.getProperties();
			final List<String> list = new CopyOnWriteArrayList<>();
			wo.setForm(new RelatedForm(form, form.getMobileDataOrData()));
			CompletableFuture<Map<String, RelatedForm>> getRelatedFormFuture = this.getRelatedFormFuture(properties,
					list);
			CompletableFuture<Map<String, RelatedScript>> getRelatedScriptFuture = this
					.getRelatedScriptFuture(properties, list);
			wo.setRelatedFormMap(getRelatedFormFuture.get(10, TimeUnit.SECONDS));
			wo.setRelatedScriptMap(getRelatedScriptFuture.get(10, TimeUnit.SECONDS));
			if (StringUtils.isNotBlank(tag)) {
				wo.setMaxAge(3600 * 24);
			}
			list.add(form.getId() + form.getUpdateTime().getTime());
			List<String> sortList = list.stream().sorted().collect(Collectors.toList());
			wo.setFastETag(StringUtils.join(sortList, "#"));
			CacheManager.put(cacheCategory, cacheKey, wo);
			result.setData(wo);
		}
		return result;
	}

	private CompletableFuture<Map<String, RelatedForm>> getRelatedFormFuture(FormProperties properties,
			final List<String> list) {
		return CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedForm> map = new TreeMap<>();
			if (ListTools.isNotEmpty(properties.getMobileRelatedFormList())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					Form f;
					for (String id : properties.getMobileRelatedFormList()) {
						f = bus.getFormFactory().pick(id);
						if (null != f) {
							map.put(id, new RelatedForm(f, f.getMobileDataOrData()));
							list.add(f.getId() + f.getUpdateTime().getTime());
						}
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return map;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Map<String, RelatedScript>> getRelatedScriptFuture(FormProperties properties,
			final List<String> list) {
		return CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedScript> map = new TreeMap<>();
			if ((null != properties.getMobileRelatedScriptMap())
					&& (properties.getMobileRelatedScriptMap().size() > 0)) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					map = convertScript(business, properties, list);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return map;
		}, ThisApplication.forkJoinPool());
	}

	private Map<String, RelatedScript> convertScript(Business bus, FormProperties properties, final List<String> list)
			throws Exception {
		Map<String, RelatedScript> map = new TreeMap<>();
		for (Entry<String, String> entry : properties.getMobileRelatedScriptMap().entrySet()) {
			switch (entry.getValue()) {
			case RelatedScript.TYPE_PROCESS_PLATFORM:
				com.x.processplatform.core.entity.element.Script pp = bus.process().script().pick(entry.getKey());
				if (null != pp) {
					map.put(entry.getKey(),
							new RelatedScript(pp.getId(), pp.getName(), pp.getAlias(), pp.getText(), entry.getValue()));
					list.add(pp.getId() + pp.getUpdateTime().getTime());
				}
				break;
			case RelatedScript.TYPE_CMS:
				Script cms = bus.getScriptFactory().pick(entry.getKey());
				if (null != cms) {
					map.put(entry.getKey(), new RelatedScript(cms.getId(), cms.getName(), cms.getAlias(), cms.getText(),
							entry.getValue()));
					list.add(cms.getId() + cms.getUpdateTime().getTime());
				}
				break;
			case RelatedScript.TYPE_PORTAL:
				com.x.portal.core.entity.Script p = bus.portal().script().pick(entry.getKey());
				if (null != p) {
					map.put(entry.getKey(),
							new RelatedScript(p.getId(), p.getName(), p.getAlias(), p.getText(), entry.getValue()));
					list.add(p.getId() + p.getUpdateTime().getTime());
				}
				break;
			case RelatedScript.TYPE_SERVICE:
				com.x.program.center.core.entity.Script cs = bus.centerService().script().pick(entry.getKey());
				if (null != cs) {
					map.put(entry.getKey(),
							new RelatedScript(cs.getId(), cs.getName(), cs.getAlias(), cs.getText(), entry.getValue()));
					list.add(cs.getId() + cs.getUpdateTime().getTime());
				}
				break;
			default:
				break;
			}
		}
		return map;
	}

	public static class Wo extends AbstractWo {

		private static final long serialVersionUID = 3540820372721279101L;

	}

}
