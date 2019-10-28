package com.x.processplatform.assemble.surface.jaxrs.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.entity.dataitem.ItemType;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Data.DataWork;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.core.entity.Item;

abstract class BaseAction extends StandardJaxrsAction {

	private static final String subject_path = "subject";

	protected Gson gson = XGsonBuilder.instance();

	JsonElement getData(Business business, String job, String... paths) throws Exception {
		JsonElement jsonElement = null;
		List<Item> list = business.item().listWithJobWithPath(job, paths);
		DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
		jsonElement = converter.assemble(list, paths.length);
		return jsonElement;
	}

	/** 将data中的Title 和 serial 字段同步到work中 */
	void updateTitleSerial(Business business, Work work, JsonElement jsonElement) throws Exception {
		String title = XGsonBuilder.extractString(jsonElement, Work.title_FIELDNAME);
		if (null == title) {
			title = XGsonBuilder.extractString(jsonElement, subject_path);
		}
		String serial = XGsonBuilder.extractString(jsonElement, Work.serial_FIELDNAME);
		/* 如果有数据就将数据覆盖到work task taskCompleted read readCompleted review 中 */
		if (((null != title) && (!Objects.equals(title, work.getTitle())))
				|| ((null != serial) && (!Objects.equals(serial, work.getSerial())))) {
			business.entityManagerContainer().beginTransaction(Work.class);
			business.entityManagerContainer().beginTransaction(Task.class);
			business.entityManagerContainer().beginTransaction(TaskCompleted.class);
			business.entityManagerContainer().beginTransaction(Read.class);
			business.entityManagerContainer().beginTransaction(ReadCompleted.class);
			business.entityManagerContainer().beginTransaction(Review.class);

			List<Task> tasks = business.entityManagerContainer().listEqual(Task.class, Task.job_FIELDNAME,
					work.getJob());
			List<TaskCompleted> taskCompleteds = business.entityManagerContainer().listEqual(TaskCompleted.class,
					TaskCompleted.job_FIELDNAME, work.getJob());
			List<Read> reads = business.entityManagerContainer().listEqual(Read.class, Read.job_FIELDNAME,
					work.getJob());
			List<ReadCompleted> readCompleteds = business.entityManagerContainer().listEqual(ReadCompleted.class,
					ReadCompleted.job_FIELDNAME, work.getJob());
			List<Review> reviews = business.entityManagerContainer().listEqual(Review.class, Review.job_FIELDNAME,
					work.getJob());

			if ((null != title) && (!Objects.equals(title, work.getTitle()))) {
				work.setTitle(title);
				for (Task o : tasks) {
					o.setTitle(title);
				}
				for (TaskCompleted o : taskCompleteds) {
					o.setTitle(title);
				}
				for (Read o : reads) {
					o.setTitle(title);
				}
				for (ReadCompleted o : readCompleteds) {
					o.setTitle(title);
				}
				for (Review o : reviews) {
					o.setTitle(title);
				}
			}

			if ((null != serial) && (!Objects.equals(serial, work.getSerial()))) {
				work.setSerial(serial);
				for (Task o : tasks) {
					o.setSerial(serial);
				}
				for (TaskCompleted o : taskCompleteds) {
					o.setSerial(serial);
				}
				for (Read o : reads) {
					o.setSerial(serial);
				}
				for (ReadCompleted o : readCompleteds) {
					o.setSerial(serial);
				}
				for (Review o : reviews) {
					o.setSerial(serial);
				}
			}
			/** 这里必须先提交掉,不然后面的获取会得到不一致的状态 */
			/**
			 * <openjpa-2.4.3-SNAPSHOT-r422266:1777109 nonfatal user error>
			 * org.apache.openjpa.persistence.InvalidStateException: Opera tion attempted on
			 * a deleted instance.
			 */
			business.entityManagerContainer().commit();
		}
	}

