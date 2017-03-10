package com.x.processplatform.service.processing.processor.begin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.ReviewType;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AbstractProcessor;

public class BeginProcessor extends AbstractProcessor {

	private static Logger logger = LoggerFactory.getLogger(BeginProcessor.class);

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
		/* 如果是再次进入begin节点那么就不需要设置开始时间 */
		if (work.getStartTime() == null) {
			work.setStartTime(new Date());
		}
		this.createReviewForCreator(this.business(), work);
		results.add(work);
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		Route o = routes.get(0);
		logger.debug("work title:{}, id:{}, inquire to route:{}.", work.getTitle(), work.getId(), o);
		results.add(o);
		return results;
	}

	/* 为流程启动者创建Review */
	private void createReviewForCreator(Business business, Work work) throws Exception {
		String id = business.review().getWithPersonWithJob(work.getCreatorPerson(), work.getJob());
		Review review;
		/* 如果已经创建过work的creator review 不需要重新创建 */
		if (StringUtils.isNotEmpty(id)) {
			review = business.entityManagerContainer().find(id, Review.class, ExceptionWhen.not_found);
			if (Objects.equals(ReviewType.create, review.getReviewType())) {
				return;
			}
		}
		review = new Review();
		review.setJob(work.getJob());
		review.setWork(work.getId());
		review.setSerial(work.getSerial());
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
		this.entityManagerContainer().beginTransaction(Review.class);
		logger.debug("createReviewForCreator:{}.", review);
		business.entityManagerContainer().persist(review, CheckPersistType.all);
	}

}