package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
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
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionInsufficientPermissions;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionOkrSystemAdminCheck;

public class ActionListNextWithFilterAdmin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListNextWithFilterAdmin.class);

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
		Boolean check = true;
		Wi wrapIn = null;
		EffectivePerson currentPerson = this.effectivePerson(request);

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if( !"xadmin".equalsIgnoreCase( currentPerson.getName() )) {
				try {
					if (!okrUserInfoService.getIsOkrManager(currentPerson.getDistinguishedName())) {
						check = false;
						Exception exception = new ExceptionInsufficientPermissions(currentPerson.getDistinguishedName(), ThisApplication.OKRMANAGER);
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionOkrSystemAdminCheck(e, currentPerson.getDistinguishedName());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}

		if (check) {
			if (wrapIn == null) {
				wrapIn = new Wi();
			}
		}
		if (check) {
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("title", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("defaultWorkType", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("description", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("processStatus", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("deployerName", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("deployerUnitName", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("deployerTopUnitName", wrapIn.getFilterLikeContent());
			}
		}
		if (check) {
			sequenceField = wrapIn.getSequenceField();
			try {
				result = this.standardListNext(Wo.copier, id, count, sequenceField, equalsMap, notEqualsMap, likesMap,
						insMap, notInsMap, membersMap, notMembersMap, null, false, wrapIn.getOrder());
			} catch (Exception e) {
				result.error(e);
				logger.error(e, currentPerson, request, null);
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
}