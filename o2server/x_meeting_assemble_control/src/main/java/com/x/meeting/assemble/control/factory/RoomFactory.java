package com.x.meeting.assemble.control.factory;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.meeting.assemble.control.AbstractFactory;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Meeting_;
import com.x.meeting.core.entity.Room;
import com.x.meeting.core.entity.Room_;

public class RoomFactory extends AbstractFactory {

	public RoomFactory(Business business) throws Exception {
		super(business);
	}

	/* 列示所有的Room */
	public List<String> list() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Room.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Room> root = cq.from(Room.class);
		cq.select(root.get(Room_.id));
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithBuilding(String floorId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Room.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Room> root = cq.from(Room.class);
		Predicate p = cb.equal(root.get(Room_.building), floorId);
		cq.select(root.get(Room_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public boolean checkIdle(String roomId, Date startTime, Date completedTime, String excludeMeetingId)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.equal(root.get(Meeting_.room), roomId);
		p = cb.and(p, cb.equal(root.get(Meeting_.manualCompleted), false));
		p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), ConfirmStatus.allow));
		if (StringUtils.isNotEmpty(excludeMeetingId)) {
			p = cb.and(p, cb.notEqual(root.get(Meeting_.id), excludeMeetingId));
		}
		p = cb.and(p,
				cb.or(cb.between(root.get(Meeting_.startTime), startTime, completedTime),
						cb.between(root.get(Meeting_.completedTime), startTime, completedTime),
						cb.and(cb.lessThanOrEqualTo(root.get(Meeting_.startTime), startTime),
								cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), completedTime))));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult() == 0L;
	}

	//@MethodDescribe("列示所有首字母开始的Room.")
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Room.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Room> root = cq.from(Room.class);
		Predicate p = cb.like(root.get(Room_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Room_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	//@MethodDescribe("进行模糊查询.")
	public List<String> listLike(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Room.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Room> root = cq.from(Room.class);
		Predicate p = cb.like(root.get(Room_.name), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Room_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Room_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Room_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	//@MethodDescribe("根据拼音进行模糊查询.")
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Room.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Room> root = cq.from(Room.class);
		Predicate p = cb.like(root.get(Room_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Room_.pinyinInitial), str + "%"));
		cq.select(root.get(Room_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

}