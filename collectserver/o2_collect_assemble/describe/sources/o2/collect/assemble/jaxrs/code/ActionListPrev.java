package o2.collect.assemble.jaxrs.code;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.core.entity.Code;

class ActionListPrev extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			result = this.standardListPrev(Wo.copier, id, count, "sequence", null, null, null, null, null, null, null,
					true, DESC);
			return result;
		}
	}

	public static class Wo extends Code {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("排序号")
		private Long rank;

		static WrapCopier<Code, Wo> copier = WrapCopierFactory.wo(Code.class, Wo.class,
				JpaObject.singularAttributeField(Code.class, true, true), null);

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}