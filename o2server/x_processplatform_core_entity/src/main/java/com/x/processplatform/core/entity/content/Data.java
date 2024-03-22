package com.x.processplatform.core.entity.content;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ListTools;

public class Data extends ListOrderedMap<String, Object> {

	public static final String WORK_PROPERTY = "$work";

	public static final String ATTACHMENTLIST_PROPERTY = "$attachmentList";

	private static final long serialVersionUID = 8339934499479910171L;

	@JsonIgnore
	public static void removeWork(JsonElement jsonElement) {
		if (null != jsonElement && jsonElement.isJsonObject()) {
			JsonObject o = jsonElement.getAsJsonObject();
			if (o.has(Data.WORK_PROPERTY)) {
				o.remove(Data.WORK_PROPERTY);
			}
		}
	}

	@JsonIgnore
	public Data setWork(Work work) {
		DataWork dataWork = new DataWork();
		if (null != work) {
			DataWork.workCopier.copy(work, dataWork);
			dataWork.setWorkId(work.getId());
			this.put(WORK_PROPERTY, dataWork);
		}
		return this;
	}

	@JsonIgnore
	public Data removeWork() {
		if (this.containsKey(WORK_PROPERTY)) {
			this.remove(WORK_PROPERTY);
		}
		return this;
	}

	@JsonIgnore
	public Data setWork(WorkCompleted workCompleted) {
		DataWork dataWork = new DataWork();
		if (null != workCompleted) {
			DataWork.workCompletedCopier.copy(workCompleted, dataWork);
			dataWork.setWorkId(workCompleted.getWork());
			dataWork.setWorkCompletedId(workCompleted.getId());
			dataWork.setCompleted(true);
			this.put(WORK_PROPERTY, dataWork);
		}
		return this;
	}

	@JsonIgnore
	public Data setAttachmentList(List<Attachment> attachmentList) {
		List<DataAttachment> list = new ArrayList<>();
		if (!ListTools.isEmpty(attachmentList)) {
			DataAttachment.copier.copy(attachmentList, list);
		}
		this.put(ATTACHMENTLIST_PROPERTY, list);
		return this;
	}

	@JsonIgnore
	public Data removeAttachmentList() {
		if (this.containsKey(ATTACHMENTLIST_PROPERTY)) {
			this.remove(ATTACHMENTLIST_PROPERTY);
		}
		return this;
	}

