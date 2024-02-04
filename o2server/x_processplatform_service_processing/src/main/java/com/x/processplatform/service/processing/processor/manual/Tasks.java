package com.x.processplatform.service.processing.processor.manual;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.EmpowerLog;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;
import com.x.base.core.project.utils.time.WorkTime;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.ticket.Ticket;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class Tasks {

	private static final Logger LOGGER = LoggerFactory.getLogger(Tasks.class);

	private Tasks() {
		// nothing
	}

	public static Task createTask(AeiObjects aeiObjects, Manual manual, Ticket ticket) throws Exception {
		String person = aeiObjects.business().organization().person().getWithIdentity(ticket.distinguishedName());
		String unit = aeiObjects.business().organization().unit().getWithIdentity(ticket.distinguishedName());
		Task task = new Task(aeiObjects.getWork(), ticket.act(), ticket.distinguishedName(), person, unit,
				ticket.fromDistinguishedName(), new Date(), null, aeiObjects.getRoutes(), manual.getAllowRapid());
		task.setLabel(ticket.label());
		// 是第一条待办,进行标记，调度过的待办都标记为非第一个待办
		if (BooleanUtils.isTrue(aeiObjects.getProcessingAttributes().getForceJoinAtArrive())) {
			task.setFirst(false);
		} else {
			task.setFirst(ListTools.isEmpty(aeiObjects.getJoinInquireTaskCompleteds()));
		}
		calculateExpire(aeiObjects, manual, task);
		if (StringUtils.isNotEmpty(ticket.fromDistinguishedName())) {
			aeiObjects.business().organization().empowerLog().log(
					createEmpowerLog(aeiObjects.getWork(), ticket.fromDistinguishedName(), ticket.distinguishedName()));
			String fromPerson = aeiObjects.business().organization().person()
					.getWithIdentity(ticket.fromDistinguishedName());
			String fromUnit = aeiObjects.business().organization().unit()
					.getWithIdentity(ticket.fromDistinguishedName());
			TaskCompleted empowerTaskCompleted = new TaskCompleted(aeiObjects.getWork());
			empowerTaskCompleted.setAct(TaskCompleted.ACT_EMPOWER);
			empowerTaskCompleted.setProcessingType(TaskCompleted.PROCESSINGTYPE_EMPOWER);
			empowerTaskCompleted.setJoinInquire(false);
			empowerTaskCompleted.setIdentity(ticket.fromDistinguishedName());
			empowerTaskCompleted.setDistinguishedName(ticket.fromDistinguishedName());
			empowerTaskCompleted.setUnit(fromUnit);
			empowerTaskCompleted.setPerson(fromPerson);
			empowerTaskCompleted.setEmpowerToIdentity(ticket.distinguishedName());
			aeiObjects.createTaskCompleted(empowerTaskCompleted);
			Read empowerRead = new Read(aeiObjects.getWork(), ticket.fromDistinguishedName(), fromUnit, fromPerson);
			aeiObjects.createRead(empowerRead);
		}
		if (null != aeiObjects.getWork().getGoBackStore()) {
			// 如果存储了退回说明下一步需要jump那么待办无需选择路由
			task.setRouteNameDisable(true);
		}
		return task;
	}

	@Deprecated(since = "8.2", forRemoval = true)
	public static Task createTask(AeiObjects aeiObjects, Manual manual, String identity) throws Exception {
		String fromIdentity = aeiObjects.getWork().getManualEmpowerMap().get(identity);
		String person = aeiObjects.business().organization().person().getWithIdentity(identity);
		String unit = aeiObjects.business().organization().unit().getWithIdentity(identity);
		Task task = new Task(aeiObjects.getWork(), "create", identity, person, unit, fromIdentity, new Date(), null,
				aeiObjects.getRoutes(), manual.getAllowRapid());
		// 是第一条待办,进行标记，调度过的待办都标记为非第一个待办
		if (BooleanUtils.isTrue(aeiObjects.getProcessingAttributes().getForceJoinAtArrive())) {
			task.setFirst(false);
		} else {
			task.setFirst(ListTools.isEmpty(aeiObjects.getJoinInquireTaskCompleteds()));
		}
		calculateExpire(aeiObjects, manual, task);
		if (StringUtils.isNotEmpty(fromIdentity)) {
			aeiObjects.business().organization().empowerLog()
					.log(createEmpowerLog(aeiObjects.getWork(), fromIdentity, identity));
			String fromPerson = aeiObjects.business().organization().person().getWithIdentity(fromIdentity);
			String fromUnit = aeiObjects.business().organization().unit().getWithIdentity(fromIdentity);
			TaskCompleted empowerTaskCompleted = new TaskCompleted(aeiObjects.getWork());
			empowerTaskCompleted.setProcessingType(TaskCompleted.PROCESSINGTYPE_EMPOWER);
			empowerTaskCompleted.setJoinInquire(false);
			empowerTaskCompleted.setIdentity(fromIdentity);
			empowerTaskCompleted.setUnit(fromUnit);
			empowerTaskCompleted.setPerson(fromPerson);
			empowerTaskCompleted.setEmpowerToIdentity(identity);
			aeiObjects.createTaskCompleted(empowerTaskCompleted);
			Read empowerRead = new Read(aeiObjects.getWork(), fromIdentity, fromUnit, fromPerson);
			aeiObjects.createRead(empowerRead);
		}
		if (null != aeiObjects.getWork().getGoBackStore()) {
			// 如果存储了退回说明下一步需要jump那么待办无需选择路由
			task.setRouteNameDisable(true);
		}
		return task;
	}

	private static void calculateExpire(AeiObjects aeiObjects, Manual manual, Task task) throws Exception {
		if (null != manual.getTaskExpireType()) {
			switch (manual.getTaskExpireType()) {
			case never:
				expireNever(task);
				break;
			case appoint:
				expireAppoint(manual, task);
				break;
			case script:
				expireScript(aeiObjects, manual, task);
				break;
			default:
				break;
			}
		}
		// 如果work有截至时间
		if (null != aeiObjects.getWork().getExpireTime()) {
			if (null == task.getExpireTime()) {
				task.setExpireTime(aeiObjects.getWork().getExpireTime());
			} else {
				if (task.getExpireTime().after(aeiObjects.getWork().getExpireTime())) {
					task.setExpireTime(aeiObjects.getWork().getExpireTime());
				}
			}
		}
		// 已经有过期时间了,那么设置催办时间
		if (null != task.getExpireTime()) {
			task.setUrgeTime(DateUtils.addHours(task.getExpireTime(), -2));
		} else {
			task.setExpired(false);
			task.setUrgeTime(null);
			task.setUrged(false);
		}
	}

	private static EmpowerLog createEmpowerLog(Work work, String fromIdentity, String toIdentity) {
		return new EmpowerLog().setApplication(work.getApplication()).setApplicationAlias(work.getApplicationAlias())
				.setApplicationName(work.getApplicationName()).setProcess(work.getProcess())
				.setProcessAlias(work.getProcessAlias()).setProcessName(work.getProcessName()).setTitle(work.getTitle())
				.setWork(work.getId()).setJob(work.getJob()).setFromIdentity(fromIdentity).setToIdentity(toIdentity)
				.setActivity(work.getActivity()).setActivityAlias(work.getActivityAlias())
				.setActivityName(work.getActivityName()).setEmpowerTime(new Date());
	}

	// 从不过期
	private static void expireNever(Task task) {
		task.setExpireTime(null);
	}

	private static void expireAppoint(Manual manual, Task task) throws Exception {
		if (BooleanUtils.isTrue(manual.getTaskExpireWorkTime())) {
			expireAppointWorkTime(task, manual);
		} else {
			expireAppointNaturalDay(task, manual);
		}
	}

	private static void expireAppointWorkTime(Task task, Manual manual) throws Exception {
		Integer m = 0;
		WorkTime wt = Config.workTime();
		if (BooleanUtils.isTrue(NumberTools.greaterThan(manual.getTaskExpireDay(), 0))) {
			m += manual.getTaskExpireDay() * wt.minutesOfWorkDay();
		}
		if (BooleanUtils.isTrue(NumberTools.greaterThan(manual.getTaskExpireHour(), 0))) {
			m += manual.getTaskExpireHour() * 60;
		}
		if (m > 0) {
			Date expire = wt.forwardMinutes(new Date(), m);
			task.setExpireTime(expire);
		} else {
			task.setExpireTime(null);
		}
	}

	private static void expireAppointNaturalDay(Task task, Manual manual) {
		Integer m = 0;
		if (BooleanUtils.isTrue(NumberTools.greaterThan(manual.getTaskExpireDay(), 0))) {
			m += manual.getTaskExpireDay() * 60 * 24;
		}
		if (BooleanUtils.isTrue(NumberTools.greaterThan(manual.getTaskExpireHour(), 0))) {
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

	private static void expireScript(AeiObjects aeiObjects, Manual manual, Task task) throws Exception {
		ExpireScriptResult expire = new ExpireScriptResult();
		Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(), manual,
				Business.EVENT_MANUALTASKEXPIRE);
		GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
				.putMember(GraalvmScriptingFactory.BINDING_NAME_EXPIRE, expire);
		GraalvmScriptingFactory.eval(source, bindings, jsonElement -> {
			if (null != jsonElement) {
				ExpireScriptResult res = XGsonBuilder.instance().fromJson(jsonElement, ExpireScriptResult.class);
				expire.setDate(res.getDate());
				expire.setHour(res.getHour());
				expire.setWorkHour(res.getWorkHour());
			}
		});
		if (BooleanUtils.isTrue(NumberTools.greaterThan(expire.getWorkHour(), 0))) {
			Integer m = 0;
			m += expire.getWorkHour() * 60;
			if (m > 0) {
				task.setExpireTime(Config.workTime().forwardMinutes(new Date(), m));
			} else {
				task.setExpireTime(null);
			}
		} else if (BooleanUtils.isTrue(NumberTools.greaterThan(expire.getHour(), 0))) {
			Integer m = 0;
			m += expire.getHour() * 60;
			if (m > 0) {
				Calendar cl = Calendar.getInstance();
				cl.add(Calendar.MINUTE, m);
				task.setExpireTime(cl.getTime());
			} else {
				task.setExpireTime(null);
			}
		} else if (null != expire.getDate()) {
			task.setExpireTime(expire.getDate());
		} else {
			task.setExpireTime(null);
		}
	}

	public static class ExpireScriptResult {
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

		public void setDate(String str) {
			try {
				this.date = DateTools.parse(str);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}

	}

}
