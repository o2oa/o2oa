package com.x.processplatform.service.processing.processor.delay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.DelayType;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class DelayProcessor extends AbstractDelayProcessor {

	private static Logger logger = LoggerFactory.getLogger(DelayProcessor.class);

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
				limit = Config.workTime().forwardMinutes(aeiObjects.getWork().getStartTime(), minutes);
			} else {
				limit = DateUtils.addMinutes(aeiObjects.getWork().getStartTime(), minutes);
			}
		}
		logger.debug("work title:{}, id:{}, limit time:{}.", aeiObjects.getWork().getTitle(),
				aeiObjects.getWork().getId(), limit);
		if (null == limit) {
			logger.warn("work title:{}, id:{}, on delay activity id:{}, get null date value.",
					aeiObjects.getWork().getTitle(), aeiObjects.getWork().getId(), delay.getId());
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
		Object o = aeiObjects.business().element()
				.getCompiledScript(aeiObjects.getWork().getApplication(), delay, Business.EVENT_DELAY)
				.eval(aeiObjects.scriptContext());
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

	private Integer minute(AeiObjects aeiObjects, Delay delay) throws Exception {
		if (null != delay.getDelayMinute()) {
			return delay.getDelayMinute();
		} else if (StringUtils.isNotEmpty(delay.getDelayDataPath())) {
			return Integer.parseInt(Objects.toString(aeiObjects.getData().find(delay.getDelayDataPath()), ""));
		} else if (StringUtils.isNotEmpty(delay.getDelayScript())
				|| StringUtils.isNotEmpty(delay.getDelayScriptText())) {
			Object o = aeiObjects.business().element()
					.getCompiledScript(aeiObjects.getWork().getApplication(), delay, Business.EVENT_DELAY)
					.eval(aeiObjects.scriptContext());
			return Integer.parseInt(Objects.toString(o, ""));
		}
		return null;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Delay delay) throws Exception {
		// nothing
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Delay delay) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.delayInquire(aeiObjects.getWork().getActivityToken(), delay));
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Delay delay) throws Exception {
		// nothing
	}
}
