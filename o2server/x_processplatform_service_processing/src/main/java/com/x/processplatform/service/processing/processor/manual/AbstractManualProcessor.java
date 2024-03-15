package com.x.processplatform.service.processing.processor.manual;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

/***
 * Manual活动基础功能
 */
abstract class AbstractManualProcessor extends AbstractProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractManualProcessor.class);

	protected AbstractManualProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(AeiObjects aeiObjects) throws Exception {
		Manual manual = (Manual) aeiObjects.getActivity();
		/** 更新data中的work和attachment */
		aeiObjects.getData().setWork(aeiObjects.getWork());
		aeiObjects.getData().setAttachmentList(aeiObjects.getAttachments());
		return arriving(aeiObjects, manual);
	}

	@Override
	protected List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception {
		return executing(aeiObjects, (Manual) aeiObjects.getActivity());
	}

	@Override
	protected Route inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Manual manual = (Manual) aeiObjects.getActivity();
		return inquiring(aeiObjects, manual).orElse(null);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Manual manual) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Manual manual) throws Exception;

	protected abstract Optional<Route> inquiring(AeiObjects aeiObjects, Manual manual) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Manual manual = (Manual) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, manual);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception {
		Manual manual = (Manual) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, manual, works);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Manual manual = (Manual) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, manual);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Manual manual) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Manual manual, List<Work> works) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Manual manual) throws Exception;

	protected boolean hasManualStayScript(Activity activity)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return StringUtils.isNotEmpty(activity.get(Manual.manualStayScript_FIELDNAME, String.class))
				|| StringUtils.isNotEmpty(activity.get(Manual.manualStayScriptText_FIELDNAME, String.class));
	}

	protected boolean hasManualStayScript(Process process) {
		return StringUtils.isNotEmpty(process.getManualStayScript())
				|| StringUtils.isNotEmpty(process.getManualStayScriptText());
	}

	protected void mergeTaskCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getTaskCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						aeiObjects.getUpdateTaskCompleteds().add(o);
					});
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	protected void mergeRead(AeiObjects aeiObjects, Work work, Work target) {
		try {
			aeiObjects.getReads().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(target.getId());
				aeiObjects.getUpdateReads().add(o);
			});
		} catch (Exception e) {
			LOGGER.error(e);
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
			LOGGER.error(e);
		}
	}

	protected void mergeReview(AeiObjects aeiObjects, Work work, Work target) {
		try {
			aeiObjects.getReviews().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(target.getId());
				aeiObjects.getUpdateReviews().add(o);
			});
		} catch (Exception e) {
			LOGGER.error(e);
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
			LOGGER.error(e);
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
			LOGGER.error(e);
		}
	}
}
