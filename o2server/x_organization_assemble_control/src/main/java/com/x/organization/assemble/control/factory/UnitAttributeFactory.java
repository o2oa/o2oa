package com.x.organization.assemble.control.factory;

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

import com.x.base.core.project.cache.ApplicationCache;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitAttribute_;

import net.sf.ehcache.Element;

public class UnitAttributeFactory extends AbstractFactory {

	public UnitAttributeFactory(Business business) throws Exception {
		super(business);
		cache = ApplicationCache.instance().getCache(UnitAttribute.class);
	}

	public UnitAttribute pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		UnitAttribute o = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (UnitAttribute) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
		}
		return o;
	}

	private UnitAttribute pickObject(String flag) throws Exception {
		UnitAttribute o = this.entityManagerContainer().flag(flag, UnitAttribute.class);
		if (o != null) {
			this.entityManagerContainer().get(UnitAttribute.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = PersistenceProperties.UnitAttribute.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, UnitAttribute.class);
				if (null != o) {
					this.entityManagerContainer().get(UnitAttribute.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(UnitAttribute.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<UnitAttribute> cq = cb.createQuery(UnitAttribute.class);
				Root<UnitAttribute> root = cq.from(UnitAttribute.class);
				Predicate p = cb.equal(root.get(UnitAttribute_.name), name);
				List<UnitAttribute> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}

	public List<UnitAttribute> pick(List<String> flags) throws Exception {
		List<UnitAttribute> list = new ArrayList<>();
		for (String str : flags) {
			Element element = cache.get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((UnitAttribute) element.getObjectValue());
				}
			} else {
				UnitAttribute o = this.pickObject(str);
				cache.put(new Element(str, o));
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public <T extends UnitAttribute> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(UnitAttribute::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(
								Comparator.comparing(UnitAttribute::getName, Comparator.nullsFirst(String::compareTo))
										.reversed()))
				.collect(Collectors.toList());
		return list;
	}
}