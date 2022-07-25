package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
import com.x.processplatform.core.entity.content.ReadCompleted_;

import io.swagger.v3.oas.annotations.media.Schema;

class V2ListCreatePrev extends V2Base {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2ListCreatePrev.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, id:{}, count:{}.", effectivePerson::getDistinguishedName, () -> id, () -> count);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			EntityManager em = emc.get(ReadCompleted.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<ReadCompleted> root = cq.from(ReadCompleted.class);
			Predicate p = cb.equal(root.get(ReadCompleted_.creatorPerson), effectivePerson.getDistinguishedName());
			p = cb.and(p, this.toFilterPredicate(effectivePerson, business, wi));
			ActionResult<List<Wo>> result = this.standardListPrev(Wo.copier, id, count, JpaObject.sequence_FIELDNAME,
					DESC, p);
			this.relate(business, result.getData(), wi);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.V2ListCreatePrev$Wi")
	public static class Wi extends RelateFilterWi {

		private static final long serialVersionUID = -6471031580983300176L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.V2ListCreatePrev$Wo")
	public static class Wo extends AbstractWo {
		private static final long serialVersionUID = -4773789253221941109L;
		static WrapCopier<ReadCompleted, Wo> copier = WrapCopierFactory.wo(ReadCompleted.class, Wo.class,
				JpaObject.singularAttributeField(ReadCompleted.class, true, false), JpaObject.FieldsInvisible);
	}
}
