package com.x.processplatform.assemble.surface.factory.content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class ReadFactory extends AbstractFactory {

	public ReadFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<String> listWithWork(Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.work), work.getId());
		cq.select(root.get(Read_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Read> listWithWorkObject(Work work) throws Exception {
		List<String> ids = this.listWithWork(work);
		if (ListTools.isEmpty(ids)) {
			return new ArrayList<>();
		}
		return this.business().entityManagerContainer().list(Read.class, ids);
	}

	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.job), job);
		cq.select(root.get(Read_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWithWork(String person, String work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), person);
		p = cb.and(p, cb.equal(root.get(Read_.completed), false));
		p = cb.and(p, cb.equal(root.get(Read_.work), work));
		cq.select(root.get(Read_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWithWorkCompleted(String person, String workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), person);
		p = cb.and(p, cb.equal(root.get(Read_.completed), true));
		p = cb.and(p, cb.equal(root.get(Read_.workCompleted), workCompleted));
		cq.select(root.get(Read_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithWorkCompleted(String workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.workCompleted), workCompleted);
		cq.select(root.get(Read_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Read> listWithWorkCompletedObject(WorkCompleted workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Read> cq = cb.createQuery(Read.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.workCompleted), workCompleted.getId());
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Long countWithPersonWithWork(String person, Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.work), work.getId());
		p = cb.and(p, cb.equal(root.get(Read_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPersonWithWorkCompleted(String person, WorkCompleted workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.workCompleted), workCompleted.getId());
		p = cb.and(p, cb.equal(root.get(Read_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPersonWithJob(String person, String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.job), job);
		p = cb.and(p, cb.equal(root.get(Read_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<Read> listWithPersonObject(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Read> cq = cb.createQuery(Read.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), person);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 统计指定人员的待阅数量
	 */
	public Long countWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), person);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 统计指定人员在指定应用的待阅数量
	 */
	public Long countWithPersonApplication(String person, String application) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), person);
		p = cb.and(p, cb.equal(root.get(Read_.application), application));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.process), id);
		cq.select(root.get(Read_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.application), id);
		cq.select(root.get(Read_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
//	/* read是否允许处理 */
//	public Boolean allowProcessing(EffectivePerson effectivePerson, Read read) throws Exception {
//		Business business = this.business();
//		if (StringUtils.equals(effectivePerson.getDistinguishedName(), read.getPerson())) {
//			return true;
//		}
//		Application application = business.application().pick(read.getApplication());
//		if (null != application) {
//			if (business.application().allowControl(effectivePerson, application)) {
//				return true;
//			}
//		}
//		return false;
//	}

	public <T extends Read> List<T> sort(List<T> list) {
		list = list.stream().sorted(Comparator.comparing(Read::getCreateTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return list;
	}
}