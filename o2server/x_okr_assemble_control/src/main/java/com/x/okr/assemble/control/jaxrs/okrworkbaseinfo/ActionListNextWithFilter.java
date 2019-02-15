package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
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
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;

public class ActionListNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListNextWithFilter.class);

	protected ActionResult<List<WoOkrWorkBaseInfo>> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			String id, Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<WoOkrWorkBaseInfo>> result = new ActionResult<>();
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
			Exception exception = new ExceptionWorkBaseInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("title", wrapIn.getFilterLikeContent());
				likesMap.put("shortWorkDetail", wrapIn.getFilterLikeContent());
				likesMap.put("centerTitle", wrapIn.getFilterLikeContent());
				likesMap.put("creatorIdentity", wrapIn.getFilterLikeContent());
				likesMap.put("workType", wrapIn.getFilterLikeContent());
				likesMap.put("responsibilityEmployeeName", wrapIn.getFilterLikeContent());
				likesMap.put("workProcessStatus", wrapIn.getFilterLikeContent());
			}
		}
		if (check) {
			sequenceField = wrapIn.getSequenceField();
			try {
				result = this.standardListNext(WoOkrWorkBaseInfo.copier, id, count, sequenceField, equalsMap,
						notEqualsMap, likesMap, insMap, notInsMap, membersMap, notMembersMap, null, false,
						wrapIn.getOrder());
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private String filterLikeContent = null;

		private String sequenceField =  JpaObject.sequence_FIELDNAME;

		private String order = "DESC";

		private Long rank = 0L;

		public String getFilterLikeContent() {
			return filterLikeContent;
		}

		public void setFilterLikeContent(String filterLikeContent) {
			this.filterLikeContent = filterLikeContent;
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

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}