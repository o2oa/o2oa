package com.x.processplatform.service.processing.processor.split;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class SplitProcessor extends AbstractSplitProcessor {

	private static Logger logger = LoggerFactory.getLogger(SplitProcessor.class);

	public SplitProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Split split) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.splitArrive(aeiObjects.getWork().getActivityToken(), split));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Split split) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Split split) throws Exception {
		List<Work> results = new ArrayList<>();
		// 标志拆分状态
		aeiObjects.getWork().setSplitting(true);
		aeiObjects.getWork().setSplitToken(StringTools.uniqueToken());
		aeiObjects.getWork().getSplitTokenList().add(aeiObjects.getWork().getSplitToken());
		List<String> splitValues = this.splitWithPath(aeiObjects, split);
		if (splitValues.isEmpty()) {
			throw new ExceptionSplitEmptySplitValue(split.getName(), aeiObjects.getWork().getTitle(),
					aeiObjects.getWork().getId(), aeiObjects.getWork().getJob());
		}
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.splitExecute(aeiObjects.getWork().getActivityToken(), split, splitValues));
		// 先将当前文档标志拆分值
		aeiObjects.getWork().setSplitValue(splitValues.get(0));
		aeiObjects.getWork().getSplitValueList().add(splitValues.get(0));
		results.add(aeiObjects.getWork());
		Optional<WorkLog> optionalWorkLog = aeiObjects.getWorkLogs().stream()
				.filter(o -> StringUtils.equals(aeiObjects.getWork().getActivityToken(), o.getFromActivityToken()))
				.findFirst();
		if (optionalWorkLog.isPresent()) {
			WorkLog mainWorkLog = optionalWorkLog.get();
			mainWorkLog.setSplitting(true);
			mainWorkLog.setSplitToken(aeiObjects.getWork().getSplitToken());
			mainWorkLog.getProperties().getSplitTokenList().add(aeiObjects.getWork().getSplitToken());
			mainWorkLog.setSplitValue(splitValues.get(0));
			mainWorkLog.getProperties().getSplitValueList().add(aeiObjects.getWork().getSplitValue());
			aeiObjects.getUpdateWorkLogs().add(mainWorkLog);
			// 产生后续的拆分文档并标记拆分值
			for (int i = 1; i < splitValues.size(); i++) {
				// 将文档存放在一起
				Work splitWork = new Work(aeiObjects.getWork());
				// 替work换拆分值
				splitWork.setSplitValue(splitValues.get(i));
				ListTools.set(splitWork.getSplitValueList(), -1, splitValues.get(i));
				aeiObjects.getCreateWorks().add(splitWork);
				WorkLog splitWorkLog = new WorkLog(mainWorkLog);
				splitWorkLog.setSplitWork(aeiObjects.getWork().getId());
				splitWorkLog.setWork(splitWork.getId());
				// 替workLog换拆分值
				splitWorkLog.setSplitValue(splitValues.get(i));
				ListTools.set(splitWorkLog.getProperties().getSplitValueList(), -1, splitValues.get(i));
				aeiObjects.getCreateWorkLogs().add(splitWorkLog);
				results.add(splitWork);
			}
		}
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Split split) throws Exception {
		// nothing
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Split split) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.splitInquire(aeiObjects.getWork().getActivityToken(), split));
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	private List<String> splitWithPath(AeiObjects aeiObjects, Split split) throws Exception {
		List<String> list = new ArrayList<>();
		if ((StringUtils.isNotEmpty(split.getScript())) || (StringUtils.isNotEmpty(split.getScriptText()))) {
			Object objectValue = aeiObjects.business().element()
					.getCompiledScript(aeiObjects.getWork().getApplication(), split, Business.EVENT_SPLIT)
					.eval(aeiObjects.scriptContext());
			List<String> os = ScriptFactory.asDistinguishedNameList(objectValue);
			if (ListTools.isNotEmpty(os)) {
				list.addAll(os);
			}
		}
		return list;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Split split) throws Exception {
		// nothing
	}
}