package com.x.processplatform.service.processing.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedForm;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedScript;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.StoreForm;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.service.processing.Business;
import com.x.query.core.entity.Item;

class ActionMerge extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionMerge.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.fetch(id, WorkCompleted.class,
					ListTools.toList(WorkCompleted.job_FIELDNAME));
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			executorSeed = workCompleted.getJob();
		}

		return ProcessPlatformExecutorFactory.get(executorSeed).submit(new CallableAction(id)).get(300, TimeUnit.SECONDS);
	}

	public static class Wo extends WoId {
	}

	public class CallableAction implements Callable<ActionResult<Wo>> {

		private String id;

		CallableAction(String id) {
			this.id = id;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			WorkCompleted workCompleted = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				workCompleted = emc.find(id, WorkCompleted.class);
				Business business = new Business(emc);
				List<Item> items = new ArrayList<>();
				List<TaskCompleted> taskCompleteds = new ArrayList<>();
				List<ReadCompleted> readCompleteds = new ArrayList<>();
				List<Review> reviews = new ArrayList<>();
				List<WorkLog> workLogs = new ArrayList<>();
				List<Record> records = new ArrayList<>();
				List<Read> reads = new ArrayList<>();
				List<DocumentVersion> documentVersions = new ArrayList<>();
				if (null != workCompleted) {

					Form form = business.element().get(workCompleted.getForm(), Form.class);
					if (null != form) {
						StoreForm storeForm = new StoreForm();
						StoreForm mobileStoreForm = new StoreForm();
						storeForm.setForm(new RelatedForm(form, form.getDataOrMobileData()));
						mobileStoreForm.setForm(new RelatedForm(form, form.getMobileDataOrData()));
						CompletableFuture.allOf(relateForm(business, form, storeForm),
								relateScript(business, form, storeForm), relateFormMobile(business, form, storeForm),
								relateScriptMobile(business, form, storeForm)).get();
						workCompleted.getProperties().setStoreForm(storeForm);
						workCompleted.getProperties().setMobileStoreForm(mobileStoreForm);
					}
					CompletableFuture.allOf(mergeItem(business, workCompleted, items),
							mergeTaskCompleted(business, workCompleted, taskCompleteds),
							mergeReadCompleted(business, workCompleted, readCompleteds),
							mergeReview(business, workCompleted, reviews),
							mergeWorkLog(business, workCompleted, workLogs),
							mergeRecord(business, workCompleted, records), listRead(business, workCompleted, reads),
							listDocumentVersion(business, workCompleted, documentVersions)).get();
					emc.beginTransaction(WorkCompleted.class);
					workCompleted.setMerged(true);
					emc.commit();
					CompletableFuture
							.allOf(deleteItem(business, items), deleteTaskCompleted(business, taskCompleteds),
									deleteReadCompleted(business, readCompleteds), deleteReview(business, reviews),
									deleteWorkLog(business, workLogs), deleteRecord(business, records),
									deleteRead(business, reads), deleteDocumentVersion(business, documentVersions))
							.get();
					emc.commit();
					logger.print("已完成工作合并, id: {}, title:{}, sequence:{}.", workCompleted.getId(),
							workCompleted.getTitle(), workCompleted.getSequence());
				}
			} catch (Exception e) {
				throw new ExceptionCombine(e, id);
			}
			Wo wo = new Wo();
			wo.setId(id);
			ActionResult<Wo> result = new ActionResult<>();
			result.setData(wo);
			return result;
		}

		private CompletableFuture<Void> mergeItem(Business business, WorkCompleted workCompleted, List<Item> items) {
			return CompletableFuture.runAsync(() -> {
				try {
					List<Item> os = business.entityManagerContainer().listEqualAndEqual(Item.class,
							DataItem.bundle_FIELDNAME, workCompleted.getJob(), DataItem.itemCategory_FIELDNAME,
							ItemCategory.pp);
					DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
					JsonElement jsonElement = converter.assemble(os);
					workCompleted.getProperties().setData(gson.fromJson(jsonElement, Data.class));
					items.addAll(os);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> mergeTaskCompleted(Business business, WorkCompleted workCompleted,
				List<TaskCompleted> taskCompleteds) {
			return CompletableFuture.runAsync(() -> {
				try {
					List<TaskCompleted> os = business.entityManagerContainer()
							.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, workCompleted.getJob())
							.stream().sorted(Comparator.comparing(TaskCompleted::getCreateTime,
									Comparator.nullsLast(Date::compareTo)))
							.collect(Collectors.toList());
					workCompleted.getProperties().setTaskCompletedList(os);
					taskCompleteds.addAll(os);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> mergeReadCompleted(Business business, WorkCompleted workCompleted,
				List<ReadCompleted> readCompleteds) {
			return CompletableFuture.runAsync(() -> {
				try {
					List<ReadCompleted> os = business.entityManagerContainer()
							.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, workCompleted.getJob())
							.stream().sorted(Comparator.comparing(ReadCompleted::getCreateTime,
									Comparator.nullsLast(Date::compareTo)))
							.collect(Collectors.toList());
					workCompleted.getProperties().setReadCompletedList(os);
					readCompleteds.addAll(os);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> mergeReview(Business business, WorkCompleted workCompleted,
				List<Review> reviews) {
			return CompletableFuture.runAsync(() -> {
				try {
					List<Review> os = business.entityManagerContainer()
							.listEqual(Review.class, Review.job_FIELDNAME, workCompleted.getJob()).stream()
							.sorted(Comparator.comparing(Review::getCreateTime, Comparator.nullsLast(Date::compareTo)))
							.collect(Collectors.toList());
					workCompleted.getProperties().setReviewList(os);
					reviews.addAll(os);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> mergeWorkLog(Business business, WorkCompleted workCompleted,
				List<WorkLog> workLogs) {
			return CompletableFuture.runAsync(() -> {
				try {
					List<WorkLog> os = business.entityManagerContainer()
							.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, workCompleted.getJob()).stream()
							.sorted(Comparator.comparing(WorkLog::getCreateTime, Comparator.nullsLast(Date::compareTo)))
							.collect(Collectors.toList());
					workCompleted.getProperties().setWorkLogList(os);
					workLogs.addAll(os);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> mergeRecord(Business business, WorkCompleted workCompleted,
				List<Record> records) {
			return CompletableFuture.runAsync(() -> {
				try {
					List<Record> os = business.entityManagerContainer()
							.listEqual(Record.class, Record.job_FIELDNAME, workCompleted.getJob()).stream()
							.sorted(Comparator.comparing(Record::getCreateTime, Comparator.nullsLast(Date::compareTo)))
							.collect(Collectors.toList());
					workCompleted.getProperties().setRecordList(os);
					records.addAll(os);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> listDocumentVersion(Business business, WorkCompleted workCompleted,
				List<DocumentVersion> documentVersions) {
			return CompletableFuture.runAsync(() -> {
				try {
					List<DocumentVersion> os = business.entityManagerContainer().listEqual(DocumentVersion.class,
							DocumentVersion.job_FIELDNAME, workCompleted.getJob());
					documentVersions.addAll(os);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> listRead(Business business, WorkCompleted workCompleted, List<Read> reads) {
			return CompletableFuture.runAsync(() -> {
				try {
					List<Read> os = business.entityManagerContainer()
							.listEqual(Read.class, Read.job_FIELDNAME, workCompleted.getJob()).stream()
							.sorted(Comparator.comparing(Read::getCreateTime, Comparator.nullsLast(Date::compareTo)))
							.collect(Collectors.toList());
					reads.addAll(os);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> deleteItem(Business business, List<Item> items) {
			return CompletableFuture.runAsync(() -> {
				try {
					business.entityManagerContainer().beginTransaction(Item.class);
					for (Item o : items) {
						business.entityManagerContainer().remove(o);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> deleteTaskCompleted(Business business, List<TaskCompleted> taskCompleteds) {
			return CompletableFuture.runAsync(() -> {
				try {
					business.entityManagerContainer().beginTransaction(TaskCompleted.class);
					for (TaskCompleted o : taskCompleteds) {
						business.entityManagerContainer().remove(o);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> deleteReadCompleted(Business business, List<ReadCompleted> readCompleteds) {
			return CompletableFuture.runAsync(() -> {
				try {
					business.entityManagerContainer().beginTransaction(ReadCompleted.class);
					for (ReadCompleted o : readCompleteds) {
						business.entityManagerContainer().remove(o);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> deleteReview(Business business, List<Review> reviews) {
			return CompletableFuture.runAsync(() -> {
				try {
					business.entityManagerContainer().beginTransaction(Review.class);
					for (Review o : reviews) {
						business.entityManagerContainer().remove(o);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> deleteWorkLog(Business business, List<WorkLog> workLogs) {
			return CompletableFuture.runAsync(() -> {
				try {
					business.entityManagerContainer().beginTransaction(WorkLog.class);
					for (WorkLog o : workLogs) {
						business.entityManagerContainer().remove(o);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> deleteRecord(Business business, List<Record> records) {
			return CompletableFuture.runAsync(() -> {
				try {
					business.entityManagerContainer().beginTransaction(Record.class);
					for (Record o : records) {
						business.entityManagerContainer().remove(o);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> deleteDocumentVersion(Business business,
				List<DocumentVersion> documentVersions) {
			return CompletableFuture.runAsync(() -> {
				try {
					business.entityManagerContainer().beginTransaction(DocumentVersion.class);
					for (DocumentVersion o : documentVersions) {
						business.entityManagerContainer().remove(o);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> deleteRead(Business business, List<Read> reads) {
			return CompletableFuture.runAsync(() -> {
				try {
					business.entityManagerContainer().beginTransaction(Read.class);
					for (Read o : reads) {
						business.entityManagerContainer().remove(o);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

		private CompletableFuture<Void> relateForm(Business business, Form form, StoreForm storeForm) {
			return CompletableFuture.runAsync(() -> {
				Map<String, RelatedForm> map = new TreeMap<>();
				try {
					Form _f;
					for (String _id : form.getProperties().getRelatedFormList()) {
						_f = business.element().get(_id, Form.class);
						if (null != _f) {
							map.put(_id, new RelatedForm(_f, _f.getDataOrMobileData()));
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
				storeForm.setRelatedFormMap(map);
			});
		}

		private CompletableFuture<Void> relateScript(Business business, Form form, StoreForm storeForm) {
			return CompletableFuture.runAsync(() -> {
				Map<String, RelatedScript> map = new TreeMap<>();
				try {
					for (Entry<String, String> entry : form.getProperties().getRelatedScriptMap().entrySet()) {
						switch (entry.getValue()) {
						case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
							Script _pp = business.element().get(entry.getKey(), Script.class);
							if (null != _pp) {
								map.put(entry.getKey(), new RelatedScript(_pp.getId(), _pp.getName(), _pp.getAlias(),
										_pp.getText(), entry.getValue()));
							}
							break;
						case WorkCompletedProperties.RelatedScript.TYPE_CMS:
							com.x.cms.core.entity.element.Script _cms = business.element().get(entry.getKey(),
									com.x.cms.core.entity.element.Script.class);
							if (null != _cms) {
								map.put(entry.getKey(), new RelatedScript(_cms.getId(), _cms.getName(), _cms.getAlias(),
										_cms.getText(), entry.getValue()));
							}
							break;
						case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
							com.x.portal.core.entity.Script _portal = business.element().get(entry.getKey(),
									com.x.portal.core.entity.Script.class);
							if (null != _portal) {
								map.put(entry.getKey(), new RelatedScript(_portal.getId(), _portal.getName(),
										_portal.getAlias(), _portal.getText(), entry.getValue()));
							}
							break;
						default:
							break;
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
				storeForm.setRelatedScriptMap(map);
			});
		}

		private CompletableFuture<Void> relateFormMobile(Business business, Form form, StoreForm storeForm) {
			return CompletableFuture.runAsync(() -> {
				Map<String, RelatedForm> map = new TreeMap<>();
				try {
					Form _f;
					for (String _id : form.getProperties().getMobileRelatedFormList()) {
						_f = business.element().get(_id, Form.class);
						if (null != _f) {
							map.put(_id, new RelatedForm(_f, _f.getMobileDataOrData()));
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
				storeForm.setRelatedFormMap(map);
			});
		}

		private CompletableFuture<Void> relateScriptMobile(Business business, Form form, StoreForm storeForm) {
			return CompletableFuture.runAsync(() -> {
				Map<String, RelatedScript> map = new TreeMap<>();
				try {
					for (Entry<String, String> entry : form.getProperties().getMobileRelatedScriptMap().entrySet()) {
						switch (entry.getValue()) {
						case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
							Script _pp = business.element().get(entry.getKey(), Script.class);
							if (null != _pp) {
								map.put(entry.getKey(), new RelatedScript(_pp.getId(), _pp.getName(), _pp.getAlias(),
										_pp.getText(), entry.getValue()));
							}
							break;
						case WorkCompletedProperties.RelatedScript.TYPE_CMS:
							com.x.cms.core.entity.element.Script _cms = business.element().get(entry.getKey(),
									com.x.cms.core.entity.element.Script.class);
							if (null != _cms) {
								map.put(entry.getKey(), new RelatedScript(_cms.getId(), _cms.getName(), _cms.getAlias(),
										_cms.getText(), entry.getValue()));
							}
							break;
						case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
							com.x.portal.core.entity.Script _portal = business.element().get(entry.getKey(),
									com.x.portal.core.entity.Script.class);
							if (null != _portal) {
								map.put(entry.getKey(), new RelatedScript(_portal.getId(), _portal.getName(),
										_portal.getAlias(), _portal.getText(), entry.getValue()));
							}
							break;
						default:
							break;
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
				storeForm.setRelatedScriptMap(map);
			});
		}
	}

}