package com.x.ai.assemble.control.bean;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author chengjian
 * @date 2025/06/06 14:03
 **/
public class McpConfig extends GsonPropertyObject {
    public static final String TYPE_HTTP = "http";

    @FieldDescribe("名称.")
    private String name;

    @FieldDescribe("显示名称.")
    private String displayName;

    @FieldDescribe("分组.")
    private String category;

    @FieldDescribe("说明.")
    private String desc;

    @FieldDescribe("是否启用.")
    private Boolean enable;

    @FieldDescribe("id.")
    private String id;

    @FieldDescribe("http选项：{\n"
            + "    \"url\": \"https://xxx/x_program_center/jaxrs/invoke/createMeeting/execute\",\n"
            + "    \"method\": \"post\",\n"
            + "    \"headerMap\": {\n"
            + "      \"Content-Type\": \"application/json\"\n"
            + "    },\n"
            + "    \"bodyMap\": {\n"
            + "      \"subject\": \"${subject}\",\n"
            + "    }.")
    private HttpOption httpOption;

    @FieldDescribe("mcp参数列表：[\n"
            + "    {\n"
            + "      \"name\": \"subject\",\n"
            + "      \"desc\": \"会议主题,描述会议主要议题或内容.\",\n"
            + "      \"type\": \"string\",\n"
            + "      \"required\": true\n"
            + "    },\n"
            + "  ].")
    private List<McpParameter> mcpParameterList;

    @FieldDescribe("创建时间.")
    private Date createDateTime;

    @FieldDescribe("修改时间.")
    private Date updateDateTime;

    @FieldDescribe("扩展参数，json对象.")
    private Map<String, Object> extra;

    private String type = "http";

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public HttpOption getHttpOption() {
        return httpOption;
    }

    public void setHttpOption(HttpOption httpOption) {
        this.httpOption = httpOption;
    }

    public List<McpParameter> getMcpParameterList() {
        return mcpParameterList;
    }

    public void setMcpParameterList(
            List<McpParameter> mcpParameterList) {
        this.mcpParameterList = mcpParameterList;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public static class McpParameter extends GsonPropertyObject{
        @FieldDescribe("参数名称.")
        private String name;

        @FieldDescribe("参数描述.")
        private String desc;

        @FieldDescribe("参数类型.")
        private String type;

        @FieldDescribe("是否必填.")
        private Boolean required;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }
    }

    public static class HttpOption extends GsonPropertyObject{
        @FieldDescribe("URL地址.")
        private String url;

        @FieldDescribe("http方法.")
        private String method;

        @FieldDescribe("http头,使用${}替换参数.")
        private Map<String,String> headerMap;

        @FieldDescribe("http体,使用${}替换参数.")
        private Map<String,String> bodyMap;

        private Boolean internalEnable = false;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Map<String, String> getHeaderMap() {
            return headerMap;
        }

        public void setHeaderMap(Map<String, String> headerMap) {
            this.headerMap = headerMap;
        }

        public Map<String, String> getBodyMap() {
            return bodyMap;
        }

        public void setBodyMap(Map<String, String> bodyMap) {
            this.bodyMap = bodyMap;
        }

        public Boolean getInternalEnable() {
            return internalEnable;
        }

        public void setInternalEnable(Boolean internalEnable) {
            this.internalEnable = internalEnable;
        }
    }
}
