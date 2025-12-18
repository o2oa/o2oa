package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.message.assemble.communicate.Business;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMConversation;
import java.net.URLEncoder;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class ActionGetConversationIcon extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetConversationIcon.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String conversationId)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            ActionResult<Wo> result = new ActionResult<>();
            CacheKey cacheKey = new CacheKey(this.getClass(), conversationId);
            Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
            if (optional.isPresent()) {
                LOGGER.debug("缓存获取 {}", conversationId);
                result.setData((Wo) optional.get());
            } else {
                Wo wo = this.get(emc, effectivePerson, conversationId);
                CacheManager.put(business.cache(), cacheKey, wo);
                result.setData(wo);
            }
            return result;
        }
    }

    private Wo get(EntityManagerContainer emc, EffectivePerson effectivePerson,
            String conversationId) throws Exception {
        IMConversation conversation = emc.find(conversationId, IMConversation.class);
        if (conversation == null) {
            LOGGER.info("没有找到会话对象 {}", conversationId);
            throw new ExceptionConversationNotExist();
        }
        String base64 = null;
        if (IMConversation.CONVERSATION_TYPE_GROUP.equals(conversation.getType())) {
            if (StringUtils.isNotEmpty(conversation.getGroupIcon())) {
                base64 = conversation.getGroupIcon();
            }
        } else if (IMConversation.CONVERSATION_TYPE_SINGLE.equals(conversation.getType())) {
            var otherPerson = conversation.getPersonList().stream()
                    .filter(p -> !p.equals(effectivePerson.getDistinguishedName())).findFirst();
            if (otherPerson.isPresent()) {
                try {
                    var dn = otherPerson.get();
                    dn = URLEncoder.encode(dn, DefaultCharset.name);
                    Application app = ThisApplication.context().applications()
                            .randomWithWeight(x_organization_assemble_control.class.getName());
                    byte[] personIcon =  ThisApplication.context().applications()
                            .getQueryBinary(false, app, "person/" + dn + "/icon");
                    if (personIcon != null) {
                        return new Wo(personIcon, this.contentType(false, "icon.png"),
                                this.contentDisposition(false, "icon.png"));
                    }
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
        if (base64 == null) {
            LOGGER.info("没有找到会话头像 icon");
            base64 = ICON_UNKOWN;
        }
        byte[] bs = Base64.decodeBase64(base64);
        return new Wo(bs, this.contentType(false, "icon.png"),
                this.contentDisposition(false, "icon.png"));
    }


    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }


}
