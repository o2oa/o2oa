package com.x.file.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.ManagerUserJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/attachment/*", "/jaxrs/folder/*", "/jaxrs/share/*", "/jaxrs/editor/*", "/jaxrs/complex/*", "/servlet/*" })
public class FileFilter extends ManagerUserJaxrsFilter {

}
