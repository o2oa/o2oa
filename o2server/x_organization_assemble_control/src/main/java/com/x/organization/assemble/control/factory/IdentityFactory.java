package com.x.organization.assemble.control.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.PersistenceProperties;

public class IdentityFactory extends AbstractFactory {

	public IdentityFactory(Business business) throws Exception {
		super(business);
		cache = new CacheCategory(Identity.class);
	}

	public Identity pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Identity o = null;
		CacheKey cacheKey = new CacheKey(Identity.class.getName(), flag);
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			o = (Identity) optional.get();
		} else {
			o = this.pickObject(flag);
			if (null != o) {
				CacheManager.put(cache, cacheKey, o);
			}
		}
		return o;
	}

	private Identity pickObject(String flag) throws Exception {
		Identity o = this.entityManagerContainer().flag(flag, Identity.class);
		if (o != null) {
			this.entityManagerContainer().get(Identity.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = PersistenceProperties.Identity.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, Identity.class);
				if (null != o) {
					this.entityManagerContainer().get(Identity.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(Identity.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
				Root<Identity> root = cq.from(Identity.class);
				Predicate p = cb.equal(root.get(Identity_.name), name);
				List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}

	public List<Identity> pick(List<String> flags) throws Exception {
		List<Identity> list = new ArrayList<>();
		for (String str : flags) {
			CacheKey cacheKey = new CacheKey(Identity.class.getName(), str);
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				list.add((Identity) optional.get());
			} else {
				Identity o = this.pickObject(str);
				if (null != o) {
					CacheManager.put(cache, cacheKey, o);
					list.add(o);
				}
			}
		}
		return list;
	}

	public <T extends Identity> List<T> sort(List<T> list) throws Exception {
		if(Config.person().getPersonUnitOrderByAsc()) {
			list = list.stream().sorted(
					Comparator.comparing(Identity::getOrderNumber, Comparator.nullsLast(Integer::compareTo)).thenComparing(
							Comparator.comparing(Identity::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
					.collect(Collectors.toList());
		}else{
			list = list.stream().sorted(
					Comparator.comparing(Identity::getOrderNumber, Comparator.nullsLast(Integer::compareTo)).reversed().thenComparing(
							Comparator.comparing(Identity::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
					.collect(Collectors.toList());
		}
		return list;
	}

}
