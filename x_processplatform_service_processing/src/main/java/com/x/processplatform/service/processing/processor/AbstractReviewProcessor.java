package com.x.processplatform.service.processing.processor;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.collaboration.core.message.Collaboration;
import com.x.collaboration.core.message.notification.ReviewMessage;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.ReviewType;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;

public abstract class AbstractReviewProcessor extends AbstractExpireProcessor {

	protected AbstractReviewProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	protected void createReview(String identity, Work work) throws Exception {
		String person = this.business().organization().person().getWithIdentity(identity).getName();
		if (StringUtils.isNoneEmpty(person)) {
			String id = this.business().review().getWithPersonWithJob(person, work.getJob());
			if (StringUtils.isEmpty(id)) {
				Review review = this.create(this.business(), work, identity);
				this.entityManagerContainer().beginTransaction(Review.class);
				this.entityManagerContainer().persist(review, CheckPersistType.all);
				this.sendReviewMessage(review);
			}
		}
	}

	private void sendReviewMessage(Review review) {
		try {
			ReviewMessage message = new ReviewMessage(review.getPerson(), review.getWork(), review.getId());
			Collaboration.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Review create(Business business, Work work, String identity) throws Exception {
		Review review = new Review();
		review.setJob(work.getJob());
		review.setWork(work.getId());
		review.setCompleted(false);
		review.setTitle(work.getTitle());
		review.setStartTime(work.getStartTime());
		review.setApplication(work.getApplication());
		review.setApplicationName(work.getApplicationName());
		review.setProcess(work.getProcess());
		review.setProcessName(work.getProcessName());
		review.setIdentity(identity);
		review.setPerson(business.organization().person().getWithIdentity(identity).getName());
		review.setDepartment(business.organization().department().getWithIdentity(identity).getName());
		review.setCompany(business.organization().company().getWithIdentity(identity).getName());
		review.setCreatorIdentity(work.getCreatorIdentity());
		review.setCreatorPerson(work.getCreatorPerson());
		review.setCreatorDepartment(work.getCreatorDepartment());
		review.setCreatorCompany(work.getCreatorCompany());
		review.setReviewType(ReviewType.review);
		review.setActivity(work.getActivity());
		review.setActivityName(work.getActivityName());
		review.setActivityToken(work.getActivityToken());
		review.setActivityType(work.getActivityType());
		return review;
	}
}