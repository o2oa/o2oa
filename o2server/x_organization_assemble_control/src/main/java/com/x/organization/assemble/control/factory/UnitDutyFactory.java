package com.x.organization.assemble.control.factory;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;
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

public class UnitDutyFactory extends AbstractFactory {

	public UnitDutyFactory(Business business) throws Exception {
		super(business);
		cache = new CacheCategory(UnitDuty.class);
	}

	public UnitDuty pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		UnitDuty o = null;
		CacheKey cacheKey = new CacheKey(UnitDuty.class.getName(), flag);
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			o = (UnitDuty) optional.get();
		} else {
			o = this.pickObject(flag);
			if (null != o) {
				CacheManager.put(cache, cacheKey, o);
			}
		}
		return o;
	}

	private UnitDuty pickObject(String flag) throws Exception {
		UnitDuty o = this.entityManagerContainer().flag(flag, UnitDuty.class);
		if (o != null) {
			this.entityManagerContainer().get(UnitDuty.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = PersistenceProperties.UnitDuty.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, UnitDuty.class);
				if (null != o) {
					this.entityManagerContainer().get(UnitDuty.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(UnitDuty.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
				Root<UnitDuty> root = cq.from(UnitDuty.class);
				Predicate p = cb.equal(root.get(UnitDuty_.name), name);
				List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}

	public List<UnitDuty> pick(List<String> flags) throws Exception {
		List<UnitDuty> list = new ArrayList<>();
		for (String str : flags) {
			CacheKey cacheKey = new CacheKey(UnitDuty.class.getName(), str);
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				list.add((UnitDuty) optional.get());
			} else {
				UnitDuty o = this.pickObject(str);
				if (null != o) {
					CacheManager.put(cache, cacheKey, o);
					list.add(o);
				}
			}
		}
		return list;
	}

	public <T extends UnitDuty> List<T> sort(List<T> list) {
		list = list.stream().sorted(
				Comparator.comparing(UnitDuty::getOrderNumber, Comparator.nullsLast(Integer::compareTo)).thenComparing(
						Comparator.comparing(UnitDuty::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
				.collect(Collectors.toList());
		return list;
	}

	public List<UnitDuty> listObjByIdentity(String identityId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.isMember(identityId, root.get(UnitDuty_.identityList));
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}

	public List<String> listByIdentity(String identityId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.isMember(identityId, root.get(UnitDuty_.identityList));
		return em.createQuery(cq.select(root.get(UnitDuty_.id)).where(p)).getResultList();
	}
}
