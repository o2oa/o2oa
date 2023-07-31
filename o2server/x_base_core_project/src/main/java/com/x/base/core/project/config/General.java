package com.x.base.core.project.config;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.*;

import com.x.base.core.project.tools.Host;
import com.x.base.core.project.tools.ListTools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;

public class General extends ConfigObject {

    private static final long serialVersionUID = 4393280516414081348L;
    private static final Boolean DEFAULT_WEBSOCKETENABLE = true;
    private static final Boolean DEFAULT_CONFIGAPIENABLE = true;
    private static final List<String> DEFAULT_SCRIPTINGBLOCKEDCLASSES = Arrays.asList(Runtime.class.getName(),
            File.class.getName(), Path.class.getName(), java.lang.ProcessBuilder.class.getName(),
            FileWriter.class.getName(), java.lang.System.class.getName());
    private static final Boolean DEFAULT_REQUESTLOGENABLE = false;
    private static final Integer DEFAULT_REQUESTLOGRETAINDAYS = 7;
    private static final Boolean DEFAULT_REQUESTLOGBODYENABLE = false;

    private static final Boolean DEFAULT_DEPLOYRESOURCEENABLE = false;
    private static final Boolean DEFAULT_DEPLOYWARENABLE = false;

    private static final Boolean DEFAULT_STATENABLE = false;
    private static final String DEFAULT_STATEXCLUSIONS = "*.js,*.gif,*.jpg,*.png,*.css,*.ico";
    private static final Boolean DEFAULT_EXPOSEJEST = true;
    private static final String DEFAULT_REFERERHEADCHECKREGULAR = "";
    private static final String DEFAULT_ACCESSCONTROLALLOWORIGIN = "";
    private static final String DEFAULT_IDFORMATCHECKREGULAR = "";
    private static final String DEFAULT_HTTP_WHITE = "*";
    private static final List<String> DEFAULT_HTTPWHITELIST = Arrays.asList(DEFAULT_HTTP_WHITE);

    public static General defaultInstance() {
        General o = new General();
        o.webSocketEnable = DEFAULT_WEBSOCKETENABLE;
        o.configApiEnable = DEFAULT_CONFIGAPIENABLE;
        o.scriptingBlockedClasses = DEFAULT_SCRIPTINGBLOCKEDCLASSES;
        o.requestLogEnable = DEFAULT_REQUESTLOGENABLE;
        o.requestLogRetainDays = DEFAULT_REQUESTLOGRETAINDAYS;
        o.requestLogBodyEnable = DEFAULT_REQUESTLOGBODYENABLE;
        o.deployResourceEnable = DEFAULT_DEPLOYRESOURCEENABLE;
        o.deployWarEnable = DEFAULT_DEPLOYWARENABLE;
        o.statEnable = DEFAULT_STATENABLE;
        o.statExclusions = DEFAULT_STATEXCLUSIONS;
        o.exposeJest = DEFAULT_EXPOSEJEST;
        o.refererHeadCheckRegular = DEFAULT_REFERERHEADCHECKREGULAR;
        o.accessControlAllowOrigin = DEFAULT_ACCESSCONTROLALLOWORIGIN;
        o.idFormatCheckRegular = DEFAULT_IDFORMATCHECKREGULAR;
        o.httpWhiteList = DEFAULT_HTTPWHITELIST;
        o.attachmentConfig = new AttachmentConfig();
        return o;
    }

    @FieldDescribe("启用访问日志功能.")
    private Boolean requestLogEnable;

    @FieldDescribe("访问日志记录天数,默认7天.")
    private Integer requestLogRetainDays;

    @FieldDescribe("访问日志是否记录post或者put的body内容,只对content-type为application/json的请求有效.")
    private Boolean requestLogBodyEnable;

    @FieldDescribe("是否启用webSocket链接.")
    private Boolean webSocketEnable;

    @FieldDescribe("允许通过接口修改系统配置.")
    private Boolean configApiEnable;

    @FieldDescribe("是否允许部署war包.")
    private Boolean deployWarEnable;

    @FieldDescribe("是否允许部署静态资源.")
    private Boolean deployResourceEnable;

    @FieldDescribe("启用统计,默认启用统计.")
    private Boolean statEnable;

    @FieldDescribe("统计忽略路径,默认忽略*.js,*.gif,*.jpg,*.png,*.css,*.ico")
    private String statExclusions;

    @FieldDescribe("暴露jest接口.")
    private Boolean exposeJest;

    @FieldDescribe("脚本中禁止用的类名,保持为空则默认禁用Runtime,File,Path.")
    private List<String> scriptingBlockedClasses;

    @FieldDescribe("http referer 校验正则表达式,可以对CSRF攻击进行防护校验,样例:(.+?)o2oa.net(.+?)")
    private String refererHeadCheckRegular = "";

