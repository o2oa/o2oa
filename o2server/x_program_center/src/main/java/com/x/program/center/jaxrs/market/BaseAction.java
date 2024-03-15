package com.x.program.center.jaxrs.market;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.x.base.core.project.tools.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.Path;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
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
    private static final String APP_CUSTOM_CONFIG = "customConfig.json";
    private static final String CONFIG_OPERATE_REPLACE = "replace";
    private static final String MAC_DS_STORE = ".ds_store";

    protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(InstallLog.class);

    public boolean hasAuth(EffectivePerson effectivePerson, String person) {
        return effectivePerson.isManager() || effectivePerson.getDistinguishedName().equals(person);
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
        File[] files = dist.listFiles(pathname -> !pathname.getName().toLowerCase().endsWith(MAC_DS_STORE));
        if (files == null || files.length == 0) {
            return installData;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().toLowerCase().equals(InstallTypeEnum.CUSTOM.getValue())) {
                    this.deployCustomApp(file, installData, app);
                } else if (file.getName().toLowerCase().equals(InstallTypeEnum.XAPP.getValue())) {
                    this.deployXapp(file, installData);
                } else if (file.getName().toLowerCase().equals(InstallTypeEnum.WEB.getValue())) {
                    this.deployWeb(file, installData, app);
                } else if (file.getName().toLowerCase().equals(InstallTypeEnum.CONFIG.getValue())) {
                    this.deployConfig(file, installData, app);
                }
            }
        }
        try {
            FileUtils.deleteDirectory(tempFile);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        return installData;
    }

    private void deployCustomApp(File file, InstallData installData, Application2 app) throws Exception{
        File[] subFiles = file
                .listFiles(pathname -> !pathname.getName().toLowerCase().endsWith(MAC_DS_STORE));
        if (subFiles != null && subFiles.length > 0) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                List<String> list = new ArrayList<>();
                boolean flag = ZipTools.toZip(file, out, list);
                if (flag) {
                    logger.info("开始部署[{}]的customApp", app.getName());
                    this.installDispatch(app.getId() + "-custom.zip", out.toByteArray(), InstallTypeEnum.CUSTOM.getValue());
                    logger.info("完成部署[{}]的customApp，安装内容：{}", app.getName(), gson.toJson(list));
                    installData.setCustomList(list);
                }
            }
        }
    }

    private void deployXapp(File file, InstallData installData) throws Exception{
        File[] subFiles = file.listFiles(pathname -> pathname.getName().toLowerCase().endsWith(".xapp"));
        if (subFiles != null && subFiles.length > 0) {
            List<WrapModule> moduleList = new ArrayList<>();
            for (File subFile : subFiles) {
                logger.info("开始部署[{}]", subFile.getName());
                String json = FileUtils.readFileToString(subFile, DefaultCharset.charset);
                Gson gson = new Gson();
                JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
                WrapModule module = this.convertToWrapIn(jsonElement, WrapModule.class);
                this.installModule(module);
                moduleList.add(module);
                logger.info("完成部署[{}]", subFile.getName());
            }
            installData.setWrapModuleList(moduleList);
        }
    }

    private void deployWeb(File file, InstallData installData, Application2 app) throws Exception{
        File[] subFiles = file
                .listFiles(pathname -> !pathname.getName().toLowerCase().endsWith(MAC_DS_STORE));
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
    }

    private void deployConfig(File file, InstallData installData, Application2 app) throws Exception{
        File[] subFiles = file.listFiles(pathname -> pathname.getName().toLowerCase().endsWith(".json"));
        if (subFiles != null && subFiles.length > 0) {
            logger.info("开始部署[{}]的config配置", app.getName());
            final List<String> list = new ArrayList<>();
            for (File subFile : subFiles) {
                if(APP_CUSTOM_CONFIG.equalsIgnoreCase(subFile.getName())){
                    Map<String, JsonElement> map = new HashMap<>();
                    List<CustomConfig> customConfigList = gson.fromJson(Files.readString(subFile.toPath(), StandardCharsets.UTF_8),
                            new TypeToken<List<CustomConfig>>(){}.getType());
                    for (CustomConfig customConfig : customConfigList){
                        String nameEnd = ".json";
                        if(!StringUtils.endsWith(customConfig.getConfigName(), nameEnd)){
                            continue;
                        }
                        JsonElement fileJson = map.containsKey(customConfig.getConfigName()) ? map.get(customConfig.getConfigName()) :
                                getCustomConfig(customConfig.getConfigName());
                        if(fileJson!=null && customConfig.getData()!=null){
                            JsonElement newFileJson;
                            if(CONFIG_OPERATE_REPLACE.equalsIgnoreCase(customConfig.getOperate())){
                                newFileJson = XGsonBuilder.replace(customConfig.getData(), fileJson, customConfig.getPath());
                            }else{
                                newFileJson = XGsonBuilder.cover(customConfig.getData(), fileJson, customConfig.getPath());
                            }
                            if(newFileJson != null){
                                map.put(customConfig.getConfigName(), newFileJson);
                            }
                        }
                    }
                    map.entrySet().stream().forEach(o -> {
                        try {
                            String filePath = Config.DIR_CONFIG + Path.SEPARATOR + o.getKey();
                            this.installDispatch(filePath, XGsonBuilder.toJson(o.getValue()).getBytes(DefaultCharset.charset), InstallTypeEnum.CONFIG.getValue());
                            list.add(o.getKey());
                        } catch (Exception e) {
                            logger.error(e);
                        }
                    });
                }else {
                    String filePath = Config.DIR_CONFIG + Path.SEPARATOR + subFile.getName();
                    this.installDispatch(filePath, FileUtils.readFileToByteArray(subFile), InstallTypeEnum.CONFIG.getValue());
                    list.add(subFile.getName());
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                logger.debug(e.getMessage());
            }
            this.configFlush();
            installData.setConfigList(list);
            logger.info("完成部署[{}]的config配置", app.getName());
        }
    }

    private JsonElement getCustomConfig(String configName) throws Exception{
        JsonElement config = BaseTools.readConfigObject(Config.DIR_CONFIG + "/" + configName, JsonObject.class);
        JsonElement configSample = BaseTools.readConfigObject(Config.DIR_CONFIGSAMPLE + "/" + configName, JsonObject.class);
        if(config == null){
            return configSample;
        }else if(configSample != null){
            return XGsonBuilder.cover(config, configSample, "");
        }
        return config;
    }

    protected ActionInstallOffline.InstallWo installModule(WrapModule module) throws Exception {
        ActionInstallOffline.InstallWo wo = new ActionInstallOffline.InstallWo();
        String[] paths = {"input","cover"};
        if (module.getProcessPlatformList() != null) {
            for (WrapProcessPlatform obj : module.getProcessPlatformList()) {
                wo.getProcessPlatformList().add(
                        ThisApplication.context().applications().putQuery(x_processplatform_assemble_designer.class,
                                Applications.joinQueryUri(paths), obj).getData(WoId.class).getId());
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
                        .putQuery(x_cms_assemble_control.class, Applications.joinQueryUri(paths), obj)
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
                        .putQuery(x_portal_assemble_designer.class, Applications.joinQueryUri(paths), obj)
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
                        .putQuery(x_query_assemble_designer.class, Applications.joinQueryUri(paths), obj)
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
                        .add(CipherConnectionAction.put(false, Config.url_x_program_center_jaxrs(paths), obj)
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

    protected void installDispatch(String fileName, byte[] bytes, String installType) throws Exception {
        Nodes nodes = Config.nodes();
        for (Map.Entry<String, Node> entry : nodes.entrySet()) {
            String node = entry.getKey();
            if (BooleanUtils.isNotFalse(entry.getValue().nodeAgentEnable())) {
                logger.info("socket deploy installType={} file={} to {}:{}",installType, fileName, node, entry.getValue().nodeAgentPort());
                try (Socket socket = new Socket(node, entry.getValue().nodeAgentPort())) {
                    socket.setKeepAlive(true);
                    socket.setSoTimeout(6000);
                    try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                        Map<String, Object> commandObject = new HashMap<>();
                        if(InstallTypeEnum.CONFIG.getValue().equals(installType)){
                            commandObject.put("command", "syncFile:" + fileName);
                        }else if(InstallTypeEnum.CUSTOM.getValue().equals(installType)) {
                            commandObject.put("command", "redeploy:customZip");
                        }else{
                            return;
                        }
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
                }catch (Exception e){
                    logger.error(e);
                }
            }
        }
    }

    private void configFlush() throws Exception {
        Config.flush();
        ThisApplication.context().applications().values().forEach(o ->
            o.stream().forEach(app -> {
                try {
                    CipherConnectionAction.get(false, app, "cache", "config", "flush");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
        );
        List<Map.Entry<String, CenterServer>> centerList = Config.nodes().centerServers().orderedEntry();
        for (Map.Entry<String, CenterServer> centerEntry : centerList) {
            try {
                CipherConnectionAction.get(false,
                        Config.url_x_program_center_jaxrs(centerEntry, "cache", "config", "flush"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class CustomConfig extends GsonPropertyObject {
        @FieldDescribe("配置文件名称，名称后缀必须为.json的文件名.")
        private String configName;
        @FieldDescribe("目标参数路径(可以为空，多级用.隔开，如：aaa.bbb).")
        private String path;
        @FieldDescribe("replace(完全替换)|cover(覆盖).")
        private String operate;
        @FieldDescribe("新的配置信息，必须是Json对象或者Json数组.")
        private JsonElement data;

        public String getConfigName() {
            return configName;
        }

        public void setConfigName(String configName) {
            this.configName = configName;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getOperate() {
            return operate;
        }

        public void setOperate(String operate) {
            this.operate = operate;
        }

        public JsonElement getData() {
            return data;
        }

        public void setData(JsonElement data) {
            this.data = data;
        }
    }

    public static class InstallData extends GsonPropertyObject {
        private List<WrapModule> wrapModuleList;

        private List<String> webList;

        private List<String> customList;

        private List<String> configList;

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

        public List<String> getConfigList() {
            return configList;
        }

        public void setConfigList(List<String> configList) {
            this.configList = configList;
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
