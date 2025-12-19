package com.x.ai.assemble.control.jaxrs.chat;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.core.entity.Completion;
import com.x.ai.core.entity.Completion_;
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

class ActionListCompletionPaging extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListCompletionPaging.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String clueId, Integer page,
            Integer size)
            throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        ActionResult<List<Wo>> result = new ActionResult<>();
        page = this.adjustPage(page);
        size = this.adjustSize(size);
        AiConfig aiConfig = Business.getConfig();
        if (BooleanUtils.isTrue(aiConfig.getO2AiEnable())
                && StringUtils.isNotBlank(aiConfig.getO2AiBaseUrl())
                && StringUtils.isNotBlank(aiConfig.getO2AiToken())) {
            String url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-completion/list/paging/" + page
                    + "/size/" + size;
            List<NameValuePair> heads = List.of(
                    new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
            Map<String, Object> map = new HashMap<>();
            map.put("clueIdList", List.of(clueId));
            ActionResponse resp = ConnectionAction.post(url, heads, map);
            result.setData(resp.getDataAsList(Wo.class));
            result.setCount(resp.getCount());
        } else {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                EntityManager em = emc.get(Completion.class);
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Completion> cq = cb.createQuery(Completion.class);
                Root<Completion> root = cq.from(Completion.class);
                Predicate p = cb.equal(root.get(Completion_.clueId), clueId);
                List<Wo> wos = emc.fetchDescPaging(
                        Completion.class, Wo.copier, p, page, size, JpaObject.createTime_FIELDNAME);
                wos.forEach(wo -> {
                    wo.setCreateDateTime(wo.getCreateTime());
                    wo.setUpdateDateTime(wo.getUpdateTime());
                });
                result.setData(wos);
                result.setCount(emc.count(Completion.class, p));
            }
        }
        return result;
    }


    public static class Wo extends Completion {

        static WrapCopier<Completion, Wo> copier = WrapCopierFactory.wo(Completion.class, Wo.class,
                null,
                ListTools.toList(JpaObject.FieldsInvisible, "createDateTime", "updateDateTime",
                        Completion.referenceIdList_FIELDNAME, Completion.extra_FIELDNAME));
    }

}
