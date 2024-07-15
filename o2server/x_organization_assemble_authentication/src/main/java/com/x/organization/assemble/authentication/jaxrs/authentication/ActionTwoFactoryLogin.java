package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.enums.PersonStatusEnum;
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionCaptchaLoginWi;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 双因素认证，用户名密码认证成功后发送短信验证码，然后再校验验证码(校验验证码走codeLogin接口)
 */
class ActionTwoFactoryLogin extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionTwoFactoryLogin.class);

    ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response,
            EffectivePerson effectivePerson,
            JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        Wo wo = new Wo();
        wo.setValue(true);
        this.validate(wi);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            String password = password(wi.getPassword());
            if (BooleanUtils.isTrue(Config.person().getCaptchaLogin()) && (BooleanUtils
                    .isFalse(business.instrument().captcha()
                            .validate(wi.getCaptcha(), wi.getCaptchaAnswer())))) {
                throw new ExceptionInvalidCaptcha();
            }
            if (Config.token().isInitialManager(wi.getCredential())) {
                if (!Config.token().verifyPassword(wi.getCredential(), password)) {
                    throw new ExceptionPersonNotExistOrInvalidPassword();
                }
            } else {
                // 普通用户登录,也有可能拥有管理员角色.增加相同标识(name允许重复)的认证
                List<String> people = this.listWithCredential(business, wi.getCredential());
                Person person = null;
                if (people.isEmpty()) {
                    throw new ExceptionPersonNotExistOrInvalidPassword();
                } else if (people.size() == 1) {
                    person = this.personLogin(business, people.get(0), password,
                            wi.getCredential());
                } else {
                    person = this.peopleLogin(business, people, password, wi.getCredential());
                }
                if (null == person) {
                    throw new ExceptionPersonNotExistOrInvalidPassword();
                } else {
                    if (this.failureLocked(person)) {
                        throw new ExceptionFailureLocked(DateTools.format(person.getLockExpireTime()));
                    }
                    if (PersonStatusEnum.BAN.getValue().equals(person.getStatus())) {
                        throw new ExceptionFailureBanned();
                    }
                    if(!passwordExpired(wo, person)) {
                        try {
                            business.instrument().code().create(person.getMobile());
                        } catch (Exception e) {
                            throw new ExceptionSendCodeError(e);
                        }
                    }
                }
            }
            result.setData(wo);
            return result;
        }
    }

    private void validate(Wi wi) throws Exception {
        if (StringUtils.isEmpty(wi.getCredential())) {
            throw new ExceptionCredentialEmpty();
        }
        if (StringUtils.isEmpty(wi.getPassword())) {
            throw new ExceptionPasswordEmpty();
        }
        // 可以通过设置跳过图片验证码.
        if (BooleanUtils.isTrue(Config.person().getCaptchaLogin())
                && (StringUtils.isEmpty(wi.getCaptcha()) || StringUtils.isEmpty(
                wi.getCaptchaAnswer()))) {
            throw new ExceptionCaptchaEmpty();
        }
    }

    private boolean passwordExpired(Wo wo, Person person) throws Exception {
        if (Config.person().getFirstLoginModifyPwd() && (person.getChangePasswordTime() == null)) {
            wo.setPasswordExpired(true);
            return true;
        }
        Integer passwordPeriod = Config.person().getPasswordPeriod();
        if (passwordPeriod == 0) {
            return false;
        }
        if (null != person.getPasswordExpiredTime()) {
            if (person.getPasswordExpiredTime().getTime() < (new Date()).getTime()) {
                wo.setPasswordExpired(true);
                return true;
            }
        } else if (person.getChangePasswordTime() != null) {
            Date date = DateTools.addDay(person.getChangePasswordTime(), passwordPeriod);
            if (date.getTime() < (new Date()).getTime()) {
                wo.setPasswordExpired(true);
                return true;
            }
        }
        return false;
    }

    @Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionCaptchaLogin$Wi")
    public static class Wi extends ActionCaptchaLoginWi {

        private static final long serialVersionUID = 216758837350255868L;

    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = 1398135678113303824L;

        @FieldDescribe("口令是否过期")
        private Boolean passwordExpired = false;

        public Boolean getPasswordExpired() {
            return passwordExpired;
        }

        public void setPasswordExpired(Boolean passwordExpired) {
            this.passwordExpired = passwordExpired;
            if (BooleanUtils.isTrue(passwordExpired)) {
                this.setValue(false);
            }
        }
    }

}
