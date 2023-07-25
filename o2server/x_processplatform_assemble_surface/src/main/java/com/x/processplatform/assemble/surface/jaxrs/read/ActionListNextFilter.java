package com.x.processplatform.assemble.surface.jaxrs.read;

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
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.express.assemble.surface.jaxrs.read.ActionListNextFilterWi;

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
		equals.put(Read.person_FIELDNAME, effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			ins.put(Read.application_FIELDNAME, wi.getApplicationList());
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if (BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				ins.put(Read.process_FIELDNAME, wi.getProcessList());
			} else {
				ins.put(Read.process_FIELDNAME, business.process().listEditionProcess(wi.getProcessList()));
			}
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			ins.put(Read.creatorUnit_FIELDNAME, wi.getCreatorUnitList());
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			ins.put(Read.startTimeMonth_FIELDNAME, wi.getStartTimeMonthList());
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			ins.put(Read.activityName_FIELDNAME, wi.getActivityNameList());
		}
		if (StringUtils.isNotEmpty(wi.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				likes.put(Read.title_FIELDNAME, key);
				likes.put(Read.opinion_FIELDNAME, key);
				likes.put(Read.serial_FIELDNAME, key);
				likes.put(Read.creatorPerson_FIELDNAME, key);
				likes.put(Read.creatorUnit_FIELDNAME, key);
			}
		}
		result = this.standardListNext(Wo.copier, id, count, Read.sequence_FIELDNAME, equals, null, likes, ins, null,
				null, null, null, true, DESC);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionListNextFilter$Wi")
	public class Wi extends ActionListNextFilterWi {

		private static final long serialVersionUID = 7663402485733004299L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionListNextFilter$Wo")
	public static class Wo extends Read {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Read, Wo> copier = WrapCopierFactory.wo(Read.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("排序号.")
		@Schema(description = "排序号.")
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
