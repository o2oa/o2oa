package com.x.base.core.project.tools;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class LdapTools {
    private static Logger logger = LoggerFactory.getLogger(LdapTools.class);

    public static boolean auth(String uid, String password){
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
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");//设置连接LDAP的实现工厂
            env.put(Context.PROVIDER_URL, ldapUrl);// 指定LDAP服务器的主机名和端口号
            env.put(Context.SECURITY_AUTHENTICATION, "simple");//给环境提供认证方法,有SIMPLE、SSL/TLS和SASL
            env.put("com.sun.jndi.ldap.connect.timeout", "3000");//连接超时设置为3秒
            env.put(Context.SECURITY_PRINCIPAL, userName);//指定进入的目录识别名DN
            env.put(Context.SECURITY_CREDENTIALS, password); //进入的目录密码
            //env.put("filter", filter);
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
}
