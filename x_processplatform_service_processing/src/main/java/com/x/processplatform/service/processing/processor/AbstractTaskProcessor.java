package com.x.processplatform.service.processing.processor;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.NumberTools;
import com.x.base.core.utils.time.WorkTime;
import com.x.collaboration.core.message.Collaboration;
import com.x.collaboration.core.message.notification.TaskMessage;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;

public abstract class AbstractTaskProcessor extends AbstractReadProcessor {

	protected AbstractTaskProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	protected void sendTaskMessage(Task task) {
		try {
			TaskMessage message = new TaskMessage(task.getPerson(), task.getWork(), task.getId());
			Collaboration.send(message);
			// Collaboration.notification(task.getPerson(), "您有新的待办内容.",
			// task.getTitle(),
			// "应用:" + task.getApplicationName() + ", 流程:" +
			// task.getProcessName() + ".", "task");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Task createTask(Business business, Manual manual, Work work, ProcessingAttributes attributes, Data data,
			String identity) throws Exception {
		String person = business.organization().person().getWithIdentity(identity).getName();
		Task task = null;
		if (StringUtils.isNotEmpty(person)) {
			task = new Task();
			task.setModified(false);
			task.setViewed(false);
			task.setPerson(person);
			task.setManualMode(manual.getManualMode());
			task.setTitle(work.getTitle());
			task.setActivity(work.getActivity());
			task.setActivityName(work.getActivityName());
			task.setActivityToken(work.getActivityToken());
			task.setActivityType(manual.getActivityType());
			task.setApplication(work.getApplication());
			task.setApplicationName(work.getApplicationName());
			task.setProcess(work.getProcess());
			task.setProcessName(work.getProcessName());
			task.setJob(work.getJob());
			task.setStartTime(new Date());
			task.setWork(work.getId());
			task.setIdentity(identity);
			task.setDepartment(business.organization().department().getWithIdentity(identity).getName());
			task.setCompany(business.organization().company().getWithIdentity(identity).getName());
			task.setCreatorPerson(work.getCreatorPerson());
			task.setCreatorIdentity(work.getCreatorIdentity());
			task.setCreatorDepartment(work.getCreatorDepartment());
			task.setCreatorCompany(work.getCreatorCompany());
			task.setAllowRapid(manual.getAllowRapid());
			this.calculateExpire(task, work, manual, attributes, data);
		}
		return task;
	}

	private void calculateExpire(Task task, Work work, Manual manual, ProcessingAttributes attributes, Data data)
			throws Exception {
		if (null != manual.getTaskExpireType()) {
			switch (manual.getTaskExpireType()) {
			case never:
				this.expireNever(task);
				break;
			case appoint:
				this.expireAppoint(task, manual);
				break;
			case script:
				this.expireScript(task, work, manual, attributes, data);
				break;
			default:
				break;
			}
		}
	}

	private void expireNever(Task task) {
		task.setExpireTime(null);
	}

	private void expireAppoint(Task task, Manual manual) throws Exception {
		if (BooleanUtils.isTrue(manual.getTaskExpireWorkTime())) {
			this.expireAppointWorkTime(task, manual);
		} else {
			this.expireAppointNaturalDay(task, manual);
		}
	}

	private void expireAppointWorkTime(Task task, Manual manual) throws Exception {
		Integer m = 0;
		WorkTime wt = new WorkTime();
		if (NumberTools.greaterThan(manual.getTaskExpireDay(), 0)) {
			m += manual.getTaskExpireDay() * wt.minutesOfWorkDay();
		}
		if (NumberTools.greaterThan(manual.getTaskExpireHour(), 0)) {
			m += manual.getTaskExpireHour() * 60;
		}
		if (m > 0) {
			Date expire = wt.forwardMinutes(new Date(), m);
			task.setExpireTime(expire);
		} else {
			task.setExpireTime(null);
		}
	}

	private void expireAppointNaturalDay(Task task, Manual manual) throws Exception {
		Integer m = 0;
		if (NumberTools.greaterThan(manual.getTaskExpireDay(), 0)) {
			m += manual.getTaskExpireDay() * 60 * 24;
		}
		if (NumberTools.greaterThan(manual.getTaskExpireHour(), 0)) {
			m += manual.getTaskExpireHour() * 60;
		}
		if (m > 0) {
			Calendar cl = Calendar.getInstance();
			cl.add(Calendar.MINUTE, m);
			task.setExpireTime(cl.getTime());
		} else {
			task.setExpireTime(null);
		}
	}

	private void expireScript(Task task, Work work, Manual manual, ProcessingAttributes attributes, Data data)
			throws Exception {
		ScriptHelper sh = ScriptHelperFactory.create(this.business(), attributes, work, data, manual,
				new BindingPair("task", task));
		String str = Objects.toString(
				sh.eval(work.getApplication(), manual.getTaskExpireScript(), manual.getTaskExpireScriptText()));
		if (StringUtils.isNotEmpty(str)) {
			ExpireScriptResult result = XGsonBuilder.instance().fromJson(str, ExpireScriptResult.class);
			if (NumberTools.greaterThan(result.getWorkHour(), 0)) {
				Integer m = 0;
				m += result.getWorkHour() * 60;
				if (m > 0) {
					WorkTime wt = new WorkTime();
					task.setExpireTime(wt.forwardMinutes(new Date(), m));
				} else {
					task.setExpireTime(null);
				}
			} else if (NumberTools.greaterThan(result.getHour(), 0)) {
				Integer m = 0;
				m += result.getHour() * 60;
				if (m > 0) {
					Calendar cl = Calendar.getInstance();
					cl.add(Calendar.MINUTE, m);
					task.setExpireTime(cl.getTime());
				} else {
					task.setExpireTime(null);
				}
			} else if (null != result.getDate()) {
				task.setExpireTime(result.getDate());
			} else {
				task.setExpireTime(null);
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