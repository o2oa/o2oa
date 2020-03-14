package com.x.processplatform.service.processing.processor.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.script.ScriptContext;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class ParallelProcessor extends AbstractParallelProcessor {

	private static Logger logger = LoggerFactory.getLogger(ParallelProcessor.class);

	public ParallelProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Parallel parallel) throws Exception {
		logger.info(
				"parallel arrvie processing, work title:{}, id:{}, actvity name:{}, id:{}, activityToken:{}, process name:{}, id{}.",
				aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(), parallel.getName(), parallel.getId(),
				aeiObjects.getWork().getActivityToken(), aeiObjects.getWork().getProcessName(),
				aeiObjects.getWork().getProcess());
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Parallel parallel) throws Exception {

	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Parallel parallel) throws Exception {
		List<Work> results = new ArrayList<>();
		aeiObjects.getWork().setSplitting(true);
		aeiObjects.getWork().setSplitToken(StringTools.uniqueToken());
		aeiObjects.getWork().getSplitTokenList().add(aeiObjects.getWork().getSplitToken());
		/* 并行拆分不影响splitValue */
		// aeiObjects.getWork().setSplitValue("");
		/* 新创建并行Work需要单独的workLog,拷贝当前的WorkLog */
		WorkLog mainWorkLog = aeiObjects.getWorkLogs().stream()
				.filter(o -> StringUtils.equals(aeiObjects.getWork().getId(), o.getWork())
						&& StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getFromActivityToken()))
				.findFirst().orElse(null);
		mainWorkLog.setSplitting(aeiObjects.getWork().getSplitting());
		mainWorkLog.getProperties().setSplitTokenList(aeiObjects.getWork().getSplitTokenList());
		mainWorkLog.setSplitToken(aeiObjects.getWork().getSplitToken());
		mainWorkLog.setSplitValue(aeiObjects.getWork().getSplitValue());
		aeiObjects.getUpdateWorkLogs().add(mainWorkLog);

		List<Route> routes = new ArrayList<>();
		/* 多条路由进行判断 */
		for (Route o : aeiObjects.getRoutes()) {
			ScriptContext scriptContext = aeiObjects.scriptContext();
			scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put(ScriptFactory.BINDING_NAME_ROUTE, o);
			Object objectValue = aeiObjects.business().element()
					.getCompiledScript(aeiObjects.getWork().getApplication(), o, Business.EVENT_ROUTE)
					.eval(scriptContext);
			if (BooleanUtils.toBoolean(StringUtils.trimToNull(Objects.toString(objectValue))) == true) {
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
				/* 创建新的Token */
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
	protected void executingCommitted(AeiObjects aeiObjects, Parallel parallel) throws Exception {
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Parallel parallel) throws Exception {
		List<Route> results = new ArrayList<>();
		aeiObjects.getRoutes().stream().forEach(o -> {
			if (StringUtils.equals(o.getId(), aeiObjects.getWork().getDestinationRoute())) {
				results.add(o);
			}
		});
		if (results.isEmpty()) {
			aeiObjects.getRoutes().stream().forEach(o -> {
				if (StringUtils.equals(o.getName(), aeiObjects.getWork().getDestinationRouteName())) {
					results.add(o);
				}
			});
		}
		return results;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Parallel parallel) throws Exception {
	}
}