	void updateData(Business business, Work work, JsonElement jsonElement, String... paths) throws Exception {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		if (paths.length == 0) {
			DataWork dataWork = DataWork.workCopier.copy(work);
			dataWork.setWorkId(work.getId());
			dataWork.setWorkCompletedId("");
			dataWork.setCompleted(false);
			jsonObject.add(Data.WORK_PROPERTY, gson.toJsonTree(dataWork));
			jsonObject.add(Data.ATTACHMENTLIST_PROPERTY,
					gson.toJsonTree(this.listDataAttachment(business, work.getJob())));
		}
		DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
		List<Item> exists = business.item().listWithJobWithPath(work.getJob(), paths);
		List<Item> currents = converter.disassemble(jsonObject, paths);
		List<Item> removes = converter.subtract(exists, currents);
		List<Item> adds = converter.subtract(currents, exists);
		if ((!removes.isEmpty()) || (!adds.isEmpty())) {
			business.entityManagerContainer().beginTransaction(Item.class);
			for (Item _o : removes) {
				business.entityManagerContainer().remove(_o);
			}
			for (Item _o : adds) {
				this.fill(_o, work);
				business.entityManagerContainer().persist(_o);
			}
			/* 标记数据已经被修改 */
			business.entityManagerContainer().beginTransaction(Work.class);
			work.setDataChanged(true);
			/* 基于前面的原因,这里进行单独提交 */
			business.entityManagerContainer().commit();
		}
	}

	void updateData(Business business, WorkCompleted workCompleted, JsonElement jsonElement, String... paths)
			throws Exception {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		if (paths.length == 0) {
			DataWork dataWork = DataWork.workCompletedCopier.copy(workCompleted);
			dataWork.setWorkCompletedId(workCompleted.getId());
			dataWork.setWorkId(workCompleted.getWork());
			dataWork.setCompleted(true);
			jsonObject.add(Data.WORK_PROPERTY, gson.toJsonTree(dataWork));
			jsonObject.add(Data.ATTACHMENTLIST_PROPERTY,
					gson.toJsonTree(this.listDataAttachment(business, workCompleted.getJob())));
		}
		DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
		List<Item> exists = business.item().listWithJobWithPath(workCompleted.getJob(), paths);
		List<Item> currents = converter.disassemble(jsonObject, paths);
		List<Item> removes = converter.subtract(exists, currents);
		List<Item> adds = converter.subtract(currents, exists);
		if ((!removes.isEmpty()) || (!adds.isEmpty())) {
			business.entityManagerContainer().beginTransaction(Item.class);
			for (Item _o : removes) {
				business.entityManagerContainer().remove(_o);
			}
			for (Item _o : adds) {
				this.fill(_o, workCompleted);
				business.entityManagerContainer().persist(_o);
			}
			/** 基于前面的原因,这里进行单独提交 */
			business.entityManagerContainer().commit();

		}

	}

