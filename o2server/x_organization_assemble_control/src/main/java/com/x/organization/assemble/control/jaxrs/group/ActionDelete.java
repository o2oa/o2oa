package com.x.organization.assemble.control.jaxrs.group;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

class ActionDelete extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String groupFlag) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Group group = business.group().pick(groupFlag);
            if (null == group) {
                throw new ExceptionGroupNotExist(groupFlag);
            }
            if (!business.editable(effectivePerson, group)) {
                throw new ExceptionDenyDeleteGroup(effectivePerson, groupFlag);
            }
            emc.beginTransaction(Group.class);
            group = emc.find(group.getId(), Group.class);
            emc.beginTransaction(Role.class);
            // 删除有群组成员的群组和角色成员
            this.removeGroupMember(business, group);
            this.removeRoleMember(business, group);
            emc.remove(group, CheckRemoveType.all);
            emc.commit();
            CacheManager.notify(Group.class);

            Wo wo = new Wo();
            wo.setId(group.getId());
            result.setData(wo);
            return result;
        }
    }

    private void removeGroupMember(Business business, Group group) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Group> cq = cb.createQuery(Group.class);
        Root<Group> root = cq.from(Group.class);
        Predicate p = cb.isMember(group.getId(), root.get(Group_.groupList));
        List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList();
        for (Group o : os) {
            o.getGroupList().remove(group.getId());
        }
    }

    private void removeRoleMember(Business business, Group group) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Role.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);
        Root<Role> root = cq.from(Role.class);
        Predicate p = cb.isMember(group.getId(), root.get(Role_.groupList));
        List<Role> os = em.createQuery(cq.select(root).where(p)).getResultList();
        for (Role o : os) {
            o.getGroupList().remove(group.getId());
        }
    }

    public static class Wo extends WoId {

    }

}