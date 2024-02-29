package com.x.program.center.jaxrs.apppack;

import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.JpushConfig;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.AppPackApkFile;
import com.x.program.center.jaxrs.config.ActionSave;

/**
 * Created by fancyLou on 11/30/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionPublishAPK2Local extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionPublishAPK2Local.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
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
                appPackApkFile.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(fileName)));
                appPackApkFile.setStorage(mapping.getName());
                appPackApkFile.setPackInfoId(wi.getPackInfoId());
                Date now = new Date();
                appPackApkFile.setCreateTime(now);
                appPackApkFile.setLastUpdateTime(now);
                appPackApkFile.setStatus(AppPackApkFile.statusInit);
                appPackApkFile.setAppVersionNo(wi.getAppVersionNo());
                appPackApkFile.setAppVersionName(wi.getAppVersionName());
                appPackApkFile.setIsPackAppIdOuter(wi.getIsPackAppIdOuter());
                emc.check(appPackApkFile, CheckPersistType.all);
                appPackApkFile.saveContent(mapping, new byte[] {}, fileName);
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
                logger.info("下载apk的url： " + url);
                byte[] bytes = ConnectionAction.getBinary(url, null);
                try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                    AppPackApkFile file = emc.find(this.id, AppPackApkFile.class);
                    if (file != null) {
                        emc.beginTransaction(AppPackApkFile.class);
                        file.updateContent(mapping, bytes);
                        file.setStatus(AppPackApkFile.statusCompleted);
                        file.setLastUpdateTime(new Date());
                        emc.commit();
                        // 发布成功了，需要修改jpush配置文件，把outer包的key配置上去
                        if (StringUtils.isNotBlank(file.getIsPackAppIdOuter())
                                && "2".equals(file.getIsPackAppIdOuter())) {
                            updateJpushConfig();
                        }
                        updateAppUrl(wi.getWebUrl());
                        logger.info("下载发布apk成功！");
                    } else {
                        logger.error(new Exception("错误，没有找到对应的文件对象，id：" + id));
                    }
                }

            } catch (Exception e) {
                logger.error(e);
                logger.info("下载失败，更新错误状态.");
                try {
                    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                        AppPackApkFile file = emc.find(this.id, AppPackApkFile.class);
                        if (file != null) {
                            emc.beginTransaction(AppPackApkFile.class);
                            file.setStatus(AppPackApkFile.statusError);
                            file.setLastUpdateTime(new Date());
                            emc.commit();
                        } else {
                            logger.error(new IllegalStateException("错误，没有找到对应的文件对象，id：" + id + "."));
                        }
                    }
                } catch (Exception e1) {
                    logger.error(e1);
                }
            }
        }

        private void updateJpushConfig() {
            try {
                logger.info("开始修改jpush文件.......");
                JpushConfig newConfig = JpushConfig.defaultInstance().getOuterApplicationJpushConfig();
                updateConfigFileOnLine(Config.NAME_CONFIG_JPUSH, newConfig.toString());
            } catch (Exception e) {
                logger.error(e);
            }
        }

        /**
         * 更新appUrl collect.json配置文件 登录界面扫码可以下载
         */
        private void updateAppUrl(String webUrl) {
            try {
                logger.info("updateAppUrl : " + webUrl);
                if (StringUtils.isNotEmpty(webUrl)) {
                    String myUrl = webUrl;
                    String protocol = Config.nodes().centerServers().first().getValue().getHttpProtocol();
                    String webProxyHost = "";
                    int webProxyPort = 80;
                    for (Map.Entry<String, Node> en : Config.nodes().entrySet()) {
                        if (null != en.getValue()) {
                            WebServer webServer = en.getValue().getWeb();
                            if (null != webServer && BooleanUtils.isTrue(webServer.getEnable())) {
                                webProxyHost = webServer.getProxyHost();
                                webProxyPort = webServer.getProxyPort();
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(webProxyHost)) {
                        String url = protocol + "://" + webProxyHost + ":" + webProxyPort;
                        if (!url.equals(webUrl)) {
                            myUrl = url;
                        }
                    }
                    logger.info("最后的URL ：" + myUrl);
                    Config.collect().setAppUrl(myUrl + "/x_desktop/appDownload.html");
                    updateConfigFileOnLine(Config.NAME_CONFIG_COLLECT, Config.collect().toString());
                }

            } catch (Exception e) {
                logger.error(e);
            }
        }

        /**
         * 更新配置文件
         * 
         * @param fileName 配置文件名称 如 jpushConfig.json
         * @param content  配置文件内容 json
         * @throws Exception
         */
        private void updateConfigFileOnLine(String fileName, String content) throws Exception {
            logger.info("更新配置文件！文件：{} ， 内容：{}", fileName, content);
            JPushConfigSaveWi wi = new JPushConfigSaveWi();
            wi.setFileName(fileName);
            wi.setFileContent(content);
            ActionResponse response = CipherConnectionAction.post(false,
                    Config.url_x_program_center_jaxrs("config", "save"), wi);
            if (response != null) {
                ActionSave.Wo wo = response.getData(ActionSave.Wo.class);
                if (wo != null && wo.getStatus() != null) {
                    logger.info("修改保存[{}]配置文件成功！", fileName);
                }
            } else {
                logger.info("保存[{}]配置文件失败, 返回为空！", fileName);
            }
        }
    }

    public static class JPushConfigSaveWi extends GsonPropertyObject {
        private String fileName;
        private String fileContent;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
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
        @FieldDescribe("是否使用外部包名，2: 就是用 net.zoneland.x.bpm.mobile.v1.zoneXBPM.outer 作为apk的applicationId")
        private String isPackAppIdOuter;

        @FieldDescribe("前端web地址，如 http://dd.o2oa.net")
        private String webUrl;

        public String getWebUrl() {
            return webUrl;
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

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

        public String getIsPackAppIdOuter() {
            return isPackAppIdOuter;
        }

        public void setIsPackAppIdOuter(String isPackAppIdOuter) {
            this.isPackAppIdOuter = isPackAppIdOuter;
        }
    }

    public static class Wo extends WrapBoolean {

    }
}
