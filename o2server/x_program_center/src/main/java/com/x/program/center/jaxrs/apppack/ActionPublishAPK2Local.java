package com.x.program.center.jaxrs.apppack;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.AppPackApkFile;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by fancyLou on 11/30/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionPublishAPK2Local extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionPublishAPK2Local.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)  throws Exception {
        ActionResult<Wo> result = new ActionResult<>();

        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isBlank(wi.getToken())) {
            throw new ExceptionNoToken();
        }
        if (StringUtils.isBlank(wi.getApkPath())) {
            throw new ExceptionEmptyProperty("apkPath");
        }
        if (StringUtils.isBlank(wi.getPackInfoId())) {
            throw new ExceptionEmptyProperty("packInfoId");
        }
        try {
            String fileName = wi.getPackInfoId() + ".apk";
            StorageMapping mapping = ThisApplication.context().storageMappings().random(AppPackApkFile.class);
            if (null == mapping) {
                throw new ExceptionAllocateStorageMapping();
            }
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                AppPackApkFile appPackApkFile = new AppPackApkFile();
                appPackApkFile.setName(fileName);
                appPackApkFile.setStorage(mapping.getName());
                appPackApkFile.setPackInfoId(wi.getPackInfoId());
                Date now = new Date();
                appPackApkFile.setCreateTime(now);
                appPackApkFile.setLastUpdateTime(now);
                appPackApkFile.setStatus(AppPackApkFile.statusInit);
                emc.check(appPackApkFile, CheckPersistType.all);
                appPackApkFile.saveContent(mapping, new byte[]{}, fileName);
                emc.beginTransaction(AppPackApkFile.class);
                emc.persist(appPackApkFile);
                emc.commit();

                // 开始异步下载apk文件 并更新
                DownloadJob job = new DownloadJob(wi, appPackApkFile.getId());
                job.start();
            }
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        } catch (Exception e) {
            logger.error(e);

        }
        return result;
    }


    public class DownloadJob extends Thread {
        private Wi wi;
        private String id;

        DownloadJob(Wi wi, String appPackApkFileId) {
            this.wi = wi;
            this.id = appPackApkFileId;
        }

        @Override
        public void run() {
            logger.info("开始下载apk文件");
            try {
                StorageMapping mapping = ThisApplication.context().storageMappings().random(AppPackApkFile.class);
                if (null == mapping) {
                    throw new ExceptionAllocateStorageMapping();
                }
                String url = Config.collect().appPackServerUrl() + wi.getApkPath() + "?token=" + wi.getToken();
                logger.info("下载apk的url： "+url);
                byte[] bytes = ConnectionAction.getBinary(url, null);
                try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                    AppPackApkFile file = emc.find(this.id, AppPackApkFile.class);
                    if (file != null) {
                        emc.beginTransaction(AppPackApkFile.class);
                        file.updateContent(mapping, bytes);
                        file.setStatus(AppPackApkFile.statusCompleted);
                        file.setLastUpdateTime(new Date());
                       // emc.persist(file);
                        emc.commit();
                    } else {
                        logger.info("错误，没有找到对应的文件对象，id："+id);
                    }
                }

            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("打包服务器的token.")
        private String token;
        @FieldDescribe("app的下载路径.")
        private String apkPath;
        @FieldDescribe("app的打包id")
        private String packInfoId;

        // 版本号 暂时还没有
        @FieldDescribe("app的版本号名称")
        private String appVersionName;
        @FieldDescribe("app的版本编译号")
        private String appVersionNo;


        public String getAppVersionName() {
            return appVersionName;
        }

        public void setAppVersionName(String appVersionName) {
            this.appVersionName = appVersionName;
        }

        public String getAppVersionNo() {
            return appVersionNo;
        }

        public void setAppVersionNo(String appVersionNo) {
            this.appVersionNo = appVersionNo;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getApkPath() {
            return apkPath;
        }

        public void setApkPath(String apkPath) {
            this.apkPath = apkPath;
        }

        public String getPackInfoId() {
            return packInfoId;
        }

        public void setPackInfoId(String packInfoId) {
            this.packInfoId = packInfoId;
        }
    }


    public static class Wo extends WrapBoolean {

    }
}
