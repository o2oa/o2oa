package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

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
import com.x.processplatform.core.entity.content.ReadCompleted;

import io.swagger.v3.oas.annotations.media.Schema;

class V2ListPaging extends V2Base {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(V2ListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		
		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			List<Wo> wos = emc.fetchDescPaging(ReadCompleted.class, Wo.copier, p, page, size, ReadCompleted.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(ReadCompleted.class, p));
			this.relate(business, result.getData(), wi);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.V2ListPaging$Wi")
	public static class Wi extends RelateFilterWi {

		private static final long serialVersionUID = -643629328471810838L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.V2ListPaging$Wo")
	public static class Wo extends AbstractWo {
		private static final long serialVersionUID = -4773789253221941109L;
		static WrapCopier<ReadCompleted, Wo> copier = WrapCopierFactory.wo(ReadCompleted.class, Wo.class,
				JpaObject.singularAttributeField(ReadCompleted.class, true, false), JpaObject.FieldsInvisible);
	}
}
