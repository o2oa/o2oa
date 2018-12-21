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
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

import net.sf.ehcache.Element;

public class RoleFactory extends AbstractFactory {

	public RoleFactory(Business business) throws Exception {
		super(business);
		cache = ApplicationCache.instance().getCache(Role.class);
	}

	public Role pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Role o = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (Role) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
		}
		return o;
	}

	private Role pickObject(String flag) throws Exception {
		Role o = this.entityManagerContainer().flag(flag, Role.class);
		if (o != null) {
			this.entityManagerContainer().get(Role.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = PersistenceProperties.Role.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, Role.class);
				if (null != o) {
					this.entityManagerContainer().get(Role.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(Role.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Role> cq = cb.createQuery(Role.class);
				Root<Role> root = cq.from(Role.class);
				Predicate p = cb.equal(root.get(Role_.name), name);
				List<Role> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}

	public List<Role> pick(List<String> flags) throws Exception {
		List<Role> list = new ArrayList<>();
		for (String str : flags) {
			Element element = cache.get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((Role) element.getObjectValue());
				}
			} else {
				Role o = this.pickObject(str);
				cache.put(new Element(str, o));
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public <T extends Role> List<T> sort(List<T> list) {
		list = list
				.stream().sorted(
						Comparator.comparing(Role::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
								.thenComparing(Comparator
										.comparing(Role::getName, Comparator.nullsFirst(String::compareTo)).reversed()))
				.collect(Collectors.toList());
		return list;
	}

}