package com.x.attendance.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.ManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;


@WebFilter(urlPatterns = {
        "/jaxrs/v2/shift/*",
        "/jaxrs/v2/group/*",
        "/jaxrs/v2/workplace/*",
        "/jaxrs/v2/detail/*",
        "/jaxrs/v2/config/*",
        "/jaxrs/v2/leave/*",
        "/jaxrs/v2/record/*"
}, asyncSupported = true)
public class V2ManagerJaxrsFilter extends ManagerUserJaxrsFilter {
}
