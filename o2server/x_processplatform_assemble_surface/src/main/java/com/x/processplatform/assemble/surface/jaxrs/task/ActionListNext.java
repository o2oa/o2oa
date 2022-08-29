package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListNext extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListNext.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		LOGGER.debug("execute:{}, id:{}, count:{}.", effectivePerson::getDistinguishedName, () -> id, () -> count);
		EqualsTerms equals = new EqualsTerms();
		equals.put(Task.person_FIELDNAME, effectivePerson.getDistinguishedName());
		return this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null, null, null, null,
				null, null, null, true, DESC);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionListNext.Wo")
	public static class Wo extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("排序号")
		@Schema(description = "排序号")
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