    @FieldDescribe("跨源资源共享许可,设置http返回的Access-Control-Allow-Origin标识,可以用于CORS攻击防护,样例:https://www.o2oa.net")
    private String accessControlAllowOrigin = "";

    @FieldDescribe("附件上传限制大小或者类型.")
    private AttachmentConfig attachmentConfig;

    @FieldDescribe("对象id格式校验正则表达式.")
    private String idFormatCheckRegular = "";

    @FieldDescribe("外部http接口服务地址白名单，*代表不限制.")
    private List<String> httpWhiteList;

    public String getIdFormatCheckRegular() {
        return this.idFormatCheckRegular;
    }

    public String getRefererHeadCheckRegular() {
        return (StringUtils.isBlank(refererHeadCheckRegular) ? DEFAULT_REFERERHEADCHECKREGULAR
                : this.refererHeadCheckRegular);
    }

    public String getAccessControlAllowOrigin() {
        return (StringUtils.isBlank(accessControlAllowOrigin) ? DEFAULT_ACCESSCONTROLALLOWORIGIN
                : this.accessControlAllowOrigin);
    }

    public Boolean getExposeJest() {
        return BooleanUtils.isNotFalse(this.exposeJest);
    }

    public String getStatExclusions() {
        return (StringUtils.isEmpty(statExclusions) ? DEFAULT_STATEXCLUSIONS : this.statExclusions) + ",/druid/*";
    }

    public Boolean getStatEnable() {
        return BooleanUtils.isNotFalse(statEnable);
    }

    public Boolean getRequestLogEnable() {
        return BooleanUtils.isTrue(this.requestLogEnable);
    }

    public Integer getRequestLogRetainDays() {
        return (null == this.requestLogRetainDays || this.requestLogRetainDays < 1) ? DEFAULT_REQUESTLOGRETAINDAYS
                : this.requestLogRetainDays;
    }

    public Boolean getRequestLogBodyEnable() {
        return BooleanUtils.isTrue(this.requestLogBodyEnable);
    }

    public List<String> getScriptingBlockedClasses() {
        return (null == this.scriptingBlockedClasses) ? DEFAULT_SCRIPTINGBLOCKEDCLASSES : this.scriptingBlockedClasses;
    }

    public Boolean getWebSocketEnable() {
        return null == this.webSocketEnable ? DEFAULT_WEBSOCKETENABLE : this.webSocketEnable;
    }

    public Boolean getConfigApiEnable() {
        return null == this.configApiEnable ? DEFAULT_CONFIGAPIENABLE : this.configApiEnable;
    }

    public Boolean getDeployWarEnable() {

        return null == this.deployWarEnable ? DEFAULT_DEPLOYWARENABLE : this.deployWarEnable;
    }

    public Boolean getDeployResourceEnable() {

        return null == this.deployResourceEnable ? DEFAULT_DEPLOYRESOURCEENABLE : this.deployResourceEnable;
    }

    public List<String> getHttpWhiteList() {
        Set<String> httpWhiteSet = httpWhiteList == null ? new HashSet<>(DEFAULT_HTTPWHITELIST) : new HashSet<>(httpWhiteList);
        if(httpWhiteSet.contains(DEFAULT_HTTP_WHITE)){
            return Collections.EMPTY_LIST;
        }
        httpWhiteSet.add(Host.ROLLBACK_IPV4);
        httpWhiteSet.add(Collect.Default_server);
        httpWhiteSet.add(Collect.Default_appPackServerHost);
        try {
            Config.nodes().entrySet().stream().forEach(n -> httpWhiteSet.add(n.getKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(httpWhiteSet);
    }

    public AttachmentConfig getAttachmentConfig() {
        return this.attachmentConfig == null ? new AttachmentConfig() : attachmentConfig;
    }

    public static class AttachmentConfig extends ConfigObject {

        private static final long serialVersionUID = -5672631798073576284L;

        public static AttachmentConfig defaultInstance() {
            return new AttachmentConfig();
        }

        public static final Integer DEFAULT_FILE_SIZE = 2047;

        @FieldDescribe("附件大小限制（单位M，最大2048M）.")
        private Integer fileSize = DEFAULT_FILE_SIZE;

        @FieldDescribe("只允许上传的文件后缀")
        private List<String> fileTypeIncludes = Arrays.asList("doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf",
                "xapp", "text", "zip", "rar", "mp3", "mp4", "png", "jpg", "gif");

        @FieldDescribe("不允许上传的文件后缀")
        private List<String> fileTypeExcludes = Collections.EMPTY_LIST;

        public Integer getFileSize() {
            return fileSize;
        }

        public List<String> getFileTypeIncludes() {
            return fileTypeIncludes;
        }

        public List<String> getFileTypeExcludes() {
            return fileTypeExcludes;
        }

    }

    public void save() throws Exception {
        File file = new File(Config.base(), Config.PATH_CONFIG_GENERAL);
        FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
        BaseTools.executeSyncFile(Config.PATH_CONFIG_GENERAL);
    }

}
