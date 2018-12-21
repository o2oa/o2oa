package com.x.organization.assemble.express.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.CacheFactory;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class PersonAttributeFactory extends AbstractFactory {

	private Ehcache cache;

	public PersonAttributeFactory(Business business) throws Exception {
		super(business);
		this.cache = CacheFactory.getPersonAttributeCache();
	}

	public PersonAttribute pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		PersonAttribute o = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (PersonAttribute) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
		}
		return o;
	}

	private PersonAttribute pickObject(String flag) throws Exception {
		PersonAttribute o = this.entityManagerContainer().flag(flag, PersonAttribute.class);
		if (o != null) {
			this.entityManagerContainer().get(PersonAttribute.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = personAttribute_distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, PersonAttribute.class);
				if (null != o) {
					this.entityManagerContainer().get(PersonAttribute.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(PersonAttribute.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
				Root<PersonAttribute> root = cq.from(PersonAttribute.class);
				Predicate p = cb.equal(root.get(PersonAttribute_.name), name);
				List<PersonAttribute> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}

	public List<PersonAttribute> pick(List<String> flags) throws Exception {
		List<PersonAttribute> list = new ArrayList<>();
		for (String str : flags) {
			Element element = cache.get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((PersonAttribute) element.getObjectValue());
				}
			} else {
				PersonAttribute o = this.pickObject(str);
				cache.put(new Element(str, o));
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public <T extends PersonAttribute> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(PersonAttribute::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(
								Comparator.comparing(PersonAttribute::getName, Comparator.nullsFirst(String::compareTo))
										.reversed()))
				.collect(Collectors.toList());
		return list;
	}

}