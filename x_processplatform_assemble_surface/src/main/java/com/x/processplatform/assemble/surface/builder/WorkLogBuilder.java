package com.x.processplatform.assemble.surface.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTaskCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkLog;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;

public class WorkLogBuilder {

	private static List<String> IncludeTaskFields = new ArrayList<>();
	private static List<String> IncludeTaskCompletedFields = new ArrayList<>();
	private static List<String> IncludeWorkLogFields = new ArrayList<>();

	static {

		IncludeTaskFields.add("createTime");
		IncludeTaskFields.add("updateTime");
		IncludeTaskFields.add("startTime");
		IncludeTaskFields.add("startTimeMonth");
		IncludeTaskFields.add("person");
		IncludeTaskFields.add("identity");
		IncludeTaskFields.add("department");
		IncludeTaskFields.add("company");
		IncludeTaskFields.add("routeList");
		IncludeTaskFields.add("routeNameList");
		IncludeTaskFields.add("work");
		IncludeTaskFields.add("allowRapid");

		IncludeTaskCompletedFields.add("createTime");
		IncludeTaskCompletedFields.add("updateTime");
		IncludeTaskCompletedFields.add("startTime");
		IncludeTaskCompletedFields.add("startTimeMonth");
		IncludeTaskCompletedFields.add("completedTime");
		IncludeTaskCompletedFields.add("completedTimeMonth");
		IncludeTaskCompletedFields.add("completed");
		IncludeTaskCompletedFields.add("person");
		IncludeTaskCompletedFields.add("identity");
		IncludeTaskCompletedFields.add("department");
		IncludeTaskCompletedFields.add("company");
		IncludeTaskCompletedFields.add("routeName");
		IncludeTaskCompletedFields.add("opinion");
		IncludeTaskCompletedFields.add("work");
		IncludeTaskCompletedFields.add("processingType");
		IncludeTaskCompletedFields.add("retractTime");

		IncludeWorkLogFields.add("createTime");
		IncludeWorkLogFields.add("updateTime");
		IncludeWorkLogFields.add("completed");
		IncludeWorkLogFields.add("fromActivity");
		IncludeWorkLogFields.add("fromActivityType");
		IncludeWorkLogFields.add("fromActivityName");
		IncludeWorkLogFields.add("fromActivityToken");
		IncludeWorkLogFields.add("fromTime");
		IncludeWorkLogFields.add("arrivedActivity");
		IncludeWorkLogFields.add("arrivedActivityType");
		IncludeWorkLogFields.add("arrivedActivityName");
		IncludeWorkLogFields.add("arrivedActivityToken");
		IncludeWorkLogFields.add("arrivedTime");
		IncludeWorkLogFields.add("route");
		IncludeWorkLogFields.add("routeName");
		IncludeWorkLogFields.add("work");
		IncludeWorkLogFields.add("workCompleted");
		// IncludeWorkLogFields.add("processingType");
		IncludeWorkLogFields.add("connected");
		IncludeWorkLogFields.add("splitting");
		IncludeWorkLogFields.add("splitTokenList");
		IncludeWorkLogFields.add("processingType");
	}

	private static BeanCopyTools<WorkLog, WrapOutWorkLog> workLogCopier = BeanCopyToolsBuilder.create(WorkLog.class,
			WrapOutWorkLog.class, IncludeWorkLogFields, null);

	private static BeanCopyTools<Task, WrapOutTask> taskCopier = BeanCopyToolsBuilder.create(Task.class,
			WrapOutTask.class, IncludeTaskFields, null);

	private static BeanCopyTools<TaskCompleted, WrapOutTaskCompleted> taskCompletedCopier = BeanCopyToolsBuilder
			.create(TaskCompleted.class, WrapOutTaskCompleted.class, IncludeTaskCompletedFields, null);

	public static WrapOutWorkLog complex(Business business, WorkLog workLog) throws Exception {
		WrapOutWorkLog wrap = workLogCopier.copy(workLog);
		if (!workLog.getConnected()) {
			complexTask(business, wrap);
		} else {
			/* 已经完成的不会有待办，返回一个空数组 */
			wrap.setTaskList(new ArrayList<WrapOutTask>());
		}
		complexTaskCompleted(business, wrap);
		return wrap;
	}

	public static void complexTaskCompleted(Business business, WrapOutWorkLog wrap) throws Exception {
		List<String> ids = business.taskCompleted().listWithActivityToken(wrap.getFromActivityToken());
		List<WrapOutTaskCompleted> list = taskCompletedCopier
				.copy(business.entityManagerContainer().list(TaskCompleted.class, ids));
		Collections.sort(list, new Comparator<WrapOutTaskCompleted>() {
			public int compare(WrapOutTaskCompleted o1, WrapOutTaskCompleted o2) {
				return ObjectUtils.compare(o1.getCompletedTime(), o2.getCompletedTime(), true);
			}
		});
		/* 补充召回 */
		List<WrapOutTaskCompleted> results = new ArrayList<>();
		for (WrapOutTaskCompleted o : list) {
			results.add(o);
			if (o.getProcessingType().equals(ProcessingType.retract)) {
				WrapOutTaskCompleted retract = new WrapOutTaskCompleted();
				o.copyTo(retract);
				retract.setRouteName("撤回");
				retract.setOpinion("撤回");
				retract.setStartTime(retract.getRetractTime());
				retract.setCompletedTime(retract.getRetractTime());
				results.add(retract);
			}
		}
		wrap.setTaskCompletedList(results);
	}

	public static void complexTask(Business business, WrapOutWorkLog wrap) throws Exception {
		List<String> ids = business.task().listWithActivityToken(wrap.getFromActivityToken());
		List<WrapOutTask> list = taskCopier.copy(business.entityManagerContainer().list(Task.class, ids));
		SortTools.asc(list, false, "startTime");
		wrap.setTaskList(list);
	}

	public static List<WrapOutWorkLog> complex(Business business, List<WorkLog> list) throws Exception {
		List<WrapOutWorkLog> results = new ArrayList<>();
		for (WorkLog o : list) {
			results.add(complex(business, o));
		}
		SortTools.asc(results, false, "arrivedTime");
		return results;
	}

}