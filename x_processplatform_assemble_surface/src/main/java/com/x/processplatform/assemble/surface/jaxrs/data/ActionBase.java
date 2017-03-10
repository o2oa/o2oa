package com.x.processplatform.assemble.surface.jaxrs.data;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.entity.item.ItemType;
import com.x.base.core.gson.XGsonBuilder;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.DataLobItem;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;

abstract class ActionBase {

	private static final String title_path = "title";
	private static final String subject_path = "subject";
	private static final String serial_path = "serial";

	JsonElement getData(Business business, String job, String... paths) throws Exception {
		JsonElement jsonElement = null;
		List<DataItem> list = business.dataItem().listWithJobWithPath(job, paths);
		for (DataItem o : list) {
			if (o.isLobItem()) {
				DataLobItem lob = business.entityManagerContainer().find(o.getLobItem(), DataLobItem.class);
				if (null != o) {
					o.setStringLobValue(lob.getData());
				}
			}
		}
		ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
		jsonElement = converter.assemble(list, paths.length);
		return jsonElement;
	}

	/** 将data中的Title 和 serial 字段同步到work中 */
	void updateTitleSerial(Business business, Work work, JsonElement jsonElement) throws Exception {
		String title = XGsonBuilder.extractString(jsonElement, title_path);
		if (null == title) {
			title = XGsonBuilder.extractString(jsonElement, subject_path);
		}
		String serial = XGsonBuilder.extractString(jsonElement, serial_path);
		/* 如果有数据就将数据覆盖到work task taskCompleted read readCompleted review 中 */
		if (((null != title) && (!Objects.equals(title, work.getTitle())))
				|| ((null != serial) && (!Objects.equals(serial, work.getSerial())))) {
			business.entityManagerContainer().beginTransaction(Work.class);
			business.entityManagerContainer().beginTransaction(Task.class);
			business.entityManagerContainer().beginTransaction(TaskCompleted.class);
			business.entityManagerContainer().beginTransaction(Read.class);
			business.entityManagerContainer().beginTransaction(ReadCompleted.class);
			business.entityManagerContainer().beginTransaction(Review.class);
			business.entityManagerContainer().beginTransaction(DataItem.class);
			if ((null != title) && (!Objects.equals(title, work.getTitle()))) {
				work.setTitle(title);
			}
			if ((null != serial) && (!Objects.equals(serial, work.getSerial()))) {
				work.setSerial(serial);
			}
			for (Task o : business.entityManagerContainer().list(Task.class, business.task().listWithWork(work))) {
				o.setTitle(work.getTitle());
				o.setSerial(work.getSerial());
			}
			for (TaskCompleted o : business.entityManagerContainer().list(TaskCompleted.class,
					business.taskCompleted().listWithWork(work))) {
				o.setTitle(work.getTitle());
				o.setSerial(work.getSerial());
			}
			for (Read o : business.entityManagerContainer().list(Read.class, business.read().listWithWork(work))) {
				o.setTitle(work.getTitle());
				o.setSerial(work.getSerial());
			}
			for (ReadCompleted o : business.entityManagerContainer().list(ReadCompleted.class,
					business.readCompleted().listWithWork(work))) {
				o.setTitle(work.getTitle());
				o.setSerial(work.getSerial());
			}
			for (Review o : business.entityManagerContainer().list(Review.class,
					business.review().listWithWork(work))) {
				o.setTitle(work.getTitle());
				o.setSerial(work.getSerial());
			}
			for (DataItem o : business.dataItem().listWithJobWithPath(work.getJob())) {
				o.setTitle(work.getTitle());
				o.setSerial(work.getSerial());
			}
			/** 这里必须先提交掉,不然后面的获取会得到不一致的状态 */
			/**
			 * <openjpa-2.4.3-SNAPSHOT-r422266:1777109 nonfatal user error>
			 * org.apache.openjpa.persistence.InvalidStateException: Opera tion
			 * attempted on a deleted instance.
			 */
			business.entityManagerContainer().commit();
		}
	}

