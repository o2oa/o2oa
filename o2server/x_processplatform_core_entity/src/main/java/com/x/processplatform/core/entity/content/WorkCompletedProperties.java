package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.Form;

public class WorkCompletedProperties extends JsonProperties {

	private static final long serialVersionUID = -299664500202382316L;

	@FieldDescribe("合并数据对象")
	private Data data;

	@FieldDescribe("合并已办对象")
	private List<TaskCompleted> taskCompletedList = new ArrayList<>();

	@FieldDescribe("合并已阅对象")
	private List<ReadCompleted> readCompletedList = new ArrayList<>();

	@FieldDescribe("合并参阅对象")
	private List<Review> reviewList = new ArrayList<>();

	@FieldDescribe("合并记录对象")
	private List<Record> recordList = new ArrayList<>();

	@FieldDescribe("合并工作日志对象")
	private List<WorkLog> workLogList = new ArrayList<>();

	@FieldDescribe("合并工作Form")
	private StoreForm storeForm;

	@FieldDescribe("合并工作Form,移动端.")
	private StoreForm mobileStoreForm;

	@FieldDescribe("标题")
	private String title;

	@FieldDescribe("父工作,在当前工作是通过子流程调用时产生.")
	private String parentWork;

	@FieldDescribe("父工作Job,在当前工作是通过子流程调用时产生.")
	private String parentJob;

	public String getParentWork() {
		return parentWork;
	}

	public void setParentWork(String parentWork) {
		this.parentWork = parentWork;
	}

	public String getParentJob() {
		return parentJob;
	}

	public void setParentJob(String parentJob) {
		this.parentJob = parentJob;
	}

	public List<Record> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<Record> recordList) {
		this.recordList = recordList;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public List<WorkLog> getWorkLogList() {
		return workLogList;
	}

	public void setWorkLogList(List<WorkLog> workLogList) {
		this.workLogList = workLogList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<TaskCompleted> getTaskCompletedList() {
		return taskCompletedList;
	}

	public void setTaskCompletedList(List<TaskCompleted> taskCompletedList) {
		this.taskCompletedList = taskCompletedList;
	}

	public List<ReadCompleted> getReadCompletedList() {
		return readCompletedList;
	}

	public void setReadCompletedList(List<ReadCompleted> readCompletedList) {
		this.readCompletedList = readCompletedList;
	}

	public List<Review> getReviewList() {
		return reviewList;
	}

	public void setReviewList(List<Review> reviewList) {
		this.reviewList = reviewList;
	}

	public StoreForm getStoreForm() {
		return storeForm;
	}

	public void setStoreForm(StoreForm storeForm) {
		this.storeForm = storeForm;
	}

	public StoreForm getMobileStoreForm() {
		return mobileStoreForm;
	}

	public void setMobileStoreForm(StoreForm mobileStoreForm) {
		this.mobileStoreForm = mobileStoreForm;
	}

	public static class StoreForm extends GsonPropertyObject {

		private static final long serialVersionUID = 6402145056972301805L;

		private RelatedForm form;

		private Map<String, RelatedForm> relatedFormMap = new HashMap<>();

		private Map<String, RelatedScript> relatedScriptMap = new HashMap<>();

		public RelatedForm getForm() {
			return form;
		}

		public void setForm(RelatedForm form) {
			this.form = form;
		}

		public Map<String, RelatedForm> getRelatedFormMap() {
			return relatedFormMap;
		}

		public void setRelatedFormMap(Map<String, RelatedForm> relatedFormMap) {
			this.relatedFormMap = relatedFormMap;
		}

		public Map<String, RelatedScript> getRelatedScriptMap() {
			return relatedScriptMap;
		}

		public void setRelatedScriptMap(Map<String, RelatedScript> relatedScriptMap) {
			this.relatedScriptMap = relatedScriptMap;
		}

	}

	public static class RelatedScript extends GsonPropertyObject {

		private static final long serialVersionUID = 4695405501650343555L;

		public static final String TYPE_PROCESSPLATFORM = "processPlatform";
		public static final String TYPE_CMS = "cms";
		public static final String TYPE_PORTAL = "portal";
		public static final String TYPE_SERVICE = "service";

		public RelatedScript() {

		}

		public RelatedScript(String id, String name, String alias, String text, String type) {
			this.id = id;
			this.alias = alias;
			this.name = name;
			this.text = text;
			this.type = type;
		}

		private String type;

		private String id;

		private String alias;

		private String name;

		private String text;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

	public static class RelatedForm extends GsonPropertyObject {

		private static final long serialVersionUID = -8345275108890011204L;

		public RelatedForm() {

		}

		public RelatedForm(Form form, String data) {
			this.id = form.getId();
			this.alias = form.getAlias();
			this.name = form.getName();
			this.category = form.getCategory();
			this.application = form.getApplication();
			this.hasMobile = form.getHasMobile();
			this.data = data;
		}

		private String id;
		private String alias;
		private String name;
		private String category;
		private String application;
		private Boolean hasMobile;
		private String data;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public Boolean getHasMobile() {
			return hasMobile;
		}

		public void setHasMobile(Boolean hasMobile) {
			this.hasMobile = hasMobile;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}

}
