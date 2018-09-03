package o2.collect.assemble.jaxrs;

import javax.servlet.annotation.WebFilter;

import o2.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/captcha/*", asyncSupported = true)
public class CaptchaJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
