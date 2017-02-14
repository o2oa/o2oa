package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.x.base.core.http.WrapOutMap;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.ActivityStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;
import com.x.processplatform.core.entity.element.ActivityType;

public class TimerCategory extends ActionBase {

	public WrapOutMap execute(Business business) throws Exception {
		WrapOutMap wrap = new WrapOutMap();
		Date start = this.getStart();
		Date current = new Date();
		wrap.put("application", this.application(business, start, current));
		wrap.put("process", this.process(business, start, current));
		wrap.put("activity", this.activity(business, start, current));
		return wrap;
	}

	private List<WrapOutMap> application(Business business, Date start, Date current) throws Exception {
		List<WrapOutMap> list = new ArrayList<>();
		for (ApplicationStub stub : ThisApplication.state.getApplicationStubs()) {
			Long taskCount = 0L;
			Long taskExpiredCount = 0L;
			Long taskDuration = 0L;
			Long taskCompletedCount = 0L;
			Long taskCompletedExpiredCount = 0L;
			Long taskCompletedDuration = 0L;
			Long workCount = 0L;
			Long workExpiredCount = 0L;
			Long workDuration = 0L;
			Long workCompletedCount = 0L;
			Long workCompletedExpiredCount = 0L;
			Long workCompletedDuration = 0L;
			taskCount = business.task().count(start, stub);
			if (taskCount > 0) {
				taskExpiredCount = business.task().expiredCount(start, current, stub);
				taskDuration = business.task().duration(start, current, stub);
			}
			taskCompletedCount = business.taskCompleted().count(start, stub);
			if (taskCompletedCount > 0) {
				taskCompletedExpiredCount = business.taskCompleted().expiredCount(start, stub);
				taskCompletedDuration = business.taskCompleted().duration(start, stub);
			}
			workCount = business.work().count(start, stub);
			if (workCount > 0) {
				workExpiredCount = business.work().expiredCount(start, current, stub);
				workDuration = business.work().duration(start, current, stub);
			}
			workCompletedCount = business.workCompleted().count(start, stub);
			if (workCompletedCount > 0) {
				workCompletedExpiredCount = business.workCompleted().expiredCount(start, stub);
				workCompletedDuration = business.workCompleted().duration(start, stub);
			}
			WrapOutMap wrap = new WrapOutMap();
			wrap.put("name", stub.getName());
			wrap.put("value", stub.getValue());
			wrap.put("taskCount", taskCount);
			wrap.put("taskExpiredCount", taskExpiredCount);
			wrap.put("taskDuration", taskDuration);
			wrap.put("taskCompletedCount", taskCompletedCount);
			wrap.put("taskCompletedExpiredCount", taskCompletedExpiredCount);
			wrap.put("taskCompletedDuration", taskCompletedDuration);
			wrap.put("workCount", workCount);
			wrap.put("workExpiredCount", workExpiredCount);
			wrap.put("workDuration", workDuration);
			wrap.put("workCompletedCount", workCompletedCount);
			wrap.put("workCompletedExpiredCount", workCompletedExpiredCount);
			wrap.put("workCompletedDuration", workCompletedDuration);
			list.add(wrap);
		}
		SortTools.asc(list, "name");
		return list;
	}

