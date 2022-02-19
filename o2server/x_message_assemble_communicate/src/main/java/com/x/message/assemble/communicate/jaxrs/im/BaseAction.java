package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {
    public static final String IM_CONFIG_KEY_NAME = "imConfig"; // 这个配置会已对象写入到 web.json ，已imConfig作为key名称
}