package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.ReviewType;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public class BeginProcessor extends AbstractProcessor {

	public BeginProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		return work;
	}

	@Override
	protected List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception {
		List<Work> results = new ArrayList<>();
		work.setStartTime(new Date());
		Review review = this.createReviewWithCreator(work);
		this.entityManagerContainer().beginTransaction(Review.class);
		this.entityManagerContainer().persist(review, CheckPersistType.all);
		results.add(work);
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		results.add(routes.get(0));
		return results;
	}

	/* 为流程启动者创建Review */
	private Review createReviewWithCreator(Work work) {
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
		review.setCompany(work.getCreatorCompany());
		review.setDepartment(work.getCreatorDepartment());
		review.setIdentity(work.getCreatorIdentity());
		review.setPerson(work.getCreatorPerson());
		review.setReviewType(ReviewType.create);
		review.setActivity(work.getActivity());
		review.setActivityName(work.getActivityName());
		review.setActivityToken(work.getActivityToken());
		review.setActivityType(work.getActivityType());
		return review;
	}

}