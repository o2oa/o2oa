package com.x.attendance.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;


@WebFilter(urlPatterns = {
        "/jaxrs/v2/mobile/*",
        "/jaxrs/v2/appeal/*",
        "/jaxrs/v2/my/*",
}, asyncSupported = true)
public class V2UserJaxrsFilter extends ManagerUserJaxrsFilter {
}
