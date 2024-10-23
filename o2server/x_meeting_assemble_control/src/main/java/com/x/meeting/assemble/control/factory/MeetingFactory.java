package com.x.meeting.assemble.control.factory;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.meeting.assemble.control.AbstractFactory;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Meeting_;

public class MeetingFactory extends AbstractFactory {

	public MeetingFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithRoom(String roomId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.equal(root.get(Meeting_.room), roomId);
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithAppliedWait(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.equal(root.get(Meeting_.applicant), person);
		p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
		p = cb.and(p, cb.greaterThan(root.get(Meeting_.startTime), new Date()));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithAppliedProcessing(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Date date = new Date();
		Predicate p = cb.equal(root.get(Meeting_.applicant), person);
		p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.startTime), date));
		p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), date));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithAppliedCompleted(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.equal(root.get(Meeting_.applicant), person);
		p = cb.and(p, cb.or(cb.lessThan(root.get(Meeting_.completedTime), new Date()),
				cb.equal(root.get(Meeting_.manualCompleted), true)));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithInvitedWait(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.isMember(person, root.get(Meeting_.invitePersonList));
		p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
		p = cb.and(p, cb.greaterThan(root.get(Meeting_.startTime), new Date()));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithInvitedProcessing(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Date date = new Date();
		Predicate p = cb.isMember(person, root.get(Meeting_.invitePersonList));
		p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.startTime), date));
		p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), date));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithInvitedCompleted(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.isMember(person, root.get(Meeting_.invitePersonList));
		p = cb.and(p, cb.or(cb.lessThan(root.get(Meeting_.completedTime), new Date()),
				cb.equal(root.get(Meeting_.manualCompleted), true)));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithInvitedRejected(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.isMember(person, root.get(Meeting_.invitePersonList));
		p = cb.and(p, cb.isMember(person, root.get(Meeting_.rejectPersonList)));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWaitAccept(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), new Date());
		p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
		p = cb.and(p, cb.isMember(person, root.get(Meeting_.invitePersonList)));
		p = cb.and(p, cb.isNotMember(person, root.get(Meeting_.acceptPersonList)));
		p = cb.and(p, cb.isNotMember(person, root.get(Meeting_.rejectPersonList)));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWaitConfirm() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), new Date());
		p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), ConfirmStatus.wait));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWaitConfirm(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), new Date());
		p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), ConfirmStatus.wait));
		p = cb.and(p,
				cb.or(cb.equal(root.get(Meeting_.applicant), person), cb.equal(root.get(Meeting_.auditor), person),
						cb.isMember(person, root.get(Meeting_.invitePersonList))));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithDate(Date start, Date end) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		//Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), start);
		//p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.startTime), end));
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), start);
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.completedTime), end));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithDateAndRoom(Date start, Date end,String roomId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		//Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), start);
		//p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.startTime), end));
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), start);
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.completedTime), end));
		p = cb.and(p, cb.equal(root.get(Meeting_.room), roomId));

		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWithDate(String person, Date start, Date end) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		//Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), start);
		//p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.startTime), end));
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), start);
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.completedTime), end));
		p = cb.and(p,
				cb.or(cb.equal(root.get(Meeting_.applicant), person), cb.equal(root.get(Meeting_.auditor), person),
						cb.isMember(person, root.get(Meeting_.invitePersonList))));
		p = cb.and(p, cb.equal(root.get(Meeting_.manualCompleted), false));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listFutureWithRoom(String roomId, boolean allowOnly) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		//Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), new Date());
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), new Date());
		p = cb.and(p, cb.equal(root.get(Meeting_.room), roomId));
		p = cb.and(p, cb.equal(root.get(Meeting_.manualCompleted), false));
		if (allowOnly) {
			p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), ConfirmStatus.allow));
		}
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listAllWithRoom(String roomId, boolean allowOnly,Date startTime, Date completedTime) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), startTime);
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.completedTime), completedTime));
		p = cb.and(p, cb.equal(root.get(Meeting_.room), roomId));
		p = cb.and(p, cb.equal(root.get(Meeting_.manualCompleted), false));
		if (allowOnly) {
			p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), ConfirmStatus.allow));
		}
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

//	@MethodDescribe("列示所有首字母开始的Building.")
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.like(root.get(Meeting_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	//@MethodDescribe("进行模糊查询.")
	public List<String> listLike(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.like(root.get(Meeting_.subject), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Meeting_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Meeting_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	//@MethodDescribe("根据拼音进行模糊查询.")
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.like(root.get(Meeting_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Meeting_.pinyinInitial), str + "%"));
		cq.select(root.get(Meeting_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

}
