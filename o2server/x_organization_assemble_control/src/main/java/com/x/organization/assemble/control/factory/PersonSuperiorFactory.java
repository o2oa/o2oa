package com.x.organization.assemble.control.factory;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.PersonSuperior;
import com.x.organization.core.entity.PersonSuperior_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author sy
 * @date 2022/8/15 15:14
 * @description
 */
public class PersonSuperiorFactory extends AbstractFactory {

    public PersonSuperiorFactory(Business business) throws Exception {
        super(business);
        cache =  new Cache.CacheCategory(PersonSuperior.class);
    }

    public PersonSuperior pick(String flag) throws Exception {
        if (StringUtils.isEmpty(flag)) {
            return null;
        }
        PersonSuperior o = null;
        Cache.CacheKey cacheKey = new Cache.CacheKey(flag);
        Optional<?> optional = CacheManager.get(cache, cacheKey);
        if (optional.isPresent()) {
            o = (PersonSuperior) optional.get();
        } else {
            o = this.pickObject(flag);
            CacheManager.put(cache, cacheKey, o);
        }
        return o;
    }

    private PersonSuperior pickObject(String flag) throws Exception {
        PersonSuperior o = this.entityManagerContainer().flag(flag, PersonSuperior.class);
        if (o != null) {
            this.entityManagerContainer().get(PersonSuperior.class).detach(o);
        } else {
            String name = flag;
            Matcher matcher = PersistenceProperties.PersonSuperior.distinguishedName_pattern.matcher(flag);
            if (matcher.find()) {
                name = matcher.group(1);
                String unique = matcher.group(2);
                o = this.entityManagerContainer().flag(unique, PersonSuperior.class);
                if (null != o) {
                    this.entityManagerContainer().get(PersonSuperior.class).detach(o);
                }
            }
            if (null == o) {
                EntityManager em = this.entityManagerContainer().get(PersonSuperior.class);
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<PersonSuperior> cq = cb.createQuery(PersonSuperior.class);
                Root<PersonSuperior> root = cq.from(PersonSuperior.class);
                Predicate p = cb.equal(root.get(PersonSuperior_.name), name);
                List<PersonSuperior> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
                if (os.size() == 1) {
                    o = os.get(0);
                    em.detach(o);
                }
            }
        }
        return o;
    }

    public List<PersonSuperior> pick(List<String> flags) throws Exception {
        List<PersonSuperior> list = new ArrayList<>();
        for (String str : flags) {
            Cache.CacheKey cacheKey = new Cache.CacheKey(str);
            Optional<?> optional = CacheManager.get(cache, cacheKey);
            if (optional.isPresent()) {
                list.add((PersonSuperior) optional.get());
            } else {
                PersonSuperior o = this.pickObject(str);
                CacheManager.put(cache, cacheKey, o);
                if (null != o) {
                    list.add(o);
                }
            }
        }
        return list;
    }

    public <T extends PersonSuperior> List<T> sort(List<T> list) {
        list = list.stream()
                .sorted(Comparator.comparing(PersonSuperior::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(
                                Comparator.comparing(PersonSuperior::getName, Comparator.nullsFirst(String::compareTo))
                                        .reversed()))
                .collect(Collectors.toList());
        return list;
    }


}
