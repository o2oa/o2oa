package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.Crypto;
import org.apache.commons.lang3.StringUtils;

/**
 * 三元管理配置
 */
public class TernaryManagement extends ConfigObject {

    public static final String initPassword = "o2";

    public static final String INIT_SYSTEM_MANAGER = "systemManager";

    public static final String INIT_SYSTEM_MANAGER_DISTINGUISHED_NAME = "系统管理员@systemManager@P";

    public static final String INIT_SECURITY_MANAGER = "securityManager";

    public static final String INIT_SECURITY_MANAGER_DISTINGUISHED_NAME = "安全管理员@securityManager@P";

    public static final String INIT_AUDIT_MANAGER = "auditManager";

    public static final String INIT_AUDIT_MANAGER_DISTINGUISHED_NAME = "审计管理员@auditManager@P";

    private transient String _systemManagerPassword;

    private transient String _securityManagerPassword;

    private transient String _auditManagerPassword;

    public static TernaryManagement defaultInstance() {
        TernaryManagement o = new TernaryManagement();
        return o;
    }

    public TernaryManagement(){
        this.enable = false;
        this.systemManager = INIT_SYSTEM_MANAGER;
        this.systemManagerDistinguishedName = INIT_SYSTEM_MANAGER_DISTINGUISHED_NAME;
        this.securityManager = INIT_SECURITY_MANAGER;
        this.securityManagerDistinguishedName = INIT_SECURITY_MANAGER_DISTINGUISHED_NAME;
        this.auditManager = INIT_AUDIT_MANAGER;
        this.auditManagerDistinguishedName = INIT_AUDIT_MANAGER_DISTINGUISHED_NAME;
    }

    public boolean isTernaryManagement(String name) {
        return StringUtils.equals(this.getSystemManager(), name)
                || StringUtils.equals(this.getSystemManagerDistinguishedName(), name)
                || StringUtils.equals(this.getSystemManagerName(), name)
                || StringUtils.equals(this.getSecurityManager(), name)
                || StringUtils.equals(this.getSecurityManagerDistinguishedName(), name)
                || StringUtils.equals(this.getSecurityManagerName(), name)
                || StringUtils.equals(this.getAuditManager(), name)
                || StringUtils.equals(this.getAuditManagerDistinguishedName(), name)
                || StringUtils.equals(this.getAuditManagerName(), name);
    }

    public boolean isSystemManager(String name) {
        return StringUtils.equals(this.getSystemManager(), name)
                || StringUtils.equals(this.getSystemManagerDistinguishedName(), name)
                || StringUtils.equals(this.getSystemManagerName(), name);
    }

    public boolean isSecurityManager(String name) {
        return StringUtils.equals(this.getSecurityManager(), name)
                || StringUtils.equals(this.getSecurityManagerDistinguishedName(), name)
                || StringUtils.equals(this.getSecurityManagerName(), name);
    }

    public boolean isAuditManager(String name) {
        return StringUtils.equals(this.getAuditManager(), name)
                || StringUtils.equals(this.getAuditManagerDistinguishedName(), name)
                || StringUtils.equals(this.getAuditManagerName(), name);
    }

    public boolean verifyPassword(String name, String password) {
        if(this.isSystemManager(name)){
            return StringUtils.equals(this.getSystemManagerPassword(), password);
        } else if(this.isSecurityManager(name)){
            return StringUtils.equals(this.getSecurityManagerPassword(), password);
        } else if(this.isAuditManager(name)){
            return StringUtils.equals(this.getAuditManagerPassword(), password);
        } else{
            return false;
        }
    }

    @FieldDescribe("是否启用三元管理.")
    private Boolean enable;

    @FieldDescribe("系统管理员账号，不可更改.")
    private String systemManager;

    @FieldDescribe("系统管理员账号全称，不可更改.")
    private String systemManagerDistinguishedName;

    @FieldDescribe("系统管理员账号密码.")
    private String systemManagerPassword;

    @FieldDescribe("安全管理员账号，不可更改.")
    private String securityManager;

    @FieldDescribe("安全管理员账号全称，不可更改.")
    private String securityManagerDistinguishedName;

    @FieldDescribe("安全管理员账号密码.")
    private String securityManagerPassword;

    @FieldDescribe("审计管理员账号，不可更改.")
    private String auditManager;

    @FieldDescribe("审计管理员账号全称，不可更改.")
    private String auditManagerDistinguishedName;

    @FieldDescribe("审计管理员账号密码.")
    private String auditManagerPassword;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getSystemManager() {
        return systemManager;
    }

    public void setSystemManager(String systemManager) {
        this.systemManager = systemManager;
    }

    public String getSystemManagerDistinguishedName() {
        return systemManagerDistinguishedName;
    }

    public void setSystemManagerDistinguishedName(String systemManagerDistinguishedName) {
        this.systemManagerDistinguishedName = systemManagerDistinguishedName;
    }

    public String getSystemManagerName() {
        return systemManagerDistinguishedName.split("@")[0];
    }

    public String getSystemManagerPassword() {
        if (StringUtils.isEmpty(this._systemManagerPassword)) {
            String password = StringUtils.isBlank(this.systemManagerPassword) ? initPassword : this.systemManagerPassword;
            this._systemManagerPassword = Crypto.plainText(password);
        }
        return this._systemManagerPassword;
    }

    public void setSystemManagerPassword(String systemManagerPassword) {
        this.systemManagerPassword = systemManagerPassword;
    }

    public String getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(String securityManager) {
        this.securityManager = securityManager;
    }

    public String getSecurityManagerDistinguishedName() {
        return securityManagerDistinguishedName;
    }

    public String getSecurityManagerName() {
        return securityManagerDistinguishedName.split("@")[0];
    }

    public void setSecurityManagerDistinguishedName(String securityManagerDistinguishedName) {
        this.securityManagerDistinguishedName = securityManagerDistinguishedName;
    }

    public String getSecurityManagerPassword() {
        if (StringUtils.isEmpty(this._securityManagerPassword)) {
            String password = StringUtils.isBlank(this.securityManagerPassword) ? initPassword : this.securityManagerPassword;
            this._securityManagerPassword = Crypto.plainText(password);
        }
        return this._securityManagerPassword;
    }

    public void setSecurityManagerPassword(String securityManagerPassword) {
        this.securityManagerPassword = securityManagerPassword;
    }

    public String getAuditManager() {
        return auditManager;
    }

    public void setAuditManager(String auditManager) {
        this.auditManager = auditManager;
    }

    public String getAuditManagerDistinguishedName() {
        return auditManagerDistinguishedName;
    }

    public void setAuditManagerDistinguishedName(String auditManagerDistinguishedName) {
        this.auditManagerDistinguishedName = auditManagerDistinguishedName;
    }

    public String getAuditManagerName() {
        return auditManagerDistinguishedName.split("@")[0];
    }

    public String getAuditManagerPassword() {
        if (StringUtils.isEmpty(this._auditManagerPassword)) {
            String password = StringUtils.isBlank(this.auditManagerPassword) ? initPassword : this.auditManagerPassword;
            this._auditManagerPassword = Crypto.plainText(password);
        }
        return this._auditManagerPassword;
    }

    public void setAuditManagerPassword(String auditManagerPassword) {
        this.auditManagerPassword = auditManagerPassword;
    }
}
