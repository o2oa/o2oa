package com.x.processplatform.service.processing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ProcessPlatform.ArchiveHadoop;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.message.Event;
import com.x.processplatform.core.entity.message.Event_;
import com.x.query.core.entity.Item;

public class ArchiveHadoopQueue extends AbstractQueue<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveHadoopQueue.class);

	private static final String SYSTEM_PROPERTY_HADOOP_USER_NAME = "HADOOP_USER_NAME";

	private static final int RETRYMINUTES = 60;
	private static final int THRESHOLDMINUTES = 60 * 24 * 3;

	private static final String FILENAME_WORKCOMPLETED = "workCompleted";
	private static final String FILENAME_DATA = "data";
	private static final String FILENAME_TASKCOMPLETEDS = "taskCompleteds";
	private static final String FILENAME_READCOMPLETEDS = "readCompleteds";
	private static final String FILENAME_REVIEWS = "reviews";
	private static final String FILENAME_RECORDS = "records";
	private static final String FILENAME_WORKLOGS = "workLogs";
	private static final String FILENAME_ATTACHMENTS = "attachments";
	private static final String FILENAME_ATTACHMENT_PREFIX = "attachment_";

	private final Gson gson = XGsonBuilder.compactInstance();

	private final DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);

	protected void execute(String id) throws Exception {
		if (StringUtils.isNotEmpty(id)) {
			archive(id);
		}
		List<String> ids = this.checkOverstay();
		if (!ids.isEmpty()) {
			for (String s : ids) {
				archive(s);
			}
			clean();
		}
	}

	private boolean archive(String id) throws Exception {
		Event event = exist(id);
		if ((null != event) && StringUtils.equals(event.getType(), Event.EVENTTYPE_ARCHIVEHADOOP)) {
			if (transfer(event)) {
				success(id);
			} else {
				failure(id);
				LOGGER.warn("归档到Hadoop失败:{}.", () -> id);
			}
		}
		return false;
	}

	private boolean transfer(Event event) throws Exception {
		WorkCompleted workCompleted = null;
		List<Item> itemList = null;
		List<TaskCompleted> taskCompletedList = null;
		List<ReadCompleted> readCompletedList = null;
		List<Review> reviewList = null;
		List<WorkLog> workLogList = null;
		List<Record> recordList = null;
		List<Attachment> attachmentList = null;
		try {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, event.getJob());
				Business business = new Business(emc);
				if (null != workCompleted) {
					itemList = business.entityManagerContainer().listEqualAndEqual(Item.class,
							DataItem.bundle_FIELDNAME, workCompleted.getJob(), DataItem.itemCategory_FIELDNAME,
							ItemCategory.pp);
					taskCompletedList = this.listTaskCompleted(business, workCompleted.getJob());
					readCompletedList = this.listReadCompleted(business, workCompleted.getJob());
					reviewList = this.listReview(business, workCompleted.getJob());
					workLogList = this.listWorkLog(business, workCompleted.getJob());
					recordList = this.listRecord(business, workCompleted.getJob());
					attachmentList = this.listAttachment(business, workCompleted.getJob());
				}
			}
			if (null != workCompleted) {
				ArchiveHadoop archiveHadoop = Config.processPlatform().getArchiveHadoop();
				org.apache.hadoop.fs.Path dir = dir(archiveHadoop, workCompleted);
				try (FileSystem fileSystem = FileSystem.get(configuration(archiveHadoop))) {
					if (fileSystem.exists(dir)) {
						fileSystem.delete(dir, true);
					}
					transferWorkCompleted(fileSystem, dir, workCompleted);
					transferData(fileSystem, dir, itemList);
					transferTaskCompleteds(fileSystem, dir, taskCompletedList);
					transferReadCompleteds(fileSystem, dir, readCompletedList);
					transferReviews(fileSystem, dir, reviewList);
					transferRecords(fileSystem, dir, recordList);
					transferWorkLogs(fileSystem, dir, workLogList);
					transferAttachments(fileSystem, dir, attachmentList);
				}
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	private void transferWorkCompleted(FileSystem fileSystem, Path dir, WorkCompleted workCompleted)
			throws IOException {
		Path path = new Path(dir, FILENAME_WORKCOMPLETED);
		try (FSDataOutputStream out = fileSystem.create(path);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				BufferedWriter bufferedOutputStreamWriter = new BufferedWriter(outputStreamWriter)) {
			bufferedOutputStreamWriter.write(this.gson.toJson(workCompleted));
		}
	}

	private void transferData(FileSystem fileSystem, Path dir, List<Item> itemList) throws IOException {
		Path path = new Path(dir, FILENAME_DATA);
		try (FSDataOutputStream out = fileSystem.create(path);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				BufferedWriter bufferedOutputStreamWriter = new BufferedWriter(outputStreamWriter)) {
			bufferedOutputStreamWriter.write(this.gson.toJson(converter.assemble(itemList)));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void transferTaskCompleteds(FileSystem fileSystem, Path dir, List<TaskCompleted> taskCompletedList)
			throws IOException {
		Path path = new Path(dir, FILENAME_TASKCOMPLETEDS);
		try (FSDataOutputStream out = fileSystem.create(path);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				BufferedWriter bufferedOutputStreamWriter = new BufferedWriter(outputStreamWriter)) {
			for (int i = 0; i < taskCompletedList.size(); i++) {
				if (i > 0) {
					bufferedOutputStreamWriter.write(StringTools.LF);
				}
				bufferedOutputStreamWriter.write(this.gson.toJson(taskCompletedList.get(i)));
			}
		}
	}

	private void transferReadCompleteds(FileSystem fileSystem, Path dir, List<ReadCompleted> readCompletedList)
			throws IOException {
		Path path = new Path(dir, FILENAME_READCOMPLETEDS);
		try (FSDataOutputStream out = fileSystem.create(path);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				BufferedWriter bufferedOutputStreamWriter = new BufferedWriter(outputStreamWriter)) {
			for (int i = 0; i < readCompletedList.size(); i++) {
				if (i > 0) {
					bufferedOutputStreamWriter.write(StringTools.LF);
				}
				bufferedOutputStreamWriter.write(this.gson.toJson(readCompletedList.get(i)));
			}
		}
	}

	private void transferReviews(FileSystem fileSystem, Path dir, List<Review> reviewList) throws IOException {
		Path path = new Path(dir, FILENAME_REVIEWS);
		try (FSDataOutputStream out = fileSystem.create(path);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				BufferedWriter bufferedOutputStreamWriter = new BufferedWriter(outputStreamWriter)) {
			for (int i = 0; i < reviewList.size(); i++) {
				if (i > 0) {
					bufferedOutputStreamWriter.write(StringTools.LF);
				}
				bufferedOutputStreamWriter.write(this.gson.toJson(reviewList.get(i)));
			}
		}
	}

	private void transferRecords(FileSystem fileSystem, Path dir, List<Record> recordList) throws IOException {
		Path path = new Path(dir, FILENAME_RECORDS);
		try (FSDataOutputStream out = fileSystem.create(path);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				BufferedWriter bufferedOutputStreamWriter = new BufferedWriter(outputStreamWriter)) {
			for (int i = 0; i < recordList.size(); i++) {
				if (i > 0) {
					bufferedOutputStreamWriter.write(StringTools.LF);
				}
				bufferedOutputStreamWriter.write(this.gson.toJson(recordList.get(i)));
			}
		}
	}

	private void transferWorkLogs(FileSystem fileSystem, Path dir, List<WorkLog> workLogList) throws IOException {
		Path path = new Path(dir, FILENAME_WORKLOGS);
		try (FSDataOutputStream out = fileSystem.create(path);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				BufferedWriter bufferedOutputStreamWriter = new BufferedWriter(outputStreamWriter)) {
			for (int i = 0; i < workLogList.size(); i++) {
				if (i > 0) {
					bufferedOutputStreamWriter.write(StringTools.LF);
				}
				bufferedOutputStreamWriter.write(this.gson.toJson(workLogList.get(i)));
			}
		}
	}

	private void transferAttachments(FileSystem fileSystem, Path dir, List<Attachment> attachmentList)
			throws Exception {
		Path path = new Path(dir, FILENAME_ATTACHMENTS);
		try (FSDataOutputStream out = fileSystem.create(path);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, StandardCharsets.UTF_8);
				BufferedWriter bufferedOutputStreamWriter = new BufferedWriter(outputStreamWriter)) {
			for (int i = 0; i < attachmentList.size(); i++) {
				if (i > 0) {
					bufferedOutputStreamWriter.write(StringTools.LF);
				}
				bufferedOutputStreamWriter.write(this.gson.toJson(attachmentList.get(i)));
			}
		}
		for (Attachment attachment : attachmentList) {
			Path attachmentPath = new Path(dir, FILENAME_ATTACHMENT_PREFIX + attachment.getId());
			try (FSDataOutputStream out = fileSystem.create(attachmentPath)) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						attachment.getStorage());
				if (null != mapping) {
					byte[] bytes = attachment.readContent(mapping);
					IOUtils.write(bytes, out);
				}
			}
		}
	}

	private org.apache.hadoop.conf.Configuration configuration(ArchiveHadoop archiveHadoop) {
		if (StringUtils.isNotEmpty(archiveHadoop.getUsername())) {
			System.setProperty(SYSTEM_PROPERTY_HADOOP_USER_NAME, archiveHadoop.getUsername());
		}
		org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
		configuration.set(FileSystem.FS_DEFAULT_NAME_KEY, archiveHadoop.getFsDefaultFS());
		return configuration;
	}

	private org.apache.hadoop.fs.Path dir(ArchiveHadoop archiveHadoop, WorkCompleted workCompleted) {
		org.apache.hadoop.fs.Path path;
		if (StringUtils.isEmpty(archiveHadoop.getPath())) {
			path = new org.apache.hadoop.fs.Path(Path.SEPARATOR);
		} else if (StringUtils.startsWith(archiveHadoop.getPath(), Path.SEPARATOR)) {
			path = new org.apache.hadoop.fs.Path(archiveHadoop.getPath());
		} else {
			path = new org.apache.hadoop.fs.Path(Path.SEPARATOR + archiveHadoop.getPath());
		}
		path = new org.apache.hadoop.fs.Path(path, new org.apache.hadoop.fs.Path(workCompleted.getApplication()));
		path = new org.apache.hadoop.fs.Path(path, new org.apache.hadoop.fs.Path(workCompleted.getProcess()));
		String id = workCompleted.getId();
		path = new org.apache.hadoop.fs.Path(path, new org.apache.hadoop.fs.Path(id.substring(0, 2)));
		path = new org.apache.hadoop.fs.Path(path, new org.apache.hadoop.fs.Path(id.substring(2, 4)));
		path = new org.apache.hadoop.fs.Path(path, new org.apache.hadoop.fs.Path(id));
		return path;
	}

	private List<TaskCompleted> listTaskCompleted(Business business, String job) throws Exception {
		return business.entityManagerContainer().listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, job);
	}

	private List<ReadCompleted> listReadCompleted(Business business, String job) throws Exception {
		return business.entityManagerContainer().listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, job);
	}

	private List<Review> listReview(Business business, String job) throws Exception {
		return business.entityManagerContainer().listEqual(Review.class, Review.job_FIELDNAME, job);
	}

	private List<Record> listRecord(Business business, String job) throws Exception {
		return business.entityManagerContainer().listEqual(Record.class, Review.job_FIELDNAME, job);
	}

	private List<WorkLog> listWorkLog(Business business, String job) throws Exception {
		return business.entityManagerContainer().listEqual(WorkLog.class, Review.job_FIELDNAME, job);
	}

	private List<Attachment> listAttachment(Business business, String job) throws Exception {
		return business.entityManagerContainer().listEqual(Attachment.class, ReadCompleted.job_FIELDNAME, job);
	}

	private Event exist(String id) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Event.class);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
	}

	private void success(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Event event = emc.find(id, Event.class);
			if (null != event) {
				emc.beginTransaction(Event.class);
				emc.remove(event);
				emc.commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void failure(String id) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Event event = emc.find(id, Event.class);
			if (null != event) {
				emc.beginTransaction(Event.class);
				Integer failure = event.getFailure();
				failure = (failure == null) ? 1 : failure + 1;
				event.setFailure(failure);
				emc.commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private List<String> checkOverstay() throws Exception {
		List<String> list = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Event.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Event> root = cq.from(Event.class);
			Predicate p = cb.equal(root.get(Event_.type), Event.EVENTTYPE_ARCHIVEHADOOP);
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(JpaObject_.updateTime),
					DateUtils.addMinutes(new Date(), -RETRYMINUTES)));
			list.addAll(em.createQuery(cq.select(root.get(Event_.id)).where(p)).setMaxResults(100).getResultList());
		}
		if (!list.isEmpty()) {
			LOGGER.info("查找到 {} 条处理失败的归档到hadoop事件.", list::size);
		}
		return list;
	}

	private void clean() throws Exception {
		List<String> list = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Event.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Event> root = cq.from(Event.class);
			Predicate p = cb.equal(root.get(Event_.type), Event.EVENTTYPE_ARCHIVEHADOOP);
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(JpaObject_.createTime),
					DateUtils.addDays(new Date(), -THRESHOLDMINUTES)));
			list.addAll(em.createQuery(cq.select(root.get(Event_.id)).where(p)).setMaxResults(100).getResultList());
			if (!list.isEmpty()) {
				emc.beginTransaction(Event.class);
				for (String id : list) {
					Event event = emc.find(id, Event.class);
					if (null != event) {
						emc.remove(event);
					}
				}
				emc.commit();
			}
		}
		if (!list.isEmpty()) {
			LOGGER.info("删除 {} 条超期的归档到hadoop事件.", list::size);
		}
	}
}