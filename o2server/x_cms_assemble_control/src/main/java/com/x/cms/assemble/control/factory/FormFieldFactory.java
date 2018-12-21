package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.FormField;
import com.x.cms.core.entity.element.FormField_;

public class FormFieldFactory extends AbstractFactory {

	public FormFieldFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithAppInfo(String appId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(FormField.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<FormField> root = cq.from(FormField.class);
		Predicate p = cb.equal(root.get(FormField_.appId), appId);
		cq.select(root.get(FormField_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithForm(String form) throws Exception {
		EntityManager em = this.entityManagerContainer().get(FormField.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<FormField> root = cq.from(FormField.class);
		Predicate p = cb.equal(root.get(FormField_.form), form);
		cq.select(root.get(FormField_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}