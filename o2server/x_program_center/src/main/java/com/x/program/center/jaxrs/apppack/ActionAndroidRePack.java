package com.x.program.center.jaxrs.apppack;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

/**
 * Created by fancyLou on 6/17/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionAndroidRePack extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger(ActionAndroidPack.class);


    ActionResult<Wo> execute(String token) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(token)) {
            throw new ExceptionNoToken();
        }
        String collectNameEncode = URLEncoder.encode(Config.collect().getName(), DefaultCharset.name);
        String url = Config.collect().appPackServerApi(String.format(Collect.ADDRESS_APPPACK_INFO_RESTART, collectNameEncode));
        List<NameValuePair> header = new ArrayList<>();
        header.add(new NameValuePair("token", token));
        String json = HttpConnection.postAsString(url, header, "{}");
        logger.info("返回结果： "+json);
        Type type = new TypeToken<AppPackResult<ActionAndroidPack.IdValue>>() {
        }.getType();
        AppPackResult<ActionAndroidPack.IdValue> appPackResult = XGsonBuilder.instance().fromJson(json, type);
        Wo wo = new Wo();
        if (appPackResult.getResult().equals(AppPackResult.result_failure)) {
            wo.setValue(false);
            result.setMessage(appPackResult.getMessage());
        } else {
            wo.setValue(true);
        }
        result.setData(wo);
        return result;
    }

    public static class Wo extends WrapBoolean {

    }
}
