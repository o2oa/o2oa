package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import javax.persistence.criteria.Predicate;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;

import io.swagger.v3.oas.annotations.media.Schema;

class V2ListPaging extends V2Base {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2ListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}. page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			List<Wo> wos = emc.fetchDescPaging(Task.class, Wo.copier, p, page, size, Task.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(Task.class, p));
			this.relate(business, result.getData(), wi);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.V2ListPaging$Wi")
	public static class Wi extends RelateFilterWi {

		private static final long serialVersionUID = -3322648572903820279L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.V2ListPaging$Wo")
	public static class Wo extends AbstractWo {

		private static final long serialVersionUID = -4773789253221941109L;

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class,
				JpaObject.singularAttributeField(Task.class, true, false), JpaObject.FieldsInvisible);

	}
}
