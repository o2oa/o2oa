package com.x.processplatform.service.processing.processor.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class ChoiceProcessor extends AbstractChoiceProcessor {

	public ChoiceProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Choice choice) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.choiceArrive(aeiObjects.getWork().getActivityToken(), choice));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Choice choice) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Choice choice) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.choiceExecute(aeiObjects.getWork().getActivityToken(), choice));
		List<Work> results = new ArrayList<>();
		results.add(aeiObjects.getWork());
		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Choice choice, List<Work> works) throws Exception {
		// nothing
	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Choice choice) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.choiceInquire(aeiObjects.getWork().getActivityToken(), choice));
		// 多条路由进行判断
		for (Route o : aeiObjects.getRoutes()) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(), o,
					Business.EVENT_ROUTE);

			Optional<Boolean> opt = GraalvmScriptingFactory.evalAsBoolean(source, aeiObjects.bindings());

			if (opt.isPresent() && BooleanUtils.isTrue(opt.get())) {
				return Optional.of(o);
			}
		}
		return Optional.empty();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Choice choice) throws Exception {
		// nothing
	}
}