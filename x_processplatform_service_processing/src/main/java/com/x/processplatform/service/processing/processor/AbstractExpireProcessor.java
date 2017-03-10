package com.x.processplatform.service.processing.processor;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.NumberTools;
import com.x.base.core.utils.time.WorkTime;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.configurator.ActivityProcessingConfigurator;

public abstract class AbstractExpireProcessor extends AbstractBaseProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractExpireProcessor.class);

	protected AbstractExpireProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	protected void calculateExpire(ActivityProcessingConfigurator activityConfigurator, Work work, Process process,
			Activity activity, ProcessingAttributes attributes, Data data) throws Exception {
		if (activityConfigurator.getCalculateExpire()) {
			if (null != process.getExpireType()) {
				switch (process.getExpireType()) {
				case never:
					this.expireNever(work);
					break;
				case appoint:
					this.expireAppoint(work, process);
					break;
				case script:
					this.expireScript(work, process, activity, attributes, data);
					break;
				default:
					break;
				}
			}
		}
	}

	private void expireNever(Work work) {
		work.setExpireTime(null);
	}

	private void expireAppoint(Work work, Process process) throws Exception {
		if (BooleanUtils.isTrue(process.getExpireWorkTime())) {
			this.expireAppointWorkTime(work, process);
		} else {
			this.expireAppointNaturalDay(work, process);
		}
	}

	private void expireAppointWorkTime(Work work, Process process) throws Exception {
		Integer m = 0;
		WorkTime wt = new WorkTime();
		if (NumberTools.greaterThan(process.getExpireDay(), 0)) {
			m += process.getExpireDay() * wt.minutesOfWorkDay();
		}
		if (NumberTools.greaterThan(process.getExpireHour(), 0)) {
			m += process.getExpireHour() * 60;
		}
		if (m > 0) {
			work.setExpireTime(wt.forwardMinutes(work.getCreateTime(), m));
		} else {
			work.setExpireTime(null);
		}
	}

	private void expireAppointNaturalDay(Work work, Process process) throws Exception {
		Integer m = 0;
		if (NumberTools.greaterThan(process.getExpireDay(), 0)) {
			m += process.getExpireDay() * 60 * 24;
		}
		if (NumberTools.greaterThan(process.getExpireHour(), 0)) {
			m += process.getExpireHour() * 60;
		}
		if (m > 0) {
			Calendar cl = Calendar.getInstance();
			cl.setTime(work.getCreateTime());
			cl.add(Calendar.MINUTE, m);
			work.setExpireTime(cl.getTime());
		} else {
			work.setExpireTime(null);
		}
	}

	private void expireScript(Work work, Process process, Activity activity, ProcessingAttributes attributes, Data data)
			throws Exception {
		ScriptHelper sh = ScriptHelperFactory.create(this.business(), attributes, work, data, activity);
		String str = Objects
				.toString(sh.eval(work.getApplication(), process.getExpireScript(), process.getExpireScriptText()), "");
		if (StringUtils.isNotEmpty(str)) {
			ExpireScriptResult result = XGsonBuilder.instance().fromJson(str, ExpireScriptResult.class);
			if (NumberTools.greaterThan(result.getWorkHour(), 0)) {
				Integer m = 0;
				m += result.getWorkHour() * 60;
				if (m > 0) {
					WorkTime wt = new WorkTime();
					work.setExpireTime(wt.forwardMinutes(work.getCreateTime(), m));
				} else {
					work.setExpireTime(null);
				}
			} else if (NumberTools.greaterThan(result.getHour(), 0)) {
				Integer m = 0;
				m += result.getHour() * 60;
				if (m > 0) {
					Calendar cl = Calendar.getInstance();
					cl.setTime(work.getCreateTime());
					cl.add(Calendar.MINUTE, m);
					work.setExpireTime(cl.getTime());
				} else {
					work.setExpireTime(null);
				}
			} else if (null != result.getDate()) {
				work.setExpireTime(result.getDate());
			} else {
				work.setExpireTime(null);
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
