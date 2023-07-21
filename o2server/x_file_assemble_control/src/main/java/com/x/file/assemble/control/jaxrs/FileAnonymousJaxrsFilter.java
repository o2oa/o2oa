package com.x.file.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

/**
 * @author sword
 */
@WebFilter(urlPatterns = {
		"/jaxrs/anonymous/*",
		}, asyncSupported = true )
public class FileAnonymousJaxrsFilter extends AnonymousCipherManagerUserJaxrsFilter {

}
