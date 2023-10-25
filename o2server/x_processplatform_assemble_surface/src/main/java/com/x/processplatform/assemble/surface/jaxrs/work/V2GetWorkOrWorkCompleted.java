package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Identity;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.ManualMode;
import com.x.processplatform.core.entity.element.Route;
import com.x.query.core.entity.Item;

class V2GetWorkOrWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2GetWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Work work = null;
		WorkCompleted workCompleted = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			work = emc.find(workOrWorkCompleted, Work.class);
			if (null == work) {
				workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
			}
		}

		CompletableFuture<Boolean> checkControlFuture = this.checkControlVisitFuture(effectivePerson,
				workOrWorkCompleted);

		if (null != work) {
			CompletableFuture<Void> workJsonFuture = this.workJsonFuture(work, wo);
			CompletableFuture<Void> activityRouteFuture = this.activityRouteFuture(work, wo);
			CompletableFuture<Void> dataFuture = this.dataFuture(work, wo);
			CompletableFuture<Void> taskFuture = this.taskFuture(effectivePerson, work.getJob(), work.getId(), wo);
			CompletableFuture<Void> readFuture = this.readFuture(effectivePerson, work.getJob(), wo);
			CompletableFuture<Void> creatorIdentityFuture = this.creatorIdentityFuture(work.getCreatorIdentity(), wo);
			CompletableFuture<Void> creatorPersonFuture = this.creatorPersonFuture(work.getCreatorPerson(), wo);
			CompletableFuture<Void> creatorUnitFuture = this.creatorUnitFuture(work.getCreatorUnit(), wo);
			CompletableFuture<Void> attachmentFuture = this.attachmentFuture(effectivePerson, work.getJob(), wo);
			CompletableFuture<Void> recordFuture = this.recordFuture(effectivePerson, work.getJob(), wo);
			workJsonFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			activityRouteFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			dataFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			taskFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			readFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			creatorIdentityFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			creatorPersonFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			creatorUnitFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			attachmentFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			recordFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			for (WoTask woTask : wo.getTaskList()) {
				wo.getRecordList().add(taskToRecord(woTask));
			}
			jobView(effectivePerson, work);
		} else if (null != workCompleted) {
			CompletableFuture<Void> workCompletedJsonFuture = this.workCompletedJsonFuture(workCompleted, wo);
			CompletableFuture<Void> workCompletedDataFuture = this.workCompletedDataFuture(workCompleted, wo);
			CompletableFuture<Void> readFuture = readFuture(effectivePerson, workCompleted.getJob(), wo);
			CompletableFuture<Void> creatorIdentityFuture = creatorIdentityFuture(workCompleted.getCreatorIdentity(),
					wo);
			CompletableFuture<Void> creatorPersonFuture = creatorPersonFuture(workCompleted.getCreatorPerson(), wo);
			CompletableFuture<Void> creatorUnitFuture = creatorUnitFuture(workCompleted.getCreatorUnit(), wo);
			CompletableFuture<Void> attachmentFuture = attachmentFuture(effectivePerson, workCompleted.getJob(), wo);
			CompletableFuture<Void> workCompletedRecordFuture = this.workCompletedRecordFuture(effectivePerson,
					workCompleted, wo);
			workCompletedJsonFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			workCompletedDataFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			readFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			creatorIdentityFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			creatorPersonFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			creatorUnitFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			attachmentFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
			workCompletedRecordFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		}

		if (BooleanUtils
				.isFalse(checkControlFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS))) {
			throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
		}

		result.setData(wo);
		return result;
	}

	/**
	 * 通过接口标志待办待阅已读,viewTime标志为当前时间
	 * 
	 * @param effectivePerson
	 * @param work
	 * @throws Exception
	 */
	private void jobView(EffectivePerson effectivePerson, Work work) throws Exception {
		ThisApplication.context().applications()
				.getQuery(
						x_processplatform_service_processing.class, Applications.joinQueryUri("job", "v2",
								work.getJob(), "person", effectivePerson.getDistinguishedName(), "view"),
						work.getJob());
	}

	private WoRecord taskToRecord(WoTask woTask) {
		WoRecord o = new WoRecord();
		// id必须设置为空,否则会影响每次输出的内容都不同,那么会导致etag每次不同.
		o.setId("");
		// order设置为task的最后修改时间,否则会影响每次输出的内容都不同,那么会导致etag每次不同.
		o.setOrder(woTask.getUpdateTime().getTime());
		// recordTime设置为task的最后修改时间,否则会影响每次输出的内容都不同,那么会导致etag每次不同.
		o.setRecordTime(woTask.getUpdateTime());
		o.setType(Record.TYPE_CURRENTTASK);
		o.setFromActivity(woTask.getActivity());
		o.setFromActivityAlias(woTask.getActivityAlias());
		o.setFromActivityName(woTask.getActivityName());
		o.setFromActivityToken(woTask.getActivityToken());
		o.setFromActivityType(woTask.getActivityType());
		o.setPerson(woTask.getPerson());
		o.setIdentity(o.getIdentity());
		o.setUnit(woTask.getUnit());
		o.getProperties().setStartTime(woTask.getStartTime());
		o.getProperties().setEmpowerFromIdentity(woTask.getEmpowerFromIdentity());
		return o;
	}

	private CompletableFuture<Void> dataFuture(Work work, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<Item> list = emc.listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME, work.getJob(),
						DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
				if (!list.isEmpty()) {
					JsonElement jsonElement = itemConverter.assemble(list);
					// 必须是Object对象
					if (jsonElement.isJsonObject()) {
						wo.setData(gson.fromJson(jsonElement, Data.class));
					}
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> taskFuture(EffectivePerson effectivePerson, String job, String workId, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				wo.setTaskList(WoTask.copier.copy(emc.listEqual(Task.class, Task.job_FIELDNAME, job)));
				wo.setCurrentTaskIndex(ListUtils.indexOf(wo.getTaskList(),
						e -> effectivePerson.isPerson(e.getPerson()) && (StringUtils.equals(e.getWork(), workId))));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> attachmentFuture(EffectivePerson effectivePerson, String job, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
				List<String> units = business.organization().unit().listWithPerson(effectivePerson);
				List<WoAttachment> wos = new ArrayList<>();
				for (Attachment attachment : business.entityManagerContainer().listEqual(Attachment.class,
						Attachment.job_FIELDNAME, job)) {
					boolean canControl = attachmentControl(attachment, effectivePerson, identities, units);
					boolean canEdit = attachmentEdit(attachment, effectivePerson, identities, units) || canControl;
					boolean canRead = attachmentRead(attachment, effectivePerson, identities, units) || canEdit;
					WoAttachment woAttachment = WoAttachment.copier.copy(attachment);
					if (canRead) {
						woAttachment.getControl().setAllowRead(true);
						woAttachment.getControl().setAllowEdit(canEdit);
						woAttachment.getControl().setAllowControl(canControl);
						wos.add(woAttachment);
					}
				}
				wos = wos.stream()
						.sorted(Comparator
								.comparing(WoAttachment::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
								.thenComparing(Comparator.comparing(WoAttachment::getCreateTime,
										Comparator.nullsLast(Date::compareTo))))
						.collect(Collectors.toList());
				wo.setAttachmentList(wos);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> readFuture(EffectivePerson effectivePerson, String job, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				wo.setReadList(WoRead.copier.copy(emc.listEqual(Read.class, Read.job_FIELDNAME, job)));
				wo.setCurrentReadIndex(
						ListUtils.indexOf(wo.getReadList(), e -> effectivePerson.isPerson(e.getPerson())));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> workJsonFuture(Work work, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setWork(gson.toJsonTree(WoWork.copier.copy(work)));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> activityRouteFuture(Work work, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			WoActivity woActivity = new WoActivity();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Activity activity = business.getActivity(work);
				if (null != activity) {
					activity.copyTo(woActivity);
					wo.setActivity(woActivity);
					if (Objects.equals(ActivityType.manual, activity.getActivityType())) {
						wo.setRouteList(WoRoute.copier.copy(business.route().pick(((Manual) activity).getRouteList())));
					}
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> creatorIdentityFuture(String creatorIdentity, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				wo.setCreatorIdentity(business.organization().identity().getObject(creatorIdentity));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> creatorPersonFuture(String creatorPerson, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				wo.setCreatorPerson(business.organization().person().getObject(creatorPerson));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> creatorUnitFuture(String creatorUnit, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				wo.setCreatorUnit(business.organization().unit().getObject(creatorUnit));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> workCompletedJsonFuture(WorkCompleted workCompleted, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setWork(gson.toJsonTree(WoWorkCompleted.copier.copy(workCompleted)));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> recordFuture(EffectivePerson effectivePerson, String job, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				wo.setRecordList(emc.listEqual(Record.class, Record.job_FIELDNAME, job).stream()
						.sorted(Comparator.comparing(Record::getOrder)).map(WoRecord.copier::copy)
						.collect(Collectors.toList()));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> workCompletedRecordFuture(EffectivePerson effectivePerson,
			WorkCompleted workCompleted, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				if (ListTools.isNotEmpty(workCompleted.getRecordList())) {
					wo.setRecordList(WoRecord.copier.copy(workCompleted.getRecordList()).stream()
							.sorted(Comparator.comparing(WoRecord::getOrder)).collect(Collectors.toList()));
				} else {
					wo.setRecordList(emc.listEqual(Record.class, Record.job_FIELDNAME, workCompleted.getJob()).stream()
							.sorted(Comparator.comparing(Record::getOrder)).map(WoRecord.copier::copy)
							.collect(Collectors.toList()));
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Void> workCompletedDataFuture(WorkCompleted workCompleted, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			if (BooleanUtils.isTrue(workCompleted.getMerged())) {
				wo.setData(workCompleted.getData());
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					List<Item> list = emc.listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME,
							workCompleted.getJob(), DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
					if (!list.isEmpty()) {
						JsonElement jsonElement = itemConverter.assemble(list);
						// 必须是Object对象
						if (jsonElement.isJsonObject()) {
							wo.setData(gson.fromJson(jsonElement, Data.class));
						}
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		}, ThisApplication.forkJoinPool());
	}

	private boolean attachmentRead(Attachment attachment, EffectivePerson effectivePerson, List<String> identities,
			List<String> units) throws Exception {
		boolean value = false;
		if (effectivePerson.isManager()) {
			value = true;
		} else if (effectivePerson.isPerson(attachment.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(attachment.getReadIdentityList())
				&& ListTools.isEmpty(attachment.getReadUnitList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, attachment.getReadIdentityList())
					|| ListTools.containsAny(units, attachment.getReadUnitList())) {
				value = true;
			}
		}
		return value;
	}

	private boolean attachmentEdit(Attachment attachment, EffectivePerson effectivePerson, List<String> identities,
			List<String> units) throws Exception {
		boolean value = false;
		if (effectivePerson.isManager()) {
			value = true;
		} else if (effectivePerson.isPerson(attachment.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(attachment.getEditIdentityList())
				&& ListTools.isEmpty(attachment.getEditUnitList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, attachment.getEditIdentityList())
					|| ListTools.containsAny(units, attachment.getEditUnitList())) {
				value = true;
			}
		}
		return value;
	}

	private boolean attachmentControl(Attachment attachment, EffectivePerson effectivePerson, List<String> identities,
			List<String> units) throws Exception {
		boolean value = false;
		if (effectivePerson.isManager()) {
			value = true;
		} else if (effectivePerson.isPerson(attachment.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(attachment.getControllerUnitList())
				&& ListTools.isEmpty(attachment.getControllerIdentityList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, attachment.getControllerIdentityList())
					|| ListTools.containsAny(units, attachment.getControllerUnitList())) {
				value = true;
			}
		}
		return value;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -2651851022553574012L;
		// work和workCompleted都有
		private JsonElement work;
		// work和workCompleted都有
		private Data data;
		// work和workCompleted都有
		private List<WoRead> readList;
		// work和workCompleted都有
		private Integer currentReadIndex = -1;

		// work和workCompleted都有
		private Identity creatorIdentity;

		// work和workCompleted都有
		private Person creatorPerson;

		// work和workCompleted都有
		private Unit creatorUnit;

		// work和workCompleted都有
		private List<WoAttachment> attachmentList;

		// work和workCompleted都有,需要先行初始化,因为record可能为空
		private List<WoRecord> recordList = new ArrayList<>();

		// 只有work有
		private WoActivity activity;
		// 只有work有
		private List<WoTask> taskList;
		// 只有work有
		private Integer currentTaskIndex = -1;
		// 只有work有
		private List<WoRoute> routeList;

		public JsonElement getWork() {
			return work;
		}

		public void setWork(JsonElement work) {
			this.work = work;
		}

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

		public Integer getCurrentReadIndex() {
			return currentReadIndex;
		}

		public void setCurrentReadIndex(Integer currentReadIndex) {
			this.currentReadIndex = currentReadIndex;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public Integer getCurrentTaskIndex() {
			return currentTaskIndex;
		}

		public void setCurrentTaskIndex(Integer currentTaskIndex) {
			this.currentTaskIndex = currentTaskIndex;
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public WoActivity getActivity() {
			return activity;
		}

		public void setActivity(WoActivity activity) {
			this.activity = activity;
		}

		public Person getCreatorPerson() {
			return creatorPerson;
		}

		public void setCreatorPerson(Person creatorPerson) {
			this.creatorPerson = creatorPerson;
		}

		public Identity getCreatorIdentity() {
			return creatorIdentity;
		}

		public void setCreatorIdentity(Identity creatorIdentity) {
			this.creatorIdentity = creatorIdentity;
		}

		public Unit getCreatorUnit() {
			return creatorUnit;
		}

		public void setCreatorUnit(Unit creatorUnit) {
			this.creatorUnit = creatorUnit;
		}

		public List<WoRoute> getRouteList() {
			return routeList;
		}

		public void setRouteList(List<WoRoute> routeList) {
			this.routeList = routeList;
		}

		public List<WoAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WoAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

		public List<WoRecord> getRecordList() {
			return recordList;
		}

		public void setRecordList(List<WoRecord> recordList) {
			this.recordList = recordList;
		}

	}

	public static class WoWork extends Work {

		private static final long serialVersionUID = 5244996549744746585L;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class, null,
				JpaObject.FieldsInvisibleIncludeProperites);

	}

	public static class WoWorkCompleted extends WorkCompleted {

		private static final long serialVersionUID = -1772642962691214007L;

		static WrapCopier<WorkCompleted, WoWorkCompleted> copier = WrapCopierFactory.wo(WorkCompleted.class,
				WoWorkCompleted.class, null, JpaObject.FieldsInvisibleIncludeProperites);
	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 5244996549744746585L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = 5244996549744746585L;

		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoActivity extends GsonPropertyObject {

		private static final long serialVersionUID = 1L;

		static WrapCopier<Activity, WoActivity> copier = WrapCopierFactory.wo(Activity.class, WoActivity.class,
				JpaObject.singularAttributeField(Activity.class, true, true),
				JpaObject.FieldsInvisibleIncludeProperites);

		private String id;

		private String name;

		private String description;

		private String alias;

		private String position;

		private String resetRange;

		private Integer resetCount;

		private Boolean allowReset;

		private Boolean processingTaskOnceUnderSamePerson;

		public Boolean getProcessingTaskOnceUnderSamePerson() {
			return processingTaskOnceUnderSamePerson;
		}

		public void setProcessingTaskOnceUnderSamePerson(Boolean processingTaskOnceUnderSamePerson) {
			this.processingTaskOnceUnderSamePerson = processingTaskOnceUnderSamePerson;
		}

		private ManualMode manualMode;

		private JsonElement customData;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getResetRange() {
			return resetRange;
		}

		public void setResetRange(String resetRange) {
			this.resetRange = resetRange;
		}

		public Integer getResetCount() {
			return resetCount;
		}

		public void setResetCount(Integer resetCount) {
			this.resetCount = resetCount;
		}

		public Boolean getAllowReset() {
			return allowReset;
		}

		public void setAllowReset(Boolean allowReset) {
			this.allowReset = allowReset;
		}

		public ManualMode getManualMode() {
			return manualMode;
		}

		public void setManualMode(ManualMode manualMode) {
			this.manualMode = manualMode;
		}

		public JsonElement getCustomData() {
			return customData;
		}

		public void setCustomData(JsonElement customData) {
			this.customData = customData;
		}

	}

	public static class WoRoute extends Route {

		private static final long serialVersionUID = 556378904185283486L;

		static WrapCopier<Route, WoRoute> copier = WrapCopierFactory.wo(Route.class, WoRoute.class, null,
				ListTools.toList(Route.createTime_FIELDNAME, Route.edition_FIELDNAME, Route.position_FIELDNAME,
						Route.process_FIELDNAME, Route.updateTime_FIELDNAME, Route.track_FIELDNAME));
	}

	public static class WoAttachment extends Attachment {

		private static final long serialVersionUID = -5323646346508661416L;

		static WrapCopier<Attachment, WoAttachment> copier = WrapCopierFactory.wo(Attachment.class, WoAttachment.class,
				null, JpaObject.FieldsInvisibleIncludeProperites);

		private WoAttachmentControl control = new WoAttachmentControl();

		public WoAttachmentControl getControl() {
			return control;
		}

		public void setControl(WoAttachmentControl control) {
			this.control = control;
		}
	}

	public static class WoAttachmentControl extends GsonPropertyObject {

		private static final long serialVersionUID = -1159880170066584166L;
		private Boolean allowRead = false;
		private Boolean allowEdit = false;
		private Boolean allowControl = false;

		public Boolean getAllowRead() {
			return allowRead;
		}

		public void setAllowRead(Boolean allowRead) {
			this.allowRead = allowRead;
		}

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowControl() {
			return allowControl;
		}

		public void setAllowControl(Boolean allowControl) {
			this.allowControl = allowControl;
		}

	}

	public static class WoRecord extends Record {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<Record, WoRecord> copier = WrapCopierFactory.wo(Record.class, WoRecord.class, null,
				JpaObject.FieldsInvisible);

	}

}