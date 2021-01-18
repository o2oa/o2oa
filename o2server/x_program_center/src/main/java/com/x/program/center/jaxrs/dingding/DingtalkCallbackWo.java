package com.x.program.center.jaxrs.dingding;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * Created by fancyLou on 2020-10-26.
 * Copyright © 2020 O2. All rights reserved.
 */
public class DingtalkCallbackWo extends GsonPropertyObject {

    @FieldDescribe("消息体签名")
    private String msg_signature; //消息体签名
    @FieldDescribe("时间戳")
    private String timeStamp;
    @FieldDescribe("随机字符串")
    private String nonce;
    @FieldDescribe("字符串“success”加密值")
    private String encrypt;
}
