package com.x.okr.assemble.control.jaxrs.okrtask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
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
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.entity.OkrTask;

public class ActionListMyTaskNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListMyTaskNextWithFilter.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		OkrUserCache okrUserCache = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if (check) {
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName(currentPerson.getDistinguishedName());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache(e, currentPerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}

		if (check && (okrUserCache == null || okrUserCache.getLoginIdentityName() == null)) {
			check = false;
			Exception exception = new ExceptionUserNoLogin(currentPerson.getDistinguishedName());
			result.error(exception);
		}

		if (check) {
			try {
				// 只允许查询属于自己的登录身份的数据
				EqualsTerms equalsMap = new EqualsTerms();
				NotEqualsTerms notEqualsMap = new NotEqualsTerms();
				InTerms insMap = new InTerms();
				NotInTerms notInsMap = new NotInTerms();
				MemberTerms membersMap = new MemberTerms();
				NotMemberTerms notMembersMap = new NotMemberTerms();
				LikeTerms likesMap = new LikeTerms();
				equalsMap.put("targetIdentity", okrUserCache.getLoginIdentityName());
				Collection<String> dynamicObjectTypeNotIn = null;
				if (notInsMap.get("dynamicObjectType") == null) {
					dynamicObjectTypeNotIn = new ArrayList<String>();
				} else {
					if (notInsMap.get("dynamicObjectType") != null) {
						dynamicObjectTypeNotIn = (Collection<String>) notInsMap.get("dynamicObjectType");
					}
				}
				dynamicObjectTypeNotIn.add("工作汇报");
				notInsMap.put("dynamicObjectType", dynamicObjectTypeNotIn);

				result = this.standardListNext(Wo.copier, id, count, wrapIn.getSequenceField(), equalsMap, notEqualsMap,
						likesMap, insMap, notInsMap, membersMap, notMembersMap, null, true, wrapIn.getOrder());
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		} else {
			result.setCount(0L);
			result.setData(new ArrayList<Wo>());
		}
		return result;
	}

	public static class Wi {

		@FieldDescribe("用于模糊查询的字符串.")
		private String filterLikeContent = null;

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField =  JpaObject.sequence_FIELDNAME;

		@FieldDescribe("用于列表排序的方式.")
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

	public static class Wo extends OkrTask {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrTask, Wo> copier = WrapCopierFactory.wo(OkrTask.class, Wo.class, null, Wo.Excludes);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}