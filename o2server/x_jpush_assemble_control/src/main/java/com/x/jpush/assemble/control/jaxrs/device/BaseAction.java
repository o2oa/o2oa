package com.x.jpush.assemble.control.jaxrs.device;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.MD5Tool;

public class BaseAction extends StandardJaxrsAction{


    /**
     * md5(deviceType+deviceId+pushType+person)
     * @return
     */
    protected String deviceUnique(String deviceType, String deviceId, String pushType, String person) {
       return MD5Tool.getMD5Str(deviceType+deviceId+pushType+person);
    }
}
