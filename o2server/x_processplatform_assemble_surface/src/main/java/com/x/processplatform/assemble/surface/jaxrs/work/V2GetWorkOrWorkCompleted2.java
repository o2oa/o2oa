package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
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

class V2GetWorkOrWorkCompleted2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2GetWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			CompletableFuture<Wo> _wo = CompletableFuture.supplyAsync(() -> {
				Wo wo = null;
				try {
					Work work = business.entityManagerContainer().find(workOrWorkCompleted, Work.class);
					if (null != work) {
						wo = this.work(effectivePerson, business, work);
					} else {
						WorkCompleted workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
						if (null != workCompleted) {
							wo = this.workCompleted(business, effectivePerson, workCompleted);
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
				return wo;
			});

			CompletableFuture<Boolean> _control = CompletableFuture.supplyAsync(() -> {
				Boolean value = false;
				try {
					value = business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
							new ExceptionEntityNotExist(workOrWorkCompleted));
				} catch (Exception e) {
					logger.error(e);
				}
				return value;
			});

 
			if (BooleanUtils.isFalse(_control.get())) {
				throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
			}
			result.setData(_wo.get());
 
			return result;
		}
	}

	private Wo work(EffectivePerson effectivePerson, Business business, Work work)
			throws InterruptedException, ExecutionException {
		Wo wo = new Wo();
		CompletableFuture.allOf(workJson(work, wo), activity(business, work, wo), data(business, work, wo),
				task(effectivePerson, business, work, wo), read(effectivePerson, business, work.getJob(), wo),
				creatorIdentity(business, work.getCreatorIdentity(), wo),
				creatorPerson(business, work.getCreatorPerson(), wo), creatorUnit(business, work.getCreatorUnit(), wo),
				attachment(effectivePerson, business, work.getJob(), wo),
				record(effectivePerson, business, work.getJob(), wo)).get();
		for (WoTask woTask : wo.getTaskList()) {
			wo.getRecordList().add(taskToRecord(woTask));
		}
		return wo;
	}

	private WoRecord taskToRecord(WoTask woTask) {
		WoRecord o = new WoRecord();
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

	private CompletableFuture<Void> data(Business business, Work work, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				List<Item> list = business.entityManagerContainer().listEqualAndEqual(Item.class,
						DataItem.bundle_FIELDNAME, work.getJob(), DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
				if (!list.isEmpty()) {
					JsonElement jsonElement = itemConverter.assemble(list);
					// 必须是Object对象
					if (jsonElement.isJsonObject()) {
						wo.setData(gson.fromJson(jsonElement, Data.class));
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> record(EffectivePerson effectivePerson, Business business, String job, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setRecordList(business.entityManagerContainer()
						.fetchEqual(Record.class, WoRecord.copier, Record.job_FIELDNAME, job).stream()
						.sorted(Comparator.comparing(WoRecord::getOrder)).collect(Collectors.toList()));
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> completedRecord(EffectivePerson effectivePerson, Business business,
			WorkCompleted workCompleted, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				if (ListTools.isNotEmpty(workCompleted.getProperties().getRecordList())) {
					wo.setRecordList(WoRecord.copier.copy(workCompleted.getProperties().getRecordList()).stream()
							.sorted(Comparator.comparing(WoRecord::getOrder)).collect(Collectors.toList()));
				} else {
					wo.setRecordList(business.entityManagerContainer()
							.fetchEqual(Record.class, WoRecord.copier, Record.job_FIELDNAME, workCompleted.getJob())
							.stream().sorted(Comparator.comparing(WoRecord::getOrder)).collect(Collectors.toList()));
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> activity(Business business, Work work, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				Activity activity = business.getActivity(work);
				if (null != activity) {
					WoActivity woActivity = new WoActivity();
					activity.copyTo(woActivity);
					wo.setActivity(woActivity);
					if (Objects.equals(ActivityType.manual, activity.getActivityType())) {
						wo.setRouteList(WoRoute.copier.copy(business.route().pick(((Manual) activity).getRouteList())));
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> task(EffectivePerson effectivePerson, Business business, Work work, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setTaskList(WoTask.copier.copy(
						business.entityManagerContainer().listEqual(Task.class, Task.job_FIELDNAME, work.getJob())));
				wo.setCurrentTaskIndex(
						ListUtils.indexOf(wo.getTaskList(), e -> effectivePerson.isPerson(e.getPerson())));
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> attachment(EffectivePerson effectivePerson, Business business, String job, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
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
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> read(EffectivePerson effectivePerson, Business business, String job, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setReadList(WoRead.copier
						.copy(business.entityManagerContainer().listEqual(Read.class, Read.job_FIELDNAME, job)));
				wo.setCurrentReadIndex(
						ListUtils.indexOf(wo.getReadList(), e -> effectivePerson.isPerson(e.getPerson())));
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> workJson(Work work, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setWork(gson.toJsonTree(WoWork.copier.copy(work)));
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> creatorIdentity(Business business, String creatorIdentity, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setCreatorIdentity(business.organization().identity().getObject(creatorIdentity));
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> creatorPerson(Business business, String creatorPerson, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setCreatorPerson(business.organization().person().getObject(creatorPerson));
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> creatorUnit(Business business, String creatorUnit, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setCreatorUnit(business.organization().unit().getObject(creatorUnit));
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private Wo workCompleted(Business business, EffectivePerson effectivePerson, WorkCompleted workCompleted)
			throws InterruptedException, ExecutionException {
		Wo wo = new Wo();
		CompletableFuture.allOf(completedJson(workCompleted, wo), completedData(business, workCompleted, wo),
				read(effectivePerson, business, workCompleted.getJob(), wo),
				creatorIdentity(business, workCompleted.getCreatorIdentity(), wo),
				creatorPerson(business, workCompleted.getCreatorPerson(), wo),
				creatorUnit(business, workCompleted.getCreatorUnit(), wo),
				attachment(effectivePerson, business, workCompleted.getJob(), wo),
				completedRecord(effectivePerson, business, workCompleted, wo)).get();
		return wo;
	}

	private CompletableFuture<Void> completedJson(WorkCompleted workCompleted, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			try {
				wo.setWork(gson.toJsonTree(WoWorkCompleted.copier.copy(workCompleted)));
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CompletableFuture<Void> completedData(Business business, WorkCompleted workCompleted, Wo wo) {
		return CompletableFuture.runAsync(() -> {
			if (BooleanUtils.isTrue(workCompleted.getMerged())) {
				wo.setData(workCompleted.getProperties().getData());
			} else {
				try {
					List<Item> list = business.entityManagerContainer().listEqualAndEqual(Item.class,
							DataItem.bundle_FIELDNAME, workCompleted.getJob(), DataItem.itemCategory_FIELDNAME,
							ItemCategory.pp);
					if (!list.isEmpty()) {
						JsonElement jsonElement = itemConverter.assemble(list);
						// 必须是Object对象
						if (jsonElement.isJsonObject()) {
							wo.setData(gson.fromJson(jsonElement, Data.class));
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
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

		// work和workCompleted都有
		private List<WoRecord> recordList;

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

		private ManualMode manualMode;

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

	}

	public static class WoRoute extends Route {

		private static final long serialVersionUID = 556378904185283486L;

		static WrapCopier<Route, WoRoute> copier = WrapCopierFactory.wo(Route.class, WoRoute.class, null,
				ListTools.toList(Route.createTime_FIELDNAME, Route.edition_FIELDNAME, Route.position_FIELDNAME,
						Route.process_FIELDNAME, Route.updateTime_FIELDNAME, Route.track_FIELDNAME,
						Route.properties_FIELDNAME));
	}

	public static class WoAttachment extends Attachment {
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

		static WrapCopier<Record, WoRecord> copier = WrapCopierFactory.wo(Record.class, WoRecord.class,
				JpaObject.singularAttributeField(Record.class, true, false), JpaObject.FieldsInvisible);

	}

}