	private List<WrapOutMap> process(Business business, Date start, Date current) throws Exception {
		List<WrapOutMap> list = new ArrayList<>();
		for (ApplicationStub applicationStub : ThisApplication.state.getApplicationStubs()) {
			for (ProcessStub stub : applicationStub.getProcessStubs()) {
				Long taskCount = 0L;
				Long taskExpiredCount = 0L;
				Long taskDuration = 0L;
				Long taskCompletedCount = 0L;
				Long taskCompletedExpiredCount = 0L;
				Long taskCompletedDuration = 0L;
				Long workCount = 0L;
				Long workExpiredCount = 0L;
				Long workDuration = 0L;
				Long workCompletedCount = 0L;
				Long workCompletedExpiredCount = 0L;
				Long workCompletedDuration = 0L;
				taskCount = business.task().count(start, stub);
				if (taskCount > 0) {
					taskExpiredCount = business.task().expiredCount(start, current, stub);
					taskDuration = business.task().duration(start, current, stub);
				}
				taskCompletedCount = business.taskCompleted().count(start, stub);
				if (taskCompletedCount > 0) {
					taskCompletedExpiredCount = business.taskCompleted().expiredCount(start, stub);
					taskCompletedDuration = business.taskCompleted().duration(start, stub);
				}
				workCount = business.work().count(start, stub);
				if (workCount > 0) {
					workExpiredCount = business.work().expiredCount(start, current, stub);
					workDuration = business.work().duration(start, current, stub);
				}
				workCompletedCount = business.workCompleted().count(start, stub);
				if (workCompletedCount > 0) {
					workCompletedExpiredCount = business.workCompleted().expiredCount(start, stub);
					workCompletedDuration = business.workCompleted().duration(start, stub);
				}
				WrapOutMap wrap = new WrapOutMap();
				wrap.put("name", stub.getName());
				wrap.put("value", stub.getValue());
				wrap.put("applicationName", applicationStub.getName());
				wrap.put("applicationValue", applicationStub.getValue());
				wrap.put("taskCount", taskCount);
				wrap.put("taskExpiredCount", taskExpiredCount);
				wrap.put("taskDuration", taskDuration);
				wrap.put("taskCompletedCount", taskCompletedCount);
				wrap.put("taskCompletedExpiredCount", taskCompletedExpiredCount);
				wrap.put("taskCompletedDuration", taskCompletedDuration);
				wrap.put("workCount", workCount);
				wrap.put("workExpiredCount", workExpiredCount);
				wrap.put("workDuration", workDuration);
				wrap.put("workCompletedCount", workCompletedCount);
				wrap.put("workCompletedExpiredCount", workCompletedExpiredCount);
				wrap.put("workCompletedDuration", workCompletedDuration);
				list.add(wrap);
			}
		}
		SortTools.asc(list, "name");
		return list;
	}

	private List<WrapOutMap> activity(Business business, Date start, Date current) throws Exception {
		List<WrapOutMap> list = new ArrayList<>();
		for (ApplicationStub applicationStub : ThisApplication.state.getApplicationStubs()) {
			for (ProcessStub processStub : applicationStub.getProcessStubs()) {
				for (ActivityStub stub : processStub.getActivityStubs()) {
					Long taskCount = 0L;
					Long taskExpiredCount = 0L;
					Long taskDuration = 0L;
					Long taskCompletedCount = 0L;
					Long taskCompletedExpiredCount = 0L;
					Long taskCompletedDuration = 0L;
					Long workCount = 0L;
					Long workExpiredCount = 0L;
					Long workDuration = 0L;
					taskCount = business.task().count(start, stub);
					if (taskCount > 0) {
						taskExpiredCount = business.task().expiredCount(start, current, stub);
						taskDuration = business.task().duration(start, current, stub);
					}
					taskCompletedCount = business.taskCompleted().count(start, stub);
					if (taskCompletedCount > 0) {
						taskCompletedExpiredCount = business.taskCompleted().expiredCount(start, stub);
						taskCompletedDuration = business.taskCompleted().duration(start, stub);
					}
					workCount = business.work().count(start, stub);
					if (taskCompletedCount > 0) {
						workExpiredCount = business.work().expiredCount(start, current, stub);
						workDuration = business.work().duration(start, current, stub);
					}
					WrapOutMap wrap = new WrapOutMap();
					/* 如果是开始或者结束,且所有数据为0,那么忽略数据 */
					if ((!Objects.equals(ActivityType.begin, stub.getActivityType()))
							&& (!Objects.equals(ActivityType.end, stub.getActivityType()))) {
						if (taskCount != 0L || taskCompletedCount != 0L || workCount != 0L) {
							wrap.put("name", stub.getName());
							wrap.put("value", stub.getValue());
							wrap.put("applicationName", applicationStub.getName());
							wrap.put("applicationValue", applicationStub.getValue());
							wrap.put("processName", processStub.getName());
							wrap.put("processValue", processStub.getValue());
							wrap.put("taskCount", taskCount);
							wrap.put("taskExpiredCount", taskExpiredCount);
							wrap.put("taskDuration", taskDuration);
							wrap.put("taskCompletedCount", taskCompletedCount);
							wrap.put("taskCompletedExpiredCount", taskCompletedExpiredCount);
							wrap.put("taskCompletedDuration", taskCompletedDuration);
							wrap.put("workCount", workCount);
							wrap.put("workExpiredCount", workExpiredCount);
							wrap.put("workDuration", workDuration);
							list.add(wrap);
						}
					}
				}
			}
		}
		SortTools.asc(list, "name");
		return list;
	}

}