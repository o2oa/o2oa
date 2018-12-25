package com.x.strategydeploy.assemble.control.measures;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
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
import com.x.strategydeploy.core.entity.MeasuresInfo;

public class ActionListPrevWithFilter extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(ActionListPrevWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, BaseAction.Wi wrapIn) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();

		if (id == null || id.isEmpty()) {
			id = EMPTY_SYMBOL;
		}

		if (count == null) {
			count = DEFAULT_COUNT;
		}

		WrapCopier<MeasuresInfo, BaseAction.Wo> wrapout_copier = WrapCopierFactory.wo(MeasuresInfo.class,
				BaseAction.Wo.class, null, JpaObject.FieldsInvisible);

		String sequenceField = wrapIn.getSequenceField();
		EqualsTerms equals = new EqualsTerms();
		NotEqualsTerms notEquals = new NotEqualsTerms();
		LikeTerms likes = new LikeTerms();
		InTerms ins = new InTerms();
		NotInTerms notIns = new NotInTerms();
		MemberTerms members = new MemberTerms();
		NotMemberTerms notMembers = new NotMemberTerms();
		Boolean andJoin = false;

		String order = ASC;

		result = this.standardListPrev(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins, notIns,
				members, notMembers, null, andJoin, order);
		return result;
	}
}
