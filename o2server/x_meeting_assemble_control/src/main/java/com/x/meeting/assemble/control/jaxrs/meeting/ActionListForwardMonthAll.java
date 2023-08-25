package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Meeting_;
import com.x.meeting.core.entity.Room;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class ActionListForwardMonthAll extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer monthCount) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Calendar calendar = Calendar.getInstance();
			Date start = calendar.getTime();
			calendar.add(Calendar.MONTH, monthCount);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.MILLISECOND, -1);
			Date end = calendar.getTime();
			List<Meeting> os = this.list(effectivePerson, business, start, end);
			List<Wo> wos = Wo.copier.copy(os);
			this.setOnlineLink(business, effectivePerson, wos);
			WrapTools.decorate(business, wos, effectivePerson);
			WrapTools.setAttachment(business, wos);
			// this.decorateRoom(business, wos);
			wos = wos.stream()
					.sorted(Comparator.comparing(Meeting::getStartTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	// private void decorateRoom(Business business, List<Wo> wos) throws Exception {
	// List<String> ids = ListTools.extractProperty(wos, Meeting.room_FIELDNAME,
	// String.class, true, true);
	// EntityManager em = business.entityManagerContainer().get(Room.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<Room> cq = cb.createQuery(Room.class);
	// Root<Room> root = cq.from(Room.class);
	// Predicate p = root.get(Room_.id).in(ids);
	// cq.select(root).where(p);
	// List<WoRoom> os = WoRoom.copier.copy(em.createQuery(cq).getResultList());
	// for (Wo wo : wos) {
	// wo.setWoRoom(ListTools.findWithProperty(os, Room.id_FIELDNAME,
	// wo.getRoom()));
	// }
	// }

	private List<Meeting> list(EffectivePerson effectivePerson, Business business, Date start, Date end)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Meeting> cq = cb.createQuery(Meeting.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), start);
		/** 这里两个都是startTime是对的 */
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.startTime), end));
		p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public static class Wo extends WrapOutMeeting {

		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null,
				Wo.FieldsInvisible);

	}

	public static class WoRoom extends Room {

		private static final long serialVersionUID = -4703274623056091697L;
		public static WrapCopier<Room, WoRoom> copier = WrapCopierFactory.wo(Room.class, WoRoom.class, null,
				JpaObject.FieldsInvisible);
	}

}
