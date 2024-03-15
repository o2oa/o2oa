package com.x.processplatform.service.processing.processor.end;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

abstract class AbstractEndProcessor extends AbstractProcessor {

	private static Logger logger = LoggerFactory.getLogger(AbstractEndProcessor.class);

	protected AbstractEndProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		return arriving(aeiObjects, end);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		return executing(aeiObjects, end);
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		return inquiring(aeiObjects, end).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, End end) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, End end) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, End end) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, end);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		End end = (End) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, end, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		End end = (End) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, end);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, End end) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, End end, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, End end) throws Exception;

	protected void mergeTaskCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getTaskCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateTaskCompleteds().add(o);
					});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected void mergeRead(AeiObjects aeiObjects, Work work, Work target) {
		try {
			aeiObjects.getReads().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(target.getId());
				aeiObjects.getUpdateReads().add(o);
			});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected void mergeReadCompleted(AeiObjects aeiObjects, Work work, Work target) {
		try {
			aeiObjects.getReadCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(target.getId());
						aeiObjects.getUpdateReadCompleteds().add(o);
					});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected void mergeReview(AeiObjects aeiObjects, Work work, Work target) {
		try {
			aeiObjects.getReviews().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(target.getId());
				aeiObjects.getUpdateReviews().add(o);
			});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected void mergeAttachment(AeiObjects aeiObjects, Work work, Work target) {
		try {
			aeiObjects.getAttachments().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(target.getId());
						aeiObjects.getUpdateAttachments().add(o);
					});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected void mergeWorkLog(AeiObjects aeiObjects, Work work, Work target) {
		try {
			aeiObjects.getWorkLogs().stream()
					.filter(o -> StringUtils.equals(work.getActivityToken(), o.getArrivedActivityToken())
							&& StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(target.getId());
						aeiObjects.getUpdateWorkLogs().add(o);
					});
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected void mergeRecord(AeiObjects aeiObjects, Work work, Work target) {
		try {
			aeiObjects.getRecords().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(target.getId());
				aeiObjects.getUpdateRecords().add(o);
			});
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
