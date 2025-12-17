package com.x.ai.assemble.control.bean;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.List;
import java.util.Map;

/**
 * @author chengjian
 * @date 2025/04/09 15:18
 **/
public class ChartWi extends GsonPropertyObject {
    public static final String GENERATE_TYPE_CHAT = "chat";
    public static final String GENERATE_TYPE_AUTO = "auto";
    public static final String GENERATE_TYPE_RAG = "rag";
    public static final String GENERATE_TYPE_MCP = "mcp";
    public static final Map<String, Object> OPTION_DEFAULT  = Map.of("maxTurns", 5);

    @FieldDescribe("输入内容.")
    private String input;

    @FieldDescribe("线索标识.")
    private String clueId;

    private String person;

    private String token;

    @FieldDescribe("ai模式：chat|rag|mcp.")
    private String generateType;

    @FieldDescribe("附件标识列表.")
    private List<String> referenceIdList;

    private List<String> permissionList;
    @FieldDescribe("模型名称.")
    private String endpointName;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getClueId() {
        return clueId;
    }

    public void setClueId(String clueId) {
        this.clueId = clueId;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getGenerateType() {
        return generateType;
    }

    public void setGenerateType(String generateType) {
        this.generateType = generateType;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public List<String> getReferenceIdList() {
        return referenceIdList;
    }

    public void setReferenceIdList(List<String> referenceIdList) {
        this.referenceIdList = referenceIdList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
