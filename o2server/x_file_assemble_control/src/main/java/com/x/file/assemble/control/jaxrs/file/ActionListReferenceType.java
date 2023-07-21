package com.x.file.assemble.control.jaxrs.file;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.File_;
import com.x.file.core.entity.open.ReferenceType;

class ActionListReferenceType extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			for (ReferenceType o : this.listReferenceTypeWithPerson(business, effectivePerson)) {
				Wo wo = new Wo();
				wo.setName(o.toString());
				wo.setValue(o.toString());
				wo.setCount(this.countWithPersonWithReferenceType(business, effectivePerson, o));
				wos.add(wo);
			}
			result.setData(wos);
			return result;
		}
	}

	private List<ReferenceType> listReferenceTypeWithPerson(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ReferenceType> cq = cb.createQuery(ReferenceType.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.person), effectivePerson.getDistinguishedName());
		cq.select(root.get(File_.referenceType)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	private Long countWithPersonWithReferenceType(Business business, EffectivePerson effectivePerson,
			ReferenceType referenceType) throws Exception {
		EntityManager em = business.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(File_.referenceType), referenceType));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("名称")
		private String name;
		@FieldDescribe("值")
		private String value;
		@FieldDescribe("数量")
		private Long count;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

	}
}
