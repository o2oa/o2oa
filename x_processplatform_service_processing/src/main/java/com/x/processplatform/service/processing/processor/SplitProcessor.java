package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.utils.StringTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public class SplitProcessor extends AbstractProcessor {

	public SplitProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
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
		Split split = (Split) activity;
		/* 标志拆分状态 */
		work.setSplitting(true);
		work.setSplitToken(StringTools.uniqueToken());
		work.getSplitTokenList().add(work.getSplitToken());
		work.setSplitValue("");
		List<String> splitValues = this.splitWithPath(attributes, split, work, data);
		if (splitValues.isEmpty()) {
			throw new Exception("split value is empty, work{id:" + work.getId() + "}.");
		}
		/* 先将当前文档标志拆分值 */
		work.setSplitValue(splitValues.get(0));
		results.add(work);
		/* 产生后续的拆分文档并标记拆分值 */
		for (int i = 1; i < splitValues.size(); i++) {
			results.add(this.split(work, splitValues.get(i)));
		}
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		results.add(routes.get(0));
		return results;
	}

	private List<String> splitWithPath(ProcessingAttributes  attributes, Split split, Work work, Data data)
			throws Exception {
		ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data, split);
		List<String> values = scriptHelper.evalAsStringList(work.getApplication(), split.getScript(),
				split.getScriptText());
		return values;
	}

	private Work split(Work work, String value) throws Exception {
		Work o = new Work();
		work.copyTo(o, JpaObject.ID_DISTRIBUTEFACTOR);
		o.setSplitValue(value);
		this.entityManagerContainer().persist(o, CheckPersistType.all);
		return o;
	}
}