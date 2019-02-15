package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.jaxrs.MemberTerms;
import com.x.base.core.project.jaxrs.NotEqualsTerms;
import com.x.base.core.project.jaxrs.NotInTerms;
import com.x.base.core.project.jaxrs.NotMemberTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportFilter;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWrapInConvert;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionListPrevWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPrevWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		Wi wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			if (wrapIn == null) {
				wrapIn = new Wi();
			}
		}
		if (check) {
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("title", wrapIn.getFilterLikeContent());
				likesMap.put("creatorIdentity", wrapIn.getFilterLikeContent());
				likesMap.put("currentProcessorIdentity", wrapIn.getFilterLikeContent());
				likesMap.put("description", wrapIn.getFilterLikeContent());
				likesMap.put("processStatus", wrapIn.getFilterLikeContent());
				likesMap.put("reporterIdentity", wrapIn.getFilterLikeContent());
			}
		}
		if (check) {
			sequenceField = wrapIn.getSequenceField();
			try {
				result = this.standardListPrev(Wo.copier, id, count, sequenceField, equalsMap, notEqualsMap, likesMap,
						insMap, notInsMap, membersMap, notMembersMap, null, false, wrapIn.getOrder());
			} catch (Exception e) {
				Exception exception = new ExceptionWorkReportFilter(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用于模糊查询的字符串.")
		private String filterLikeContent = null;

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField =  JpaObject.sequence_FIELDNAME;

		@FieldDescribe("用于列表排序的方式.")
		private String order = "DESC";

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getSequenceField() {
			return sequenceField;
		}

		public void setSequenceField(String sequenceField) {
			this.sequenceField = sequenceField;
		}

		public String getOrder() {
			return order;
		}

		public void setOrder(String order) {
			this.order = order;
		}

		public String getFilterLikeContent() {
			return filterLikeContent;
		}

		public void setFilterLikeContent(String filterLikeContent) {
			this.filterLikeContent = filterLikeContent;
		}
	}

	public static class Wo extends OkrWorkReportBaseInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkReportBaseInfo, Wo> copier = WrapCopierFactory.wo(OkrWorkReportBaseInfo.class,
				Wo.class, null, Wo.Excludes);

		private Boolean isReporter = false;

		private Boolean isWorkAdmin = false;

		private Boolean isReadLeader = false;

		private Boolean isCreator = false;

		private Boolean needReportProgress = false;

		/**
		 * 管理员督办信息
		 */
		private String adminSuperviseInfo = "";

		private String workPointAndRequirements = "";
		/**
		 * 填写汇报时填写的具体进展描述信息
		 */
		private String progressDescription = "";
		/**
		 * 下一步工作计划信息
		 */
		private String workPlan = "";
		/**
		 * 说明备注信息
		 */
		private String memo = "";

		private List<WoOkrWorkReportProcessLog> processLogs = null;

		private WoOkrWorkBaseInfo workInfo = null;

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Boolean getIsReporter() {
			return isReporter;
		}

		public void setIsReporter(Boolean isReporter) {
			this.isReporter = isReporter;
		}

		public Boolean getIsWorkAdmin() {
			return isWorkAdmin;
		}

		public void setIsWorkAdmin(Boolean isWorkAdmin) {
			this.isWorkAdmin = isWorkAdmin;
		}

		public Boolean getIsReadLeader() {
			return isReadLeader;
		}

		public void setIsReadLeader(Boolean isReadLeader) {
			this.isReadLeader = isReadLeader;
		}

		public Boolean getIsCreator() {
			return isCreator;
		}

		public void setIsCreator(Boolean isCreator) {
			this.isCreator = isCreator;
		}

		public String getProgressDescription() {
			return progressDescription;
		}

		public void setProgressDescription(String progressDescription) {
			this.progressDescription = progressDescription;
		}

		public String getWorkPlan() {
			return workPlan;
		}

		public void setWorkPlan(String workPlan) {
			this.workPlan = workPlan;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getWorkPointAndRequirements() {
			return workPointAndRequirements;
		}

		public void setWorkPointAndRequirements(String workPointAndRequirements) {
			this.workPointAndRequirements = workPointAndRequirements;
		}

		public List<WoOkrWorkReportProcessLog> getProcessLogs() {
			return processLogs;
		}

		public void setProcessLogs(List<WoOkrWorkReportProcessLog> processLogs) {
			this.processLogs = processLogs;
		}

		public String getAdminSuperviseInfo() {
			return adminSuperviseInfo;
		}

		public void setAdminSuperviseInfo(String adminSuperviseInfo) {
			this.adminSuperviseInfo = adminSuperviseInfo;
		}

		public WoOkrWorkBaseInfo getWorkInfo() {
			return workInfo;
		}

		public void setWorkInfo(WoOkrWorkBaseInfo workInfo) {
			this.workInfo = workInfo;
		}

		public Boolean getNeedReportProgress() {
			return needReportProgress;
		}

		public void setNeedReportProgress(Boolean needReportProgress) {
			this.needReportProgress = needReportProgress;
		}

	}
}