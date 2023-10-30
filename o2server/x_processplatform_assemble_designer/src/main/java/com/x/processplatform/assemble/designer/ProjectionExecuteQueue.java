package com.x.processplatform.assemble.designer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Projection;
import com.x.processplatform.core.entity.element.util.ProjectionFactory;
import com.x.query.core.entity.Item;

public class ProjectionExecuteQueue extends AbstractQueue<String> {

	private static Logger logger = LoggerFactory.getLogger(ProjectionExecuteQueue.class);

	private DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);

	@Override
	protected void execute(String id) throws Exception {
		logger.print("开始执行流程数据映射process：{}", id);
		Process process = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			process = emc.find(id, Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(id, Process.class);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		try {
			if (process != null) {
				if (XGsonBuilder.isJsonArray(process.getProjection())) {
					List<Projection> projections = XGsonBuilder.instance().fromJson(process.getProjection(),
							new TypeToken<List<Projection>>() {
							}.getType());
					logger.print("开始执行流转中工作数据映射process：{}", id);
					this.work(process, projections);
					logger.print("开始执行已完成工作数据映射process：{}", id);
					this.workCompleted(process, projections);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

		logger.print("完成流程数据映射process：{}", id);
	}

	private void work(Process process, final List<Projection> projections) throws Exception {
		List<String> jobList;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			jobList = business.work().listJobWithProcess(process.getId());
		}
		if (ListTools.isNotEmpty(jobList)) {
			logger.print("流转中工作需要执行数据映射个数：{}", jobList.size());
			for (List<String> partJobs : ListTools.batch(jobList, 10)) {
				List<CompletableFuture<Void>> futures = new TreeList<>();
				for (String job : partJobs) {
					CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
						try {
							this.workProjection(job, process.getId(), projections);
						} catch (Exception e) {
							logger.warn("流程{}的工作job={}数据映射异常：{}", process.getId(), job, e.getMessage());
							logger.error(e);
						}
					}, ThisApplication.forkJoinPool());
					futures.add(future);
				}
				for (CompletableFuture<Void> future : futures) {
					try {
						future.get(300, TimeUnit.SECONDS);
					} catch (Exception e) {
						logger.warn("允许流程数据映射任务异常：{}", e.getMessage());
					}
				}
				futures.clear();
			}
		}
	}

	private void workProjection(String job, String process, List<Projection> projections) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Data data = this.data(business, job);
			emc.beginTransaction(Work.class);
			emc.beginTransaction(Task.class);
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			emc.beginTransaction(Review.class);
			for (Work work : business.entityManagerContainer().listEqualAndEqual(Work.class, Work.job_FIELDNAME, job,
					Work.process_FIELDNAME, process)) {
				ProjectionFactory.projectionWork(projections, data, work);
			}
			for (Task task : business.entityManagerContainer().listEqualAndEqual(Task.class, Task.job_FIELDNAME, job,
					Task.process_FIELDNAME, process)) {
				ProjectionFactory.projectionTask(projections, data, task);
			}
			for (TaskCompleted taskCompleted : business.entityManagerContainer().listEqualAndEqual(TaskCompleted.class,
					TaskCompleted.job_FIELDNAME, job, TaskCompleted.process_FIELDNAME, process)) {
				ProjectionFactory.projectionTaskCompleted(projections, data, taskCompleted);
			}
			for (Read read : business.entityManagerContainer().listEqualAndEqual(Read.class, Read.job_FIELDNAME, job,
					Read.process_FIELDNAME, process)) {
				ProjectionFactory.projectionRead(projections, data, read);
			}
			for (ReadCompleted readCompleted : business.entityManagerContainer().listEqualAndEqual(ReadCompleted.class,
					ReadCompleted.job_FIELDNAME, job, ReadCompleted.process_FIELDNAME, process)) {
				ProjectionFactory.projectionReadCompleted(projections, data, readCompleted);
			}
			for (Review review : business.entityManagerContainer().listEqualAndEqual(Review.class, Review.job_FIELDNAME,
					job, Review.process_FIELDNAME, process)) {
				ProjectionFactory.projectionReview(projections, data, review);
			}
			emc.commit();
		}
	}

	private void workCompleted(Process process, final List<Projection> projections) throws Exception {
		List<String> workComList;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			workComList = business.workCompleted().listWithProcess(process.getId());
		}
		if (ListTools.isNotEmpty(workComList)) {
			logger.print("已完成工作需要执行数据映射个数：{}", workComList.size());
			for (List<String> partWorkComList : ListTools.batch(workComList, 10)) {
				List<CompletableFuture<Void>> futures = new TreeList<>();
				for (String workCompletedId : partWorkComList) {
					CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
						try {
							this.workCompletedProjection(workCompletedId, projections);
						} catch (Exception e) {
							logger.warn("流程{}的工作workCompletedId={}数据映射异常：{}", process.getId(), workCompletedId,
									e.getMessage());
							logger.error(e);
						}
					}, ThisApplication.forkJoinPool());
					futures.add(future);
				}
				for (CompletableFuture<Void> future : futures) {
					try {
						future.get(300, TimeUnit.SECONDS);
					} catch (Exception e) {
						logger.warn("允许流程数据映射任务异常：{}", e.getMessage());
					}
				}
				futures.clear();
			}
		}
	}

	private void workCompletedProjection(String workCompletedId, List<Projection> projections) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCompleted o = emc.find(workCompletedId, WorkCompleted.class);
			Data data = this.data(business, o);
			emc.beginTransaction(WorkCompleted.class);
			emc.beginTransaction(Task.class);
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			emc.beginTransaction(Review.class);
			ProjectionFactory.projectionWorkCompleted(projections, data, o);
			for (Task task : business.entityManagerContainer().listEqualAndEqual(Task.class, Task.job_FIELDNAME,
					o.getJob(), Task.process_FIELDNAME, o.getProcess())) {
				ProjectionFactory.projectionTask(projections, data, task);
			}
			for (TaskCompleted taskCompleted : business.entityManagerContainer().listEqualAndEqual(TaskCompleted.class,
					TaskCompleted.job_FIELDNAME, o.getJob(), TaskCompleted.process_FIELDNAME, o.getProcess())) {
				ProjectionFactory.projectionTaskCompleted(projections, data, taskCompleted);
			}
			for (Read read : business.entityManagerContainer().listEqualAndEqual(Read.class, Read.job_FIELDNAME,
					o.getJob(), Read.process_FIELDNAME, o.getProcess())) {
				ProjectionFactory.projectionRead(projections, data, read);
			}
			for (ReadCompleted readCompleted : business.entityManagerContainer().listEqualAndEqual(ReadCompleted.class,
					ReadCompleted.job_FIELDNAME, o.getJob(), ReadCompleted.process_FIELDNAME, o.getProcess())) {
				ProjectionFactory.projectionReadCompleted(projections, data, readCompleted);
			}
			for (Review review : business.entityManagerContainer().listEqualAndEqual(Review.class, Review.job_FIELDNAME,
					o.getJob(), Review.process_FIELDNAME, o.getProcess())) {
				ProjectionFactory.projectionReview(projections, data, review);
			}
			emc.commit();
		}
	}

	private Data data(Business business, String job) throws Exception {
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME,
				job, DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
		if (items.isEmpty()) {
			return new Data();
		} else {
			JsonElement jsonElement = converter.assemble(items);
			if (jsonElement.isJsonObject()) {
				return XGsonBuilder.convert(jsonElement, Data.class);
			} else {
				// 如果不是Object强制返回一个Map对象
				return new Data();
			}
		}
	}

	private Data data(Business business, WorkCompleted workCompleted) throws Exception {
		if (BooleanUtils.isTrue(workCompleted.getMerged())) {
			return workCompleted.getData();
		}
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME,
				workCompleted.getJob(), DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
		if (items.isEmpty()) {
			return new Data();
		} else {
			JsonElement jsonElement = converter.assemble(items);
			if (jsonElement.isJsonObject()) {
				return XGsonBuilder.convert(jsonElement, Data.class);
			} else {
				// 如果不是Object强制返回一个Map对象
				return new Data();
			}
		}
	}
}
