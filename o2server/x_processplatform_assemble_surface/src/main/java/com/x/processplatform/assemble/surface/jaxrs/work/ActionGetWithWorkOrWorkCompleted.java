package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ManualMode;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

class ActionGetWithWorkOrWorkCompleted extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetWithWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
					new ExceptionEntityNotExist(workOrWorkCompleted))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = null;
			Work work = emc.find(workOrWorkCompleted, Work.class);
			if (null != work) {
				wo = this.work(business, effectivePerson, work);
			} else {
				wo = this.workCompleted(business, effectivePerson, emc.flag(workOrWorkCompleted, WorkCompleted.class));
			}
			result.setData(wo);
			return result;
		}
	}

	private Wo work(Business business, EffectivePerson effectivePerson, Work work) throws Exception {
		Wo wo = new Wo();
		wo.setWork(gson.toJsonTree(WoWork.copier.copy(work)));
		wo.setActivity(this.activity(business, work));
		CompletableFuture<Data> future_data = CompletableFuture.supplyAsync(() -> {
			return this.data(business, work.getJob());
		});
		CompletableFuture<List<WoTask>> future_task = CompletableFuture.supplyAsync(() -> {
			return this.tasks(business, work.getId());
		});
		CompletableFuture<List<WoRead>> future_read = CompletableFuture.supplyAsync(() -> {
			return this.reads(business, work.getJob());
		});
		wo.setData(future_data.get());
		wo.setTaskList(future_task.get());
		wo.setReadList(future_read.get());
		this.setCurrentReadIndex(effectivePerson, wo);
		this.setCurrentTaskIndex(effectivePerson, wo);
		return wo;
	}

	private Wo workCompleted(Business business, EffectivePerson effectivePerson, WorkCompleted workCompleted)
			throws Exception {
		Wo wo = new Wo();
		wo.setWork(gson.toJsonTree(WoWorkCompleted.copier.copy(workCompleted)));
		CompletableFuture<Data> future_data = CompletableFuture.supplyAsync(() -> {
			if (BooleanUtils.isTrue(workCompleted.getDataMerged())) {
				/* 如果data已经merged */
				return gson.fromJson(workCompleted.getData(), Data.class);
			} else {
				return this.data(business, workCompleted.getJob());
			}
		});
		CompletableFuture<List<WoRead>> future_read = CompletableFuture.supplyAsync(() -> {
			return this.reads(business, workCompleted.getJob());
		});
		wo.setData(future_data.get());
		wo.setReadList(future_read.get());
		this.setCurrentReadIndex(effectivePerson, wo);
		return wo;
	}

	private Data data(Business business, String job) {
		try {
			EntityManager em = business.entityManagerContainer().get(Item.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Item> cq = cb.createQuery(Item.class);
			Root<Item> root = cq.from(Item.class);
			Predicate p = cb.equal(root.get(Item_.bundle), job);
			p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
			List<Item> list = em.createQuery(cq.where(p)).getResultList();
			if (list.isEmpty()) {
				return new Data();
			} else {
				JsonElement jsonElement = itemConverter.assemble(list);
				if (jsonElement.isJsonObject()) {
					return gson.fromJson(jsonElement, Data.class);
				} else {
					/* 如果不是Object强制返回一个Map对象 */
					return new Data();
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	private List<WoTask> tasks(Business business, String workId) {
		try {
			return WoTask.copier
					.copy(business.entityManagerContainer().listEqual(Task.class, Task.work_FIELDNAME, workId));
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	private List<WoRead> reads(Business business, String job) {
		try {
			return WoRead.copier.copy(business.entityManagerContainer().listEqual(Read.class, Read.job_FIELDNAME, job));
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	private WoActivity activity(Business business, Work work) throws Exception {
		WoActivity woActivity = new WoActivity();
		Activity activity = business.getActivity(work);
		if (null != activity) {
			activity.copyTo(woActivity);
		}
		return woActivity;
	}

	private void setCurrentTaskIndex(EffectivePerson effectivePerson, Wo wo) {
		int loop = 0;
		for (WoTask task : wo.getTaskList()) {
			if (effectivePerson.isPerson(task.getPerson())) {
				wo.setCurrentTaskIndex(loop);
				/* 发送抢办信号 */
				if (Objects.equals(ManualMode.grab, wo.getActivity().getManualMode())) {
					CompletableFuture.runAsync(() -> {
						try {
							ThisApplication.context().applications().getQuery(
									x_processplatform_service_processing.class,
									Applications.joinQueryUri("task", task.getId(), "grab"));
						} catch (Exception e) {
							logger.error(e);
						}
					});
				}
				break;
			}
			loop++;
		}
	}

	private void setCurrentReadIndex(EffectivePerson effectivePerson, Wo wo) {
		int loop = 0;
		for (WoRead read : wo.getReadList()) {
			if (effectivePerson.isPerson(read.getPerson())) {
				wo.setCurrentReadIndex(loop);
				break;
			}
			loop++;
		}

	}

	public static class Wo extends GsonPropertyObject {

		/* work和workCompleted都有 */
		private JsonElement work;
		/* work和workCompleted都有 */
		private Data data;
		/* work和workCompleted都有 */
		private List<WoRead> readList;
		/* work和workCompleted都有 */
		private Integer currentReadIndex = -1;

		/* 只有work有 */
		private WoActivity activity;
		/* 只有work有 */
		private List<WoTask> taskList;
		/* 只有work有 */
		private Integer currentTaskIndex = -1;

		public JsonElement getWork() {
			return work;
		}

		public void setWork(JsonElement work) {
			this.work = work;
		}

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

		public Integer getCurrentReadIndex() {
			return currentReadIndex;
		}

		public void setCurrentReadIndex(Integer currentReadIndex) {
			this.currentReadIndex = currentReadIndex;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public Integer getCurrentTaskIndex() {
			return currentTaskIndex;
		}

		public void setCurrentTaskIndex(Integer currentTaskIndex) {
			this.currentTaskIndex = currentTaskIndex;
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public WoActivity getActivity() {
			return activity;
		}

		public void setActivity(WoActivity activity) {
			this.activity = activity;
		}

	}

	public static class WoWork extends Work {

		private static final long serialVersionUID = 5244996549744746585L;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoWorkCompleted extends WorkCompleted {

		private static final long serialVersionUID = -1772642962691214007L;

		static WrapCopier<WorkCompleted, WoWorkCompleted> copier = WrapCopierFactory.wo(WorkCompleted.class,
				WoWorkCompleted.class, null, ListTools.toList(JpaObject.FieldsInvisible,
						WorkCompleted.formData_FIELDNAME, WorkCompleted.formMobileData_FIELDNAME));
	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 5244996549744746585L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = 5244996549744746585L;

		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoActivity extends GsonPropertyObject {

		static WrapCopier<Activity, WoActivity> copier = WrapCopierFactory.wo(Activity.class, WoActivity.class,
				JpaObject.singularAttributeField(Activity.class, true, true), JpaObject.FieldsInvisible);

		private String id;

		private String name;

		private String description;

		private String alias;

		private String position;

		private String resetRange;

		private Integer resetCount;

		private Boolean allowReset;

		private ManualMode manualMode;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getResetRange() {
			return resetRange;
		}

		public void setResetRange(String resetRange) {
			this.resetRange = resetRange;
		}

		public Integer getResetCount() {
			return resetCount;
		}

		public void setResetCount(Integer resetCount) {
			this.resetCount = resetCount;
		}

		public Boolean getAllowReset() {
			return allowReset;
		}

		public void setAllowReset(Boolean allowReset) {
			this.allowReset = allowReset;
		}

		public ManualMode getManualMode() {
			return manualMode;
		}

		public void setManualMode(ManualMode manualMode) {
			this.manualMode = manualMode;
		}

	}

}