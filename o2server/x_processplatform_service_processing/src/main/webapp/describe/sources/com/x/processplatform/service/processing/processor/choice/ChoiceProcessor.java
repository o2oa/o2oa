package com.x.processplatform.service.processing.processor.choice;

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
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.Business;
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
			ScriptContext scriptContext = aeiObjects.scriptContext();
			scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put(ScriptFactory.BINDING_NAME_ROUTE, o);
			Object obj = aeiObjects.business().element()
					.getCompiledScript(aeiObjects.getWork().getApplication(), o, Business.EVENT_ROUTE)
					.eval(scriptContext);
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