package com.x.processplatform.assemble.surface.jaxrs.snap;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Snap;
import com.x.processplatform.core.entity.content.Snap_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

abstract class BaseAction extends StandardJaxrsAction {

	protected Predicate manageFilter(Business business, FilterWi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Snap.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Snap> cq = cb.createQuery(Snap.class);
		Root<Snap> root = cq.from(Snap.class);
		Predicate p = cb.conjunction();
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Snap_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Snap_.process).in(business.process().listEditionProcess(wi.getProcessList())));
		}
		if (ListTools.isNotEmpty(wi.getPersonList())) {
			p = cb.and(p, root.get(Snap_.person).in(business.organization().person().list(wi.getPersonList())));
		}
		if (StringUtils.isNoneBlank(wi.getKey())) {
			String key = StringTools.escapeSqlLikeKey(wi.getKey());
			p = cb.and(p,
					cb.or(cb.like(root.get(Snap_.title), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR),
							cb.like(root.get(Snap_.creatorPerson), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR),
							cb.like(root.get(Snap_.creatorUnit), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR)));
		}
		return p;
	}

	protected Predicate filter(EffectivePerson effectivePerson, Business business, FilterWi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Snap.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Snap> cq = cb.createQuery(Snap.class);
		Root<Snap> root = cq.from(Snap.class);
		Predicate p = cb.equal(root.get(Snap_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Snap_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Snap_.process).in(business.process().listEditionProcess(wi.getProcessList())));
		}
		if (StringUtils.isNoneBlank(wi.getKey())) {
			String key = StringTools.escapeSqlLikeKey(wi.getKey());
			p = cb.and(p,
					cb.or(cb.like(root.get(Snap_.title), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR),
							cb.like(root.get(Snap_.creatorPerson), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR),
							cb.like(root.get(Snap_.creatorUnit), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR)));
		}
		return p;
	}

	protected Predicate myFilter(EffectivePerson effectivePerson, Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Snap.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Snap> cq = cb.createQuery(Snap.class);
		Root<Snap> root = cq.from(Snap.class);
		return cb.equal(root.get(Snap_.person), effectivePerson.getDistinguishedName());
	}

	protected Predicate myApplicationFilter(EffectivePerson effectivePerson, Business business, Application application)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Snap.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Snap> cq = cb.createQuery(Snap.class);
		Root<Snap> root = cq.from(Snap.class);
		Predicate p = cb.equal(root.get(Snap_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Snap_.application), application.getId()));
		return p;
	}

	protected Predicate myProcessFilter(EffectivePerson effectivePerson, Business business, Process process)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Snap.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Snap> cq = cb.createQuery(Snap.class);
		Root<Snap> root = cq.from(Snap.class);
		Predicate p = cb.equal(root.get(Snap_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Snap_.process), process.getId()));
		return p;
	}

	protected class FilterWi {

		@FieldDescribe("应用id")
		private List<String> applicationList;

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("创建人")
		private List<String> personList;

		@FieldDescribe("匹配关键字")
		private String key;

		public List<String> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(List<String> applicationList) {
			this.applicationList = applicationList;
		}

		public List<String> getProcessList() {
			return processList;
		}

		public void setProcessList(List<String> processList) {
			this.processList = processList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

	protected abstract static class RankWo extends Snap {

		private Long rank;

		private static final long serialVersionUID = 2279846765261247910L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
