package com.x.program.center.jaxrs.apppack;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.core.entity.AppPackApkFile;
import com.x.program.center.core.entity.AppPackApkFile_;

/**
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionPackInfo extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger(ActionPackInfo.class);


    ActionResult<Wo> execute(String token) throws Exception {
        ActionResult<Wo> result = new ActionResult<Wo>();
        if (StringUtils.isEmpty(token)) {
            throw new ExceptionNoToken();
        }
        Wo wo = getPackInfo(token);
        if (wo != null) {
            try {
                // 查询 关键的发布apk信息，
                try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                    EntityManager em = emc.get(AppPackApkFile.class);
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<AppPackApkFile> query = cb.createQuery(AppPackApkFile.class);
                    Root<AppPackApkFile> root = query.from(AppPackApkFile.class);
                    Predicate p = cb.equal(root.get(AppPackApkFile_.packInfoId), wo.getId());
                    query.orderBy(cb.desc(root.get(AppPackApkFile_.lastUpdateTime)));
                    query.select(root).where(p);
                    List<AppPackApkFile> list = em.createQuery(query).getResultList();
                    if (list != null && !list.isEmpty()) {
                        AppPackApkFile appPackApkFile = list.get(0);
                        wo.setAppFile(appPackApkFile);
                    }
                }
            }catch (Exception e) {
                logger.error(e);
            }
            result.setData(wo);
        } else {
            throw new ExceptionNoPackInfo();
        }
        return result;
    }

    private Wo getPackInfo(String token) {
        try {
            logger.info("打包服务器token：" +  token);
            String collectNameEncode = URLEncoder.encode(Config.collect().getName(), DefaultCharset.name);
            String url = Config.collect().appPackServerApi(String.format(Collect.ADDRESS_APPPACK_INFO, collectNameEncode));
            logger.info("打包信息请求，url：" + url);
            ArrayList<NameValuePair> heads = new ArrayList<>();
            heads.add(new NameValuePair("token", token));
            String result = HttpConnection.getAsString(url, heads);
            logger.info("获取到打包信息，结果: " + result);
            Type type = new TypeToken<AppPackResult<Wo>>() {
            }.getType();
            AppPackResult<Wo> appPackResult = XGsonBuilder.instance().fromJson(result, type);
            if (appPackResult != null && StringUtils.isNotEmpty(appPackResult.getResult()) && appPackResult.getResult().equals(AppPackResult.result_success)) {
                return appPackResult.getData();
            }
        }catch (Exception e) {
            logger.error(e);
        }
        return null;
    }


    /**
     * 打包信息对象
     */
    public static class Wo extends GsonPropertyObject {

        private String id;
        private String appName;
        // o2oa 服务器信息
        private String o2ServerProtocol; // 中心服务器协议 http | https
        private String o2ServerHost; // 中心服务器地址  ip 或 域名
        private String o2ServerPort; // 中心服务器端口 端口号
        private String o2ServerContext; //  /x_program_center
        // collect 账号
        private String collectName;
        private String createTime;
        // 打包状态 0 准备（入库）1 开始打包 2 打包结束 3 反馈结果完成
        private String packStatus;

        private String appLogoPath; // logo图片地址 相对路径
        private String apkPath; // apk下载地址 相对路径

        // 版本名称
        private String versionName;
        // 版本号 100
        private String buildNo;
        // 是否使用外部包名，2: 就是用 net.zoneland.x.bpm.mobile.v1.zoneXBPM.outer 作为apk的applicationId，默认使用老的applicationId，兼容老的版本
        private String isPackAppIdOuter;

        // 代理地址
        private String urlMapping;

        //错误信息
        private String packErrorLogs;

        // 2 表示要删除华为推送包，防止不是华为手机打开app需要安装HMS服务框架
        private String deleteHuawei;


        private AppPackApkFile appFile; // 关联的下载文件


        public String getPackErrorLogs() {
            return packErrorLogs;
        }

        public void setPackErrorLogs(String packErrorLogs) {
            this.packErrorLogs = packErrorLogs;
        }

        public String getDeleteHuawei() {
            return deleteHuawei;
        }

        public void setDeleteHuawei(String deleteHuawei) {
            this.deleteHuawei = deleteHuawei;
        }

        public String getUrlMapping() {
            return urlMapping;
        }

        public void setUrlMapping(String urlMapping) {
            this.urlMapping = urlMapping;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getBuildNo() {
            return buildNo;
        }

        public void setBuildNo(String buildNo) {
            this.buildNo = buildNo;
        }

        public String getIsPackAppIdOuter() {
            return isPackAppIdOuter;
        }

        public void setIsPackAppIdOuter(String isPackAppIdOuter) {
            this.isPackAppIdOuter = isPackAppIdOuter;
        }

        public AppPackApkFile getAppFile() {
            return appFile;
        }

        public void setAppFile(AppPackApkFile appFile) {
            this.appFile = appFile;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }


        public String getO2ServerProtocol() {
            return o2ServerProtocol;
        }

        public void setO2ServerProtocol(String o2ServerProtocol) {
            this.o2ServerProtocol = o2ServerProtocol;
        }

        public String getO2ServerHost() {
            return o2ServerHost;
        }

        public void setO2ServerHost(String o2ServerHost) {
            this.o2ServerHost = o2ServerHost;
        }

        public String getO2ServerPort() {
            return o2ServerPort;
        }

        public void setO2ServerPort(String o2ServerPort) {
            this.o2ServerPort = o2ServerPort;
        }

        public String getO2ServerContext() {
            return o2ServerContext;
        }

        public void setO2ServerContext(String o2ServerContext) {
            this.o2ServerContext = o2ServerContext;
        }

        public String getCollectName() {
            return collectName;
        }

        public void setCollectName(String collectName) {
            this.collectName = collectName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getPackStatus() {
            return packStatus;
        }

        public void setPackStatus(String packStatus) {
            this.packStatus = packStatus;
        }

        public String getAppLogoPath() {
            return appLogoPath;
        }

        public void setAppLogoPath(String appLogoPath) {
            this.appLogoPath = appLogoPath;
        }

        public String getApkPath() {
            return apkPath;
        }

        public void setApkPath(String apkPath) {
            this.apkPath = apkPath;
        }
    }
}
