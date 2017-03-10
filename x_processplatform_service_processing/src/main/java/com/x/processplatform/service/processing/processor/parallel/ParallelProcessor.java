package com.x.processplatform.service.processing.processor.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.StringTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AbstractProcessor;

public class ParallelProcessor extends AbstractProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(ParallelProcessor.class);

	public ParallelProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		return work;
	}

	@Override
	public List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		List<Work> results = new ArrayList<>();
		work.setSplitting(true);
		String token = StringTools.uniqueToken();
		work.getSplitTokenList().add(token);
		work.setSplitToken(token);
		results.add(work);
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		/* 多条路由进行判断 */
		for (Route o : routes) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data, activity,
					new BindingPair(Binding_name_route, o));
			Object obj = scriptHelper.eval(work.getApplication(), o.getScript(), o.getScriptText());
			if (BooleanUtils.toBoolean(StringUtils.trimToNull(Objects.toString(obj))) == true) {
				results.add(o);
			}
		}
		return results;
	}
}
