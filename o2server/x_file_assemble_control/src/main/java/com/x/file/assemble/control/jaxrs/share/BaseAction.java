package com.x.file.assemble.control.jaxrs.share;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.Group;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.service.FileCommonService;
import com.x.file.core.entity.personal.Share;
import com.x.file.core.entity.personal.Share_;

abstract class BaseAction extends StandardJaxrsAction {

    protected FileCommonService fileCommonService = new FileCommonService();

    protected void message_send_attachment_share(Share share, String person) throws Exception {
        String title = "收到来自(" + OrganizationDefinition.name(share.getPerson()) + ")的共享文件:" + share.getName()
                + ".";
        MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_SHARE, title, person,
                XGsonBuilder.convert(share, Share.class));
    }

    protected void message_send_attachment_shareCancel(Share share, String person) throws Exception {
        String title = "(" + OrganizationDefinition.name(share.getPerson()) + ")取消了对:" + share.getName()
                + ",文件的共享.";
        MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_SHARECANCEL, title, person,
                XGsonBuilder.convert(share, Share.class));
    }

    protected boolean exist(Business business, EffectivePerson effectivePerson, String name,
                            String excludeId) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Share.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Share> root = cq.from(Share.class);
        Predicate p = cb.equal(root.get(Share_.person), effectivePerson.getDistinguishedName());
        p = cb.and(p, cb.equal(root.get(Share_.fileId), name));
        if (StringUtils.isNotEmpty(excludeId)) {
            p = cb.and(p, cb.notEqual(root.get(Share_.id), excludeId));
        }
        cq.select(cb.count(root)).where(p);
        long count = em.createQuery(cq).getSingleResult();
        return count > 0;
    }

    protected boolean hasPermission(Business business, EffectivePerson effectivePerson, Share share) throws Exception {
        boolean flag = true;
        if (!StringUtils.equals(effectivePerson.getDistinguishedName(), share.getPerson())
                && (!share.getShareUserList().contains(effectivePerson.getDistinguishedName()))) {
            flag = false;
            List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
            for (String str : identities) {
                List<String> units = business.organization().unit().listWithIdentitySupNested(str);
                for (String unitName : units) {
                    Unit unit = business.organization().unit().getObject(unitName);
                    if (unit != null) {
                        if (share.getShareOrgList().contains(unit.getUnique())) {
                            return true;
                        }
                    }
                }
            }
            List<String> groupIds = business.organization().group().listWithPersonReference(
                    ListTools.toList(effectivePerson.getDistinguishedName()),true,true, false);
            if(ListTools.isNotEmpty(groupIds)) {
                List<Group> groupList = business.organization().group().listObject(groupIds);
                for(Group group : groupList) {
                    if (share.getShareGroupList().contains(group.getUnique())) {
                        return true;
                    }
                }
            }

        }
        return flag;
    }

}
