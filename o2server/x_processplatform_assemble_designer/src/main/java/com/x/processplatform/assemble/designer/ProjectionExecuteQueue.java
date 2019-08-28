package com.x.processplatform.assemble.designer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
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
			Projection projection = emc.find(id, Projection.class);
			if (null == projection) {
				throw new ExceptionEntityNotExist(id, Projection.class);
			}
			if (BooleanUtils.isTrue(projection.getEnable())) {
				switch (Objects.toString(projection.getType(), "")) {
				case Projection.TYPE_WORK:
					this.work(business, projection);
					break;
				case Projection.TYPE_WORKCOMPLETED:
					this.workCompleted(business, projection);
					break;
				case Projection.TYPE_TASK:
					this.task_work(business, projection);
					break;
				case Projection.TYPE_TASKCOMPLETED:
					this.taskCompleted_work(business, projection);
					this.taskCompleted_workCompleted(business, projection);
					break;
				case Projection.TYPE_READ:
					this.read_work(business, projection);
					this.read_workCompleted(business, projection);
					break;
				case Projection.TYPE_READCOMPLETED:
					this.readCompleted_work(business, projection);
					this.readCompleted_workCompleted(business, projection);
					break;
				case Projection.TYPE_REVIEW:
					this.review_work(business, projection);
					this.review_workCompleted(business, projection);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void work(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<Work> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.process_FIELDNAME,
						projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.application_FIELDNAME,
						projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Work.class);
				for (Work o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					ProjectionFactory.projectionWork(projection, data, o);
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void workCompleted(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.application_FIELDNAME, projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(WorkCompleted.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					ProjectionFactory.projectionWorkCompleted(projection, data, o);
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void task_work(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<Work> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.process_FIELDNAME,
						projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.application_FIELDNAME,
						projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Task.class);
				for (Work o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					for (Task task : business.entityManagerContainer().listEqualAndEqual(Task.class, Task.job_FIELDNAME,
							o.getJob(), Task.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionTask(projection, data, task);
					}
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void taskCompleted_work(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<Work> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.process_FIELDNAME,
						projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.application_FIELDNAME,
						projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(TaskCompleted.class);
				for (Work o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					for (TaskCompleted taskCompleted : business.entityManagerContainer().listEqualAndEqual(
							TaskCompleted.class, TaskCompleted.job_FIELDNAME, o.getJob(),
							TaskCompleted.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionTaskCompleted(projection, data, taskCompleted);
					}
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void taskCompleted_workCompleted(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.application_FIELDNAME, projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(TaskCompleted.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					for (TaskCompleted taskCompleted : business.entityManagerContainer().listEqualAndEqual(
							TaskCompleted.class, TaskCompleted.job_FIELDNAME, o.getJob(),
							TaskCompleted.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionTaskCompleted(projection, data, taskCompleted);
					}
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void read_work(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<Work> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.process_FIELDNAME,
						projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.application_FIELDNAME,
						projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Read.class);
				for (Work o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					for (Read read : business.entityManagerContainer().listEqualAndEqual(Read.class, Read.job_FIELDNAME,
							o.getJob(), Read.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionRead(projection, data, read);
					}

				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void read_workCompleted(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.application_FIELDNAME, projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Read.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					for (Read read : business.entityManagerContainer().listEqualAndEqual(Read.class, Read.job_FIELDNAME,
							o.getJob(), Read.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionRead(projection, data, read);
					}

				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void readCompleted_work(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<Work> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.process_FIELDNAME,
						projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.application_FIELDNAME,
						projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(ReadCompleted.class);
				for (Work o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					for (ReadCompleted readCompleted : business.entityManagerContainer().listEqualAndEqual(
							ReadCompleted.class, ReadCompleted.job_FIELDNAME, o.getJob(),
							ReadCompleted.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionReadCompleted(projection, data, readCompleted);
					}
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void readCompleted_workCompleted(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.application_FIELDNAME, projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(ReadCompleted.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					for (ReadCompleted readCompleted : business.entityManagerContainer().listEqualAndEqual(
							ReadCompleted.class, ReadCompleted.job_FIELDNAME, o.getJob(),
							ReadCompleted.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionReadCompleted(projection, data, readCompleted);
					}

				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void review_work(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<Work> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.process_FIELDNAME,
						projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(Work.class, Work.application_FIELDNAME,
						projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Review.class);
				for (Work o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					for (Review review : business.entityManagerContainer().listEqualAndEqual(Review.class,
							Review.job_FIELDNAME, o.getJob(), Review.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionReview(projection, data, review);
					}
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void review_workCompleted(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(projection.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(projection.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.application_FIELDNAME, projection.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Review.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					for (Review review : business.entityManagerContainer().listEqualAndEqual(Review.class,
							Review.job_FIELDNAME, o.getJob(), Review.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionReview(projection, data, review);
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