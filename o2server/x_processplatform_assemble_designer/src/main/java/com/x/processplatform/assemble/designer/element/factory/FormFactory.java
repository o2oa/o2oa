package com.x.processplatform.assemble.designer.element.factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Form_;

public class FormFactory extends AbstractFactory {

	public FormFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithApplication(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Form.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Form> root = cq.from(Form.class);
		Predicate p = cb.equal(root.get(Form_.application), application);
		cq.select(root.get(Form_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplications(List<String> applications) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Form.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Form> root = cq.from(Form.class);
		Predicate p = cb.conjunction();
		if(ListTools.isNotEmpty(applications)) {
			p = cb.isMember(root.get(Form_.application), cb.literal(applications));
		}
		cq.select(root.get(Form_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Form> listWithApplicationObject(String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Form.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Form> cq = cb.createQuery(Form.class);
		Root<Form> root = cq.from(Form.class);
		Predicate p = cb.equal(root.get(Form_.application), application);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public <T extends Form> List<T> sort(List<T> list) {
		list = list.stream().sorted(Comparator.comparing(Form::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}
