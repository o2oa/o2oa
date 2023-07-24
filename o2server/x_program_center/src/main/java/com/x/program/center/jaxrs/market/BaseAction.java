package com.x.program.center.jaxrs.market;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.base.core.project.tools.ZipTools;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.program.center.Business;
import com.x.program.center.ThisApplication;
import com.x.program.center.WrapModule;
import com.x.program.center.core.entity.InstallLog;
import com.x.program.center.core.entity.InstallTypeEnum;
import com.x.program.center.core.entity.wrap.WrapAgent;
import com.x.program.center.core.entity.wrap.WrapInvoke;
import com.x.program.center.core.entity.wrap.WrapServiceModule;
import com.x.query.core.entity.wrap.WrapQuery;

abstract class BaseAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

    protected static final String COLLECT_MARKET_CATEGORY = "/o2_collect_assemble/jaxrs/application2/list/category";
    protected static final String COLLECT_MARKET_LIST_INFO = "/o2_collect_assemble/jaxrs/application2/list/paging/{page}/size/{size}";
    protected static final String COLLECT_MARKET_INFO = "/o2_collect_assemble/jaxrs/application2/";
    protected static final String COLLECT_MARKET_INSTALL_INFO = "/o2_collect_assemble/jaxrs/application2/install/";
    protected static final String COLLECT_UNIT_IS_VIP = "/o2_collect_assemble/jaxrs/unit/is/vip";
    private static final String APP_SETUP_NAME = "setup.json";

    protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(InstallLog.class);

    public boolean hasAuth(EffectivePerson effectivePerson, String person) {
        if (effectivePerson.isManager()) {
            return true;
        }
        if (effectivePerson.getDistinguishedName().equals(person)) {
            return true;
        }
        return false;
    }

    protected InstallData install(Application2 app, byte[] bytes) throws Exception {
        InstallData installData = new InstallData();
        String id = StringTools.uniqueToken();
        File tempFile = new File(Config.base(), "local/temp/install/" + id);
        FileTools.forceMkdir(tempFile);
        if (StringUtils.isNotBlank(app.getId())) {
            id = app.getId();
        }
        File zipFile = new File(tempFile.getAbsolutePath(), id + ".zip");
        FileUtils.writeByteArrayToFile(zipFile, bytes);
        File dist = new File(tempFile.getAbsolutePath(), "data");
        FileTools.forceMkdir(dist);
        ZipTools.unZip(zipFile, new ArrayList<>(), dist, true, null);
        if (StringUtils.isBlank(app.getId())) {
            File[] setupFile = dist.listFiles(pathname -> pathname.getName().equals(APP_SETUP_NAME));
            if (setupFile == null || setupFile.length == 0) {
                throw new ExceptionErrorInstallPackage();
            }
            String json = FileUtils.readFileToString(setupFile[0], DefaultCharset.charset);
            Application2 offlineApp = gson.fromJson(json, Application2.class);
            if (StringUtils.isBlank(offlineApp.getId()) || StringUtils.isBlank(offlineApp.getName())) {
                throw new ExceptionErrorInstallPackage();
            }
            String token = Business.loginCollect();
            if (StringUtils.isNotEmpty(token)) {
                try {
                    ActionResponse response = ConnectionAction.get(
                            Config.collect().url(COLLECT_MARKET_INSTALL_INFO + offlineApp.getId()),
                            ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, token)));
                    offlineApp = response.getData(Application2.class);
                } catch (Exception e) {
                    logger.warn("get market info form o2cloud error: {}.", e.getMessage());
                }
            }
            BeanUtils.copyProperties(app, offlineApp);
        }
        File[] files = dist.listFiles(pathname -> !pathname.getName().toLowerCase().endsWith(".ds_store"));
        if (files == null || files.length == 0) {
            return installData;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().toLowerCase().equals(InstallTypeEnum.CUSTOM.getValue())) {
                    File[] subFiles = file
                            .listFiles(pathname -> !pathname.getName().toLowerCase().endsWith(".ds_store"));
                    if (subFiles != null && subFiles.length > 0) {
                        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                            List<String> list = new ArrayList<>();
                            boolean flag = ZipTools.toZip(file, out, list);
                            if (flag) {
                                logger.info("开始部署[{}]的customApp", app.getName());
                                this.installCustomApp(app.getId() + "-custom.zip", out.toByteArray());
                                logger.info("完成部署[{}]的customApp，安装内容：{}", app.getName(), gson.toJson(list));
                                installData.setCustomList(list);
                            }
                        }
                    }
                } else if (file.getName().toLowerCase().equals(InstallTypeEnum.XAPP.getValue())) {
                    File[] subFiles = file.listFiles(pathname -> pathname.isFile());
                    if (subFiles != null && subFiles.length > 0) {
                        List<WrapModule> moduleList = new ArrayList<>();
                        for (File subFile : subFiles) {
                            if (subFile.getName().toLowerCase().endsWith(".xapp")) {
                                logger.info("开始部署[{}]", subFile.getName());
                                String json = FileUtils.readFileToString(subFile, DefaultCharset.charset);
                                Gson gson = new Gson();
                                JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                                WrapModule module = this.convertToWrapIn(jsonElement, WrapModule.class);
                                this.installModule(module);
                                moduleList.add(module);
                                logger.info("完成部署[{}]", subFile.getName());
                            }
                        }
                        installData.setWrapModuleList(moduleList);
                    }
                } else if (file.getName().toLowerCase().equals(InstallTypeEnum.WEB.getValue())) {
                    File[] subFiles = file
                            .listFiles(pathname -> !pathname.getName().toLowerCase().endsWith(".ds_store"));
                    if (subFiles != null && subFiles.length > 0) {
                        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                            List<String> list = new ArrayList<>();
                            boolean flag = ZipTools.toZip(file, out, list);
                            if (flag) {
                                logger.info("开始部署[{}]的web资源", app.getName());
                                Business.dispatch(false, app.getId() + "-web.zip", "", out.toByteArray());
                                logger.info("完成部署[{}]的web资源", app.getName());
                                installData.setWebList(list);
                            }
                        }
                    }
                } else if (file.getName().toLowerCase().equals(InstallTypeEnum.DATA.getValue())) {
                    File[] subFiles = file.listFiles();
                    if (subFiles != null && subFiles.length > 0) {
                        // todo
                    }
                }
            }
        }
        try {
            FileUtils.cleanDirectory(tempFile);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        return installData;
    }

    protected ActionInstallOffline.InstallWo installModule(WrapModule module) throws Exception {
        ActionInstallOffline.InstallWo wo = new ActionInstallOffline.InstallWo();
        if (module.getProcessPlatformList() != null) {
            for (WrapProcessPlatform obj : module.getProcessPlatformList()) {
                wo.getProcessPlatformList().add(
                        ThisApplication.context().applications().putQuery(x_processplatform_assemble_designer.class,
                                Applications.joinQueryUri("input", "cover"), obj).getData(WoId.class).getId());
                obj.setIcon(null);
                obj.setApplicationDictList(null);
                obj.setFileList(null);
                obj.setFormList(null);
                obj.setProcessList(null);
                obj.setScriptList(null);
            }
        }
        if (module.getCmsList() != null) {
            for (WrapCms obj : module.getCmsList()) {
                wo.getCmsList().add(ThisApplication.context().applications()
                        .putQuery(x_cms_assemble_control.class, Applications.joinQueryUri("input", "cover"), obj)
                        .getData(WoId.class).getId());
                obj.setAppIcon(null);
                obj.setAppDictList(null);
                obj.setCategoryInfoList(null);
                obj.setFileList(null);
                obj.setFormList(null);
                obj.setScriptList(null);
            }
        }
        if (module.getPortalList() != null) {
            for (WrapPortal obj : module.getPortalList()) {
                wo.getPortalList().add(ThisApplication.context().applications()
                        .putQuery(x_portal_assemble_designer.class, Applications.joinQueryUri("input", "cover"), obj)
                        .getData(WoId.class).getId());
                obj.setIcon(null);
                obj.setFileList(null);
                obj.setPageList(null);
                obj.setScriptList(null);
                obj.setWidgetList(null);
            }
        }
        if (module.getQueryList() != null) {
            for (WrapQuery obj : module.getQueryList()) {
                wo.getQueryList().add(ThisApplication.context().applications()
                        .putQuery(x_query_assemble_designer.class, Applications.joinQueryUri("input", "cover"), obj)
                        .getData(WoId.class).getId());
                obj.setIcon(null);
                obj.setViewList(null);
                obj.setStatementList(null);
                obj.setViewList(null);
                obj.setTableList(null);
            }
        }

        if (module.getServiceModuleList() != null) {
            for (WrapServiceModule obj : module.getServiceModuleList()) {
                wo.getServiceModuleList()
                        .add(CipherConnectionAction.put(false, Config.url_x_program_center_jaxrs("input", "cover"), obj)
                                .getData(WoId.class).getId());
                if (obj.getAgentList() != null) {
                    for (WrapAgent agent : obj.getAgentList()) {
                        agent.setText(null);
                    }
                }
                if (obj.getInvokeList() != null) {
                    for (WrapInvoke invoke : obj.getInvokeList()) {
                        invoke.setText(null);
                    }
                }
            }
        }

        return wo;
    }

    protected void installCustomApp(String fileName, byte[] bytes) throws Exception {
        Nodes nodes = Config.nodes();
        for (String node : nodes.keySet()) {
            if (nodes.get(node).getApplication().getEnable()) {
                logger.info("socket deploy custom app{} to {}:{}", fileName, node, nodes.get(node).nodeAgentPort());
                try (Socket socket = new Socket(node, nodes.get(node).nodeAgentPort())) {
                    socket.setKeepAlive(true);
                    socket.setSoTimeout(10000);
                    try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                        Map<String, Object> commandObject = new HashMap<>();
                        commandObject.put("command", "redeploy:customZip");
                        commandObject.put("credential", Crypto.rsaEncrypt("o2@", Config.publicKey()));

                        dos.writeUTF(XGsonBuilder.toJson(commandObject));
                        dos.flush();
                        dos.writeUTF(fileName);
                        dos.flush();

                        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                            byte[] onceBytes = new byte[1024];
                            int length = 0;
                            while ((length = bis.read(onceBytes, 0, onceBytes.length)) != -1) {
                                dos.write(onceBytes, 0, length);
                                dos.flush();
                            }
                        }
                    }

                }
            }
        }
    }

    public static class InstallData extends GsonPropertyObject {
        private List<WrapModule> wrapModuleList;

        private List<String> webList;

        private List<String> customList;

        public List<WrapModule> getWrapModuleList() {
            return wrapModuleList;
        }

        public void setWrapModuleList(List<WrapModule> wrapModuleList) {
            this.wrapModuleList = wrapModuleList;
        }

        public List<String> getWebList() {
            return webList;
        }

        public void setWebList(List<String> webList) {
            this.webList = webList;
        }

        public List<String> getCustomList() {
            return customList;
        }

        public void setCustomList(List<String> customList) {
            this.customList = customList;
        }
    }

    public static class Application2 extends GsonPropertyObject {

        @FieldDescribe("主键.")
        private String id;

        @FieldDescribe("名称.必填")
        private String name;

        @FieldDescribe("分类.必填")
        private String category;

        @FieldDescribe("子分类.")
        private String subCategory;

        @FieldDescribe("版本.必填")
        private String version;

        @FieldDescribe("价格.")
        private Double price;

        @FieldDescribe("状态：draft|audit|publish|invalid.")
        private String status;

        @FieldDescribe("宣传图片url链接.")
        private String broadcastPic;

        @FieldDescribe("封面图片url链接.")
        private String indexPic;

        @FieldDescribe("视频url链接.")
        private String video;

        @FieldDescribe("依赖中间件(如：onlyOffice)")
        private String middleware;

        @FieldDescribe("适配O2的版本(向上兼容)")
        private String o2Version;

        @FieldDescribe("配置文件配置地址(web端)")
        private String configPath;

        @FieldDescribe("描述.必填")
        private String describe;

        @FieldDescribe("应用简介.必填")
        private String abort;

        @FieldDescribe("应用安装步骤.必填")
        private String installSteps;

        @FieldDescribe("发布者.")
        private String publisher;

        @FieldDescribe("发布时间")
        private Date publishTime;

        @FieldDescribe("排序号,升序排列,为空在最后")
        private Integer orderNumber;

        @FieldDescribe("推荐指数")
        private Integer recommend;

        @FieldDescribe("下载次数")
        private Integer downloadCount;

        @FieldDescribe("最后更新时间")
        private Date lastUpdateTime;

        @FieldDescribe("安装后是否需要重启")
        private Boolean restart = false;

        @FieldDescribe("是否是VIP应用")
        private Boolean vipApp = false;

        @FieldDescribe("创建时间.")
        private Date createTime;

        @FieldDescribe("修改时间.")
        private Date updateTime;

        /**
         * 是否有下载安装权限
         */
        Boolean hasInstallPermission;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public void setSubCategory(String subCategory) {
            this.subCategory = subCategory;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getBroadcastPic() {
            return broadcastPic;
        }

        public void setBroadcastPic(String broadcastPic) {
            this.broadcastPic = broadcastPic;
        }

        public String getIndexPic() {
            return indexPic;
        }

        public void setIndexPic(String indexPic) {
            this.indexPic = indexPic;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getMiddleware() {
            return middleware;
        }

        public void setMiddleware(String middleware) {
            this.middleware = middleware;
        }

        public String getO2Version() {
            return o2Version;
        }

        public void setO2Version(String o2Version) {
            this.o2Version = o2Version;
        }

        public String getConfigPath() {
            return configPath;
        }

        public void setConfigPath(String configPath) {
            this.configPath = configPath;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getAbort() {
            return abort;
        }

        public void setAbort(String abort) {
            this.abort = abort;
        }

        public String getInstallSteps() {
            return installSteps;
        }

        public void setInstallSteps(String installSteps) {
            this.installSteps = installSteps;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public Date getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(Date publishTime) {
            this.publishTime = publishTime;
        }

        public Integer getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
        }

        public Integer getRecommend() {
            return recommend;
        }

        public void setRecommend(Integer recommend) {
            this.recommend = recommend;
        }

        public Integer getDownloadCount() {
            return downloadCount;
        }

        public void setDownloadCount(Integer downloadCount) {
            this.downloadCount = downloadCount;
        }

        public Date getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(Date lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        public Boolean getRestart() {
            return restart;
        }

        public void setRestart(Boolean restart) {
            this.restart = restart;
        }

        public Boolean getVipApp() {
            return vipApp;
        }

        public void setVipApp(Boolean vipApp) {
            this.vipApp = vipApp;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public Boolean getHasInstallPermission() {
            return hasInstallPermission;
        }

        public void setHasInstallPermission(Boolean hasInstallPermission) {
            this.hasInstallPermission = hasInstallPermission;
        }
    }

}
