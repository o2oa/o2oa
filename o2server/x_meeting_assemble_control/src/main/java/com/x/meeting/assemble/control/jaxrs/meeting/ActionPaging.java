package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Meeting_;

class ActionPaging extends BaseAction {
	Logger logger = LoggerFactory.getLogger(ActionPaging.class);
	
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson,Integer page , Integer size, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			
			Business business = new Business(emc);
			List<String> ids = new ArrayList<>();
			
			EntityManager em = emc.get(Meeting.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Meeting> root = cq.from(Meeting.class);
			
			Predicate p = cb.equal(root.get(Meeting_.applicant), effectivePerson.getDistinguishedName());
			
			if(!StringUtils.isBlank(wi.getRoom())) {
				p = cb.and(p, cb.equal(root.get(Meeting_.room), wi.getRoom()));
			}
		   
			if(wi.getStartTime() != null) {
			   p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), wi.getStartTime()));
			}
			
			if(wi.getCompletedTime()!= null) {
			   p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.completedTime), wi.getCompletedTime()));
			}
			
			if(!StringUtils.isBlank(wi.getConfirmStatus())) {
		    	p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), wi.getConfirmStatus()));
			}
			
			cq.select(root.get(Meeting_.id)).where(p);
			
			 TypedQuery<String> typedQuery = em.createQuery(cq);
			    //设置分页
			 int pageIndex = (page-1)*size;
			 int pageSize = page*size;
			 typedQuery.setFirstResult(pageIndex);
			 typedQuery.setMaxResults(pageSize);
			    
			 //logger.info("typedQuery="+  typedQuery.toString()); 
			 ids =  typedQuery.getResultList();
			
			List<Wo> wos = Wo.copier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(business, wos, effectivePerson);
			WrapTools.setAttachment(business, wos);
			SortTools.desc(wos, Meeting.startTime_FIELDNAME);
			result.setData(wos);
			return result;
		}
	}


	
	public static class Wo extends WrapOutMeeting {
		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

    public static class Wi  {
    	
		@FieldDescribe("所属楼层.")
		private String room;
		
		@FieldDescribe("开始时间.")
		private Date startTime;
		
		@FieldDescribe("结束时间.")
		private Date completedTime;
		
		@FieldDescribe("会议状态.(wait|processing|completed)")
		private String confirmStatus;
		


		public String getConfirmStatus() {
			return confirmStatus;
		}

		public void setConfirmStatus(String confirmStatus) {
			this.confirmStatus = confirmStatus;
		}

		public String getRoom() {
			return room;
		}

		public void setRoom(String room) {
			this.room = room;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}
			
	}
}
