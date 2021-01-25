package com.x.cms.assemble.control.jaxrs.form;

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
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.FormProperties;
import com.x.cms.core.entity.element.Script;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

class V2GetMobile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2GetMobile.class);

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
		Form form;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			form = business.getFormFactory().pick(id);
		}
		if (null == form) {
			throw new ExceptionEntityNotExist(id, Form.class);
		}
		Wo wo = new Wo();
		final FormProperties properties = form.getProperties();
		wo.setForm(new RelatedForm(form, form.getMobileDataOrData()));
		final List<String> list = new CopyOnWriteArrayList<>();
		CompletableFuture<Map<String, RelatedForm>> _relatedWidget = CompletableFuture.supplyAsync(() -> {
			Map<String, RelatedForm> map = new TreeMap<>();
			if (ListTools.isNotEmpty(properties.getMobileRelatedFormList())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					Form _f;
					for (String _id : properties.getMobileRelatedFormList()) {
						_f = bus.getFormFactory().pick(_id);
						if (null != _f) {
							map.put(_id, new RelatedForm(_f, _f.getMobileDataOrData()));
							list.add(_f.getId() + _f.getUpdateTime().getTime());
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
			if ((null != properties.getMobileRelatedScriptMap())
					&& (properties.getMobileRelatedScriptMap().size() > 0)) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business bus = new Business(emc);
					for (Entry<String, String> entry : properties.getMobileRelatedScriptMap().entrySet()) {
						switch (entry.getValue()) {
							case RelatedScript.TYPE_PROCESS_PLATFORM:
								com.x.processplatform.core.entity.element.Script _pp = bus.process().script().pick(entry.getKey());
								if (null != _pp) {
									map.put(entry.getKey(), new RelatedScript(_pp.getId(), _pp.getName(), _pp.getAlias(),
											_pp.getText(), entry.getValue()));
									list.add(_pp.getId() + _pp.getUpdateTime().getTime());
								}
								break;
							case RelatedScript.TYPE_CMS:
								Script _cms = bus.getScriptFactory().pick(entry.getKey());
								if (null != _cms) {
									map.put(entry.getKey(), new RelatedScript(_cms.getId(), _cms.getName(), _cms.getAlias(),
											_cms.getText(), entry.getValue()));
									list.add(_cms.getId() + _cms.getUpdateTime().getTime());
								}
								break;
							case RelatedScript.TYPE_PORTAL:
								com.x.portal.core.entity.Script _p = bus.portal().script().pick(entry.getKey());
								if (null != _p) {
									map.put(entry.getKey(), new RelatedScript(_p.getId(), _p.getName(), _p.getAlias(),
											_p.getText(), entry.getValue()));
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
			return map;
		});
		list.add(form.getId() + form.getUpdateTime().getTime());
		List<String> sortList = list.stream().sorted().collect(Collectors.toList());
		wo.setFastETag(StringUtils.join(sortList, "#"));
		wo.setRelatedFormMap(_relatedWidget.get());
		wo.setRelatedScriptMap(_relatedScript.get());
		return wo;
	}

	public static class Wo extends AbstractWo {

	}

}
