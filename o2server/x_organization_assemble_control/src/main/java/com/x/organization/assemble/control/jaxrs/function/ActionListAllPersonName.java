package com.x.organization.assemble.control.jaxrs.function;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.WrapInStringList;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

public class ActionListAllPersonName {

	protected List<Tuple> execute(Business business, WrapInStringList wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Person> root = cq.from(Person.class);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : wrapIn.getValueList()) {
			selections.add(root.get(str));
		}
		cq.multiselect(selections);
		List<Tuple> wraps = em.createQuery(cq).getResultList();
		return wraps;
	}

}
