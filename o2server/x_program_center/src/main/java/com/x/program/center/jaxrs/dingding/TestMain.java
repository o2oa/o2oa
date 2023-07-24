package com.x.program.center.jaxrs.dingding;


import java.util.Date;
import java.util.Map;

import com.google.gson.Gson;
import com.x.program.center.jaxrs.dingding.encrypt.DingTalkEncryptor;

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

//            String j = dingTalkEncryptor.getDecryptMsg("111108bb8e6dbce3c9671d6fdb69d15066227608", "1783610513", "123456",
//                    "1ojQf0NSvw2WPvW7LijxS8UvISr8pdDP+rXpPbcLGOmIBNbWetRg7IP0vdhVgkVwSoZBJeQwY2zhROsJq/HJ+q6tp1qhl9L1+ccC9ZjKs1wV5bmA9NoAWQiZ+7MpzQVq+j74rJQljdVyBdI/dGOvsnBSCxCVW0ISWX0vn9lYTuuHSoaxwCGylH9xRhYHL9bRDskBc7bO0FseHQQasdfghjkl");
//            System.out.println(j+"点点滴滴");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
