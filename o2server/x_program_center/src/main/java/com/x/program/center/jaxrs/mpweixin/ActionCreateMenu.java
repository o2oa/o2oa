package com.x.program.center.jaxrs.mpweixin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Mpweixin;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.MPWeixinMenu;

/**
 * Created by fancyLou on 3/15/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionCreateMenu  extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(ActionCreateMenu.class);

    ActionResult<Wo> execute() throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (Config.mpweixin() == null || BooleanUtils.isNotTrue(Config.mpweixin().getEnable()) || BooleanUtils.isNotTrue(Config.mpweixin().getEnablePublish())) {
            throw new ExceptionConfigError();
        }
        String accessToken = Config.mpweixin().accessToken();
        logger.info("accessToken: "+accessToken);
        String createUrl = Mpweixin.default_apiAddress + "/cgi-bin/menu/create?access_token="+accessToken;
        logger.info("url: "+createUrl);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<MPWeixinMenu> menus = emc.listAll(MPWeixinMenu.class);
            if (menus == null) {
                menus = new ArrayList<>();
            }
            List<WoMenu> firstLevelList = new ArrayList<>();
            for (int i = 0; i < menus.size(); i++) {
                MPWeixinMenu menu = menus.get(i);
                if (menu.getType()!=null && WX_MSG_RECEIVE_EVENT_SUBSCRIBE.equalsIgnoreCase(menu.getType())) {
                    continue;
                }
                if (StringUtils.isEmpty(menu.getParentId())) {
                    firstLevelList.add(WoMenu.copier.copy(menu));
                }
            }
            searchChildren(menus, firstLevelList);
            firstLevelList.sort((o1, o2) -> {
                if (StringUtils.isEmpty(o1.getOrder()) || StringUtils.isEmpty(o2.getOrder())) {
                    return 0;
                }
                return o1.getOrder().compareToIgnoreCase(o2.getOrder());
            });
            WeixinMenuObj obj = new WeixinMenuObj();
            obj.setButton(firstLevelList);
//            formatWeixinMenu(obj);
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            String json = gson.toJson(obj);
            logger.info(json);
            WeixinResp resp = HttpConnection.postAsObject(createUrl, null, json, WeixinResp.class);
            if (resp.getErrcode() != null && resp.getErrcode() == 0) {
                logger.info("保存菜单成功！");
                Wo wo = new Wo();
                wo.setValue(true);
                result.setData(wo);
                return result;
            }else {
                logger.info(resp.toString());
                throw new ExceptionCreateMenuError(resp.getErrcode(), resp.getErrmsg());
            }
        }
    }

    private void formatWeixinMenu(WeixinMenuObj obj) {
        if (obj.getButton()!=null && !obj.getButton().isEmpty()){
            for (int i = 0; i < obj.getButton().size(); i++) {
                WoMenu menu = obj.getButton().get(i);
                menu.setId(null);
                menu.setParentId(null);
                menu.setCreateTime(null);
                menu.setUpdateTime(null);
                menu.setOrder(null);
                menu.setContent(null);
                if (menu.getSub_button()!=null && !menu.getSub_button().isEmpty()) {
                    for (int j = 0; j < menu.getSub_button().size(); j++) {
                        WoMenu child = menu.getSub_button().get(j);
                        child.setId(null);
                        child.setParentId(null);
                        child.setCreateTime(null);
                        child.setUpdateTime(null);
                        child.setOrder(null);
                        child.setContent(null);
                    }
                }
            }
        }
    }


    public static class Wo extends WrapBoolean {

    }

    public static class WeixinMenuObj extends GsonPropertyObject {
        private List<WoMenu> button ;

        public List<WoMenu> getButton() {
            return button;
        }

        public void setButton(List<WoMenu> button) {
            this.button = button;
        }
    }


    public static class WeixinResp  extends GsonPropertyObject {
        private String errmsg;
        private Integer errcode;

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }
    }
}
