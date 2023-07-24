package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionManageListNextFilterWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageListNextFilter extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> person_ids = business.organization().person().list(wi.getCredentialList());
			EqualsTerms equals = new EqualsTerms();
			InTerms ins = new InTerms();
			LikeTerms likes = new LikeTerms();
			if (ListTools.isNotEmpty(wi.getApplicationList())) {
				ins.put(Task.application_FIELDNAME, wi.getApplicationList());
			}
			if (ListTools.isNotEmpty(wi.getProcessList())) {
				ins.put(Task.process_FIELDNAME, wi.getProcessList());
			}
			if (ListTools.isNotEmpty(person_ids)) {
				ins.put(Task.person_FIELDNAME, person_ids);
			}
			if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
				ins.put(Task.creatorUnit_FIELDNAME, wi.getCreatorUnitList());
			}
			if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
				ins.put(Task.startTimeMonth_FIELDNAME, wi.getStartTimeMonthList());
			}
			if (ListTools.isNotEmpty(wi.getActivityNameList())) {
				ins.put(Task.activityName_FIELDNAME, wi.getActivityNameList());
			}
			if (StringUtils.isNotEmpty(wi.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put(Task.title_FIELDNAME, key);
					likes.put(Task.opinion_FIELDNAME, key);
					likes.put(Task.serial_FIELDNAME, key);
					likes.put(Task.creatorPerson_FIELDNAME, key);
					likes.put(Task.creatorUnit_FIELDNAME, key);
				}
			}
			if (effectivePerson.isManager()) {
				result = this.standardListNext(Wo.copier, id, count, Task.sequence_FIELDNAME, equals, null, likes, ins,
						null, null, null, null, true, DESC);
			}
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageListNextFilter.Wi")
	public class Wi extends ActionManageListNextFilterWi {

		private static final long serialVersionUID = 851950023034796966L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageListNextFilter.Wo")
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
