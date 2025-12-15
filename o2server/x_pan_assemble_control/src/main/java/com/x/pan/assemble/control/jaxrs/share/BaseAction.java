package com.x.pan.assemble.control.jaxrs.share;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.Group;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.file.core.entity.personal.Share;
import com.x.file.core.entity.personal.Share_;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.service.FileCommonService;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

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
        if(effectivePerson.getDistinguishedName().equals(share.getPerson()) ||
                share.getShareUserList().contains(effectivePerson.getDistinguishedName())){
            return true;
        }
        if(ListTools.isNotEmpty(share.getShareOrgList())){
            List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson.getDistinguishedName());
            if(ListTools.containsAny(share.getShareOrgList(), units)){
                return true;
            }
        }
        if(ListTools.isNotEmpty(share.getShareGroupList())){
            List<String> groups = business.organization().group().listWithPersonReference(
                    ListTools.toList(effectivePerson.getDistinguishedName()),true,true, false);
            if(ListTools.containsAny(share.getShareGroupList(), groups)){
                return true;
            }
        }
        return false;
    }

}
