package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.RecordBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2RerouteWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2RerouteWo;

class V2Reroute extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Reroute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Param param = this.init(effectivePerson, id, jsonElement);

		reroute(param.getWork().getId(), param.getDestinationActivity().getId(), param.getMergeWork(),
				param.getDistinguishedNameList(), param.getWork().getJob());
		processing(param.getWork().getId(), param.getWork().getJob(), param.getSeries());
		Record rec = RecordBuilder.ofWorkProcessing(Record.TYPE_REROUTE, param.getWorkLog(), effectivePerson,
				param.getDestinationActivity(), param.getExistTaskIds());
		RecordBuilder.processing(rec);
		Wo wo = Wo.copier.copy(rec);
		result.setData(wo);
		return result;
	}

	private Param init(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		param.setSeries(StringTools.uniqueToken());
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.setMergeWork(BooleanUtils.isTrue(wi.getMergeWork()));
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			param.setWork(work);
			Activity destinationActivity = business.getActivity(wi.getActivity(), wi.getActivityType());
			if (null == destinationActivity) {
				throw new ExceptionEntityNotExist(wi.getActivity());
			}
			if (!StringUtils.equals(work.getProcess(), destinationActivity.getProcess())) {
				throw new ExceptionProcessNotMatch();
			}
			param.setDestinationActivity(destinationActivity);
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowReroute().build();
			if (BooleanUtils.isFalse(control.getAllowManage()) && BooleanUtils.isFalse(control.getAllowReroute())) {
				throw new ExceptionRerouteDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						destinationActivity.getName());
			}
			WorkLog workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob(),
					WorkLog.FROMACTIVITYTOKEN_FIELDNAME, work.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			param.setWorkLog(workLog);
			param.setExistTaskIds(emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob()));
			param.setDistinguishedNameList(
					business.organization().distinguishedName().list(wi.getDistinguishedNameList()));
		}
		return param;
	}

	private class Param {

		private Work work;
		private Boolean mergeWork;
		private WorkLog workLog;
		private Activity destinationActivity;
		private String series;
		private List<String> distinguishedNameList = new ArrayList<>();
		private List<String> existTaskIds = new ArrayList<>();

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}

		public Boolean getMergeWork() {
			return mergeWork;
		}

		public void setMergeWork(Boolean mergeWork) {
			this.mergeWork = mergeWork;
		}

		public Work getWork() {
			return work;
		}

		public void setWork(Work work) {
			this.work = work;
		}

		public WorkLog getWorkLog() {
			return workLog;
		}

		public void setWorkLog(WorkLog workLog) {
			this.workLog = workLog;
		}

		public Activity getDestinationActivity() {
			return destinationActivity;
		}

		public void setDestinationActivity(Activity destinationActivity) {
			this.destinationActivity = destinationActivity;
		}

		public String getSeries() {
			return series;
		}

		public void setSeries(String series) {
			this.series = series;
		}

		public List<String> getExistTaskIds() {
			return existTaskIds;
		}

		public void setExistTaskIds(List<String> existTaskIds) {
			this.existTaskIds = existTaskIds;
		}

	}

	private void reroute(String workId, String activityId, boolean mergeWork, List<String> distinguishedNameList,
			String job) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.work.V2RerouteWi req = new com.x.processplatform.core.express.service.processing.jaxrs.work.V2RerouteWi();
		req.setActivity(activityId);
		req.setDistinguishedNameList(distinguishedNameList);
		req.setMergeWork(mergeWork);
		ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", workId, "reroute"), req, job)
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.V2RerouteWo.class);
	}

	private void processing(String workId, String job, String series) throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_REROUTE);
		req.setSeries(series);
		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", workId, "processing"), req, job).getData(WoId.class);
	}

	public static class Wi extends V2RerouteWi {

		private static final long serialVersionUID = -8201594262401019064L;

	}

	public static class Wo extends V2RerouteWo {

		private static final long serialVersionUID = -8410749558739884101L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}