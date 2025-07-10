package com.x.ai.assemble.control.bean;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件
 *
 * @author sword
 * @date 2022/02/17 16:17
 **/
public class AiConfig extends GsonPropertyObject {

    private static final long serialVersionUID = -6774808981486330113L;

    public static final String appName_FIELD = "appName";
    @FieldDescribe("应用名称.")
    private String appName = "O2OA";

    public static final String appIconUrl_FIELD = "appIconUrl";
    @FieldDescribe("应用图标地址.")
    private String appIconUrl = "";

    public static final String title_FIELD = "title";
    @FieldDescribe("欢迎主题.")
    private String title = "";

    public static final String desc_FIELD = "desc";
    @FieldDescribe("主题功能描述.")
    private String desc = "";

    @FieldDescribe("知识库索引应用列表.")
    private List<String> knowledgeIndexAppList = new ArrayList<>();

    @FieldDescribe("问答库库索引应用列表.")
    private List<String> questionsIndexAppList = new ArrayList<>();

    @FieldDescribe("o2智能体附件解析支持的文件类型.")
    private List<String> o2AiFileList = List.of("pdf", "docx", "doc", "txt", "md", "pptx", "ppt", "xlsx", "xls");

    public static final String o2AiBaseUrl_FIELD = "o2AiBaseUrl";
    @FieldDescribe("o2智能体服务地址.")
    private String o2AiBaseUrl = "http://172.16.91.60:7080/x-app/api";

    public static final String o2AiToken_FIELD = "o2AiToken";
    @FieldDescribe("o2智能体服务认证秘钥.")
    private String o2AiToken = "";

    public static final String o2AiEnable_FIELD = "o2AiEnable";
    @FieldDescribe("是否启用o2智能体.")
    private Boolean o2AiEnable = false;

    public static final String deepSeekApiUrl_FIELD = "deepSeekApiUrl";
    @FieldDescribe("deepSeek服务地址.")
    private String deepSeekApiUrl = "https://api.deepseek.com/v1/chat/completions";

    public static final String aliApiUrl_FIELD = "aliApiUrl";
    @FieldDescribe("deepSeek服务地址.")
    private String aliApiUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppIconUrl() {
        return appIconUrl;
    }

    public void setAppIconUrl(String appIconUrl) {
        this.appIconUrl = appIconUrl;
    }

    public List<String> getKnowledgeIndexAppList() {
        return knowledgeIndexAppList == null ? new ArrayList<>() : knowledgeIndexAppList;
    }

    public void setKnowledgeIndexAppList(List<String> knowledgeIndexAppList) {
        this.knowledgeIndexAppList = knowledgeIndexAppList;
    }

    public String getO2AiBaseUrl() {
        return o2AiBaseUrl;
    }

    public void setO2AiBaseUrl(String o2AiBaseUrl) {
        this.o2AiBaseUrl = o2AiBaseUrl;
    }

    public List<String> getO2AiFileList() {
        return o2AiFileList;
    }

    public void setO2AiFileList(List<String> o2AiFileList) {
        this.o2AiFileList = o2AiFileList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDeepSeekApiUrl() {
        return deepSeekApiUrl;
    }

    public void setDeepSeekApiUrl(String deepSeekApiUrl) {
        this.deepSeekApiUrl = deepSeekApiUrl;
    }


    public String getO2AiToken() {
        return o2AiToken;
    }

    public void setO2AiToken(String o2AiToken) {
        this.o2AiToken = o2AiToken;
    }

    public Boolean getO2AiEnable() {
        return o2AiEnable;
    }

    public void setO2AiEnable(Boolean o2AiEnable) {
        this.o2AiEnable = o2AiEnable;
    }

    public String getAliApiUrl() {
        return aliApiUrl;
    }

    public void setAliApiUrl(String aliApiUrl) {
        this.aliApiUrl = aliApiUrl;
    }

    public List<String> getQuestionsIndexAppList() {
        return questionsIndexAppList;
    }

    public void setQuestionsIndexAppList(List<String> questionsIndexAppList) {
        this.questionsIndexAppList = questionsIndexAppList;
    }
}
