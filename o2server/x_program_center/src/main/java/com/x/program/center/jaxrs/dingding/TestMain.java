package com.x.program.center.jaxrs.dingding;


import com.google.gson.Gson;
import com.x.program.center.jaxrs.dingding.encrypt.DingTalkEncryptException;
import com.x.program.center.jaxrs.dingding.encrypt.DingTalkEncryptor;

import java.util.Date;
import java.util.Map;

/**
 * Created by fancyLou on 2020-10-26.
 * Copyright © 2020 O2. All rights reserved.
 */
public class TestMain {

    public static void main(String[] args) {
        String token = "o2oa";
        String aseKey = "Nzk5ZjFiODIxMzkyNGMyNWIyMmFiZWZkNjIzNjIxYTg";
        String appkey = "ding21016b71e78da961";
        String nonce = "2dffdfdfdf2222";
        try {
            Long timestamp = new Date().getTime();
            DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor(token, aseKey, appkey);
            Map<String, String> map = dingTalkEncryptor.getEncryptedMap("success", timestamp, nonce);
            Gson gson = new Gson();
            String json = gson.toJson(map);
            System.out.println(json);

            //{
            //    "timeStamp": "1603697312745",
            //    "msg_signature": "502b261eda47a212db163c91322f7ddecb2e99cc",
            //    "encrypt": "QH/UQCL7dUGoPQZcBf2H2u/8ImJ+6tjDOSfHle3GmMoG/zopHbD8JpXVCBFGSUFmQ3bWXIuzwyXAZCeS2kptmA==",
            //    "nonce": "2dffdfdfdf2222"
            //}

            String j = dingTalkEncryptor.getDecryptMsg("77d1d1214d0bb07e91be9b231c802cfd575d8a5f", "1603697312745", "2dffdfdfdf2222",
                    "Z129Xx+g/4RNjuplftqRsS24/FbMqS8Zy56JGpUQtL1zpVAoqhGS1PHiF5QdxQKRwzUgNg06tDylA02Lyy/W9Q\\u003d\\u003d");
            System.out.println(j);
        } catch (DingTalkEncryptException e) {
            e.printStackTrace();
        }
    }
}
