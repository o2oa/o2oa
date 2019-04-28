package com.x.processplatform.service.processing.processor.begin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.NumberTools;
import com.x.base.core.project.utils.time.WorkTime;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;
import com.x.processplatform.service.processing.processor.AeiObjects;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class BeginProcessor extends AbstractBeginProcessor {

	private static Logger logger = LoggerFactory.getLogger(BeginProcessor.class);

	public BeginProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Begin begin) throws Exception {
		/* 创建创建者的review */
		String person = this.business().organization().person().get(aeiObjects.getWork().getCreatorPerson());
		if (StringUtils.isNotEmpty(person)) {
			aeiObjects.createReview(new Review(aeiObjects.getWork(), person));
		}
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Begin begin) throws Exception {

	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Begin begin) throws Exception {
		List<Work> list = new ArrayList<>();
		/** 如果是再次进入begin节点那么就不需要设置开始时间 */
		if (aeiObjects.getWork().getStartTime() == null) {
			aeiObjects.getWork().setStartTime(new Date());
			/** 计算过期时间 */
			this.calculateExpire(aeiObjects);
		}
		list.add(aeiObjects.getWork());
		return list;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Begin begin) throws Exception {
		if (StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterBeginScript())
				|| StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterBeginScriptText())) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(aeiObjects);
			scriptHelper.eval(aeiObjects.getWork().getApplication(),
					Objects.toString(aeiObjects.getProcess().getAfterBeginScript()),
					Objects.toString(aeiObjects.getProcess().getAfterBeginScriptText()));
		}
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Begin begin) throws Exception {
		List<Route> list = new ArrayList<>();
		Route o = aeiObjects.getRoutes().get(0);
		list.add(o);
		return list;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Begin begin) throws Exception {
	}

	private void calculateExpire(AeiObjects aeiObjects) throws Exception {
		if (aeiObjects.getActivityProcessingConfigurator().getCalculateExpire()) {
			if (null != aeiObjects.getProcess().getExpireType()) {
				switch (aeiObjects.getProcess().getExpireType()) {
				case never:
					this.expireNever(aeiObjects);
					break;
				case appoint:
					this.expireAppoint(aeiObjects);
					break;
				case script:
					this.expireScript(aeiObjects);
					break;
				default:
					break;
				}
			}
		}
	}

	private void expireNever(AeiObjects aeiObjects) {
		aeiObjects.getWork().setExpireTime(null);
	}

	private void expireAppoint(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getProcess().getExpireWorkTime())) {
			this.expireAppointWorkTime(aeiObjects);
		} else {
			this.expireAppointNaturalDay(aeiObjects);
		}
	}

	private void expireAppointWorkTime(AeiObjects aeiObjects) throws Exception {
		Integer m = 0;
		WorkTime wt = new WorkTime();
		if (NumberTools.greaterThan(aeiObjects.getProcess().getExpireDay(), 0)) {
			m += aeiObjects.getProcess().getExpireDay() * wt.minutesOfWorkDay();
		}
		if (NumberTools.greaterThan(aeiObjects.getProcess().getExpireHour(), 0)) {
			m += aeiObjects.getProcess().getExpireHour() * 60;
		}
		if (m > 0) {
			aeiObjects.getWork().setExpireTime(wt.forwardMinutes(aeiObjects.getWork().getCreateTime(), m));
		} else {
			aeiObjects.getWork().setExpireTime(null);
		}
	}

	private void expireAppointNaturalDay(AeiObjects aeiObjects) throws Exception {
		Integer m = 0;
		if (NumberTools.greaterThan(aeiObjects.getProcess().getExpireDay(), 0)) {
			m += aeiObjects.getProcess().getExpireDay() * 60 * 24;
		}
		if (NumberTools.greaterThan(aeiObjects.getProcess().getExpireHour(), 0)) {
			m += aeiObjects.getProcess().getExpireHour() * 60;
		}
		if (m > 0) {
			Calendar cl = Calendar.getInstance();
			cl.setTime(aeiObjects.getWork().getCreateTime());
			cl.add(Calendar.MINUTE, m);
			aeiObjects.getWork().setExpireTime(cl.getTime());
		} else {
			aeiObjects.getWork().setExpireTime(null);
		}
	}

	private void expireScript(AeiObjects aeiObjects) throws Exception {
		ScriptHelper sh = ScriptHelperFactory.create(aeiObjects);
		String str = Objects.toString(sh.eval(aeiObjects.getWork().getApplication(),
				aeiObjects.getProcess().getExpireScript(), aeiObjects.getProcess().getExpireScriptText()), "");
		if (StringUtils.isNotEmpty(str)) {
			ExpireScriptResult result = XGsonBuilder.instance().fromJson(str, ExpireScriptResult.class);
			if (NumberTools.greaterThan(result.getWorkHour(), 0)) {
				Integer m = 0;
				m += result.getWorkHour() * 60;
				if (m > 0) {
					WorkTime wt = new WorkTime();
					aeiObjects.getWork().setExpireTime(wt.forwardMinutes(aeiObjects.getWork().getCreateTime(), m));
				} else {
					aeiObjects.getWork().setExpireTime(null);
				}
			} else if (NumberTools.greaterThan(result.getHour(), 0)) {
				Integer m = 0;
				m += result.getHour() * 60;
				if (m > 0) {
					Calendar cl = Calendar.getInstance();
					cl.setTime(aeiObjects.getWork().getCreateTime());
					cl.add(Calendar.MINUTE, m);
					aeiObjects.getWork().setExpireTime(cl.getTime());
				} else {
					aeiObjects.getWork().setExpireTime(null);
				}
			} else if (null != result.getDate()) {
				aeiObjects.getWork().setExpireTime(result.getDate());
			} else {
				aeiObjects.getWork().setExpireTime(null);
			}
		}
	}

	public class ExpireScriptResult {
		Integer hour;
		Integer workHour;
		Date date;

		public Integer getHour() {
			return hour;
		}

		public void setHour(Integer hour) {
			this.hour = hour;
		}

		public Integer getWorkHour() {
			return workHour;
		}

		public void setWorkHour(Integer workHour) {
			this.workHour = workHour;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}

}