package com.x.processplatform.assemble.surface.jaxrs.form;

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
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedForm;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedScript;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormProperties;

import io.swagger.v3.oas.annotations.media.Schema;

class V2Get extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2Get.class);

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
                form = business.form().pick(id);
            }
            if (null == form) {
                throw new ExceptionEntityNotExist(id, Form.class);
            }
            Wo wo = new Wo();
            final FormProperties properties = form.getProperties();
            final List<String> list = new CopyOnWriteArrayList<>();
            wo.setForm(new RelatedForm(form, form.getDataOrMobileData()));
            CompletableFuture<Map<String, RelatedForm>> getRelatedFormFuture = this.getRelatedFormFuture(properties,
                    list);
            CompletableFuture<Map<String, RelatedScript>> getRelatedScriptFuture = this
                    .getRelatedScriptFuture(properties, list);
            wo.setRelatedFormMap(
                    getRelatedFormFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
            wo.setRelatedScriptMap(
                    getRelatedScriptFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
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
            if (ListTools.isNotEmpty(properties.getRelatedFormList())) {
                try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                    Business bus = new Business(emc);
                    Form f;
                    for (String id : properties.getRelatedFormList()) {
                        f = bus.form().pick(id);
                        if (null != f) {
                            map.put(id, new RelatedForm(f, f.getDataOrMobileData()));
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

    private CompletableFuture<Map<String, RelatedScript>> getRelatedScriptFuture(
            FormProperties properties,
            final List<String> list) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, RelatedScript> map = new TreeMap<>();
            if ((null != properties.getRelatedScriptMap()) && (properties.getRelatedScriptMap().size() > 0)) {
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

    private Map<String, RelatedScript> convertScript(Business business, FormProperties properties,
            final List<String> list)
            throws Exception {
        Map<String, RelatedScript> map = new TreeMap<>();
        for (Entry<String, String> entry : properties.getRelatedScriptMap().entrySet()) {
            switch (entry.getValue()) {
                case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
                    com.x.processplatform.core.entity.element.Script processPlatformScript = business.script()
                            .pick(entry.getKey());
                    if (null != processPlatformScript) {
                        map.put(processPlatformScript.getId(),
                                new RelatedScript(processPlatformScript.getId(), processPlatformScript.getName(),
                                        processPlatformScript.getAlias(),
                                        processPlatformScriptLoad(business, processPlatformScript),
                                        WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM));
                        list.add(processPlatformScript.getId() + processPlatformScript.getUpdateTime().getTime());
                    }
                    break;
                case WorkCompletedProperties.RelatedScript.TYPE_CMS:
                    com.x.cms.core.entity.element.Script cmsScript = business.cms().script().pick(entry.getKey());
                    if (null != cmsScript) {
                        map.put(cmsScript.getId(),
                                new RelatedScript(cmsScript.getId(), cmsScript.getName(), cmsScript.getAlias(),
                                        cmsScriptLoad(business, cmsScript),
                                        WorkCompletedProperties.RelatedScript.TYPE_CMS));
                        list.add(cmsScript.getId() + cmsScript.getUpdateTime().getTime());
                    }
                    break;
                case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
                    com.x.portal.core.entity.Script portalScript = business.portal().script().pick(entry.getKey());
                    if (null != portalScript) {
                        map.put(portalScript.getId(),
                                new RelatedScript(portalScript.getId(), portalScript.getName(), portalScript.getAlias(),
                                        portalScriptLoad(business, portalScript),
                                        WorkCompletedProperties.RelatedScript.TYPE_PORTAL));
                        list.add(portalScript.getId() + portalScript.getUpdateTime().getTime());
                    }
                    break;
                case WorkCompletedProperties.RelatedScript.TYPE_SERVICE:
                    com.x.program.center.core.entity.Script cs = business.centerService().script().pick(entry.getKey());
                    if (null != cs) {
                        map.put(cs.getId(),
                                new RelatedScript(cs.getId(), cs.getName(), cs.getAlias(),
                                        serviceScriptLoad(business, cs),
                                        WorkCompletedProperties.RelatedScript.TYPE_SERVICE));
                        list.add(cs.getId() + cs.getUpdateTime().getTime());
                    }
                    break;
                default:
                    break;
            }
        }
        return map;
    }

    @Schema(name = "com.x.processplatform.assemble.surface.jaxrs.form.V2Get$Wo")
    public static class Wo extends AbstractWo {

        private static final long serialVersionUID = 2776033956637839042L;

    }

}
