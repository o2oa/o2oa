package com.x.organization.assemble.control.jaxrs.personattribute;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;

class BaseAction extends StandardJaxrsAction {

	/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
	protected boolean uniqueDuplicateWhenNotEmpty(Business business, PersonAttribute personAttribute) throws Exception {
		if (StringUtils.isNotEmpty(personAttribute.getUnique())) {
			if (business.entityManagerContainer().duplicateWithFlags(personAttribute.getId(), PersonAttribute.class,
					personAttribute.getUnique())) {
				return true;
			}
		}
		return false;
	}

	protected boolean duplicateOnPerson(Business business, Person person, String name, PersonAttribute exclude)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), person.getId());
		p = cb.and(p, cb.equal(root.get(PersonAttribute_.name), name));
		p = cb.and(p, cb.notEqual(root.get(PersonAttribute_.id), exclude.getId()));
		long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}
}
