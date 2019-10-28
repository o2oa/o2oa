package com.x.processplatform.service.processing.processor.manual;

import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.processor.AbstractProcessor;
import com.x.processplatform.service.processing.processor.AeiObjects;

/***
 * Manual活动基础功能
 */
public abstract class AbstractManualProcessor extends AbstractProcessor {

	private static Logger logger = LoggerFactory.getLogger(AbstractManualProcessor.class);

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
		Manual manual = (Manual) aeiObjects.getActivity();
		List<Work> os = executing(aeiObjects, manual);
		if (ListTools.isEmpty(os)) {
			/** Manual Work 还没有处理完 发生了停留,出发了停留事件 */
			if (this.hasManualStayScript(manual)) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects);
				scriptHelper.eval(aeiObjects.getWork().getApplication(),
						Objects.toString(PropertyUtils.getProperty(manual, Manual.manualStayScript_FIELDNAME)),
						Objects.toString(PropertyUtils.getProperty(manual, Manual.manualStayScriptText_FIELDNAME)));
			}
		}
		return os;
	}

	@Override
	protected List<Route> inquireProcessing(AeiObjects aeiObjects) throws Exception {
		Manual manual = (Manual) aeiObjects.getActivity();
		return inquiring(aeiObjects, manual);
	}

	protected abstract Work arriving(AeiObjects aeiObjects, Manual manual) throws Exception;

	protected abstract List<Work> executing(AeiObjects aeiObjects, Manual manual) throws Exception;

	protected abstract List<Route> inquiring(AeiObjects aeiObjects, Manual manual) throws Exception;

	@Override
	protected void arriveCommitted(AeiObjects aeiObjects) throws Exception {
		Manual manual = (Manual) aeiObjects.getActivity();
		this.arrivingCommitted(aeiObjects, manual);
	}

	@Override
	protected void executeCommitted(AeiObjects aeiObjects) throws Exception {
		Manual manual = (Manual) aeiObjects.getActivity();
		this.executingCommitted(aeiObjects, manual);
	}

	@Override
	protected void inquireCommitted(AeiObjects aeiObjects) throws Exception {
		Manual manual = (Manual) aeiObjects.getActivity();
		this.inquiringCommitted(aeiObjects, manual);
	}

	protected abstract void arrivingCommitted(AeiObjects aeiObjects, Manual manual) throws Exception;

	protected abstract void executingCommitted(AeiObjects aeiObjects, Manual manual) throws Exception;

	protected abstract void inquiringCommitted(AeiObjects aeiObjects, Manual manual) throws Exception;

	private boolean hasManualStayScript(Activity activity) throws Exception {
		return StringUtils.isNotEmpty(activity.get(Manual.manualStayScript_FIELDNAME, String.class))
				|| StringUtils.isNotEmpty(activity.get(Manual.manualStayScriptText_FIELDNAME, String.class));
	}

	protected void mergeTaskCompleted(AeiObjects aeiObjects, Work work, Work oldest) {
		try {
			aeiObjects.getTaskCompleteds().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId()))
					.forEach(o -> {
						o.setWork(oldest.getId());
						// o.setActivityToken(oldest.getActivityToken());
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

	protected void mergeHint(AeiObjects aeiObjects, Work work, Work target) {
		try {
			aeiObjects.getHints().stream().filter(o -> StringUtils.equals(o.getWork(), work.getId())).forEach(o -> {
				o.setWork(target.getId());
				aeiObjects.getUpdateHints().add(o);
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
						// o.setArrivedActivityToken(target.getActivityToken());
						aeiObjects.getUpdateWorkLogs().add(o);
					});
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
