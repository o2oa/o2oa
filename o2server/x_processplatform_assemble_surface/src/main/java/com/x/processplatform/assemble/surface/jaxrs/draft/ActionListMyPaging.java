package com.x.processplatform.assemble.surface.jaxrs.draft;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

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
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.content.Draft_;
import com.x.processplatform.core.express.assemble.surface.jaxrs.draft.ActionListMyPagingWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListMyPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListMyPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (wi == null) {
				wi = new Wi();
			}
			Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
			List<Wo> wos = emc.fetchDescPaging(Draft.class, Wo.copier, p, page, size, Draft.createTime_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(Draft.class, p));
			return result;
		}
	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Draft.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Draft> cq = cb.createQuery(Draft.class);
		Root<Draft> root = cq.from(Draft.class);
		Predicate p = cb.equal(root.get(Draft_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Draft_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if (BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				p = cb.and(p, root.get(Draft_.process).in(wi.getProcessList()));
			} else {
				p = cb.and(p, root.get(Draft_.process).in(business.process().listEditionProcess(wi.getProcessList())));
			}
		}
		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getStartTime()))) {
			p = cb.and(p, cb.greaterThan(root.get(Draft_.createTime), DateTools.parse(wi.getStartTime())));
		}
		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getEndTime()))) {
			p = cb.and(p, cb.lessThan(root.get(Draft_.createTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Draft_.unit).in(wi.getCreatorUnitList()));
		}
		if (StringUtils.isNoneBlank(wi.getTitle())) {
			String key = StringTools.escapeSqlLikeKey(wi.getTitle());
			p = cb.and(p, cb.like(root.get(Draft_.title), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
		}

		return p;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionListMyPaging$Wo")
	public static class Wo extends Draft {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Draft, Wo> copier = WrapCopierFactory.wo(Draft.class, Wo.class,
				JpaObject.singularAttributeField(Draft.class, true, true), null);

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionListMyPaging$Wi")
	public class Wi extends ActionListMyPagingWi {

		private static final long serialVersionUID = 884991136522875211L;

	}

}
