package com.x.file.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerUserJaxrsFilter;

@WebFilter(urlPatterns = "/jaxrs/editor/*")
public class EditorJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
