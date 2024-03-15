package com.x.program.center.jaxrs.market;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;

class ActionDownload extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionDownload.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Application2 app = null;
            String token = Business.loginCollect();
            if (StringUtils.isNotEmpty(token)) {
                try {
                    ActionResponse response = ConnectionAction.get(
                            Config.collect().url(COLLECT_MARKET_INSTALL_INFO + id),
                            ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, token)));
                    app = response.getData(Application2.class);
                } catch (Exception e) {
                    logger.warn("get market info form o2cloud error: {}.", e.getMessage());
                }
            }
            if(app == null){
                throw new ExceptionEntityNotExist(id);
            }
            if(!BooleanUtils.isTrue(app.getHasInstallPermission())){
                throw new ExceptionAccessDenied(Config.collect().getName());
            }
            byte[] bytes = ConnectionAction.getBinary(
                    Config.collect().url(Collect.ADDRESS_COLLECT_APPLICATION_DOWN + "/" + id),
                    ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, token)));
            if ((null != bytes) && (bytes.length > 0)) {
                Wo wo = new Wo(bytes, this.contentType(false, app.getName()+".zip"),
                        this.contentDisposition(false, app.getName()+".zip"));
                result.setData(wo);
            }

            return result;
        }
    }

    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }



}
