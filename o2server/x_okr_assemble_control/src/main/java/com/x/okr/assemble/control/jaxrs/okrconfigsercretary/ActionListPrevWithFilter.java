package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

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
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.ExceptionWrapInConvert;
import com.x.okr.entity.OkrConfigSecretary;

public class ActionListPrevWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPrevWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
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
			if (id == null || id.isEmpty()) {
				id = "(0)";
			}
			EqualsTerms equalsMap = new EqualsTerms();
			NotEqualsTerms notEqualsMap = new NotEqualsTerms();
			InTerms insMap = new InTerms();
			NotInTerms notInsMap = new NotInTerms();
			MemberTerms membersMap = new MemberTerms();
			NotMemberTerms notMembersMap = new NotMemberTerms();
			LikeTerms likesMap = new LikeTerms();

			try {
				result = this.standardListPrev(Wo.copier, id, count, wrapIn.getSequenceField(), equalsMap, notEqualsMap,
						likesMap, insMap, notInsMap, membersMap, notMembersMap, null, wrapIn.isAndJoin(),
						wrapIn.getOrder());
			} catch (Exception e) {
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用于查询的秘书姓名.")
		private String secretaryName = null;

		@FieldDescribe("用于查询的领导姓名.")
		private String leaderName = null;

		@FieldDescribe("用于列表排序的属性.")
		private String sequenceField =  JpaObject.sequence_FIELDNAME;

		@FieldDescribe("用于列表排序的方式.")
		private String order = "DESC";

		private Integer count;

		private boolean andJoin;

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public boolean isAndJoin() {
			return andJoin;
		}

		public void setAndJoin(boolean andJoin) {
			this.andJoin = andJoin;
		}

		public String getSecretaryName() {
			return secretaryName;
		}

		public void setSecretaryName(String secretaryName) {
			this.secretaryName = secretaryName;
		}

		public String getLeaderName() {
			return leaderName;
		}

		public void setLeaderName(String leaderName) {
			this.leaderName = leaderName;
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
	}

	public static class Wo extends OkrConfigSecretary {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrConfigSecretary, Wo> copier = WrapCopierFactory.wo(OkrConfigSecretary.class,
				Wo.class, null, Wo.Excludes);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}