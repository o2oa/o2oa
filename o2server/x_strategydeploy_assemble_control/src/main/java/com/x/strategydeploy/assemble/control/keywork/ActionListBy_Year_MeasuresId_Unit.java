package com.x.strategydeploy.assemble.control.keywork;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.KeyworkInfo;
import com.x.strategydeploy.core.entity.KeyworkInfo_;

public class ActionListBy_Year_MeasuresId_Unit extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionListBy_Year_MeasuresId_Unit.class);

    public ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String year, String measuresId, String dept) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            EntityManager em = business.entityManagerContainer().get(KeyworkInfo.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<KeyworkInfo> cq = cb.createQuery(KeyworkInfo.class);
            Root<KeyworkInfo> root = cq.from(KeyworkInfo.class);
            Predicate p = cb.equal(root.get(KeyworkInfo_.keyworkyear), year);
            p = cb.and(p, cb.equal(root.get(KeyworkInfo_.keyworkunit), dept));
            p = cb.and(p, root.get(KeyworkInfo_.measureslist).in(measuresId));
            cq.select(root).where(p);
            List<KeyworkInfo> objs = em.createQuery(cq).getResultList();
            List<Wo> wrapOutList = new ArrayList<Wo>();
            wrapOutList = Wo.copier.copy(objs);
            result.setData(wrapOutList);
/*            List<String> list = new ArrayList<>();
            for (KeyworkInfo keyworkinfo : objs) {
                if (StringUtils.isNotBlank(keyworkinfo.getKeyworkyear())) {
                    list.add(keyworkinfo.getKeyworkyear());
                }
            }
            自然序逆序元素，使用Comparator 提供的reverseOrder () 方法
                    list = list.stream().filter(o -> !StringUtils.isEmpty(o)).distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            Wo wo = new Wo();
            wo.setValueList(list);
            return wo;*/
            return result;
        } catch (Exception e) {
            throw e;
        }
    }
}
