package com.x.processplatform.assemble.surface.factory.portal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Script_;
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
    public List<Script> listScriptNestedWithPortalWithFlag(String portalId, String flag) throws Exception {
        List<Script> list = new ArrayList<>();
        if (StringUtils.isBlank(portalId)) {
            return list;
        }
        CacheCategory cacheCategory = new CacheCategory(Script.class);
        CacheKey cacheKey = new CacheKey(flag, portalId, "listScriptNestedWithPortalWithFlag");
        Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
        if (optional.isPresent()) {
            list = (List<Script>) optional.get();
        } else {
            List<String> names = new ArrayList<>();
            names.add(flag);
            while (!names.isEmpty()) {
                List<String> loops = new ArrayList<>();
                for (String name : names) {
                    Script o = this.getScriptWithPortalWithFlag(portalId, name);
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

    private Script getScriptWithPortalWithFlag(String portalId, String flag) throws Exception {
        Script script = this.getWithPortalWithId(portalId, flag);
        if (null == script) {
            script = this.getWithPortalWithAlias(portalId, flag);
        }
        if (null == script) {
            script = this.getWithPortalWithName(portalId, flag);
        }
        return script;
    }

    private Script getWithPortalWithId(String portalId, String flag) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Script.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Script> cq = cb.createQuery(Script.class);
        Root<Script> root = cq.from(Script.class);
        Predicate p = cb.equal(root.get(Script_.id), flag);
        p = cb.and(p, cb.equal(root.get(Script_.portal), portalId));
        List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    private Script getWithPortalWithAlias(String portalId, String flag) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Script.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Script> cq = cb.createQuery(Script.class);
        Root<Script> root = cq.from(Script.class);
        Predicate p = cb.equal(root.get(Script_.alias), flag);
        p = cb.and(p, cb.equal(root.get(Script_.portal), portalId));
        List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    private Script getWithPortalWithName(String portalId, String flag) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Script.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Script> cq = cb.createQuery(Script.class);
        Root<Script> root = cq.from(Script.class);
        Predicate p = cb.equal(root.get(Script_.name), flag);
        p = cb.and(p, cb.equal(root.get(Script_.portal), portalId));
        List<Script> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

}