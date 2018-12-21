package com.x.organization.assemble.control.jaxrs.function;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutCount;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.core.entity.Person;

public class ActionSetText {

	protected ActionResult<WrapOutCount> execute(String attribute, String key) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutCount> result = new ActionResult<>();
			if (!StringUtils.equals(attribute, "password")) {
				EntityManager em = emc.beginTransaction(Person.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Person> cq = cb.createQuery(Person.class);
				Root<Person> root = cq.from(Person.class);
				cq.select(root);
				List<Person> list = em.createQuery(cq).getResultList();
				for (Person o : list) {
					String value = Crypto.decrypt(o.getPassword(), key);
					PropertyUtils.setProperty(o, attribute, value);
				}
				emc.commit();
				WrapOutCount wrap = new WrapOutCount();
				wrap.setCount(list.size());
				result.setData(wrap);
			}
			return result;
		}
	}

}
