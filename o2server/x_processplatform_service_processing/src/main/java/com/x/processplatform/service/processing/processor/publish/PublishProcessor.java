package com.x.processplatform.service.processing.processor.publish;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.processor.AeiObjects;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据发布节点处理器
 * @author sword
 */
public class PublishProcessor extends AbstractPublishProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublishProcessor.class);

	public PublishProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Publish publish) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.publishArrive(aeiObjects.getWork().getActivityToken(), publish));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Publish publish) throws Exception {
		// Do nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Publish publish) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.publishExecute(aeiObjects.getWork().getActivityToken(), publish));
		List<Work> results = new ArrayList<>();
		boolean passThrough = false;

		if (passThrough) {
			results.add(aeiObjects.getWork());
		}
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Publish publish, List<Work> works) throws Exception {
		// Do nothing
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Publish publish) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.publishInquire(aeiObjects.getWork().getActivityToken(), publish));
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Publish publish) throws Exception {
		// Do nothing
	}

	private boolean hasCmsAssignDataScript(Publish publish) {
		return StringUtils.isNotEmpty(publish.getTargetAssignDataScript())
				|| StringUtils.isNotEmpty(publish.getTargetAssignDataScriptText());
	}
}
