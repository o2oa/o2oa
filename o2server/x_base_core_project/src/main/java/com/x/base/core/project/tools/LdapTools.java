package com.x.base.core.project.tools;

import java.time.Duration;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.ldaptive.*;
import org.ldaptive.auth.*;

/**
 * ldap工具
 * @author sword
 */
public class LdapTools {
    private static Logger logger = LoggerFactory.getLogger(LdapTools.class);
    private static final String USER_FILTER = "={user}";

    public static boolean authByJndi(String uid, String password){
        boolean result = false;
        DirContext ctx = null;
        try {
            String userName = Config.token().getLdapAuth().getUserDn();
            userName = StringUtils.replace(userName, "*", uid);
            String ldapUrl = Config.token().getLdapAuth().getLdapUrl();
            if(!ldapUrl.endsWith("/")){
                ldapUrl = ldapUrl+"/";
            }
            ldapUrl = ldapUrl + Config.token().getLdapAuth().getBaseDn();
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put("com.sun.jndi.ldap.connect.timeout", "3000");
            env.put(Context.SECURITY_PRINCIPAL, userName);
            env.put(Context.SECURITY_CREDENTIALS, password);
            ctx = new InitialDirContext(env);
            result = true;
        } catch (AuthenticationException e) {
            logger.debug("ldap认证失败");
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
        return result;
    }

    public static boolean auth(String uid, String password){
        logger.info("用户{}进行ldap认证", uid);
        boolean result = false;
        try {
            String url = Config.token().getLdapAuth().getLdapUrl();
            String bindUser = Config.token().getLdapAuth().getBindDnUser();
            String bindUserPwd = Config.token().getLdapAuth().getBindDnPwd();
            if(StringUtils.isBlank(url) || StringUtils.isBlank(bindUser) || StringUtils.isBlank(bindUserPwd)){
                return false;
            }
            ConnectionConfig connConfig = new ConnectionConfig(url);
            connConfig.setConnectTimeout(Duration.ofSeconds(3));
            connConfig.setConnectionInitializer(
                    new BindConnectionInitializer(bindUser, new Credential(bindUserPwd)));
            SearchDnResolver dnResolver = new SearchDnResolver(new DefaultConnectionFactory(connConfig));
            dnResolver.setBaseDn(Config.token().getLdapAuth().getBaseDn());
            dnResolver.setUserFilter(Config.token().getLdapAuth().getUserDn() + USER_FILTER);
            dnResolver.setSubtreeSearch(true);
            BindAuthenticationHandler authHandler = new BindAuthenticationHandler(new DefaultConnectionFactory(connConfig));
            Authenticator auth = new Authenticator(dnResolver, authHandler);
            AuthenticationResponse response = auth.authenticate(new AuthenticationRequest(uid, new Credential(password)));
            if (response.getResult()) {
                result = true;
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return result;
    }

    public static void main(String[] args) throws Exception{
        ConnectionConfig connConfig = new ConnectionConfig("ldap://127.0.0.1:5389");
        connConfig.setConnectionInitializer(
                new BindConnectionInitializer("cn=root", new Credential("***")));
        SearchDnResolver dnResolver = new SearchDnResolver(new DefaultConnectionFactory(connConfig));
        dnResolver.setBaseDn("DC=CMCC");
        dnResolver.setUserFilter("uid={user}");
        dnResolver.setSubtreeSearch(true);
        BindAuthenticationHandler authHandler = new BindAuthenticationHandler(new DefaultConnectionFactory(connConfig));
        Authenticator auth = new Authenticator(dnResolver, authHandler);
        AuthenticationResponse response = auth.authenticate(new AuthenticationRequest("baiyumei", new Credential("***")));
        if (response.getResult()) {
            System.out.println("success");
        } else {
            System.out.println("failed");
        }
    }
}
