package com.x.processplatform.assemble.surface.jaxrs.work;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.builder.WorkLogBuilder;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutRead;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutReadCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutReview;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTaskCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkLog;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutActivity;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.content.tools.DataHelper;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

abstract class ActionBase extends StandardJaxrsAction {

	protected static Type wrapOutIdCollectionType = new TypeToken<ArrayList<WrapOutId>>() {
	}.getType();

	protected static BeanCopyTools<Work, WrapOutWork> workOutCopier = BeanCopyToolsBuilder.create(Work.class,
			WrapOutWork.class, null, WrapOutWork.Excludes);

	protected static BeanCopyTools<Task, WrapOutTask> taskOutCopier = BeanCopyToolsBuilder.create(Task.class,
			WrapOutTask.class);

	protected static BeanCopyTools<TaskCompleted, WrapOutTaskCompleted> taskCompletedOutCopier = BeanCopyToolsBuilder
			.create(TaskCompleted.class, WrapOutTaskCompleted.class, null, WrapOutTaskCompleted.Excludes);

	protected static BeanCopyTools<Read, WrapOutRead> readOutCopier = BeanCopyToolsBuilder.create(Read.class,
			WrapOutRead.class);

	protected static BeanCopyTools<ReadCompleted, WrapOutReadCompleted> readCompletedOutCopier = BeanCopyToolsBuilder
			.create(ReadCompleted.class, WrapOutReadCompleted.class, null, WrapOutReadCompleted.Excludes);

	protected static BeanCopyTools<Review, WrapOutReview> reviewOutCopier = BeanCopyToolsBuilder.create(Review.class,
			WrapOutReview.class, null, WrapOutReview.Excludes);

	protected static BeanCopyTools<Attachment, WrapOutAttachment> attachmentOutCopier = BeanCopyToolsBuilder
			.create(Attachment.class, WrapOutAttachment.class, null, WrapOutAttachment.Excludes);

	protected List<WrapOutAttachment> listAttachment(Business business, Work work) throws Exception {
		List<WrapOutAttachment> list = new ArrayList<>();
		List<Attachment> attachments = business.entityManagerContainer().list(Attachment.class,
				work.getAttachmentList());
		list = attachmentOutCopier.copy(attachments);
		for (WrapOutAttachment o : list) {
			o.setReferencedCount(business.work().countWithAttachment(o.getId()));
		}
		SortTools.asc(list, false, "createTime");
		return list;
	}

	protected List<WrapOutWorkLog> listWorkLog(Business business, Work work) throws Exception {
		List<WorkLog> workLogs = business.workLog().listWithJobObject(work.getJob());
		List<WrapOutWorkLog> results = new ArrayList<>();
		for (WorkLog o : workLogs) {
			results.add(WorkLogBuilder.complex(business, o));
		}
		SortTools.asc(results, false, "arrivedTime");
		return results;
	}

	protected WrapOutMap getComplex(Business business, EffectivePerson effectivePerson, Work work) throws Exception {
		WrapOutMap wrap = new WrapOutMap();
		/* 拼装Activity节点信息 */
		Activity activity = business.getActivity(work);
		WrapOutActivity wrapOutActivity = new WrapOutActivity();
		activity.copyTo(wrapOutActivity);
		wrap.put("activity", wrapOutActivity);
		wrap.put("work", workOutCopier.copy(work));
		wrap.put("workLogList", this.listWorkLog(business, work));
		DataHelper dataHelper = new DataHelper(business.entityManagerContainer(), work);
		wrap.put("data", dataHelper.get());
		wrap.put("attachmentList", this.listAttachment(business, work));
		List<Task> taskList = business.task().listWithWorkObject(work);
		SortTools.asc(taskList, "startTime");
		wrap.put("taskList", taskOutCopier.copy(taskList));
		/* 查找当前人的待办 */
		wrap.put("currentTaskIndex", -1);
		for (int i = 0; i < taskList.size(); i++) {
			Task o = taskList.get(i);
			if (StringUtils.equals(o.getPerson(), effectivePerson.getName())) {
				wrap.put("currentTaskIndex", i);
				break;
			}
		}
		List<Read> readList = business.read().listWithWorkObject(work);
		SortTools.asc(readList, "startTime");
		/* 查找当前人待阅 */
		wrap.put("currentReadIndex", -1);
		for (int i = 0; i < readList.size(); i++) {
			Read o = readList.get(i);
			if (StringUtils.equals(o.getPerson(), effectivePerson.getName())) {
				wrap.put("currentReadIndex", i);
				break;
			}
		}
		wrap.put("control", business.getControlOfWorkComplex(effectivePerson, work));
		return wrap;
	}

	protected String getApplicationName(Business business, EffectivePerson effectivePerson, String id)
			throws Exception {
		Application application = business.application().pick(id);
		if (null != application) {
			return application.getName();
		}
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(Work_.application), id));
		cq.select(root.get(Work_.applicationName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	protected String getProcessName(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		Process process = business.process().pick(id);
		if (null != process) {
			return process.getName();
		}
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(Work_.process), id));
		cq.select(root.get(Work_.processName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}
