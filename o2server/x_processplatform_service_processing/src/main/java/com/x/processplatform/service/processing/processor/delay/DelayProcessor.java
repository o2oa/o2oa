package com.x.processplatform.service.processing.processor.delay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.DelayType;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class DelayProcessor extends AbstractDelayProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(DelayProcessor.class);

	public DelayProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Delay delay) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.delayArrive(aeiObjects.getWork().getActivityToken(), delay));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Delay delay) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Delay delay) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.delayExecute(aeiObjects.getWork().getActivityToken(), delay));
		List<Work> results = new ArrayList<>();
		Date limit = null;
		if (null != delay.getDelayType() && Objects.equals(DelayType.until, delay.getDelayType())) {
			limit = this.until(aeiObjects, delay);
		} else {
			Integer minutes = this.minute(aeiObjects, delay);
			if (BooleanUtils.isTrue(delay.getWorkMinute())) {
				limit = Config.workTime().forwardMinutes(aeiObjects.getWork().getActivityArrivedTime(), minutes);
			} else {
				limit = DateUtils.addMinutes(aeiObjects.getWork().getActivityArrivedTime(), minutes);
			}
		}
		if (null == limit) {
			LOGGER.warn("work title:{}, id:{}, on delay activity id:{}, get null date value.",
					() -> aeiObjects.getWork().getTitle(), () -> aeiObjects.getWork().getId(), delay::getId);
		} else {
			LOGGER.debug("work title:{}, id:{}, limit time:{}.", () -> aeiObjects.getWork().getTitle(),
					() -> aeiObjects.getWork().getId(), limit::toString);
		}
		if (null != limit && (new Date()).after(limit)) {
			results.add(aeiObjects.getWork());
		}
		return results;
	}

	private Date until(AeiObjects aeiObjects, Delay delay) throws Exception {
		if (null != delay.getDelayTime()) {
			return delay.getDelayTime();
		} else if (StringUtils.isNotEmpty(delay.getDelayDataPath())) {
			return untilDelayDataPath(aeiObjects, delay);
		} else if (StringUtils.isNotEmpty(delay.getDelayScript())
				|| StringUtils.isNotEmpty(delay.getDelayScriptText())) {
			return untilDelayScript(aeiObjects, delay);
		}
		return null;
	}

	private Date untilDelayDataPath(AeiObjects aeiObjects, Delay delay) throws Exception {
		Object o = aeiObjects.getData().find(delay.getDelayDataPath());
		if (null != o) {
			if (o instanceof Date) {
				return (Date) o;
			} else {
				if (o instanceof String) {
					return DateTools.parse(o.toString());
				}
			}
		}
		return null;
	}

	private Date untilDelayScript(AeiObjects aeiObjects, Delay delay) throws Exception {
		Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(), delay,
				Business.EVENT_DELAY);
		Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, aeiObjects.bindings());
		if (opt.isPresent()) {
			return DateTools.parse(opt.get());
		} else {
			return null;
		}
	}

	private Integer minute(AeiObjects aeiObjects, Delay delay) throws Exception {
		if (null != delay.getDelayMinute()) {
			return delay.getDelayMinute();
		} else if (StringUtils.isNotEmpty(delay.getDelayDataPath())) {
			return NumberUtils.createFloat(Objects.toString(aeiObjects.getData().find(delay.getDelayDataPath()), "0"))
					.intValue();
		} else if (StringUtils.isNotEmpty(delay.getDelayScript())
				|| StringUtils.isNotEmpty(delay.getDelayScriptText())) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					delay, Business.EVENT_DELAY);
			Optional<Integer> opt = GraalvmScriptingFactory.evalAsInteger(source, aeiObjects.bindings());
			if (opt.isPresent()) {
				return opt.get();
			}
		}
		return null;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Delay delay, List<Work> works) throws Exception {
		// nothing
	}

	@Override
	protected Optional<Route> inquiring(AeiObjects aeiObjects, Delay delay) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.delayInquire(aeiObjects.getWork().getActivityToken(), delay));
		return aeiObjects.getRoutes().stream().findFirst();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Delay delay) throws Exception {
		// nothing
	}
}
