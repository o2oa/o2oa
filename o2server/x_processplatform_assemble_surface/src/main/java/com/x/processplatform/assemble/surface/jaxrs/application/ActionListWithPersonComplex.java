package com.x.processplatform.assemble.surface.jaxrs.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithPersonComplex extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithPersonComplex.class);

    @SuppressWarnings("unchecked")
    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        ActionResult<List<Wo>> result = new ActionResult<>();
        List<Wo> wos = new ArrayList<>();
        Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), effectivePerson.getDistinguishedName());
        Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
        if (optional.isPresent()) {
            wos = (List<Wo>) optional.get();
        } else {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
                // 去除部门以及上级部门,如果设置了一级部门可用,那么一级部门下属的二级部门也可用
                List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
                List<String> roles = business.organization().role().listWithPerson(effectivePerson);
                List<String> groups = business.organization().group().listWithIdentity(identities);
                List<String> ids = this.list(business, effectivePerson, roles, identities, units, groups);
                for (String id : ids) {
                    Application o = business.application().pick(id);
                    if (null != o) {
                        Wo wo = Wo.copier.copy(o);
                        wo.setProcessList(
                                this.referenceProcess(business, effectivePerson, identities, units, groups, o));
                        wos.add(wo);
                    }
                }
                wos = business.application().sort(wos);
                CacheManager.put(cacheCategory, cacheKey, wos);
            }
        }
        result.setData(wos);
        return result;
    }

    @Schema(name = "com.x.processplatform.assemble.surface.jaxrs.application.ActionListWithPersonComplex$Wo")
    public static class Wo extends Application {

        private static final long serialVersionUID = -4862564047240738097L;

        static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
                JpaObject.FieldsInvisible);

        @FieldDescribe("流程对象.")
        @Schema(description = "流程对象.")
        private List<WoProcess> processList;

        @FieldDescribe("是否可编辑.")
        @Schema(description = "是否可编辑.")
        private Boolean allowControl;

        public Boolean getAllowControl() {
            return allowControl;
        }

        public void setAllowControl(Boolean allowControl) {
            this.allowControl = allowControl;
        }

        public List<WoProcess> getProcessList() {
            return processList;
        }

        public void setProcessList(List<WoProcess> processList) {
            this.processList = processList;
        }

    }

    public static class WoProcess extends Process {

        private static final long serialVersionUID = 1521228691441978462L;

        static WrapCopier<Process, WoProcess> copier = WrapCopierFactory.wo(Process.class, WoProcess.class, null,
                JpaObject.FieldsInvisible);

    }

    /**
     * 从可见的application中获取一份ids<br/>
     * 从可启动的process中获取一份ids <br/>
     * 两份ids的交集,这样避免列示只有application没有可以启动process的应用
     */
    private List<String> list(Business business, EffectivePerson effectivePerson, List<String> roles,
            List<String> identities, List<String> units, List<String> groups) throws Exception {
        List<String> ids = this.listFromApplication(business, effectivePerson, roles, identities, units);
        List<String> fromProcessIds = this.listFromProcess(business, effectivePerson, roles, identities, units, groups);
        return ListUtils.intersection(ids, fromProcessIds);
    }

    private List<String> listFromApplication(Business business, EffectivePerson effectivePerson, List<String> roles,
            List<String> identities, List<String> units) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Application.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Application> root = cq.from(Application.class);
        if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
                OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
            Predicate p = cb.and(cb.isEmpty(root.get(Application_.availableIdentityList)),
                    cb.isEmpty(root.get(Application_.availableUnitList)));
            p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList)));
            // p = cb.or(p, cb.equal(root.get(Application_.creatorPerson),
            // effectivePerson.getDistinguishedName()));
            if (ListTools.isNotEmpty(identities)) {
                p = cb.or(p, root.get(Application_.availableIdentityList).in(identities));
            }
            if (ListTools.isNotEmpty(units)) {
                p = cb.or(p, root.get(Application_.availableUnitList).in(units));
            }
            cq.where(p);
        }
        return em.createQuery(cq.select(root.get(Application_.id))).getResultList().stream().distinct()
                .collect(Collectors.toList());
    }

    /**
     * 
     * 从Process中获取可以启动的Process的application.
     */
    private List<String> listFromProcess(Business business, EffectivePerson effectivePerson, List<String> roles,
            List<String> identities, List<String> units, List<String> groups) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Process.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Process> root = cq.from(Process.class);
        Predicate p = cb.conjunction();
        if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
                OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
            p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
                    cb.isEmpty(root.get(Process_.startableUnitList)),
                    cb.isEmpty(root.get(Process_.startableGroupList)));
            p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Process_.controllerList)));
            if (ListTools.isNotEmpty(identities)) {
                p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
            }
            if (ListTools.isNotEmpty(units)) {
                p = cb.or(p, root.get(Process_.startableUnitList).in(units));
            }
            if (ListTools.isNotEmpty(groups)) {
                p = cb.or(p, root.get(Process_.startableGroupList).in(groups));
            }
        }
        cq.select(root.get(Process_.application)).where(p);
        return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
    }

    private List<WoProcess> referenceProcess(Business business, EffectivePerson effectivePerson,
            List<String> identities, List<String> units, List<String> groups, Application application)
            throws Exception {
        List<String> ids = business.process().listStartableWithApplication(effectivePerson, identities, units, groups,
                application, "");
        List<WoProcess> wos = new ArrayList<>();
        for (String id : ids) {
            WoProcess o = WoProcess.copier.copy(business.process().pick(id));
            wos.add(o);
        }
        wos = business.process().sort(wos);
        return wos;
    }
}
