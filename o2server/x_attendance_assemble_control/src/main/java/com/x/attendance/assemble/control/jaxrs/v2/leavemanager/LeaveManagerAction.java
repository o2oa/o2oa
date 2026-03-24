package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import javax.ws.rs.Path;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("v2/leavemanager")
@JaxrsDescribe("假期管理")
public class LeaveManagerAction extends StandardJaxrsAction {

     private static Logger logger = LoggerFactory.getLogger(LeaveManagerAction.class);


}
