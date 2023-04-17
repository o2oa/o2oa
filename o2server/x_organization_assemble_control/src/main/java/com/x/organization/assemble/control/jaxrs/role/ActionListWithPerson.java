package com.x.organization.assemble.control.jaxrs.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.bean.tuple.Quintuple;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

class ActionListWithPerson extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithPerson.class);

    @SuppressWarnings("unchecked")
    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String personFlag) throws Exception {

        LOGGER.debug("execute:{}, personFlag:{}.", effectivePerson::getDistinguishedName, () -> personFlag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            CacheKey cacheKey = new Cache.CacheKey(this.getClass(), personFlag);
            Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
            if (optional.isPresent()) {
                result.setData((List<Wo>) optional.get());
            } else {
                List<Wo> wos = this.list(business, personFlag);
                CacheManager.put(business.cache(), cacheKey, wos);
                result.setData(wos);
            }
            this.updateControl(effectivePerson, business, result.getData());
            return result;
        }
    }

    public static class Wo extends WoRoleAbstract {

        private static final long serialVersionUID = -125007357898871894L;

        static WrapCopier<Role, Wo> copier = WrapCopierFactory.wo(Role.class, Wo.class, null,
                JpaObject.FieldsInvisible);

    }

    private List<Wo> list(Business business, String personFlag) throws Exception {
        Person person = business.person().pick(personFlag);
        if (null == person) {
            throw new ExceptionPersonNotExist(personFlag);
        }
        Optional<Quintuple<Collection<String>, Collection<String>, Collection<String>, Collection<String>, Collection<String>>> detail = business
                .detailOfPerson(personFlag, true, true, true, true, false);
        List<Wo> wos = new ArrayList<>();
        if (detail.isPresent()) {
            wos = Wo.copier.copy(business.entityManagerContainer().list(Role.class, detail.get().fourth()));
            wos = business.role().sort(wos);
        }
        return wos;
    }

}
