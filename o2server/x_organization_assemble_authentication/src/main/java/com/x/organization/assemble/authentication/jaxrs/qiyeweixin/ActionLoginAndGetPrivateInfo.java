package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by fancyLou on 2022/9/29.
 * Copyright © 2022 O2. All rights reserved.
 */
public class ActionLoginAndGetPrivateInfo extends BaseAction {


    private static Logger logger = LoggerFactory.getLogger(ActionLoginAndGetPrivateInfo.class);



    ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
                                            String code) throws Exception {
        if (StringUtils.isEmpty(code)) {
            throw new ExceptionCodeEmpty();
        }
        if (null == Config.qiyeweixin()) {
            throw new ExceptionQywexinNotConfigured();
        }
        if (!Config.qiyeweixin().getEnable()) {
            throw new ExceptionQywexinNotConfigured();
        }
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            String url = Config.qiyeweixin().getApiAddress() + "/cgi-bin/auth/getuserinfo?access_token="
                    + Config.qiyeweixin().corpAccessToken() + "&code=" + code;
            logger.debug("getuserinfo url:{}.", url);
            QiyeweixinGetUserInfoResp resp = HttpConnection.getAsObject(url, null, QiyeweixinGetUserInfoResp.class);
            if (resp == null || resp.getErrcode() == null || resp.getErrcode() != 0) {
                Integer errCode;
                if (resp == null) errCode = -1;
                else errCode = resp.getErrcode();
                String errMsg = resp == null ? "" : resp.getErrmsg();
                throw new ExceptionQywxResponse(errCode, errMsg);
            }
            String userId = resp.getUserid();
            if (StringUtils.isEmpty(userId)) {
                logger.info("userId为空，无法单点登录！！！");
                throw new ExceptionQywxResponse(resp.getErrcode(), resp.getErrmsg());
            }

            Business business = new Business(emc);
            String personId = business.person().getPersonIdWithQywxid(userId);
            if (StringUtils.isEmpty(personId)) {
                throw new ExceptionPersonNotExist(userId);
            }
            Person person = emc.find(personId, Person.class);
            Wo wo = Wo.copier.copy(person);
            List<String> roles = business.organization().role().listWithPerson(person.getDistinguishedName());
            wo.setRoleList(roles);
            EffectivePerson effective = new EffectivePerson(wo.getDistinguishedName(), TokenType.user,
                    Config.token().getCipher(), Config.person().getEncryptType());
            wo.setToken(effective.getToken());
            HttpToken httpToken = new HttpToken();
            httpToken.setToken(request, response, effective);
            result.setData(wo);
            UpdatePersonInfoFromQywx qywx = new UpdatePersonInfoFromQywx(resp);
            qywx.start();

            return result;
        }
    }

    public static class UpdatePersonInfoFromQywx extends Thread {
        QiyeweixinGetUserInfoResp userinfo;
        UpdatePersonInfoFromQywx(QiyeweixinGetUserInfoResp userinfo) {
            this.userinfo = userinfo;
        }

        @Override
        public void run() {
            if (userinfo != null) {
                logger.info("开始从企业微信查询用户详细信息进行更新，{}.", userinfo.toString());
            }
            try {
                if (userinfo != null && StringUtils.isNotEmpty(userinfo.getUser_ticket())) {
                    String url = Config.qiyeweixin().getApiAddress() + "/cgi-bin/auth/getuserdetail?access_token="
                            + Config.qiyeweixin().corpAccessToken();
                    if (logger.isDebugEnabled()) {
                        logger.debug("getuserdetail url:{}.", url);
                    }
                    QiyeweixinPostUserDetailBody body = new QiyeweixinPostUserDetailBody();
                    body.setUser_ticket(userinfo.getUser_ticket());
                    QiyeweixinGetUserDetailResp resp = HttpConnection.postAsObject(url, null, body.toString(), QiyeweixinGetUserDetailResp.class);
                    if (resp == null || resp.getErrcode() == null || resp.getErrcode() != 0) {
                        Integer errCode;
                        if (resp == null) errCode = -1;
                        else errCode = resp.getErrcode();
                        String errMsg = resp == null ? "" : resp.getErrmsg();
                        throw new ExceptionQywxResponse(errCode, errMsg);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("获取到企业微信用户详细信息： {}.", resp.toString());
                    }
                    String userId = resp.getUserid();
                    if (StringUtils.isEmpty(userId)) {
                        logger.info("userId为空，查询到对象！！！");
                        throw new ExceptionQywxResponse(resp.getErrcode(), resp.getErrmsg());
                    }
                    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                        Business business = new Business(emc);
                        String personId = business.person().getPersonIdWithQywxid(userId);
                        if (StringUtils.isEmpty(personId)) {
                            throw new ExceptionPersonNotExist(userId);
                        }

                        Person person = emc.find(personId, Person.class);
                        if (person == null) {
                            throw new ExceptionPersonNotExist(userId);
                        }
                        emc.beginTransaction(Person.class);
                        person.setMobile(resp.getMobile());
                        person.setMail(resp.getEmail());
                        person.setGenderType(Objects.equals("1", resp.getGender()) ? GenderType.m : GenderType.f);
                        if (StringUtils.isNotEmpty(resp.getAvatar())) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("下载微信头像的url： {}.", resp.getAvatar());
                            }
                            byte[] bytes = ConnectionAction.getBinary(resp.getAvatar(), null);
                            if (bytes != null && bytes.length > 0) {
                                try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                                     ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                     ByteArrayOutputStream baos_m = new ByteArrayOutputStream();
                                     ByteArrayOutputStream baos_l = new ByteArrayOutputStream()) {
                                    BufferedImage image = ImageIO.read(bais);

                                    BufferedImage scalrImage = Scalr.resize(image, 144, 144);
                                    ImageIO.write(scalrImage, "png", baos);
                                    String icon = Base64.encodeBase64String(baos.toByteArray());

                                    BufferedImage scalrImage_m = Scalr.resize(image, 72, 72);
                                    ImageIO.write(scalrImage_m, "png", baos_m);
                                    String icon_m = Base64.encodeBase64String(baos_m.toByteArray());

                                    BufferedImage scalrImage_l = Scalr.resize(image, 36, 36);
                                    ImageIO.write(scalrImage_l, "png", baos_l);
                                    String icon_l = Base64.encodeBase64String(baos_l.toByteArray());

                                    person.setIcon(icon);
                                    person.setIconMdpi(icon_m);
                                    person.setIconLdpi(icon_l);
                                }
                            }
                        }
                        emc.commit();
                        CacheManager.notify(Person.class);
                        if (logger.isDebugEnabled()) {
                            logger.debug("更新用户信息成功，perso：{}", person.toString());
                        }
                    }
                } else {
                    logger.info("没有获取到用户认证后的 user_ticket 无法获取用户敏感信息！！！");
                }
            }catch (Exception e) {
                logger.error(e);
            }
        }
    }


    public static class Wo extends Person {


        private static final long serialVersionUID = 1257832466537665269L;
        public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

        static {
            Excludes.add("password");
        }

        static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null, Excludes);

        private String token;
        private List<String> roleList;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public List<String> getRoleList() {
            return roleList;
        }

        public void setRoleList(List<String> roleList) {
            this.roleList = roleList;
        }
    }



    public static class QiyeweixinGetUserInfoResp extends GsonPropertyObject {

        /**
         * https://developer.work.weixin.qq.com/document/path/91023
         * <code>	 {
         *    "errcode": 0,
         *    "errmsg": "ok",
         *    "userid":"USERID",
         *    "user_ticket": "USER_TICKET"
         * }
         * </code>
         */

        private Integer errcode;
        private String errmsg;
        private String userid;
        private String user_ticket;

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUser_ticket() {
            return user_ticket;
        }

        public void setUser_ticket(String user_ticket) {
            this.user_ticket = user_ticket;
        }
    }


    public static class QiyeweixinPostUserDetailBody extends GsonPropertyObject {
        private String user_ticket;

        public String getUser_ticket() {
            return user_ticket;
        }

        public void setUser_ticket(String user_ticket) {
            this.user_ticket = user_ticket;
        }
    }

    public static class QiyeweixinGetUserDetailResp extends GsonPropertyObject {

        /**
         * https://developer.work.weixin.qq.com/document/path/95833
         * <code>	{
         *    "errcode": 0,
         *    "errmsg": "ok",
         *    "userid":"lisi",
         *    "gender":"1",
         *    "avatar":"http://shp.qpic.cn/bizmp/xxxxxxxxxxx/0",
         *    "qr_code":"https://open.work.weixin.qq.com/wwopen/userQRCode?vcode=vcfc13b01dfs78e981c",
         *    "mobile": "13800000000",
         *    "email": "zhangsan@gzdev.com",
         *    "biz_mail":"zhangsan@qyycs2.wecom.work",
         *    "address": "广州市海珠区新港中路"
         * }
         * </code>
         */

        private Integer errcode;
        private String errmsg;
        private String userid;
        private String gender;
        private String avatar;
        private String qr_code;
        private String mobile;
        private String email;
        private String biz_mail;
        private String address;

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getQr_code() {
            return qr_code;
        }

        public void setQr_code(String qr_code) {
            this.qr_code = qr_code;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBiz_mail() {
            return biz_mail;
        }

        public void setBiz_mail(String biz_mail) {
            this.biz_mail = biz_mail;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
