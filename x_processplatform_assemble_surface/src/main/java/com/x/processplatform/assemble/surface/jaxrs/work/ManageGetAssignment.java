package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutRead;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutReadCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutReview;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTaskCompleted;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Process;

class ManageGetAssignment extends ActionBase {

	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			WrapOutMap wrap = new WrapOutMap();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class, ExceptionWhen.not_found);
			/* Process 也可能为空 */
			Process process = business.process().pick(work.getProcess());
			// 需要对这个应用的管理权限
			if (!business.process().allowControl(effectivePerson, process)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has insufficient permissions.");
			}
			List<Task> tasks = business.task().listWithWorkObject(work);
			List<TaskCompleted> taskCompleteds = business.taskCompleted().listWithWorkObject(work);
			List<Read> reads = business.read().listWithWorkObject(work);
			List<ReadCompleted> readCompleteds = business.readCompleted().listWithWorkObject(work);
			List<Review> reviews = business.review().listWithWorkObject(work);
			SortTools.asc(tasks, "startTime");
			SortTools.asc(taskCompleteds, "startTime");
			SortTools.asc(reads, "startTime");
			SortTools.asc(readCompleteds, "startTime");
			SortTools.asc(reviews, "startTime");
			List<WrapOutTask> wrapOutTasks = new ArrayList<>();
			List<WrapOutTaskCompleted> wrapOutTaskCompleteds = new ArrayList<>();
			List<WrapOutRead> wrapOutReads = new ArrayList<>();
			List<WrapOutReadCompleted> wrapOutReadCompleteds = new ArrayList<>();
			List<WrapOutReview> wrapOutReviews = new ArrayList<>();
			for (Task o : tasks) {
				WrapOutTask w = taskOutCopier.copy(o);
				w.setControl(business.getControlOfTask(effectivePerson, o));
				wrapOutTasks.add(w);
			}
			for (TaskCompleted o : taskCompleteds) {
				WrapOutTaskCompleted w = taskCompletedOutCopier.copy(o);
				w.setControl(business.getControlOfTaskCompleted(effectivePerson, o));
				wrapOutTaskCompleteds.add(w);
			}
			for (Read o : reads) {
				WrapOutRead w = readOutCopier.copy(o);
				w.setControl(business.getControlOfRead(effectivePerson, o));
				wrapOutReads.add(w);
			}
			for (ReadCompleted o : readCompleteds) {
				WrapOutReadCompleted w = readCompletedOutCopier.copy(o);
				w.setControl(business.getControlOfReadCompleted(effectivePerson, o));
				wrapOutReadCompleteds.add(w);
			}
			for (Review o : reviews) {
				WrapOutReview w = reviewOutCopier.copy(o);
				w.setControl(business.getControlOfReview(effectivePerson, o));
				wrapOutReviews.add(w);
			}
			wrap.put("taskList", wrapOutTasks);
			wrap.put("taskCompletedList", wrapOutTaskCompleteds);
			wrap.put("readList", wrapOutReads);
			wrap.put("readCompletedList", wrapOutReadCompleteds);
			wrap.put("reviewList", wrapOutReviews);
			result.setData(wrap);
			return result;
		}
	}

}