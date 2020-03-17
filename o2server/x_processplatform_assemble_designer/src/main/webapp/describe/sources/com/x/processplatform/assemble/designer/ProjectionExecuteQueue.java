package com.x.processplatform.assemble.designer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
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

	private DataItemConverter<Item> converter = new DataItemConverter<Item>(Item.class);

	@Override
	protected void execute(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = emc.find(id, Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(id, Process.class);
			}
			if (StringUtils.isNotEmpty(process.getProjection()) && XGsonBuilder.isJson(process.getProjection())) {
				List<Projection> projections = XGsonBuilder.instance().fromJson(process.getProjection(),
						new TypeToken<List<Projection>>() {
						}.getType());
				this.work(business, process, projections);
				this.workCompleted(business, process, projections);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void work(Business business, Process process, List<Projection> projections) throws Exception {
		String sequence = "";
		List<Work> os = new ArrayList<>();
		Data data = null;
		do {
			os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.process_FIELDNAME,
					process.getId(), 100, sequence);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Work.class);
				business.entityManagerContainer().beginTransaction(Task.class);
				business.entityManagerContainer().beginTransaction(TaskCompleted.class);
				business.entityManagerContainer().beginTransaction(Read.class);
				business.entityManagerContainer().beginTransaction(ReadCompleted.class);
				business.entityManagerContainer().beginTransaction(Review.class);
				for (Work o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					ProjectionFactory.projectionWork(projections, data, o);
					for (Task task : business.entityManagerContainer().listEqualAndEqual(Task.class, Task.job_FIELDNAME,
							o.getJob(), Task.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionTask(projections, data, task);
					}
					for (TaskCompleted taskCompleted : business.entityManagerContainer().listEqualAndEqual(
							TaskCompleted.class, TaskCompleted.job_FIELDNAME, o.getJob(),
							TaskCompleted.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionTaskCompleted(projections, data, taskCompleted);
					}
					for (Read read : business.entityManagerContainer().listEqualAndEqual(Read.class, Read.job_FIELDNAME,
							o.getJob(), Read.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionRead(projections, data, read);
					}
					for (ReadCompleted readCompleted : business.entityManagerContainer().listEqualAndEqual(
							ReadCompleted.class, ReadCompleted.job_FIELDNAME, o.getJob(),
							ReadCompleted.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionReadCompleted(projections, data, readCompleted);
					}
					for (Review review : business.entityManagerContainer().listEqualAndEqual(Review.class,
							Review.job_FIELDNAME, o.getJob(), Review.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionReview(projections, data, review);
					}
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void workCompleted(Business business, Process process, List<Projection> projections) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
					WorkCompleted.process_FIELDNAME, process.getId(), 100, sequence);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(WorkCompleted.class);
				business.entityManagerContainer().beginTransaction(Task.class);
				business.entityManagerContainer().beginTransaction(TaskCompleted.class);
				business.entityManagerContainer().beginTransaction(Read.class);
				business.entityManagerContainer().beginTransaction(ReadCompleted.class);
				business.entityManagerContainer().beginTransaction(Review.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					ProjectionFactory.projectionWorkCompleted(projections, data, o);
					for (Task task : business.entityManagerContainer().listEqualAndEqual(Task.class, Task.job_FIELDNAME,
							o.getJob(), Task.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionTask(projections, data, task);
					}
					for (TaskCompleted taskCompleted : business.entityManagerContainer().listEqualAndEqual(
							TaskCompleted.class, TaskCompleted.job_FIELDNAME, o.getJob(),
							TaskCompleted.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionTaskCompleted(projections, data, taskCompleted);
					}
					for (Read read : business.entityManagerContainer().listEqualAndEqual(Read.class, Read.job_FIELDNAME,
							o.getJob(), Read.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionRead(projections, data, read);
					}
					for (ReadCompleted readCompleted : business.entityManagerContainer().listEqualAndEqual(
							ReadCompleted.class, ReadCompleted.job_FIELDNAME, o.getJob(),
							ReadCompleted.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionReadCompleted(projections, data, readCompleted);
					}
					for (Review review : business.entityManagerContainer().listEqualAndEqual(Review.class,
							Review.job_FIELDNAME, o.getJob(), Review.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionReview(projections, data, review);
					}
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private Data data(Business business, Work work) throws Exception {
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, Item.bundle_FIELDNAME,
				work.getJob(), Item.itemCategory_FIELDNAME, ItemCategory.pp);
		if (items.isEmpty()) {
			return new Data();
		} else {
			JsonElement jsonElement = converter.assemble(items);
			if (jsonElement.isJsonObject()) {
				return XGsonBuilder.convert(jsonElement, Data.class);
			} else {
				/* 如果不是Object强制返回一个Map对象 */
				return new Data();
			}
		}
	}

	private Data data(Business business, WorkCompleted workCompleted) throws Exception {
		if (StringUtils.isNotEmpty(workCompleted.getData())) {
			return XGsonBuilder.instance().fromJson(workCompleted.getData(), Data.class);
		}
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, Item.bundle_FIELDNAME,
				workCompleted.getJob(), Item.itemCategory_FIELDNAME, ItemCategory.pp);
		if (items.isEmpty()) {
			return new Data();
		} else {
			JsonElement jsonElement = converter.assemble(items);
			if (jsonElement.isJsonObject()) {
				return XGsonBuilder.convert(jsonElement, Data.class);
			} else {
				/* 如果不是Object强制返回一个Map对象 */
				return new Data();
			}
		}
	}
}