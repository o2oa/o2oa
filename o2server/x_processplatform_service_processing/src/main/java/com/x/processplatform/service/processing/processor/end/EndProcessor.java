package com.x.processplatform.service.processing.processor.end;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompletedProperties;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedForm;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.RelatedScript;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.StoreForm;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class EndProcessor extends AbstractEndProcessor {

	private static Logger logger = LoggerFactory.getLogger(EndProcessor.class);

	public EndProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, End end) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.endArrive(aeiObjects.getWork().getActivityToken(), end));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, End end) throws Exception {
		// nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, End end) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.endExecute(aeiObjects.getWork().getActivityToken(), end));
		List<Work> results = new ArrayList<>();

		Work other = aeiObjects.getWorks().stream().filter(o -> o != aeiObjects.getWork())
				.sorted(Comparator.comparing(Work::getCreateTime)).findFirst().orElse(null);

		if (null != other) {
			aeiObjects.getUpdateWorks().add(other);
			aeiObjects.getDeleteWorks().add(aeiObjects.getWork());
			this.mergeTaskCompleted(aeiObjects, aeiObjects.getWork(), other);
			this.mergeRead(aeiObjects, aeiObjects.getWork(), other);
			this.mergeReadCompleted(aeiObjects, aeiObjects.getWork(), other);
			this.mergeReview(aeiObjects, aeiObjects.getWork(), other);
			this.mergeAttachment(aeiObjects, aeiObjects.getWork(), other);
			this.mergeWorkLog(aeiObjects, aeiObjects.getWork(), other);
			this.mergeRecord(aeiObjects, aeiObjects.getWork(), other);
			aeiObjects.getWorkLogs().stream()
					.filter(p -> StringUtils.equals(p.getFromActivityToken(), aeiObjects.getWork().getActivityToken()))
					.forEach(obj -> {
						aeiObjects.getDeleteWorkLogs().add(obj);
					});
		} else {
			WorkCompleted workCompleted = this.createWorkCompleted(aeiObjects, aeiObjects.getWork(), end);
			workCompleted.setAllowRollback(end.getAllowRollback());
			aeiObjects.getCreateWorkCompleteds().add(workCompleted);
			aeiObjects.getTasks().stream().forEach(o -> aeiObjects.getDeleteTasks().add(o));
			aeiObjects.getDocumentVersions().stream().forEach(o -> aeiObjects.getDeleteDocumentVersions().add(o));
			aeiObjects.getTaskCompleteds().stream().forEach(o -> {
				// 已办的完成时间是不需要更新的
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 重新赋值映射字段
				o.copyProjectionFields(workCompleted);
				// 加入到更新队列保证事务开启
				aeiObjects.getUpdateTaskCompleteds().add(o);
			});
			aeiObjects.getReads().stream().forEach(o -> {
				// 待阅的完成时间是不需要更新的
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 重新赋值映射字段
				o.copyProjectionFields(workCompleted);
				// 加入到更新队列保证事务开启
				aeiObjects.getUpdateReads().add(o);
			});
			aeiObjects.getReadCompleteds().stream().forEach(o -> {
				// 已阅的完成时间是不需要更新的
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 重新赋值映射字段
				o.copyProjectionFields(workCompleted);
				// 加入到更新队列保证事务开启
				aeiObjects.getUpdateReadCompleteds().add(o);
			});
			aeiObjects.getRecords().stream().forEach(o -> {
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				aeiObjects.getUpdateRecords().add(o);
			});
			aeiObjects.getReviews().stream().forEach(o -> {
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				o.setCompletedTime(workCompleted.getCompletedTime());
				o.setCompletedTimeMonth(workCompleted.getCompletedTimeMonth());
				// 重新赋值映射字段
				o.copyProjectionFields(workCompleted);
				// 加入到更新队列保证事务开启
				aeiObjects.getUpdateReviews().add(o);
			});
			aeiObjects.getWorkLogs().stream().forEach(o -> {
				o.setSplitting(false);
				o.setSplitToken("");
				o.getProperties().setSplitTokenList(new ArrayList<>());
				o.setSplitValue("");
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 加入到更新队列保证事务开启
				aeiObjects.getUpdateWorkLogs().add(o);
				// 删除未连接的WorkLog
				if (BooleanUtils.isNotTrue(o.getConnected())) {
					aeiObjects.getDeleteWorkLogs().add(o);
				}
			});
			aeiObjects.getAttachments().stream().forEach(o -> {
				o.setCompleted(true);
				o.setWorkCompleted(workCompleted.getId());
				// 加入到更新队列保证事务开启
				aeiObjects.getUpdateAttachments().add(o);
			});
			// 已workCompleted数据为准进行更新
			aeiObjects.getData().setWork(workCompleted);
			aeiObjects.getData().setAttachmentList(aeiObjects.getAttachments());
			aeiObjects.getDeleteWorks().addAll(aeiObjects.getWorks());
			// 删除快照
			aeiObjects.getDeleteSnaps().addAll(aeiObjects.getSnaps());
		}

		return results;
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, End end) throws Exception {
		if (StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterEndScript())
				|| StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterEndScriptText())) {
			aeiObjects.business().element()
					.getCompiledScript(aeiObjects.getWork().getApplication(), end, Business.EVENT_PROCESSAFTEREND)
					.eval(aeiObjects.scriptContext());
		}
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, End end) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes().push(Signal.endInquire(aeiObjects.getWork().getActivityToken(), end));
		return new ArrayList<>();
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, End end) throws Exception {
		// nothing
	}

	/* 根据work和data创建最终保存的workCompleted */
	private WorkCompleted createWorkCompleted(AeiObjects aeiObjects, Work work, End end) throws Exception {
		Date completedTime = new Date();
		Long duration = Config.workTime().betweenMinutes(work.getStartTime(), completedTime);
		WorkCompleted workCompleted = new WorkCompleted(work, completedTime, duration);
		workCompleted.setActivity(end.getId());
		workCompleted.setActivityAlias(end.getAlias());
		workCompleted.setActivityDescription(end.getDescription());
		workCompleted.setActivityName(end.getName());
		if (StringUtils.isNotEmpty(work.getForm())) {
			Form form = (aeiObjects.business().element().get(work.getForm(), Form.class));
			if (null != form) {
				StoreForm storeForm = new StoreForm();
				StoreForm mobileStoreForm = new StoreForm();
				storeForm.setForm(new RelatedForm(form, form.getDataOrMobileData()));
				mobileStoreForm.setForm(new RelatedForm(form, form.getMobileDataOrData()));
				CompletableFuture<Map<String, RelatedForm>> _relatedForm = CompletableFuture.supplyAsync(() -> {
					Map<String, RelatedForm> map = new TreeMap<>();
					try {
						Form _f;
						for (String _id : form.getProperties().getRelatedFormList()) {
							_f = aeiObjects.business().element().get(_id, Form.class);
							if (null != _f) {
								map.put(_id, new RelatedForm(_f, _f.getDataOrMobileData()));
							}
						}
					} catch (Exception e) {
						logger.error(e);
					}
					return map;
				});
				CompletableFuture<Map<String, RelatedScript>> _relatedScript = CompletableFuture.supplyAsync(() -> {
					Map<String, RelatedScript> map = new TreeMap<>();
					try {
						for (Entry<String, String> entry : form.getProperties().getRelatedScriptMap().entrySet()) {
							switch (entry.getValue()) {
							case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
								Script _pp = aeiObjects.business().element().get(entry.getKey(), Script.class);
								if (null != _pp) {
									map.put(entry.getKey(), new RelatedScript(_pp.getId(), _pp.getName(),
											_pp.getAlias(), _pp.getText(), entry.getValue()));
								}
								break;
							case WorkCompletedProperties.RelatedScript.TYPE_CMS:
								com.x.cms.core.entity.element.Script _cms = aeiObjects.business().element()
										.get(entry.getKey(), com.x.cms.core.entity.element.Script.class);
								if (null != _cms) {
									map.put(entry.getKey(), new RelatedScript(_cms.getId(), _cms.getName(),
											_cms.getAlias(), _cms.getText(), entry.getValue()));
								}
								break;
							case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
								com.x.portal.core.entity.Script _portal = aeiObjects.business().element()
										.get(entry.getKey(), com.x.portal.core.entity.Script.class);
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
					return map;
				});
				CompletableFuture<Map<String, RelatedForm>> _relatedFormMobile = CompletableFuture.supplyAsync(() -> {
					Map<String, RelatedForm> map = new TreeMap<>();
					try {
						Form _f;
						for (String _id : form.getProperties().getMobileRelatedFormList()) {
							_f = aeiObjects.business().element().get(_id, Form.class);
							if (null != _f) {
								map.put(_id, new RelatedForm(_f, _f.getMobileDataOrData()));
							}
						}
					} catch (Exception e) {
						logger.error(e);
					}
					return map;
				});
				CompletableFuture<Map<String, RelatedScript>> _relatedScriptMobile = CompletableFuture
						.supplyAsync(() -> {
							Map<String, RelatedScript> map = new TreeMap<>();
							try {
								for (Entry<String, String> entry : form.getProperties().getMobileRelatedScriptMap()
										.entrySet()) {
									switch (entry.getValue()) {
									case WorkCompletedProperties.RelatedScript.TYPE_PROCESSPLATFORM:
										Script _pp = aeiObjects.business().element().get(entry.getKey(), Script.class);
										if (null != _pp) {
											map.put(entry.getKey(), new RelatedScript(_pp.getId(), _pp.getName(),
													_pp.getAlias(), _pp.getText(), entry.getValue()));
										}
										break;
									case WorkCompletedProperties.RelatedScript.TYPE_CMS:
										com.x.cms.core.entity.element.Script _cms = aeiObjects.business().element()
												.get(entry.getKey(), com.x.cms.core.entity.element.Script.class);
										if (null != _cms) {
											map.put(entry.getKey(), new RelatedScript(_cms.getId(), _cms.getName(),
													_cms.getAlias(), _cms.getText(), entry.getValue()));
										}
										break;
									case WorkCompletedProperties.RelatedScript.TYPE_PORTAL:
										com.x.portal.core.entity.Script _portal = aeiObjects.business().element()
												.get(entry.getKey(), com.x.portal.core.entity.Script.class);
										if (null != _portal) {
											map.put(entry.getKey(),
													new RelatedScript(_portal.getId(), _portal.getName(),
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
							return map;
						});
				storeForm.setRelatedFormMap(_relatedForm.get());
				storeForm.setRelatedScriptMap(_relatedScript.get());
				mobileStoreForm.setRelatedFormMap(_relatedFormMobile.get());
				mobileStoreForm.setRelatedScriptMap(_relatedScriptMobile.get());
				workCompleted.getProperties().setStoreForm(storeForm);
				workCompleted.getProperties().setMobileStoreForm(mobileStoreForm);
			}
		}
		return workCompleted;
	}
}