	void updateData(Business business, Work work, JsonElement jsonElement, String... paths) throws Exception {
		// WorkDataHelper helper = new
		// WorkDataHelper(business.entityManagerContainer(), work);
		// helper.update(jsonElement, paths);
		// /** 基于前面的原因,这里进行单独提交 */
		// business.entityManagerContainer().commit();
		ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
		List<DataItem> exists = business.dataItem().listWithJobWithPath(work.getJob(), paths);
		if (exists.isEmpty()) {
			throw new Exception(
					"data{job:" + work.getJob() + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
		}
		List<DataItem> currents = converter.disassemble(jsonElement, paths);
		List<DataItem> removes = converter.subtract(exists, currents);
		List<DataItem> adds = converter.subtract(currents, exists);
		if ((!removes.isEmpty()) || (!adds.isEmpty())) {
			business.entityManagerContainer().beginTransaction(DataItem.class);
			business.entityManagerContainer().beginTransaction(DataLobItem.class);
			for (DataItem o : removes) {
				if (o.isLobItem()) {
					/** 删除关联的lobItem */
					DataLobItem lob = business.entityManagerContainer().find(o.getLobItem(), DataLobItem.class);
					if (null != lob) {
						business.entityManagerContainer().remove(lob);
					}
				}
				business.entityManagerContainer().remove(o);
			}
			for (DataItem o : adds) {
				this.fill(o, work);
				if (o.isLobItem()) {
					/** 创建关联的lobItem */
					business.entityManagerContainer().persist(this.concreteDataLobItem(o));
				}
				business.entityManagerContainer().persist(o);
			}
			/** 基于前面的原因,这里进行单独提交 */
			business.entityManagerContainer().commit();
		}
	}

	void createData(Business business, Work work, JsonElement jsonElement, String... paths) throws Exception {
		String[] parentPaths = new String[] { "", "", "", "", "", "", "", "" };
		String[] cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
		for (int i = 0; i < paths.length - 1; i++) {
			parentPaths[i] = paths[i];
			cursorPaths[i] = paths[i];
		}
		cursorPaths[paths.length - 1] = paths[paths.length - 1];
		DataItem parent = business.dataItem().getWithJobWithPath(work.getJob(), parentPaths[0], parentPaths[1],
				parentPaths[2], parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6], parentPaths[7]);
		if (null == parent) {
			throw new Exception("parent not existed.");
		}
		DataItem cursor = business.dataItem().getWithJobWithPath(work.getJob(), cursorPaths[0], cursorPaths[1],
				cursorPaths[2], cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6], cursorPaths[7]);
		ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
		business.entityManagerContainer().beginTransaction(DataItem.class);
		business.entityManagerContainer().beginTransaction(DataLobItem.class);
		if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
			/* 向数组里面添加一个成员对象 */
			Integer index = business.dataItem().getArrayLastIndexWithJobWithPath(work.getJob(), paths);
			/* 新的路径开始 */
			String[] ps = new String[paths.length + 1];
			for (int i = 0; i < paths.length; i++) {
				ps[i] = paths[i];
			}
			ps[paths.length] = Integer.toString(index + 1);
			List<DataItem> adds = converter.disassemble(jsonElement, ps);
			for (DataItem o : adds) {
				this.fill(o, work);
				if (o.isLobItem()) {
					/** 创建关联的lobItem */
					business.entityManagerContainer().persist(this.concreteDataLobItem(o));
				}
				business.entityManagerContainer().persist(o);
			}
		} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
			/* 向parent对象添加一个属性值 */
			List<DataItem> adds = converter.disassemble(jsonElement, paths);
			for (DataItem o : adds) {
				this.fill(o, work);
				if (o.isLobItem()) {
					/** 创建关联的lobItem */
					business.entityManagerContainer().persist(this.concreteDataLobItem(o));
				}
				business.entityManagerContainer().persist(o);
			}
		} else {
			throw new Exception("unexpected post data with work" + work + ".path:" + StringUtils.join(paths, ".")
					+ "json:" + jsonElement);
		}
	}

	void deleteData(Business business, Work work, String... paths) throws Exception {
		List<DataItem> exists = business.dataItem().listWithJobWithPath(work.getJob(), paths);
		if (exists.isEmpty()) {
			throw new Exception(
					"data{job:" + work.getJob() + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
		}
		business.entityManagerContainer().beginTransaction(DataItem.class);
		business.entityManagerContainer().beginTransaction(DataLobItem.class);
		for (DataItem o : exists) {
			if (o.isLobItem()) {
				/** 删除关联的lobItem */
				DataLobItem lob = business.entityManagerContainer().find(o.getLobItem(), DataLobItem.class);
				if (null != lob) {
					business.entityManagerContainer().remove(lob);
				}
			}
			business.entityManagerContainer().remove(o);
		}
		if (paths.length > 0) {
			if (NumberUtils.isNumber(paths[paths.length - 1])) {
				int position = paths.length - 1;
				for (DataItem o : business.dataItem().listWithJobWithPathWithAfterLocation(work.getJob(),
						NumberUtils.toInt(paths[position]), paths)) {
					o.path(Integer.toString(o.pathLocation(position) - 1), position);
				}
			}
		}
	}

	DataLobItem concreteDataLobItem(DataItem o) {
		/** 创建关联的lobItem */
		DataLobItem lob = new DataLobItem();
		lob.setData(o.getStringLobValue());
		lob.setDistributeFactor(o.getDistributeFactor());
		o.setLobItem(lob.getId());
		return lob;
	}

	void fill(DataItem o, Work work) {
		/** 将DateItem与Work放在同一个分区 */
		o.setDistributeFactor(work.getDistributeFactor());
		o.setJob(work.getJob());
		o.setSerial(work.getSerial());
		o.setTitle(work.getTitle());
		o.setApplication(work.getApplication());
		o.setApplicationName(work.getApplicationName());
		o.setApplicationAlias(work.getApplicationAlias());
		o.setProcess(work.getProcess());
		o.setProcessName(work.getProcessName());
		o.setProcessAlias(work.getProcessAlias());
		o.setCreatorCompany(work.getCreatorCompany());
		o.setCreatorDepartment(work.getCreatorDepartment());
		o.setCreatorIdentity(work.getCreatorIdentity());
		o.setCreatorPerson(work.getCreatorPerson());
		o.setStartTime(work.getStartTime());
		o.setStartTimeMonth(work.getStartTimeMonth());
		o.setCompleted(false);
	}
}