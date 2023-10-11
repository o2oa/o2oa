package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

@Deprecated(forRemoval = true)
class ActionGetWithWorkOrWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetWithWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {

		LOGGER.debug("execute:{}, workOrWorkCompleted:{}.", effectivePerson::getDistinguishedName,
				() -> workOrWorkCompleted);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Work work = null;
		WorkCompleted workCompleted = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			work = emc.find(workOrWorkCompleted, Work.class);
			if (null == work) {
				workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
			}
		}

		CompletableFuture<Boolean> checkControlFuture = this.checkControlVisitFuture(effectivePerson,
				workOrWorkCompleted);

		if (null != work) {
			CompletableFuture<Data> dataFuture = this.dataFuture(work);
			CompletableFuture<List<WoTask>> taskFuture = this.taskFuture(work.getJob());
			CompletableFuture<List<WoRead>> readFuture = this.readFuture(work.getJob());
			wo.setData(dataFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setTaskList(taskFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setReadList(readFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			this.setCurrentReadIndex(effectivePerson, wo);
			this.setCurrentTaskIndex(effectivePerson, wo);
			wo.setWork(gson.toJsonTree(work));
		} else if (null != workCompleted) {
			CompletableFuture<Data> dataFuture = this.dataFuture(workCompleted);
			CompletableFuture<List<WoRead>> readFuture = this.readFuture(workCompleted.getJob());
			wo.setData(dataFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			wo.setReadList(readFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			this.setCurrentReadIndex(effectivePerson, wo);
			wo.setWork(gson.toJsonTree(workCompleted));
		}

		if (BooleanUtils
				.isFalse(checkControlFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS))) {
			throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
		}

		result.setData(wo);
		return result;
	}

	private CompletableFuture<Data> dataFuture(Work work) {
		return CompletableFuture.supplyAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				EntityManager em = business.entityManagerContainer().get(Item.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Item> cq = cb.createQuery(Item.class);
				Root<Item> root = cq.from(Item.class);
				Predicate p = cb.equal(root.get(Item_.bundle), work.getJob());
				p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
				List<Item> list = em.createQuery(cq.where(p)).getResultList();
				if (list.isEmpty()) {
					return new Data();
				} else {
					JsonElement jsonElement = itemConverter.assemble(list);
					if (jsonElement.isJsonObject()) {
						return gson.fromJson(jsonElement, Data.class);
					} else {
						// 如果不是Object强制返回一个Map对象
						return new Data();
					}
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return null;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Data> dataFuture(WorkCompleted workCompleted) {
		return CompletableFuture.supplyAsync(() -> {
			if (BooleanUtils.isTrue(workCompleted.getMerged())) {
				return workCompleted.getData();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					EntityManager em = business.entityManagerContainer().get(Item.class);
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Item> cq = cb.createQuery(Item.class);
					Root<Item> root = cq.from(Item.class);
					Predicate p = cb.equal(root.get(Item_.bundle), workCompleted.getJob());
					p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
					List<Item> list = em.createQuery(cq.where(p)).getResultList();
					if (list.isEmpty()) {
						return new Data();
					} else {
						JsonElement jsonElement = itemConverter.assemble(list);
						if (jsonElement.isJsonObject()) {
							return gson.fromJson(jsonElement, Data.class);
						} else {
							// 如果不是Object强制返回一个Map对象
							return new Data();
						}
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return null;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<WoTask>> taskFuture(String job) {
		return CompletableFuture.supplyAsync(() -> {
			List<WoTask> list = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				list = WoTask.copier.copy(emc.listEqual(Task.class, Task.work_FIELDNAME, job));
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return list;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<WoRead>> readFuture(String job) {
		return CompletableFuture.supplyAsync(() -> {
			List<WoRead> list = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				list = WoRead.copier.copy(emc.listEqual(Read.class, Read.job_FIELDNAME, job));
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return list;
		}, ThisApplication.forkJoinPool());
	}

	private void setCurrentTaskIndex(EffectivePerson effectivePerson, Wo wo) {
		int loop = 0;
		for (WoTask task : wo.getTaskList()) {
			if (effectivePerson.isPerson(task.getPerson())) {
				wo.setCurrentTaskIndex(loop);
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

		private static final long serialVersionUID = -869684415398137301L;
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
				WoWorkCompleted.class, null, JpaObject.FieldsInvisible);
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

}