package com.x.organization.assemble.control.factory;

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
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.PersonCard;
import com.x.organization.core.entity.PersonCard_;

public class PersonCardFactory extends AbstractFactory {

	public PersonCardFactory(Business business) throws Exception {
		super(business);
		cache = new CacheCategory(PersonCard.class);
	}

	public PersonCard pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		PersonCard o = null;
		CacheKey cacheKey = new CacheKey(flag);
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			o = (PersonCard) optional.get();
		} else {
			o = this.pickObject(flag);
			CacheManager.put(cache, cacheKey, o);
		}
		return o;
	}
	
	private PersonCard pickObject(String flag) throws Exception {
		PersonCard o = this.entityManagerContainer().flag(flag, PersonCard.class);
		if (o != null) {
			this.entityManagerContainer().get(PersonCard.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = PersistenceProperties.PersonCard.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, PersonCard.class);
				if (null != o) {
					this.entityManagerContainer().get(PersonCard.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(PersonCard.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PersonCard> cq = cb.createQuery(PersonCard.class);
				Root<PersonCard> root = cq.from(PersonCard.class);
				Predicate p = cb.equal(root.get(PersonCard_.name), name);
				List<PersonCard> os = em.createQuery(cq.select(root).where(p)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}
	public List<String> fetchAllIdsByCreator(String distinguishName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PersonCard.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PersonCard> root = cq.from(PersonCard.class);
		Predicate p = cb.equal(root.get(PersonCard_.distinguishedName), distinguishName);
		cq.select(root.get(PersonCard_.id)).where(p).orderBy(cb.asc(root.get(PersonCard_.orderNumber)));
		return em.createQuery(cq).getResultList();
	}
	public List<String> fetchAllGroupTypeByCreator(String distinguishName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PersonCard.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PersonCard> root = cq.from(PersonCard.class);
		Predicate p = cb.equal(root.get(PersonCard_.distinguishedName), distinguishName);
		cq.select(root.get(PersonCard_.groupType)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}
}