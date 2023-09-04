package com.x.processplatform.service.processing.jaxrs.snap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.DocSign;
import com.x.processplatform.core.entity.content.DocSignScrawl;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.SnapProperties;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.query.core.entity.Item;

abstract class BaseAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

	protected SnapProperties snap(Business business, String job, List<Item> items, List<Work> works, List<Task> tasks,
			List<TaskCompleted> taskCompleteds, List<Read> reads, List<ReadCompleted> readCompleteds,
			List<Review> reviews, List<WorkLog> workLogs, List<Record> records, List<DocumentVersion> documentVersions,
			List<DocSign> docSigns, List<DocSignScrawl> docSignScrawls)
			throws InterruptedException, ExecutionException {
		SnapProperties properties = new SnapProperties();
		properties.setJob(job);
		CompletableFuture.allOf(mergeItem(business, job, properties, items),
				mergeWork(business, job, properties, works), mergeTask(business, job, properties, tasks),
				mergeTaskCompleted(business, job, properties, taskCompleteds),
				mergeRead(business, job, properties, reads),
				mergeReadCompleted(business, job, properties, readCompleteds),
				mergeReview(business, job, properties, reviews), mergeWorkLog(business, job, properties, workLogs),
				mergeRecord(business, job, properties, records),
				// mergeAttachment(business, job, properties, attachments),
				mergeDocumentVersion(business, job, properties, documentVersions),
				mergeDocSign(business, job, properties, docSigns),
				mergeDocSignScrawl(business, job, properties, docSignScrawls)).get();
		if (ListTools.isNotEmpty(works)) {
			properties.setTitle(works.get(0).getTitle());
		}
		return properties;
	}

	protected SnapProperties snap(Business business, String job, List<Item> items, WorkCompleted workCompleted,
			List<TaskCompleted> taskCompleteds, List<Read> reads, List<ReadCompleted> readCompleteds,
			List<Review> reviews, List<WorkLog> workLogs, List<Record> records, List<DocSign> docSigns,
			List<DocSignScrawl> docSignScrawls) throws InterruptedException, ExecutionException {
		SnapProperties properties = new SnapProperties();
		properties.setJob(job);
		properties.setWorkCompleted(workCompleted);
		properties.setTitle(workCompleted.getTitle());
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		futures.add(mergeTaskCompleted(business, job, properties, taskCompleteds));
		futures.add(mergeRead(business, job, properties, reads));
		futures.add(mergeReadCompleted(business, job, properties, readCompleteds));
		futures.add(mergeReview(business, job, properties, reviews));
		futures.add(mergeWorkLog(business, job, properties, workLogs));
		futures.add(mergeRecord(business, job, properties, records));
		// futures.add(mergeAttachment(business, job, properties, attachments));
		futures.add(mergeDocSign(business, job, properties, docSigns));
		futures.add(mergeDocSignScrawl(business, job, properties, docSignScrawls));
		if (BooleanUtils.isNotTrue(workCompleted.getMerged())) {
			futures.add(mergeItem(business, job, properties, items));
		}
		CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).get();

		return properties;
	}

	protected void clean(Business business, List<Item> items, List<Work> works, List<Task> tasks,
			List<TaskCompleted> taskCompleteds, List<Read> reads, List<ReadCompleted> readCompleteds,
			List<Review> reviews, List<WorkLog> workLogs, List<Record> records, List<DocumentVersion> documentVersions,
			List<DocSign> docSigns, List<DocSignScrawl> docSignScrawls)
			throws InterruptedException, ExecutionException {
		CompletableFuture.allOf(deleteItem(business, items), deleteWork(business, works), deleteTask(business, tasks),
				deleteTaskCompleted(business, taskCompleteds), deleteRead(business, reads),
				deleteReadCompleted(business, readCompleteds), deleteReview(business, reviews),
				deleteWorkLog(business, workLogs), deleteRecord(business, records),
				deleteDocumentVersion(business, documentVersions), deleteDocSign(business, docSigns),
				deleteDocSignScrawl(business, docSignScrawls)).get();
	}

	protected void clean(Business business, List<Item> items, WorkCompleted workCompleted,
			List<TaskCompleted> taskCompleteds, List<Read> reads, List<ReadCompleted> readCompleteds,
			List<Review> reviews, List<WorkLog> workLogs, List<Record> records, List<DocSign> docSigns,
			List<DocSignScrawl> docSignScrawls) throws InterruptedException, ExecutionException {
		CompletableFuture.allOf(deleteItem(business, items), deleteWork(business, workCompleted),
				deleteTaskCompleted(business, taskCompleteds), deleteRead(business, reads),
				deleteReadCompleted(business, readCompleteds), deleteReview(business, reviews),
				deleteWorkLog(business, workLogs), deleteRecord(business, records), deleteDocSign(business, docSigns),
				deleteDocSignScrawl(business, docSignScrawls)).get();
	}

	private CompletableFuture<Void> mergeItem(Business business, String job, SnapProperties snapProperties,
			List<Item> items) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<Item> os = business.entityManagerContainer().listEqualAndEqual(Item.class,
						DataItem.bundle_FIELDNAME, job, DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
				DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
				JsonElement jsonElement = converter.assemble(os);
				snapProperties.setData(gson.fromJson(jsonElement, Data.class));
				items.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeWork(Business business, String job, SnapProperties snapProperties,
			List<Work> works) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<Work> os = business.entityManagerContainer().listEqual(Work.class, Work.job_FIELDNAME, job)
						.stream()
						.sorted(Comparator.comparing(Work::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setWorkList(os);
				works.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeTask(Business business, String job, SnapProperties snapProperties,
			List<Task> tasks) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<Task> os = business.entityManagerContainer().listEqual(Task.class, Task.job_FIELDNAME, job)
						.stream()
						.sorted(Comparator.comparing(Task::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setTaskList(os);
				tasks.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeTaskCompleted(Business business, String job, SnapProperties snapProperties,
			List<TaskCompleted> taskCompleteds) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<TaskCompleted> os = business.entityManagerContainer()
						.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, job).stream().sorted(Comparator
								.comparing(TaskCompleted::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setTaskCompletedList(os);
				taskCompleteds.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeRead(Business business, String job, SnapProperties snapProperties,
			List<Read> reads) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<Read> os = business.entityManagerContainer().listEqual(Read.class, Read.job_FIELDNAME, job)
						.stream()
						.sorted(Comparator.comparing(Read::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setReadList(os);
				reads.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeReadCompleted(Business business, String job, SnapProperties snapProperties,
			List<ReadCompleted> readCompleteds) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<ReadCompleted> os = business.entityManagerContainer()
						.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, job).stream().sorted(Comparator
								.comparing(ReadCompleted::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setReadCompletedList(os);
				readCompleteds.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeReview(Business business, String job, SnapProperties snapProperties,
			List<Review> reviews) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<Review> os = business.entityManagerContainer().listEqual(Review.class, Review.job_FIELDNAME, job)
						.stream()
						.sorted(Comparator.comparing(Review::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setReviewList(os);
				reviews.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeWorkLog(Business business, String job, SnapProperties snapProperties,
			List<WorkLog> workLogs) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<WorkLog> os = business.entityManagerContainer()
						.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, job).stream()
						.sorted(Comparator.comparing(WorkLog::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setWorkLogList(os);
				workLogs.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeRecord(Business business, String job, SnapProperties snapProperties,
			List<Record> records) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<Record> os = business.entityManagerContainer().listEqual(Record.class, Record.job_FIELDNAME, job)
						.stream()
						.sorted(Comparator.comparing(Record::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setRecordList(os);
				records.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeDocumentVersion(Business business, String job, SnapProperties snapProperties,
			List<DocumentVersion> documentVersions) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<DocumentVersion> os = business.entityManagerContainer()
						.listEqual(DocumentVersion.class, DocumentVersion.job_FIELDNAME, job).stream().sorted(Comparator
								.comparing(DocumentVersion::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setDocumentVersionList(os);
				documentVersions.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeDocSign(Business business, String job, SnapProperties snapProperties,
			List<DocSign> docSigns) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<DocSign> os = business.entityManagerContainer()
						.listEqual(DocSign.class, DocSign.job_FIELDNAME, job).stream()
						.sorted(Comparator.comparing(DocSign::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setDocSignList(os);
				docSigns.addAll(os);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> mergeDocSignScrawl(Business business, String job, SnapProperties snapProperties,
			List<DocSignScrawl> docSignScrawls) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<DocSignScrawl> os = business.entityManagerContainer()
						.listEqual(DocSignScrawl.class, DocSignScrawl.job_FIELDNAME, job).stream().sorted(Comparator
								.comparing(DocSignScrawl::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.collect(Collectors.toList());
				snapProperties.setDocSignScrawlList(os);
				docSignScrawls.addAll(os);
				for (DocSignScrawl docSignScrawl : os) {
					if (StringUtils.isNotBlank(docSignScrawl.getStorage())) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(DocSignScrawl.class,
								docSignScrawl.getStorage());
						if (null != mapping) {
							byte[] bytes = docSignScrawl.readContent(mapping);
							snapProperties.getAttachmentContentMap().put(docSignScrawl.getId(),
									Base64.encodeBase64URLSafeString(bytes));
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteItem(Business business, List<Item> items) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Item.class);
				for (Item o : items) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteWork(Business business, List<Work> works) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Work.class);
				for (Work o : works) {
					business.entityManagerContainer().remove(o);
					MessageFactory.work_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteWork(Business business, WorkCompleted workCompleted) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(WorkCompleted.class);
				business.entityManagerContainer().remove(workCompleted);
				MessageFactory.workCompleted_delete(workCompleted);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteTask(Business business, List<Task> tasks) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Task.class);
				for (Task o : tasks) {
					business.entityManagerContainer().remove(o);
					MessageFactory.task_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteTaskCompleted(Business business, List<TaskCompleted> taskCompleteds) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(TaskCompleted.class);
				for (TaskCompleted o : taskCompleteds) {
					business.entityManagerContainer().remove(o);
					MessageFactory.taskCompleted_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteRead(Business business, List<Read> reads) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Read.class);
				for (Read o : reads) {
					business.entityManagerContainer().remove(o);
					MessageFactory.read_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteReadCompleted(Business business, List<ReadCompleted> readCompleteds) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(ReadCompleted.class);
				for (ReadCompleted o : readCompleteds) {
					business.entityManagerContainer().remove(o);
					MessageFactory.readCompleted_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteReview(Business business, List<Review> reviews) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Review.class);
				for (Review o : reviews) {
					business.entityManagerContainer().remove(o);
					MessageFactory.review_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteWorkLog(Business business, List<WorkLog> workLogs) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(WorkLog.class);
				for (WorkLog o : workLogs) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteRecord(Business business, List<Record> records) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Record.class);
				for (Record o : records) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteDocumentVersion(Business business, List<DocumentVersion> documentVersions) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(DocumentVersion.class);
				for (DocumentVersion o : documentVersions) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteDocSign(Business business, List<DocSign> docSigns) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(DocSign.class);
				for (DocSign o : docSigns) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> deleteDocSignScrawl(Business business, List<DocSignScrawl> docSignScrawls) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(DocSignScrawl.class);
				for (DocSignScrawl o : docSignScrawls) {
					if (StringUtils.isNotBlank(o.getStorage())) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(DocSignScrawl.class,
								o.getStorage());
						if (null != mapping) {
							o.deleteContent(mapping);
						}
					}
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteItem(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Item.class);
				for (Item o : business.entityManagerContainer().listEqual(Item.class, DataItem.bundle_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteWork(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Work.class);
				for (Work o : business.entityManagerContainer().listEqual(Work.class, Work.job_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
					MessageFactory.work_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteWorkCompleted(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(WorkCompleted.class);
				for (WorkCompleted o : business.entityManagerContainer().listEqual(WorkCompleted.class,
						WorkCompleted.job_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
					MessageFactory.workCompleted_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteTask(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Task.class);
				for (Task o : business.entityManagerContainer().listEqual(Task.class, Task.job_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
					MessageFactory.task_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteTaskCompleted(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(TaskCompleted.class);
				for (TaskCompleted o : business.entityManagerContainer().listEqual(TaskCompleted.class,
						TaskCompleted.job_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
					MessageFactory.taskCompleted_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteRead(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Read.class);
				for (Read o : business.entityManagerContainer().listEqual(Read.class, Read.job_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
					MessageFactory.read_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteReadCompleted(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(ReadCompleted.class);
				for (ReadCompleted o : business.entityManagerContainer().listEqual(ReadCompleted.class,
						ReadCompleted.job_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
					MessageFactory.readCompleted_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteReview(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Review.class);
				for (Review o : business.entityManagerContainer().listEqual(Review.class, Review.job_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
					MessageFactory.review_delete(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteWorkLog(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(WorkLog.class);
				for (WorkLog o : business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME,
						job)) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteRecord(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(Record.class);
				for (Record o : business.entityManagerContainer().listEqual(Record.class, Record.job_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteDocumentVersion(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(DocumentVersion.class);
				for (DocumentVersion o : business.entityManagerContainer().listEqual(DocumentVersion.class,
						DocumentVersion.job_FIELDNAME, job)) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteDocSign(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(DocSign.class);
				for (DocSign o : business.entityManagerContainer().listEqual(DocSign.class, DocSign.job_FIELDNAME,
						job)) {
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Void> deleteDocSignScrawl(Business business, String job) {
		return CompletableFuture.runAsync(() -> {
			try {
				business.entityManagerContainer().beginTransaction(DocSignScrawl.class);
				for (DocSignScrawl o : business.entityManagerContainer().listEqual(DocSignScrawl.class,
						DocSignScrawl.job_FIELDNAME, job)) {
					if (StringUtils.isNotBlank(o.getStorage())) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(DocSignScrawl.class,
								o.getStorage());
						if (null != mapping) {
							o.deleteContent(mapping);
						}
					}
					business.entityManagerContainer().remove(o);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }, ThisApplication.threadPool());
		}, ThisApplication.forkJoinPool());
	}
}
