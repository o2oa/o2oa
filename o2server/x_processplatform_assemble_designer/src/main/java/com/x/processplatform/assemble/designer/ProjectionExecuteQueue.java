package com.x.processplatform.assemble.designer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Projection;
import com.x.processplatform.core.entity.element.util.ProjectionFactory;

public class ProjectionExecuteQueue extends AbstractQueue<String> {

	private static Logger logger = LoggerFactory.getLogger(ProjectionExecuteQueue.class);

	@Override
	protected void execute(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Projection projection = emc.find(id, Projection.class);

			if (null == projection) {
				throw new ExceptionEntityNotExist(id, Projection.class);
			}

			switch (Objects.toString(projection, "")) {
			case Projection.TYPE_WORKCOMPLETED:
				this.workCompleted(business, projection);
				break;
			case Projection.TYPE_TASKCOMPLETED:
				this.taskCompleted(business, projection);
				break;
			case Projection.TYPE_READCOMPLETED:
				this.readCompleted(business, projection);
				break;
			case Projection.TYPE_READ:
				this.read(business, projection);
				break;
			case Projection.TYPE_REVIEW:
				this.review(business, projection);
				break;
			case Projection.TYPE_TABLE:
				this.table(business, projection);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void workCompleted(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
					WorkCompleted.process_FIELDNAME, projection.getProcess(), 200, sequence);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(WorkCompleted.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = XGsonBuilder.instance().fromJson(o.getData(), Data.class);
					ProjectionFactory.projectionWorkCompleted(projection, data, o);
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void taskCompleted(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
					WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(TaskCompleted.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = XGsonBuilder.instance().fromJson(o.getData(), Data.class);
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

	private void readCompleted(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
					WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(ReadCompleted.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = XGsonBuilder.instance().fromJson(o.getData(), Data.class);
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

	private void read(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
					WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Read.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = XGsonBuilder.instance().fromJson(o.getData(), Data.class);
					for (Read read : business.entityManagerContainer().listEqualAndEqual(Read.class, Read.job_FIELDNAME,
							o.getJob(), Read.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionRead(projection, data, read);
					}

				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private void review(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
					WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(Review.class);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = XGsonBuilder.instance().fromJson(o.getData(), Data.class);
					for (Review review : business.entityManagerContainer().listEqualAndEqual(Review.class,
							Review.job_FIELDNAME, o.getJob(), Review.process_FIELDNAME, o.getProcess())) {
						ProjectionFactory.projectionReview(projection, data, review);
					}
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	@SuppressWarnings("unchecked")
	private void table(Business business, Projection projection) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		Class<JpaObject> dynamicClass = (Class<JpaObject>) Class.forName(projection.getDynamicClassName());
		do {
			os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
					WorkCompleted.process_FIELDNAME, projection.getProcess(), 100, sequence);
			if (!os.isEmpty()) {
				business.entityManagerContainer().beginTransaction(dynamicClass);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = XGsonBuilder.instance().fromJson(o.getData(), Data.class);
					JpaObject jpaObject = (JpaObject) dynamicClass.newInstance();
					ProjectionFactory.projectionTable(projection, data, jpaObject);
					business.entityManagerContainer().persist(jpaObject, CheckPersistType.all);
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}
}