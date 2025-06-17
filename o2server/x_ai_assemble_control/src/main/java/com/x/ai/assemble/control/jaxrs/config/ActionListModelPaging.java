package com.x.ai.assemble.control.jaxrs.config;

import com.x.ai.core.entity.AiModel;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

class ActionListModelPaging extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListModelPaging.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size)
            throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        if (effectivePerson.isNotManager()) {
            throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
        }
        ActionResult<List<Wo>> result = new ActionResult<>();
        page = this.adjustPage(page);
        size = this.adjustSize(size);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(AiModel.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            Predicate p = cb.conjunction();
            List<Wo> wos = emc.fetchDescPaging(
                    AiModel.class, Wo.copier, p, page, size,
                    JpaObject.createTime_FIELDNAME);

            result.setData(wos);
            result.setCount(emc.count(AiModel.class, p));
        }
        return result;
    }


    public static class Wo extends AiModel {

        static WrapCopier<AiModel, Wo> copier = WrapCopierFactory.wo(AiModel.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible, "createDateTime", "updateDateTime"));
    }

}
