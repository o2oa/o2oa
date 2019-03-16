package com.x.processplatform.service.processing.processor.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class ChoiceProcessor extends AbstractChoiceProcessor {

	private static Logger logger = LoggerFactory.getLogger(ChoiceProcessor.class);

	public ChoiceProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Choice choice) throws Exception {
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Choice choice) throws Exception {

	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Choice choice) throws Exception {
		List<Work> results = new ArrayList<>();
		results.add(aeiObjects.getWork());
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Choice choice) throws Exception {

	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Choice choice) throws Exception {
		List<Route> results = new ArrayList<>();
		/* 多条路由进行判断 */
		for (Route o : aeiObjects.getRoutes()) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects,
					new BindingPair(ScriptingEngine.BINDINGNAME_ROUTE, o));
			Object obj = scriptHelper.eval(aeiObjects.getWork().getApplication(), o.getScript(), o.getScriptText());
			if (BooleanUtils.toBoolean(StringUtils.trimToNull(Objects.toString(obj))) == true) {
				results.add(o);
				break;
			}
		}
		return results;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Choice choice) throws Exception {

	}
}