package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.express.assemble.surface.jaxrs.task.ActionListNextFilterWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListNextFilter extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListNextFilter.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, id:{}, count:{}.", effectivePerson::getDistinguishedName, () -> id, () -> count);
		ActionResult<List<Wo>> result = new ActionResult<>();
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		EqualsTerms equals = new EqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();
		equals.put(Task.person_FIELDNAME, effectivePerson.getDistinguishedName());
		predicateApplication(wi, ins);
		predicateProcess(business, wi, ins);
		predicateCreatorUnit(wi, ins);
		predicateStartTimeMonth(wi, ins);
		predicateActivityName(wi, ins);
		predicateKey(wi, likes);
		result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null, likes, ins,
				null, null, null, null, true, DESC);
		return result;
	}

	private void predicateKey(Wi wi, LikeTerms likes) {
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
	}

	private void predicateActivityName(Wi wi, InTerms ins) {
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			ins.put(Task.activityName_FIELDNAME, wi.getActivityNameList());
		}
	}

	private void predicateStartTimeMonth(Wi wi, InTerms ins) {
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			ins.put(Task.startTimeMonth_FIELDNAME, wi.getStartTimeMonthList());
		}
	}

	private void predicateCreatorUnit(Wi wi, InTerms ins) {
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			ins.put(Task.creatorUnit_FIELDNAME, wi.getCreatorUnitList());
		}
	}

	private void predicateProcess(Business business, Wi wi, InTerms ins) throws Exception {
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if (BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				ins.put(Task.process_FIELDNAME, wi.getProcessList());
			} else {
				ins.put(Task.process_FIELDNAME, business.process().listEditionProcess(wi.getProcessList()));
			}
		}
	}

	private void predicateApplication(Wi wi, InTerms ins) {
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			ins.put(Task.application_FIELDNAME, wi.getApplicationList());
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionListNextFilter.Wi")
	public class Wi extends ActionListNextFilterWi {

		private static final long serialVersionUID = -4563125130716132895L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionListNextFilter.Wo")
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