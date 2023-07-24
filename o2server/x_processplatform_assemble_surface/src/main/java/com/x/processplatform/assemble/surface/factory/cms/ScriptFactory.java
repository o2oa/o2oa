package com.x.processplatform.assemble.surface.factory.cms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.Script_;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.factory.element.ElementFactory;

public class ScriptFactory extends ElementFactory {

    public ScriptFactory(Business abstractBusiness) throws Exception {
        super(abstractBusiness);
    }

    public Script pick(String flag) throws Exception {
        return this.pick(flag, Script.class);
    }

    @SuppressWarnings("unchecked")
    public List<Script> listScriptNestedWithAppInfoWithUniqueName(String appId, String uniqueName) throws Exception {
        List<Script> list = new ArrayList<>();
        try {
            Cache.CacheCategory cacheCategory = new Cache.CacheCategory(Script.class);
            Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), "listScriptNestedWithAppInfoWithUniqueName",
                    appId, uniqueName);
            Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
            if (optional.isPresent()) {
                list = (List<Script>) optional.get();
            } else {
                List<String> names = new ArrayList<>();
                names.add(uniqueName);
                while (!names.isEmpty()) {
                    List<String> loops = new ArrayList<>();
                    for (String name : names) {
                        Script o = this.getScriptWithAppInfoWithUniqueName(appId, name);
                        if ((null != o) && (!list.contains(o))) {
                            list.add(o);
                            loops.addAll(o.getDependScriptList());
                        }
                    }
                    names = loops;
                }
                if (!list.isEmpty()) {
                    Collections.reverse(list);
                    CacheManager.put(cacheCategory, cacheKey, list);
                }
            }
            return list;
        } catch (Exception e) {
            throw new Exception("listScriptNestedWithAppInfoWithUniqueName error.", e);
        }
    }

    private Script getScriptWithAppInfoWithUniqueName(String appId, String uniqueName) throws Exception {
        Script script = null;
        try {
            Cache.CacheCategory cacheCategory = new Cache.CacheCategory(Script.class);
            Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), "getScriptWithAppInfoWithUniqueName", appId,
                    uniqueName);
            Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
            if (optional.isPresent()) {
                script = (Script) optional.get();
            } else {
                EntityManager em = this.entityManagerContainer().get(Script.class);
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Script> cq = cb.createQuery(Script.class);
                Root<Script> root = cq.from(Script.class);
                Predicate p = cb.equal(root.get(Script_.name), uniqueName);
                p = cb.or(p, cb.equal(root.get(Script_.alias), uniqueName));
                p = cb.or(p, cb.equal(root.get(Script_.id), uniqueName));
                p = cb.and(p, cb.equal(root.get(Script_.appId), appId));
                List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
                if (!list.isEmpty()) {
                    script = list.get(0);
                    em.detach(script);
                    CacheManager.put(cacheCategory, cacheKey, script);
                }
            }
            return script;
        } catch (Exception e) {
            throw new Exception("getScriptWithAppInfoWithUniqueName error.", e);
        }
    }

}