package com.x.attendance.assemble.control.jaxrs.v2.config;

import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.List;

/**
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionGet extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

    ActionResult<Wo> execute() throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
            Wo wo;
            if (list != null && !list.isEmpty()) {
                wo = Wo.copier.copy(list.get(0));
                result.setData(wo);
            } else {
                wo = new Wo();
            }
            ActionResponse response = CipherConnectionAction.get(false, Config.url_x_program_center_jaxrs("applications"));
            if (response != null) {
                Applications applications = response.getData(Applications.class);
                List<Application>  apps = applications.get("com.x.alifacedetection.assemble.control.x_ali_face_detection_assemble_control");
                wo.setAliFaceControlEnable(apps != null && !apps.isEmpty());
            } else {
                wo.setAliFaceControlEnable(false);
            }
            return result;
        }
    }

    public static class Wo extends AttendanceV2Config {

        private static final long serialVersionUID = 7957142970493421609L;
        static WrapCopier<AttendanceV2Config,  Wo> copier = WrapCopierFactory.wo(AttendanceV2Config.class,  Wo.class, null,
                JpaObject.FieldsInvisible);

        @FieldDescribe("阿里云人脸扩展是否启用")
        private Boolean aliFaceControlEnable = false;

        public Boolean getAliFaceControlEnable() {
            return aliFaceControlEnable;
        }

        public void setAliFaceControlEnable(Boolean aliFaceControlEnable) {
            this.aliFaceControlEnable = aliFaceControlEnable;
        }
    }
}
