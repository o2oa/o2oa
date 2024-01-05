package com.x.program.center.jaxrs.market;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.enums.CommonStatus;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.InstallLog;

class ActionInstallOffline extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionInstallOffline.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, String fileName, FormDataContentDisposition disposition) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            if (StringUtils.isEmpty(fileName)) {
                fileName = this.fileName(disposition);
            }
            Application2 app = new Application2();
            logger.info("{}发起离线安装或更新应用：{}", effectivePerson.getDistinguishedName(), fileName);
            Wo wo = new Wo();
            wo.setValue(false);
            if ((null != bytes) && (bytes.length > 0)) {
                InstallData installData = this.install(app, bytes);
                wo.setValue(true);
                emc.beginTransaction(InstallLog.class);
                InstallLog installLog = emc.find(app.getId(), InstallLog.class);
                boolean exist = true;
                if (installLog == null) {
                    installLog = new InstallLog();
                    installLog.setId(app.getId());
                    exist = false;
                }
                installLog.setName(app.getName());
                installLog.setVersion(app.getVersion());
                installLog.setCategory(app.getCategory());
                installLog.setStatus(CommonStatus.VALID.getValue());
                installLog.setData(gson.toJson(installData));
                installLog.setInstallPerson(effectivePerson.getDistinguishedName());
                installLog.setInstallTime(new Date());
                installLog.setUnInstallPerson(null);
                installLog.setUnInstallTime(null);
                if (!exist) {
                    emc.persist(installLog);
                }
                emc.commit();
                CacheManager.notify(InstallLog.class);
            }

            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WrapBoolean {

    }

    public static class InstallWo extends GsonPropertyObject {

        @FieldDescribe("流程应用")
        private List<String> processPlatformList = new ArrayList<>();

        @FieldDescribe("门户应用")
        private List<String> portalList = new ArrayList<>();

        @FieldDescribe("统计应用")
        private List<String> queryList = new ArrayList<>();

        @FieldDescribe("内容管理应用")
        private List<String> cmsList = new ArrayList<>();

        @FieldDescribe("服务管理应用")
        private List<String> serviceModuleList = new ArrayList<>();

        public List<String> getProcessPlatformList() {
            return processPlatformList;
        }

        public void setProcessPlatformList(List<String> processPlatformList) {
            this.processPlatformList = processPlatformList;
        }

        public List<String> getPortalList() {
            return portalList;
        }

        public void setPortalList(List<String> portalList) {
            this.portalList = portalList;
        }

        public List<String> getQueryList() {
            return queryList;
        }

        public void setQueryList(List<String> queryList) {
            this.queryList = queryList;
        }

        public List<String> getCmsList() {
            return cmsList;
        }

        public void setCmsList(List<String> cmsList) {
            this.cmsList = cmsList;
        }

        public List<String> getServiceModuleList() {
            return serviceModuleList;
        }

        public void setServiceModuleList(List<String> serviceModuleList) {
            this.serviceModuleList = serviceModuleList;
        }
    }

}
