package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
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
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.core.entity.ConfirmStatus;
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
			//p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(),root.get(Meeting_.invitePersonList)));
			Expression<List<String>> expression = root.get(Meeting_.invitePersonList);
			p = cb.or(p, expression.in(effectivePerson.getDistinguishedName()));
			
			if(!StringUtils.isBlank(wi.getSubject())) {
				p = cb.and(p, cb.like(root.get(Meeting_.subject), "%" + wi.getSubject() + "%"));
			}
			
			if(!StringUtils.isBlank(wi.getRoom())) {
				p = cb.and(p, cb.equal(root.get(Meeting_.room), wi.getRoom()));
			}
		   
			if(!StringUtils.isBlank(wi.getMeetingStatus())) {
				String meetingStatus = wi.getMeetingStatus();
				
				if(meetingStatus.equalsIgnoreCase("completed")) {
					p = cb.and(p, cb.or(cb.lessThan(root.get(Meeting_.completedTime), new Date()),
							cb.equal(root.get(Meeting_.manualCompleted), true)));
				}
				
	            if(meetingStatus.equalsIgnoreCase("processing")) {
	            	Date date = new Date();
	            	p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
	        		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.startTime), date));
	        		p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Meeting_.completedTime), date));
				}
	
                 if(meetingStatus.equalsIgnoreCase("wait")) {
                	 p = cb.and(p, cb.notEqual(root.get(Meeting_.manualCompleted), true));
             		 p = cb.and(p, cb.greaterThan(root.get(Meeting_.startTime), new Date()));
				 }
				
			}else {
				if(wi.getStartTime() != null) {
				   p = cb.and(p, cb.greaterThanOrEqualTo(root.get(Meeting_.startTime), wi.getStartTime()));
				}
				
				if(wi.getCompletedTime()!= null) {
				   p = cb.and(p, cb.lessThanOrEqualTo(root.get(Meeting_.completedTime), wi.getCompletedTime()));
				}
			}
			
			if(!StringUtils.isBlank(wi.getConfirmStatus())) {
		    	p = cb.and(p, cb.equal(root.get(Meeting_.confirmStatus), ConfirmStatus.valueOf(wi.getConfirmStatus().trim())));
			}
			
			if(!StringUtils.isBlank(wi.getApplicant())) {
				p = cb.and(p, cb.equal(root.get(Meeting_.applicant), wi.getApplicant()));
			 }
			
			if(!StringUtils.isBlank(wi.getInvitePersonList())) {
				p = cb.and(p, cb.isMember( wi.getInvitePersonList().trim(),root.get(Meeting_.invitePersonList)));
			 }
			
			if(!StringUtils.isBlank(wi.getAcceptPersonList())) {
				p = cb.and(p, cb.isMember( wi.getAcceptPersonList().trim(),root.get(Meeting_.acceptPersonList)));
			 }
			
			if(!StringUtils.isBlank(wi.getCheckinPersonList())) {
				p = cb.and(p, cb.isMember( wi.getCheckinPersonList().trim(),root.get(Meeting_.checkinPersonList)));
			 }
			
			if(wi.getManualCompleted() != null) {
				p = cb.and(p, cb.equal(root.get(Meeting_.manualCompleted), wi.getManualCompleted()));
			}
			
	        Order order;
	        String  sortField = wi.getSortField();
	        String  sortType = wi.getSortType();

	        if(!StringUtils.isBlank(sortField)) {
		        if(sortType.equalsIgnoreCase("asc")) {
		             order =  cb.asc(root.get(sortField));
		        }else {
		        	 order =  cb.desc(root.get(sortField));
		        }
	        }else {
	        	   order =  cb.desc(root.get("startTime"));
	        }
	        
			cq.select(root.get(Meeting_.id)).where(p).orderBy(order);
			cq.distinct(true);
			
			 TypedQuery<String> typedQuery = em.createQuery(cq);
			 int pageIndex = (page-1)*size;
			 int pageSize = size;
			 typedQuery.setFirstResult(pageIndex);
			 typedQuery.setMaxResults(pageSize);
			 ids =  typedQuery.getResultList();
			 //logger.info("pagingtypedQuery="+  typedQuery.toString()); 
			 
			 TypedQuery<String> tqCount = em.createQuery( cq.select(root.get(Meeting_.id)).where(p).distinct(true));
			 List<String> allid = tqCount.getResultList();
			 Long  tpsize =  (long) allid.size();
			 //logger.info("ids count="+  tpsize); 
			 
			 CriteriaQuery<Meeting> cqMeeting = cb.createQuery(Meeting.class);		
			 Predicate pMeeting = cb.isMember(root.get(Meeting_.id), cb.literal(ids));
			 Root<Meeting> rootMeeting = cqMeeting.from(Meeting.class);
			 cqMeeting.select(rootMeeting).where(pMeeting).orderBy(order);
		     List<Meeting> os = em.createQuery(cqMeeting).getResultList();
		    
			List<Wo> wos = Wo.copier.copy(os);
			WrapTools.decorate(business, wos, effectivePerson);
			WrapTools.setAttachment(business, wos);
			result.setData(wos);
			result.setCount(tpsize);
			return result;
		}
	}


	
	public static class Wo extends WrapOutMeeting {
		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

    public static class Wi  {
    	
    	@FieldDescribe("标题.")
    	private String subject;
    	
		@FieldDescribe("所属楼层.")
		private String room;
		
		@FieldDescribe("开始时间.")
		private Date startTime;
		
		@FieldDescribe("结束时间.")
		private Date completedTime;
		
		@FieldDescribe("会议预定状态.(allow|deny|wait)")
		private String confirmStatus;
		
		@FieldDescribe("创建人员.")
		private String applicant;
		
		@FieldDescribe("邀请人员,身份,组织.")
		private String invitePersonList;
		
		@FieldDescribe("接受人员.")
		private String acceptPersonList;
		
		@FieldDescribe("签到人员.")
		private String checkinPersonList;
		
		@FieldDescribe("会议是否手工结束.(true|false)")
		private Boolean manualCompleted;
		
		@FieldDescribe("会议当前状态.(wait|processing|completed)")
		private String meetingStatus;
		
		@FieldDescribe("排序字段.(startTime|completedTime|room)")
		private String sortField;
		
		@FieldDescribe("排序.(desc|asc)")
		private String sortType;
		
		
		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

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

		public String getApplicant() {
			return applicant;
		}

		public void setApplicant(String applicant) {
			this.applicant = applicant;
		}

		
		public String getInvitePersonList() {
			return invitePersonList;
		}

		public void setInvitePersonList(String invitePersonList) {
			this.invitePersonList = invitePersonList;
		}

		public String getAcceptPersonList() {
			return acceptPersonList;
		}

		public void setAcceptPersonList(String acceptPersonList) {
			this.acceptPersonList = acceptPersonList;
		}

		public String getCheckinPersonList() {
			return checkinPersonList;
		}

		public void setCheckinPersonList(String checkinPersonList) {
			this.checkinPersonList = checkinPersonList;
		}

		public Boolean getManualCompleted() {
			return manualCompleted;
		}

		public void setManualCompleted(Boolean manualCompleted) {
			this.manualCompleted = manualCompleted;
		}

		public String getMeetingStatus() {
			return meetingStatus;
		}

		public void setMeetingStatus(String meetingStatus) {
			this.meetingStatus = meetingStatus;
		}

		public String getSortField() {
			return sortField;
		}

		public void setSortField(String sortField) {
			this.sortField = sortField;
		}

		public String getSortType() {
			return sortType;
		}

		public void setSortType(String sortType) {
			this.sortType = sortType;
		}
		
			
	}
}
