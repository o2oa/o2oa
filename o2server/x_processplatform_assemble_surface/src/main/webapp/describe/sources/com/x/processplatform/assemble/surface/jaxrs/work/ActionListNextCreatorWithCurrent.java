package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.assemble.surface.jaxrs.work.ActionListNextWithApplication.Wo;
import com.x.processplatform.assemble.surface.jaxrs.work.ActionListNextWithApplication.WoControl;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.element.Application;

class ActionListNextCreatorWithCurrent extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EqualsTerms equals = new EqualsTerms();
			equals.put(Work.creatorPerson_FIELDNAME, effectivePerson.getDistinguishedName());
			ActionResult<List<Wo>> result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME,
					equals, null, null, null, null, null, null, null, true, DESC);
			return result;
		}
	}

	public static class Wo extends Work {

		private static final long serialVersionUID = -5668264661685818057L;

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
