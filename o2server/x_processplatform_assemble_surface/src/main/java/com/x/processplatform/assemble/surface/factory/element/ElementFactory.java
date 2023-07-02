package com.x.processplatform.assemble.surface.factory.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

import net.sf.ehcache.Ehcache;

public abstract class ElementFactory extends AbstractFactory {

    protected Ehcache cache;

    public ElementFactory(Business abstractBusiness) throws Exception {
        super(abstractBusiness);
    }

    @SuppressWarnings("unchecked")
    protected <T extends JpaObject> T pick(String flag, Class<T> clz) throws Exception {
        CacheCategory cacheCategory = new CacheCategory(clz);
        CacheKey cacheKey = new CacheKey(flag);
        T t = null;
        Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
        if (optional.isPresent()) {
            t = (T) optional.get();
        } else {
            t = this.entityManagerContainer().flag(flag, clz);
            if (t != null) {
                this.entityManagerContainer().get(clz).detach(t);
                CacheManager.put(cacheCategory, cacheKey, t);
            }
        }
        return t;
    }

    protected <T extends JpaObject> List<T> pick(Collection<String> flags, Class<T> clz) throws Exception {
        List<T> list = new ArrayList<>();
        for (String str : flags) {
            T t = pick(str, clz);
            if (null != t) {
                list.add(t);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    protected <T extends JpaObject> T pick(Application application, String flag, Class<T> clz) throws Exception {
        if (null == application) {
            return null;
        }
        CacheCategory cacheCategory = new CacheCategory(clz);
        CacheKey cacheKey = new CacheKey(application.getId(), flag);
        T t = null;
        Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
        if (optional.isPresent()) {
            t = (T) optional.get();
        } else {
            t = this.entityManagerContainer().restrictFlag(flag, clz, Process.application_FIELDNAME,
                    application.getId());
            if (t != null) {
                this.entityManagerContainer().get(clz).detach(t);
                CacheManager.put(cacheCategory, cacheKey, t);
            }
        }
        return t;
    }

    // 取得属于指定Process 的设计元素
    @SuppressWarnings("unchecked")
    protected <T extends JpaObject> List<T> listWithProcess(Class<T> clz, Process process) throws Exception {
        List<T> list = new ArrayList<>();
        if (null == process) {
            return list;
        }
        CacheCategory cacheCategory = new CacheCategory(clz);
        CacheKey cacheKey = new CacheKey("listWithProcess", process.getId());
        Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
        if (optional.isPresent()) {
            list = (List<T>) optional.get();
        } else {
            EntityManager em = this.entityManagerContainer().get(clz);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(clz);
            Root<T> root = cq.from(clz);
            Predicate p = cb.equal(root.get(Activity.process_FIELDNAME), process.getId());
            cq.select(root).where(p);
            List<T> os = em.createQuery(cq).getResultList();
            for (T t : os) {
                em.detach(t);
                list.add(t);
            }
            // 将object改为unmodifiable
            list = Collections.unmodifiableList(list);
            CacheManager.put(cacheCategory, cacheKey, list);
        }
        return list;
    }

}