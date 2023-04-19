package com.x.organization.assemble.control.jaxrs.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithController extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithController.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            CacheKey cacheKey = new CacheKey(this.getClass(), StringUtils.join(wi.getPersonList(), ","));
            Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
            if (optional.isPresent()) {
                result.setData((List<Wo>) optional.get());
            } else {
                List<Wo> wos = this.list(business, wi.getPersonList());
                CacheManager.put(business.cache(), cacheKey, wos);
                result.setData(wos);
            }
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {

        @Schema(description = "指定人员.")
        @FieldDescribe("指定人员.")
        private List<String> personList = new ArrayList<>();

        public List<String> getPersonList() {
            return personList;
        }

        public void setPersonList(List<String> personList) {
            this.personList = personList;
        }

    }

    public static class Wo extends WoAbstractUnit {

        static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class, null,
                JpaObject.FieldsInvisible);

    }

    private List<Wo> list(Business business, List<String> people) throws Exception {
        List<Person> list = business.person().pick(people);
        EntityManager em = business.entityManagerContainer().get(Unit.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
        Root<Unit> root = cq.from(Unit.class);
        List<String> ids = ListTools.extractField(list, JpaObject.id_FIELDNAME, String.class, true, true);
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        Predicate p = root.get(Unit_.controllerList)
                .in(ids);
        List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
        List<Wo> wos = Wo.copier.copy(os);
        wos = business.unit().sort(wos);
        return wos;
    }

}