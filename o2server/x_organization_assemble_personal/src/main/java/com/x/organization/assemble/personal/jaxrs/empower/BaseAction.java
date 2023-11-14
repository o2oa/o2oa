package com.x.organization.assemble.personal.jaxrs.empower;

import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.accredit.Empower;
import com.x.organization.core.entity.accredit.Empower_;

abstract class BaseAction extends StandardJaxrsAction {

    protected void check(Business business, Empower empower) throws Exception {
        if (StringUtils.isEmpty(empower.getFromIdentity())) {
            throw new ExceptionEmptyFromIdentity();
        }
        if (StringUtils.isEmpty(empower.getToIdentity())) {
            throw new ExceptionEmptyToIdentity();
        }
        switch (Objects.toString(empower.getType())) {
            case Empower.TYPE_ALL:
                if (this.typeAllExist(business, empower)) {
                    throw new ExceptionTypeAllExist(empower.getFromIdentity());
                }
                break;
            case Empower.TYPE_APPLICATION:
                if (this.typeApplicationExist(business, empower)) {
                    throw new ExceptionTypeApplicationExist(empower.getFromIdentity(), empower.getApplication());
                }
                break;
            case Empower.TYPE_PROCESS:
                if (this.typeProcessExist(business, empower)) {
                    throw new ExceptionTypeProcessExist(empower.getFromIdentity(), empower.getProcess());
                }
                break;
            case Empower.TYPE_FILTER:
                if (StringUtils.isEmpty(empower.getProcess()) || StringUtils.isEmpty(empower.getFilterListData())) {
                    throw new ExceptionTypeFilter(empower.getFromIdentity(), empower.getProcess(),
                            empower.getFilterListData());
                }
                break;
            default:
                throw new ExceptionEntityFieldEmpty(Empower.class, Empower.TYPE_FIELDNAME);
        }
    }

    /**
     * 检查是否在时间段内有,1.开始时间处于时间段内 2.结束时间处于时间段内 3,开始时间小于指定开始时间且结束时间大于指定结束时间
     * 合并后就是!(开始时间大于指定结束时间||结束时间小于指定开始时间)
     * 考虑到正好完全一致的时间点发生了授权可能性小允许结束时间等于新的开始时间
     * @param business
     * @param empower
     * @return
     * @throws Exception
     */
    private boolean typeAllExist(Business business, Empower empower) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Empower.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Empower> root = cq.from(Empower.class);
        Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
        p = cb.and(p, cb.equal(root.get(Empower_.type), Empower.TYPE_ALL));
        p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
        p = cb.and(p, cb.equal(root.get(Empower_.enable), true));
        p = cb.and(p, cb.not(cb.or(cb.greaterThanOrEqualTo(root.get(Empower_.startTime), empower.getCompletedTime()),
                cb.lessThanOrEqualTo(root.get(Empower_.completedTime), empower.getStartTime()))));
        Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        return count > 0;
    }

    /**
     * 检查是否在时间段内有,1.开始时间处于时间段内 2.结束时间处于时间段内 3,开始时间小于指定开始时间且结束时间大于指定结束时间
     * 合并后就是!(开始时间大于指定结束时间||结束时间小于指定开始时间)
     * 考虑到正好完全一致的时间点发生了授权可能性小允许结束时间等于新的开始时间
     * @param business
     * @param empower
     * @return
     * @throws Exception
     */
    private boolean typeApplicationExist(Business business, Empower empower) throws Exception {
        if (StringUtils.isEmpty(empower.getApplication())) {
            throw new ExceptionEntityFieldEmpty(Empower.class, Empower.APPLICATION_FIELDNAME);
        }
        EntityManager em = business.entityManagerContainer().get(Empower.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Empower> root = cq.from(Empower.class);
        Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
        p = cb.and(p, cb.equal(root.get(Empower_.type), Empower.TYPE_APPLICATION));
        p = cb.and(p, cb.equal(root.get(Empower_.application), empower.getApplication()));
        p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
        p = cb.and(p, cb.equal(root.get(Empower_.enable), true));
        p = cb.and(p, cb.not(cb.or(cb.greaterThanOrEqualTo(root.get(Empower_.startTime), empower.getCompletedTime()),
                cb.lessThanOrEqualTo(root.get(Empower_.completedTime), empower.getStartTime()))));
        Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        return count > 0;
    }

    /**
     * 检查是否在时间段内有,1.开始时间处于时间段内 2.结束时间处于时间段内 3,开始时间小于指定开始时间且结束时间大于指定结束时间
     * 合并后就是!(开始时间大于指定结束时间||结束时间小于指定开始时间)
     * 考虑到正好完全一致的时间点发生了授权可能性小允许结束时间等于新的开始时间
     * @param business
     * @param empower
     * @return
     * @throws Exception
     */
    private boolean typeProcessExist(Business business, Empower empower) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Empower.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Empower> root = cq.from(Empower.class);
        Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
        p = cb.and(p, cb.equal(root.get(Empower_.type), Empower.TYPE_PROCESS));
        p = cb.and(p, cb.equal(root.get(Empower_.process), empower.getProcess()));
        p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
        p = cb.and(p, cb.equal(root.get(Empower_.enable), true));
        p = cb.and(p, cb.not(cb.or(cb.greaterThanOrEqualTo(root.get(Empower_.startTime), empower.getCompletedTime()),
                cb.lessThanOrEqualTo(root.get(Empower_.completedTime), empower.getStartTime()))));
        Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        return count > 0;
    }

    protected String getPersonDNWithIdentityDN(Business business, String dn) throws Exception {
        Identity identity = business.identity().pick(dn);
        if (null != identity) {
            Person person = business.person().pick(identity.getPerson());
            if (null != person) {
                return person.getDistinguishedName();
            }
        }
        return null;
    }
}