package com.x.processplatform.service.processing.processor.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class ParallelProcessor extends AbstractParallelProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParallelProcessor.class);

	public ParallelProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Parallel parallel) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.parallelArrive(aeiObjects.getWork().getActivityToken(), parallel));
		LOGGER.info(
				"parallel arrvie processing, work title:{}, id:{}, actvity name:{}, id:{}, activityToken:{}, process name:{}, id{}.",
				() -> aeiObjects.getWork().getTitle(), () -> aeiObjects.getWork().getId(), parallel::getName,
				parallel::getId, () -> aeiObjects.getWork().getActivityToken(),
				() -> aeiObjects.getWork().getProcessName(), () -> aeiObjects.getWork().getProcess());
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Parallel parallel) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Parallel parallel) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.parallelExecute(aeiObjects.getWork().getActivityToken(), parallel));
		List<Work> results = new ArrayList<>();
		aeiObjects.getWork().setSplitting(true);
		aeiObjects.getWork().setSplitToken(StringTools.uniqueToken());
		aeiObjects.getWork().getSplitTokenList().add(aeiObjects.getWork().getSplitToken());
		/*
		 * 并行拆分不影响 <code>splitValue aeiObjects.getWork().setSplitValue("");</code>
		 * 新创建并行Work需要单独的workLog,拷贝当前的WorkLog
		 */
		WorkLog mainWorkLog = aeiObjects.getWorkLogs().stream()
				.filter(o -> StringUtils.equals(aeiObjects.getWork().getId(), o.getWork())
						&& StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getFromActivityToken()))
				.findFirst().orElse(null);
		if (null != mainWorkLog) {
			mainWorkLog.setSplitting(aeiObjects.getWork().getSplitting());
			mainWorkLog.getProperties().setSplitTokenList(aeiObjects.getWork().getSplitTokenList());
			mainWorkLog.setSplitToken(aeiObjects.getWork().getSplitToken());
			mainWorkLog.setSplitValue(aeiObjects.getWork().getSplitValue());
			aeiObjects.getUpdateWorkLogs().add(mainWorkLog);
		}

		List<Route> routes = new ArrayList<>();
		/* 多条路由进行判断 */
		for (Route o : aeiObjects.getRoutes()) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(), o,
					Business.EVENT_ROUTE);
			Optional<Boolean> opt = GraalvmScriptingFactory.evalAsBoolean(source, aeiObjects.bindings());
			if (opt.isPresent() && BooleanUtils.isTrue(opt.get())) {
				routes.add(o);
			}
		}

		for (int i = 0; i < routes.size(); i++) {
			Route route = routes.get(i);
			if (i == 0) {
				aeiObjects.getWork().setDestinationRoute(route.getId());
				aeiObjects.getWork().setDestinationRouteName(route.getName());
				results.add(aeiObjects.getWork());
			} else {
				Work work = new Work(aeiObjects.getWork());
				work.setDestinationRoute(route.getId());
				work.setDestinationRouteName(route.getName());
				// 创建新的Token
				WorkLog workLog = new WorkLog(mainWorkLog);
				workLog.setWork(work.getId());
				aeiObjects.getCreateWorks().add(work);
				aeiObjects.getCreateWorkLogs().add(workLog);
				results.add(work);
			}
		}
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Parallel parallel, List<Work> works) throws Exception {
		// nothing
	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Parallel parallel) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.parallelInquire(aeiObjects.getWork().getActivityToken(), parallel));
		Optional<Route> opt = aeiObjects.getRoutes().stream()
				.filter(o -> StringUtils.equals(o.getId(), aeiObjects.getWork().getDestinationRoute())).findFirst();
		if (opt.isPresent()) {
			return opt;
		} else {
			return aeiObjects.getRoutes().stream()
					.filter(o -> StringUtils.equals(o.getName(), aeiObjects.getWork().getDestinationRouteName()))
					.findFirst();
		}
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Parallel parallel) throws Exception {
		// nothing
	}
}
