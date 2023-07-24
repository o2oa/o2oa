package com.x.processplatform.assemble.surface.factory.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.factory.element.ElementFactory;
import com.x.program.center.core.entity.Script;
import com.x.program.center.core.entity.Script_;

public class ScriptFactory extends ElementFactory {

    public ScriptFactory(Business abstractBusiness) throws Exception {
        super(abstractBusiness);
    }

    public Script pick(String flag) throws Exception {
        return this.pick(flag, Script.class);
    }

    @SuppressWarnings("unchecked")
    public List<Script> listScriptNestedWithFlag(String flag) throws Exception {
        List<Script> list = new ArrayList<>();
        CacheCategory cacheCategory = new CacheCategory(Script.class);
        CacheKey cacheKey = new CacheKey(flag, "listScriptNestedWithFlag");
        Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
        if (optional.isPresent()) {
            list = (List<Script>) optional.get();
        } else {
            List<String> names = new ArrayList<>();
            names.add(flag);
            while (!names.isEmpty()) {
                List<String> loops = new ArrayList<>();
                for (String name : names) {
                    Script o = this.getScriptWithFlag(name);
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
    }

    private Script getScriptWithFlag(String flag) throws Exception {
        Script script = this.getWithId(flag);
        if (null == script) {
            script = this.getWithAlias(flag);
        }
        if (null == script) {
            script = this.getWithName(flag);
        }
        return script;
    }

    private Script getWithId(String flag) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Script.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Script> cq = cb.createQuery(Script.class);
        Root<Script> root = cq.from(Script.class);
        Predicate p = cb.equal(root.get(Script_.id), flag);
        List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    private Script getWithAlias(String flag) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Script.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Script> cq = cb.createQuery(Script.class);
        Root<Script> root = cq.from(Script.class);
        Predicate p = cb.equal(root.get(Script_.alias), flag);
        List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    private Script getWithName(String flag) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Script.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Script> cq = cb.createQuery(Script.class);
        Root<Script> root = cq.from(Script.class);
        Predicate p = cb.equal(root.get(Script_.name), flag);
        List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

}
