package com.x.custom.index.assemble.control.factory;

import java.util.Optional;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.custom.index.assemble.control.AbstractFactory;
import com.x.custom.index.assemble.control.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.Person;

public class PersonFactory extends AbstractFactory {

	public PersonFactory(Business business) throws Exception {
		super(business);
	}

	public Person pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		Person o = null;
		CacheKey cacheKey = new CacheKey(this.getClass(), flag);
		Optional<?> optional = CacheManager.get(business().personCache(), cacheKey);
		if (optional.isPresent()) {
			o = (Person) optional.get();
		} else {
			o = this.pickObject(flag);
			CacheManager.put(business().personCache(), cacheKey, o);
		}
		return o;
	}

	private Person pickObject(String flag) throws Exception {
		Person o = this.entityManagerContainer().flag(flag, Person.class);
		if (o != null) {
			this.entityManagerContainer().get(Person.class).detach(o);
		} else {
//			String name = flag;
			Matcher matcher = PersistenceProperties.Person.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
//				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, Person.class);
				if (null != o) {
					this.entityManagerContainer().get(Person.class).detach(o);
				}
			}
//			if (null == o) {
//				if Config.person().
//				EntityManager em = this.entityManagerContainer().get(Person.class);
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaQuery<Person> cq = cb.createQuery(Person.class);
//				Root<Person> root = cq.from(Person.class);
//				Predicate p = cb.equal(root.get(Person_.name), name);
//				List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList();
//				if (os.size() == 1) {
//					o = os.get(0);
//					em.detach(o);
//				}
//			}
		}
		return o;
	}

}