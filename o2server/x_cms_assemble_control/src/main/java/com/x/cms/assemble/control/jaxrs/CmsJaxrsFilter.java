package com.x.cms.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

/**
 * web服务过滤器，将指定的URL定义为需要用户认证的服务，如果用户未登录，则无法访问该服务
 */
@WebFilter(urlPatterns = {
        "/jaxrs/appcategoryadmin/*",
        "/jaxrs/appcategorypermission/*",
        "/jaxrs/appinfo/*",
        "/jaxrs/appconfig/*",
        "/jaxrs/categoryinfo/*",
        "/jaxrs/data/*",
        "/jaxrs/document/*",
        "/jaxrs/fileinfo/*",
        "/jaxrs/file/*",
        "/jaxrs/form/*",
        "/jaxrs/formversion/*",
        "/jaxrs/view/*",
        "/jaxrs/viewcategory/*",
        "/jaxrs/viewfieldconfig/*",
        "/jaxrs/image/*",
        "/jaxrs/log/*",
        "/jaxrs/design/appdict/*",
        "/jaxrs/surface/appdict/*",
        "/jaxrs/script/*",
        "/jaxrs/scriptversion/*",
        "/jaxrs/uuid/*",
        "/jaxrs/viewrecord/*",
        "/jaxrs/searchfilter/*",
        "/jaxrs/templateform/*",
        "/jaxrs/input/*",
        "/jaxrs/output/*",
        "/jaxrs/permission/*",
        "/jaxrs/docpermission/*",
        "/jaxrs/comment/*",
        "/jaxrs/commend/*",
        "/servlet/*"
}, asyncSupported = true)
public class CmsJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
