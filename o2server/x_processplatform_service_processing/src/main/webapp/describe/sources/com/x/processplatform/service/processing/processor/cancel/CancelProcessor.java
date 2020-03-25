package com.x.processplatform.service.processing.processor.cancel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class CancelProcessor extends AbstractCancelProcessor {

	private static Logger logger = LoggerFactory.getLogger(CancelProcessor.class);

	public CancelProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Cancel cancel) throws Exception {
	}

	@Override
	public List<Work> executing(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		/* 唯一work处理 */
		if (aeiObjects.getWorks().size() > 1) {
			aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
			aeiObjects.getTasks().stream().filter(o -> StringUtils.equals(o.getWork(), aeiObjects.getWork().getId()))
					.forEach(o -> aeiObjects.getDeleteTasks().add(o));
		} else {
			aeiObjects.getTasks().stream().forEach(o -> aeiObjects.getDeleteTasks().add(o));
			aeiObjects.getTaskCompleteds().stream().forEach(o -> aeiObjects.getDeleteTaskCompleteds().add(o));
			aeiObjects.getReads().stream().forEach(o -> aeiObjects.getDeleteReads().add(o));
			aeiObjects.getReadCompleteds().stream().forEach(o -> aeiObjects.getDeleteReadCompleteds().add(o));
			aeiObjects.getReviews().stream().forEach(o -> aeiObjects.getDeleteReviews().add(o));
			aeiObjects.getWorkLogs().stream().forEach(o -> aeiObjects.getDeleteWorkLogs().add(o));
			aeiObjects.getDocumentVersions().stream().forEach(o -> aeiObjects.getDeleteDocumentVersions().add(o));
			aeiObjects.getRecords().stream().forEach(o -> aeiObjects.getDeleteRecords().add(o));
			aeiObjects.getWorkLogs().stream().forEach(o -> aeiObjects.getDeleteWorkLogs().add(o));
			/* 附件删除单独处理,需要删除Attachment的二进制文件 */
			aeiObjects.getAttachments().stream().forEach(o -> aeiObjects.getDeleteAttachments().add(o));
			/* 如果只有一份数据，没有拆分，那么删除Data */
			aeiObjects.getWorkDataHelper().remove();
			aeiObjects.getWorks().stream().forEach(o -> aeiObjects.getDeleteWorks().add(o));
		}
		return new ArrayList<>();
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		/** 删除后再次检查，如果存在多个副本，且都已经在End状态，那么试图推动一个 */
		if (aeiObjects.getWorks().size() > 0) {
			Predicate<Work> p = o -> {
				return Objects.equals(ActivityType.end, o.getActivityType());
			};
			if (aeiObjects.getWorks().stream().allMatch(p)) {
				Processing processing = new Processing(new ProcessingAttributes());
				processing.processing(aeiObjects.getWorks().get(0).getId());
			}
		}
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Cancel cancel) throws Exception {
		return new ArrayList<Route>();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Cancel cancel) throws Exception {
	}

}