	public List<String> extractDistinguishedName(String path)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<String> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(path)) {
			Object o = PropertyUtils.getProperty(this, path);
			if (null != o) {
				if (o instanceof CharSequence) {
					list.add(o.toString());
				} else if (o instanceof Iterable) {
					for (Object v : (Iterable<?>) o) {
						if (null != v) {
							if ((v instanceof CharSequence)) {
								list.add(v.toString());
							} else {
								Object d = PropertyUtils.getProperty(v, JpaObject.DISTINGUISHEDNAME);
								String s = Objects.toString(d, "");
								if (StringUtils.isNotEmpty(s)) {
									list.add(s);
								}
							}
						}
					}
				} else {
					Object d = PropertyUtils.getProperty(o, JpaObject.DISTINGUISHEDNAME);
					String s = Objects.toString(d, "");
					if (StringUtils.isNotEmpty(s)) {
						list.add(s);
					}
				}
			}
		}
		return list;
	}

	public Object find(String path) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return this.find(StringUtils.split(path, "."));
	}

	public <T> T find(String path, Class<T> cls, T defaultValue)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return this.find(StringUtils.split(path, "."), cls, defaultValue);
	}

	public Object find(String[] paths) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object o = this;
		for (String path : paths) {
			if (StringUtils.isEmpty(path) || (null == o)) {
				o = null;
				break;
			}
			if (StringUtils.isNumeric(path)) {
				if (o instanceof List) {
					int idx = NumberUtils.toInt(path);
					List<?> c = (List<?>) o;
					if ((idx >= c.size()) || (idx < 0)) {
						o = null;
						break;
					}
					o = c.get(idx);
				} else {
					o = null;
					break;
				}
			} else {
				o = PropertyUtils.getProperty(o, path);
			}
		}
		return o;
	}

	@SuppressWarnings("unchecked")
	public <T> T find(String[] paths, Class<T> cls, T defaultValue)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object o = this.find(paths);
		if (null == o) {
			return defaultValue;
		}
		if (!cls.isAssignableFrom(o.getClass())) {
			return null;
		}
		return (T) o;
	}

	public static class DataWork extends GsonPropertyObject {

		private static final long serialVersionUID = -9086239850917572996L;

		public static final WrapCopier<Work, DataWork> workCopier = WrapCopierFactory.wo(Work.class, DataWork.class,
				null, JpaObject.FieldsInvisible);

		public static final WrapCopier<WorkCompleted, DataWork> workCompletedCopier = WrapCopierFactory
				.wo(WorkCompleted.class, DataWork.class, null, JpaObject.FieldsInvisible);

		private String job;
		private String workId;
		private String workCompletedId;
		private Boolean completed;
		private String title;
		private Date startTime;
		private String startTimeMonth;
		private String creatorPerson;
		private String creatorIdentity;
		private String creatorUnit;
		private String creatorUnitLevelName;
		private String application;
		private String applicationName;
		private String applicationAlias;
		private String process;
		private String processName;
		private String processAlias;
		private String serial;
		private String activityType;
		private String activityName;
		private Date activityArrivedTime;
		private String manualTaskIdentityText;
		private String completedType;
		/** 来自workCompleted的结束时间 */
		private Date completedTime;
		/** 来自workCompleted的结束时间月份 */
		private String completedTimeMonth;
		private Date updateTime;

		public String getCompletedType() {
			return completedType;
		}

		public void setCompletedType(String completedType) {
			this.completedType = completedType;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public String getStartTimeMonth() {
			return startTimeMonth;
		}

		public void setStartTimeMonth(String startTimeMonth) {
			this.startTimeMonth = startTimeMonth;
		}

		public String getCreatorPerson() {
			return creatorPerson;
		}

		public void setCreatorPerson(String creatorPerson) {
			this.creatorPerson = creatorPerson;
		}

		public String getCreatorIdentity() {
			return creatorIdentity;
		}

		public void setCreatorIdentity(String creatorIdentity) {
			this.creatorIdentity = creatorIdentity;
		}

		public String getCreatorUnit() {
			return creatorUnit;
		}

		public void setCreatorUnit(String creatorUnit) {
			this.creatorUnit = creatorUnit;
		}

		public String getCreatorUnitLevelName() {
			return creatorUnitLevelName;
		}

		public void setCreatorUnitLevelName(String creatorUnitLevelName) {
			this.creatorUnitLevelName = creatorUnitLevelName;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getApplicationAlias() {
			return applicationAlias;
		}

		public void setApplicationAlias(String applicationAlias) {
			this.applicationAlias = applicationAlias;
		}

		public String getProcess() {
			return process;
		}

		public void setProcess(String process) {
			this.process = process;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public String getProcessAlias() {
			return processAlias;
		}

		public void setProcessAlias(String processAlias) {
			this.processAlias = processAlias;
		}

		public String getSerial() {
			return serial;
		}

		public void setSerial(String serial) {
			this.serial = serial;
		}

		public String getActivityType() {
			return activityType;
		}

		public void setActivityType(String activityType) {
			this.activityType = activityType;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public String getCompletedTimeMonth() {
			return completedTimeMonth;
		}

		public void setCompletedTimeMonth(String completedTimeMonth) {
			this.completedTimeMonth = completedTimeMonth;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}

		public String getWorkCompletedId() {
			return workCompletedId;
		}

		public void setWorkCompletedId(String workCompletedId) {
			this.workCompletedId = workCompletedId;
		}

		public Boolean getCompleted() {
			return completed;
		}

		public void setCompleted(Boolean completed) {
			this.completed = completed;
		}

		public String getManualTaskIdentityText() {
			return manualTaskIdentityText;
		}

		public void setManualTaskIdentityText(String manualTaskIdentityText) {
			this.manualTaskIdentityText = manualTaskIdentityText;
		}

		public Date getActivityArrivedTime() {
			return activityArrivedTime;
		}

		public void setActivityArrivedTime(Date activityArrivedTime) {
			this.activityArrivedTime = activityArrivedTime;
		}

		public Date getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}
	}

	public static class DataAttachment extends GsonPropertyObject {

		private static final long serialVersionUID = 368032075562133125L;

		public static final WrapCopier<Attachment, DataAttachment> copier = WrapCopierFactory.wo(Attachment.class,
				DataAttachment.class, null, JpaObject.FieldsInvisible);

		private String id;
		private String name;
		private String extension;
		private String storage;
		private Long length;
		private String site;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getExtension() {
			return extension;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}

		public String getStorage() {
			return storage;
		}

		public void setStorage(String storage) {
			this.storage = storage;
		}

		public Long getLength() {
			return length;
		}

		public void setLength(Long length) {
			this.length = length;
		}

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

	/* 除了默认的数据之外不保有数据 */
	public Boolean emptySet() {
		return this.keyList().stream().filter(
				o -> (!StringUtils.equals(WORK_PROPERTY, o)) && (!StringUtils.equals(ATTACHMENTLIST_PROPERTY, o)))
				.count() == 0;
	}

	@SuppressWarnings("unchecked")
	public void replaceContent(String json) {
		this.clear();
		this.putAll(XGsonBuilder.instance().fromJson(json, Map.class));
	}

	@Override
	/* 需要重载,前端toString需要这个方法. */
	public String toString() {
		return XGsonBuilder.toJson(this);
	}

}
