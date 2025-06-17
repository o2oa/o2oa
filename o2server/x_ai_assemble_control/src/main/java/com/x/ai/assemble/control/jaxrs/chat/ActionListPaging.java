package com.x.ai.assemble.control.jaxrs.chat;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.core.entity.Clue;
import com.x.ai.core.entity.Clue_;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionListPaging extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size)
            throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        ActionResult<List<Wo>> result = new ActionResult<>();
        page = this.adjustPage(page);
        size = this.adjustSize(size);
        String person = effectivePerson.getUnique();
        AiConfig aiConfig = Business.getConfig();
        if (BooleanUtils.isTrue(aiConfig.getO2AiEnable())
                && StringUtils.isNotBlank(aiConfig.getO2AiBaseUrl())
                && StringUtils.isNotBlank(aiConfig.getO2AiToken())) {
            String url =
                    aiConfig.getO2AiBaseUrl() + "/ai-gateway-clue/list/paging/" + page + "/size/"
                            + size;
            List<NameValuePair> heads = List.of(
                    new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
            Map<String, Object> map = new HashMap<>();
            map.put("personList", List.of(person));
            ActionResponse resp = ConnectionAction.post(url, heads, map);
            result.setData(resp.getDataAsList(Wo.class));
            result.setCount(resp.getCount());
        } else {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                EntityManager em = emc.get(Clue.class);
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Clue> cq = cb.createQuery(Clue.class);
                Root<Clue> root = cq.from(Clue.class);
                Predicate p = cb.equal(root.get(Clue_.person), person);
                List<Wo> wos = emc.fetchDescPaging(
                        Clue.class, Wo.copier, p, page, size, JpaObject.createTime_FIELDNAME);
                wos.forEach(wo -> {
                    wo.setCreateDateTime(wo.getCreateTime());
                    wo.setUpdateDateTime(wo.getUpdateTime());
                });
                result.setData(wos);
                result.setCount(emc.count(Clue.class, p));
            }
        }
        return result;
    }


    public static class Wo extends Clue {

        static WrapCopier<Clue, Wo> copier = WrapCopierFactory.wo(Clue.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible, "createDateTime", "updateDateTime"));
    }

}
