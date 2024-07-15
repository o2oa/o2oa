package com.x.processplatform.service.processing;

import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.core.express.Organization;
import com.x.processplatform.service.processing.factory.AttachmentFactory;
import com.x.processplatform.service.processing.factory.DataRecordFactory;
import com.x.processplatform.service.processing.factory.DocumentVersionFactory;
import com.x.processplatform.service.processing.factory.ElementFactory;
import com.x.processplatform.service.processing.factory.ItemFactory;
import com.x.processplatform.service.processing.factory.ProcessFactory;
import com.x.processplatform.service.processing.factory.ReadCompletedFactory;
import com.x.processplatform.service.processing.factory.ReadFactory;
import com.x.processplatform.service.processing.factory.ReviewFactory;
import com.x.processplatform.service.processing.factory.TaskCompletedFactory;
import com.x.processplatform.service.processing.factory.TaskFactory;
import com.x.processplatform.service.processing.factory.WorkCompletedFactory;
import com.x.processplatform.service.processing.factory.WorkFactory;
import com.x.processplatform.service.processing.factory.WorkLogFactory;

public class Business {

	public static final String EVENT_MANUALTASKEXPIRE = "manualTaskExpire";
	public static final String EVENT_MANUALTASK = "manualTask";
	public static final String EVENT_MANUALSTAY = "manualStay";
	public static final String EVENT_MANUALBEFORETASK = "manualBeforeTask";
	public static final String EVENT_MANUALAFTERTASK = "manualAfterTask";
	public static final String EVENT_MANUALAFTERPROCESSING = "manualAfterProcessing";
	public static final String EVENT_BEFOREARRIVE = "beforeArrive";
	public static final String EVENT_AFTERARRIVE = "afterArrive";
	public static final String EVENT_BEFOREEXECUTE = "beforeExecute";
	public static final String EVENT_AFTEREXECUTE = "afterExecute";
	public static final String EVENT_BEFOREINQUIRE = "beforeInquire";
	public static final String EVENT_AFTERINQUIRE = "afterInquire";
	public static final String EVENT_INVOKEJAXWSPARAMETER = "invokeJaxwsParameter";
	public static final String EVENT_INVOKEJAXRSPARAMETER = "invokeJaxrsParameter";
	public static final String EVENT_INVOKEJAXWSRESPONSE = "invokeJaxwsResponse";
	public static final String EVENT_INVOKEJAXRSRESPONSE = "invokeJaxrsResponse";
	public static final String EVENT_INVOKEJAXRSBODY = "invokeJaxrsBody";
	public static final String EVENT_INVOKEJAXRSHEAD = "invokeJaxrsHead";
	public static final String EVENT_PUBLISHCMSBODY = "publishCmsBody";
	public static final String EVENT_PUBLISHCMSCREATOR = "publishCmsCreator";
	public static final String EVENT_SERVICE = "service";
	public static final String EVENT_ROUTE = "route";
	public static final String EVENT_ROUTEAPPENDTASKIDENTITY = "routeAppendTaskIdentity";
	public static final String EVENT_READ = "read";
	public static final String EVENT_REVIEW = "review";
	public static final String EVENT_AGENT = "agent";
	public static final String EVENT_AGENTINTERRUPT = "agentInterrupt";
	public static final String EVENT_DELAY = "delay";
	public static final String EVENT_EMBEDTARGETASSIGNDATA = "embedTargetAssignData";
	public static final String EVENT_EMBEDTARGETIDENTITY = "embedTargetIdentity";
	public static final String EVENT_EMBEDTARGETTITLE = "embedTargetTitle";
	public static final String EVENT_EMBEDCOMPLETED = "embedCompleted";
	public static final String EVENT_EMBEDCOMPLETEDEND = "embedCompletedEnd";
	public static final String EVENT_EMBEDCOMPLETEDCANCEL = "embedCompletedCancel";
	public static final String EVENT_PROCESSAFTERBEGIN = "processAfterBegin";
	public static final String EVENT_PROCESSAFTEREND = "processAfterEnd";
	public static final String EVENT_PROCESSEXPIRE = "processExpire";
	public static final String EVENT_SPLIT = "split";
	public static final String EVENT_TASKDUTY = "taskDuty";
	public static final String EVENT_READDUTY = "readDuty";
	public static final String EVENT_REVIEWDUTY = "reviewDuty";
	public static final String EVENT_PERMISSIONWRITE = "permissionWrite";

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private AttachmentFactory attachment;

	public AttachmentFactory attachment() throws Exception {
		if (null == this.attachment) {
			this.attachment = new AttachmentFactory(this);
		}
		return attachment;
	}

	private ItemFactory item;

	public ItemFactory item() throws Exception {
		if (null == this.item) {
			this.item = new ItemFactory(this);
		}
		return item;
	}

	private TaskFactory task;

	public TaskFactory task() throws Exception {
		if (null == this.task) {
			this.task = new TaskFactory(this);
		}
		return task;
	}

	private TaskCompletedFactory taskCompleted;

	public TaskCompletedFactory taskCompleted() throws Exception {
		if (null == this.taskCompleted) {
			this.taskCompleted = new TaskCompletedFactory(this);
		}
		return taskCompleted;
	}

	private ReadFactory read;

	public ReadFactory read() throws Exception {
		if (null == this.read) {
			this.read = new ReadFactory(this);
		}
		return read;
	}

	private ReadCompletedFactory readCompleted;

	public ReadCompletedFactory readCompleted() throws Exception {
		if (null == this.readCompleted) {
			this.readCompleted = new ReadCompletedFactory(this);
		}
		return readCompleted;
	}

	private ReviewFactory review;

	public ReviewFactory review() throws Exception {
		if (null == this.review) {
			this.review = new ReviewFactory(this);
		}
		return review;
	}

	private WorkLogFactory workLog;

	public WorkLogFactory workLog() throws Exception {
		if (null == this.workLog) {
			this.workLog = new WorkLogFactory(this);
		}
		return workLog;
	}

	private WorkFactory work;

	public WorkFactory work() throws Exception {
		if (null == this.work) {
			this.work = new WorkFactory(this);
		}
		return work;
	}

	private WorkCompletedFactory workCompleted;

	public WorkCompletedFactory workCompleted() throws Exception {
		if (null == this.workCompleted) {
			this.workCompleted = new WorkCompletedFactory(this);
		}
		return workCompleted;
	}

	private DocumentVersionFactory documentVersion;

	public DocumentVersionFactory documentVersion() throws Exception {
		if (null == this.documentVersion) {
			this.documentVersion = new DocumentVersionFactory(this);
		}
		return documentVersion;
	}

	private ElementFactory element;

	public ElementFactory element() throws Exception {
		if (null == this.element) {
			this.element = new ElementFactory(this);
		}
		return element;
	}

	private DataRecordFactory dataRecord;

	public DataRecordFactory dataRecord() throws Exception {
		if (null == this.dataRecord) {
			this.dataRecord = new DataRecordFactory(this);
		}
		return dataRecord;
	}

	private ProcessFactory process;

	public ProcessFactory process() throws Exception {
		if (null == this.process) {
			this.process = new ProcessFactory(this);
		}
		return process;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

}
