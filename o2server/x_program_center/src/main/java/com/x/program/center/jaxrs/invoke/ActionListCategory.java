package com.x.program.center.jaxrs.invoke;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.portal.core.entity.Portal;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Invoke;
import com.x.program.center.core.entity.Invoke_;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

class ActionListCategory extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListCategory.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
        LOGGER.debug(effectivePerson.getDistinguishedName());
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            List<Wo> wos = new ArrayList<>();
            List<Wo> allWos = this.countPortCategory(business);
            List<Wo> defaultWos = allWos.stream().filter(o -> (StringUtils.isBlank(o.getCategory())
                            || Invoke.CATEGORY_DEFAULT.equals(o.getCategory())))
                    .collect(Collectors.toList());
            if (!defaultWos.isEmpty()) {
                Wo wo = new Wo();
                wo.setCategory(Portal.CATEGORY_DEFAULT);
                wo.setCount(defaultWos.stream().mapToLong(Wo::getCount).sum());
                wos.add(wo);
            }
            wos.addAll(allWos);
            wos.removeAll(defaultWos);
            result.setData(wos);
            return result;
        }
    }

    private List<Wo> countPortCategory(Business business) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Invoke.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Wo> cq = cb.createQuery(Wo.class);
        Root<Invoke> root = cq.from(Invoke.class);
        Predicate p = cb.conjunction();
        Path<String> path = root.get(Invoke_.category);
        cq.multiselect(path, cb.count(root)).where(p).groupBy(path).orderBy(cb.asc(path));
        return em.createQuery(cq).getResultList();
    }

    public static class Wo extends GsonPropertyObject {

        public Wo() {
        }

        public Wo(String date, Long count) {
            this.category = date;
            this.count = count;
        }

        @FieldDescribe("分类")
        private String category;

        @FieldDescribe("数量")
        private Long count;

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

}
