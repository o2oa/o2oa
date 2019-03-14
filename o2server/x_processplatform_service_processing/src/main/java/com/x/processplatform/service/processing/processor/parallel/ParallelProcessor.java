package com.x.processplatform.service.processing.processor.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
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
		aeiObjects.getWork().setSplitValue("");
		/* 新创建并行Work需要单独的workLog,拷贝当前的WorkLog */
		WorkLog mainWorkLog = aeiObjects.getWorkLogs().stream()
				.filter(o -> StringUtils.equals(aeiObjects.getWork().getId(), o.getWork())
						&& StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getFromActivityToken()))
				.findFirst().orElse(null);
		mainWorkLog.setSplitting(aeiObjects.getWork().getSplitting());
		mainWorkLog.setSplitTokenList(aeiObjects.getWork().getSplitTokenList());
		mainWorkLog.setSplitToken(aeiObjects.getWork().getSplitToken());
		mainWorkLog.setSplitValue(aeiObjects.getWork().getSplitValue());
		aeiObjects.getUpdateWorkLogs().add(mainWorkLog);

		List<Route> routes = new ArrayList<>();
		/* 多条路由进行判断 */
		for (Route o : aeiObjects.getRoutes()) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
					new BindingPair(ScriptingEngine.BINDINGNAME_ROUTE, o));
			Object obj = scriptHelper.eval(aeiObjects.getWork().getApplication(), o.getScript(), o.getScriptText());
			if (BooleanUtils.toBoolean(StringUtils.trimToNull(Objects.toString(obj))) == true) {
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
				// aeiObjects.getWork().copyTo(work, JpaObject.id_FIELDNAME);
				work.setDestinationRoute(route.getId());
				work.setDestinationRouteName(route.getName());
				// String activityToken = StringTools.uniqueToken();
				// work.setActivityToken(activityToken);
				/* 创建新的Token */
				WorkLog workLog = new WorkLog(mainWorkLog);
				workLog.setWork(work.getId());
				// workLog.setFromActivityToken(activityToken);
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
