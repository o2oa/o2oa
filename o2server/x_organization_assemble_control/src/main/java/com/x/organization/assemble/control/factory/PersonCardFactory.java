package com.x.organization.assemble.control.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.PersonCard;
import com.x.organization.core.entity.PersonCard_;

import net.sf.ehcache.Element;

public class PersonCardFactory extends AbstractFactory {

	public PersonCardFactory(Business business) throws Exception {
		super(business);
		cache = ApplicationCache.instance().getCache(PersonCard.class);
	}

	public PersonCard pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		PersonCard o = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (PersonCard) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
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
				List<PersonCard> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
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
		cq.select(root.get(PersonCard_.groupType)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}
}