	void createData(Business business, Work work, JsonElement jsonElement, String... paths) throws Exception {
		if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();

			jsonObject.add(Data.WORK_PROPERTY, gson.toJsonTree(Data.DataWork.workCopier.copy(work)));
			jsonObject.add(Data.ATTACHMENTLIST_PROPERTY,
					gson.toJsonTree(this.listDataAttachment(business, work.getJob())));

			jsonElement = jsonObject;
		}
		String[] parentPaths = new String[] { "", "", "", "", "", "", "", "" };
		String[] cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
		for (int i = 0; i < paths.length - 1; i++) {
			parentPaths[i] = paths[i];
			cursorPaths[i] = paths[i];
		}
		cursorPaths[paths.length - 1] = paths[paths.length - 1];
		Item parent = business.item().getWithJobWithPath(work.getJob(), parentPaths[0], parentPaths[1], parentPaths[2],
				parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6], parentPaths[7]);
		if (null == parent) {
			throw new Exception("parent not existed.");
		}
		Item cursor = business.item().getWithJobWithPath(work.getJob(), cursorPaths[0], cursorPaths[1], cursorPaths[2],
				cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6], cursorPaths[7]);
		DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
		business.entityManagerContainer().beginTransaction(Item.class);
		if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
			/* 向数组里面添加一个成员对象 */
			Integer index = business.item().getArrayLastIndexWithJobWithPath(work.getJob(), paths);
			/* 新的路径开始 */
			String[] ps = new String[paths.length + 1];
			for (int i = 0; i < paths.length; i++) {
				ps[i] = paths[i];
			}
			ps[paths.length] = Integer.toString(index + 1);
			List<Item> adds = converter.disassemble(jsonElement, ps);
			for (Item o : adds) {
				this.fill(o, work);
				business.entityManagerContainer().persist(o);
			}
		} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
			/* 向parent对象添加一个属性值 */
			List<Item> adds = converter.disassemble(jsonElement, paths);
			for (Item o : adds) {
				this.fill(o, work);
				business.entityManagerContainer().persist(o);
			}
		} else {
			throw new Exception("unexpected post data with work" + work + ".path:" + StringUtils.join(paths, ".")
					+ "json:" + jsonElement);
		}
		/* 标记数据已经被修改 */
		business.entityManagerContainer().beginTransaction(Work.class);
		work.setDataChanged(true);
	}

	void deleteData(Business business, Work work, String... paths) throws Exception {
		List<Item> exists = business.item().listWithJobWithPath(work.getJob(), paths);
		if (exists.isEmpty()) {
			throw new Exception(
					"data{job:" + work.getJob() + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
		}
		business.entityManagerContainer().beginTransaction(Item.class);
		for (Item o : exists) {
			business.entityManagerContainer().remove(o);
		}
		if (paths.length > 0) {
			if (NumberUtils.isCreatable(paths[paths.length - 1])) {
				int position = paths.length - 1;
				for (Item o : business.item().listWithJobWithPathWithAfterLocation(work.getJob(),
						NumberUtils.toInt(paths[position]), paths)) {
					o.path(Integer.toString(o.pathLocation(position) - 1), position);
				}
			}
		}
		/* 标记数据已经被修改 */
		business.entityManagerContainer().beginTransaction(Work.class);
		work.setDataChanged(true);
	}

	void fill(Item o, Work work) {
		/** 将DateItem与Work放在同一个分区 */
		o.setDistributeFactor(work.getDistributeFactor());
		o.setBundle(work.getJob());
		o.setItemCategory(ItemCategory.pp);
	}

	void fill(Item o, WorkCompleted workCompleted) {
		/** 将DateItem与Work放在同一个分区 */
		o.setDistributeFactor(workCompleted.getDistributeFactor());
		o.setBundle(workCompleted.getJob());
		o.setItemCategory(ItemCategory.pp);
	}

	private List<Data.DataAttachment> listDataAttachment(Business business, String job) throws Exception {
		return business.entityManagerContainer().fetchEqual(Attachment.class, Data.DataAttachment.copier,
				Attachment.job_FIELDNAME, job);
	}

	protected Boolean manager(Business business, EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		return (business.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformManager));
	}

	protected Boolean applicationControl(Business business, EffectivePerson effectivePerson, String job)
			throws Exception {
		List<String> ids = new ArrayList<>();
		ids.addAll(this.listApplicationWithWork(business, job));
		ids.addAll(this.listApplicationWithWorkCompleted(business, job));
		ids = ListTools.trim(ids, true, true);
		List<Application> os = business.application().pick(ids);
		for (Application o : os) {
			if (ListTools.contains(o.getControllerList(), effectivePerson.getDistinguishedName())) {
				return true;
			}
		}
		return false;
	}

	private List<String> listApplicationWithWork(Business business, String job) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.job), job);
		cq.select(root.get(Work_.application)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private List<String> listApplicationWithWorkCompleted(Business business, String job) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.job), job);
		cq.select(root.get(WorkCompleted_.application)).where(p);
		return em.createQuery(cq).getResultList();
	}
}