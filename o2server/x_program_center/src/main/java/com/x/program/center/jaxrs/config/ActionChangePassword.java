package com.x.program.center.jaxrs.config;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionChangePassword extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionChangePassword.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (BooleanUtils.isNotTrue(Config.general().getConfigApiEnable())) {
            throw new ExceptionModifyConfig();
        }

        check(wi);

        String oldPassword = password(wi.getOldPassword());
        String newPassword = password(wi.getNewPassword());

        checkPasswordComplexity(wi.getCredential(), newPassword);

        if (Config.ternaryManagement().isAuditManager(wi.getCredential())) {
            changeAuditManagerPassword(effectivePerson, wi.getCredential(), oldPassword, newPassword);
        } else if (Config.ternaryManagement().isSecurityManager(wi.getCredential())) {
            changeSecurityManager(effectivePerson, wi.getCredential(), oldPassword, newPassword);
        } else if (Config.ternaryManagement().isSystemManager(wi.getCredential())) {
            changeSystemManager(effectivePerson, wi.getCredential(), oldPassword, newPassword);
        } else if (StringUtils.equals(wi.getCredential(), Config.token().getInitialManager())) {
            changeInitialManager(effectivePerson, oldPassword, newPassword);
        } else {
            throw new ExceptionInvalidCredential();
        }

        Wo wo = new Wo();
        wo.setValue(true);
        result.setData(wo);
        return result;
    }

    private void changeAuditManagerPassword(EffectivePerson effectivePerson, String credential, String oldPassword,
            String newPassword) throws Exception {
        if (BooleanUtils.isNotTrue(Config.ternaryManagement().verifyPassword(credential, oldPassword))) {
            throw new ExceptionInvalidOldPassword();
        }
        Config.ternaryManagement().setAuditManagerPassword(newPassword);
        Config.ternaryManagement().save();
        this.configFlush(effectivePerson);
    }

    private void changeSecurityManager(EffectivePerson effectivePerson, String credential, String oldPassword,
            String newPassword) throws Exception {
        if (BooleanUtils.isNotTrue(Config.ternaryManagement().verifyPassword(credential, oldPassword))) {
            throw new ExceptionInvalidOldPassword();
        }
        Config.ternaryManagement().setSecurityManagerPassword(newPassword);
        Config.ternaryManagement().save();
        this.configFlush(effectivePerson);
    }

    private void changeSystemManager(EffectivePerson effectivePerson, String credential, String oldPassword,
            String newPassword) throws Exception {
        if (BooleanUtils.isNotTrue(Config.ternaryManagement().verifyPassword(credential, oldPassword))) {
            throw new ExceptionInvalidOldPassword();
        }
        Config.ternaryManagement().setSystemManagerPassword(newPassword);
        Config.ternaryManagement().save();
        this.configFlush(effectivePerson);
    }

    private void changeInitialManager(EffectivePerson effectivePerson, String oldPassword, String newPassword)
            throws Exception {
        if (BooleanUtils.isNotTrue(StringUtils.equals(Config.token().getPassword(), oldPassword))) {
            throw new ExceptionInvalidOldPassword();
        }
        this.changeInternalDataServerPassword(oldPassword, newPassword);
        Config.token().setPassword(newPassword);
        Config.token().save();
        this.configFlush(effectivePerson);
    }

    private void changeInternalDataServerPassword(String oldPassword, String newPassword) throws Exception {
        org.h2.Driver.load();
        for (Entry<String, DataServer> en : Config.nodes().dataServers().entrySet()) {
            DataServer o = en.getValue();
            if (BooleanUtils.isTrue(o.getEnable()) && (!Config.externalDataSources().enable())) {
                try (Connection conn = DriverManager.getConnection(
                        "jdbc:h2:tcp://" + en.getKey() + ":" + o.getTcpPort() + "/X", "sa", oldPassword)) {
                    RunScript.execute(conn, new StringReader("ALTER USER SA SET PASSWORD '" + newPassword + "'"));
                } catch (Exception e) {
                    throw new IllegalStateException("Verify that the dataServer:" + en.getKey()
                            + " is started and that the dataServer password is updated synchronously.", e);
                }
            }
        }
    }

    private void check(Wi wi) throws ExceptionEmptyCredential, ExceptionEmptyOldPassword, ExceptionEmptyNewPassword {
        if (StringUtils.isEmpty(wi.getCredential())) {
            throw new ExceptionEmptyCredential();
        }
        if (StringUtils.isEmpty(wi.getOldPassword())) {
            throw new ExceptionEmptyOldPassword();
        }
        if (StringUtils.isEmpty(wi.getCredential())) {
            throw new ExceptionEmptyNewPassword();
        }
    }

    private void checkPasswordComplexity(String credential, String newPassword) throws Exception {
        if (!newPassword.matches(Config.person().getPasswordRegex())) {
            throw new ExceptionInvalidPassword(credential, Config.person().getPasswordRegexHint());
        }
    }

    protected String password(String text) throws Exception {
        return BooleanUtils.isTrue(Config.token().getRsaEnable()) ? Crypto.rsaDecrypt(text, Config.privateKey()) : text;
    }

    @Schema(name = "com.x.program.center.jaxrs.config.ActionChangePassword$Wo")
    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -6564786947838509994L;
    }

    @Schema(name = "com.x.program.center.jaxrs.config.ActionChangePassword$Wi")
    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -8627814779546541124L;

        @FieldDescribe("用户标识.")
        @Schema(description = "用户标识.")
        private String credential;
        @FieldDescribe("原密码.")
        @Schema(description = "原密码.")
        private String oldPassword;
        @FieldDescribe("新密码.")
        @Schema(description = "新密码.")
        private String newPassword;

        public String getCredential() {
            return credential;
        }

        public void setCredential(String credential) {
            this.credential = credential;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

    }

}
