package com.x.program.center.jaxrs.mpweixin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.core.entity.MPWeixinMenu;

abstract class BaseAction extends StandardJaxrsAction {

    public static final String WX_MSG_RECEIVE_TYPE_EVENT = "event"; //公众号接收到的消息类型 事件 主要是菜单点击和订阅取消订阅
    public static final String WX_MSG_RECEIVE_TYPE_TEXT = "text"; //公众号接收到的消息类型 文本



    public static final String WX_MSG_RECEIVE_EVENT_SUBSCRIBE = "subscribe"; //关注事件
    public static final String WX_MSG_RECEIVE_EVENT_UNSUBSCRIBE = "unsubscribe"; //取消关注事件
    public static final String WX_MSG_RECEIVE_EVENT_LOCATION = "LOCATION"; //上报地理位置事件
    public static final String WX_MSG_RECEIVE_EVENT_CLICK = "CLICK"; //自定义菜单事件
    public static final String WX_MSG_RECEIVE_EVENT_VIEW = "VIEW"; //点击菜单跳转链接时的事件推送


    public static final String WX_MSG_BACK_MEDIA_KEY_IMAGE = "media:image:";
    public static final String WX_MSG_BACK_MEDIA_KEY_VIDEO = "media:video:";
    public static final String WX_MSG_BACK_MEDIA_KEY_VOICE = "media:voice:";
    public static final String WX_MSG_BACK_MEDIA_KEY_SCRIPT = "o2oa:script:";





    protected void searchChildren(List<MPWeixinMenu> menus, List<WoMenu> firstLevelList) throws Exception {

        for (int i = 0; i < firstLevelList.size(); i++) {
            WoMenu parent = firstLevelList.get(i);
            List<WoMenu> sub_button = new ArrayList<>();
            for (int j = 0; j < menus.size(); j++) {
                MPWeixinMenu menu = menus.get(j);
                if (StringUtils.isNotBlank(menu.getParentId()) && parent.getId().equals(menu.getParentId())) {
                    sub_button.add(WoMenu.copier.copy(menu));
                }
            }
            if (!sub_button.isEmpty()) {
                sub_button.sort((o1, o2) -> {
                    if (StringUtils.isEmpty(o1.getOrder()) || StringUtils.isEmpty(o2.getOrder())) {
                        return 0;
                    }
                    return o1.getOrder().compareToIgnoreCase(o2.getOrder());
                });
                parent.setSub_button(sub_button);
            }
        }
    }

    protected MPWeixinMenu findMenuWithEventKey(String eventKey) throws Exception {
        if (StringUtils.isBlank(eventKey)) {
            return null;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<MPWeixinMenu> list = emc.listEqual(MPWeixinMenu.class, MPWeixinMenu.key_FIELDNAME, eventKey);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    public static class WoMenu extends MPWeixinMenu {
        private static final long serialVersionUID = 3434938936805201380L;
        static WrapCopier<MPWeixinMenu, WoMenu> copier = WrapCopierFactory.wo(MPWeixinMenu.class, WoMenu.class, null, FieldsInvisible);

        private List<WoMenu> sub_button;

        public List<WoMenu> getSub_button() {
            return sub_button;
        }

        public void setSub_button(List<WoMenu> sub_button) {
            this.sub_button = sub_button;
        }
    }


}
