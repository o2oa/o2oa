package com.x.meeting.assemble.control.jaxrs.room;

import com.x.base.core.project.tools.SortTools;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Meeting_;
import com.x.meeting.core.entity.Room;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = business.room().list();
			List<Wo> wos = Wo.copier.copy(emc.list(Room.class, ids));
			SortTools.asc(wos, Room.orderNumber_FIELDNAME, Room.name_FIELDNAME);
			List<Meeting> meetings = this.listMeeting(business);
			Map<String, List<Meeting>> map = meetings.stream().collect(Collectors.groupingBy(Meeting::getRoom));
			for (Wo wo : wos) {
				List<Meeting> list = map.get(wo.getId());
				if (null != list) {
					list = list.stream().sorted(Comparator.comparing(Meeting::getStartTime))
							.collect(Collectors.toList());
					wo.setMeetingList(WoMeeting.copier.copy(list));
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Room {

		private static final long serialVersionUID = -969148596991975992L;

		static WrapCopier<Room, Wo> copier = WrapCopierFactory.wo(Room.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		private List<WoMeeting> meetingList;

		public List<WoMeeting> getMeetingList() {
			return meetingList;
		}

		public void setMeetingList(List<WoMeeting> meetingList) {
			this.meetingList = meetingList;
		}

	}

	public static class WoMeeting extends Meeting {

		private static final long serialVersionUID = 4259697355042558631L;

		static WrapCopier<Meeting, WoMeeting> copier = WrapCopierFactory.wo(Meeting.class, WoMeeting.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}

	public List<Meeting> listMeeting(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Meeting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Meeting> cq = cb.createQuery(Meeting.class);
		Root<Meeting> root = cq.from(Meeting.class);
		Predicate p = cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), new Date());
		// p = cb.and(p, cb.equal(root.get(Meeting_.room), room.getId()));
		p = cb.and(p, cb.equal(root.get(Meeting_.manualCompleted), false));
		p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), ConfirmStatus.allow));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

}
