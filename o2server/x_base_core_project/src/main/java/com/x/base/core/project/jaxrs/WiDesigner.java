package com.x.base.core.project.jaxrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WiDesigner extends GsonPropertyObject {

    @FieldDescribe("搜索关键字.")
    private String keyword;
    @FieldDescribe("是否区分大小写.")
    private Boolean caseSensitive;
    @FieldDescribe("是否全字匹配.")
    private Boolean matchWholeWord;
    @FieldDescribe("是否正则表达式匹配.")
    private Boolean matchRegExp;
    @FieldDescribe("设计类型：script|form|page|widget|process")
    private List<String> designerTypes = new ArrayList<>();
    @FieldDescribe("模块的应用列表.")
    private List<ModuleApp> moduleAppList = new ArrayList<>();

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Boolean getCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public Boolean getMatchWholeWord() {
        return matchWholeWord;
    }

    public void setMatchWholeWord(Boolean matchWholeWord) {
        this.matchWholeWord = matchWholeWord;
    }

    public Boolean getMatchRegExp() {
        return matchRegExp;
    }

    public void setMatchRegExp(Boolean matchRegExp) {
        this.matchRegExp = matchRegExp;
    }

    public List<String> getDesignerTypes() {
        return designerTypes == null ? new ArrayList<>() : designerTypes;
    }

    public void setDesignerTypes(List<String> designerTypes) {
        this.designerTypes = designerTypes;
    }

    public List<ModuleApp> getModuleAppList() {
        return moduleAppList == null ? new ArrayList<>() : moduleAppList;
    }

    public void setModuleAppList(List<ModuleApp> moduleAppList) {
        this.moduleAppList = moduleAppList;
    }

    public List<String> getAppIdList(){
        Set<String> set = new HashSet<>();
        for (ModuleApp app : this.getModuleAppList()){
            set.add(app.getAppId());
        }
        return new ArrayList<>(set);
    }

    public Map<String, List<String>> getAppDesigner(){
        Map<String, List<String>> map = new HashMap<>();
        for (ModuleApp app : this.getModuleAppList()){
            for(Designer designer : app.getDesignerList()){
                String designerType = designer.getDesignerType();
                if(StringUtils.isNotBlank(designerType)){
                    if (map.containsKey(designerType)){
                        map.get(designerType).addAll(designer.getDesignerIdList());
                    } else{
                        if(!designer.getDesignerIdList().isEmpty()) {
                            map.put(designerType, designer.getDesignerIdList());
                        }
                    }
                }
            }
        }
        return map;
    }

    public class ModuleApp extends GsonPropertyObject {
        @FieldDescribe("应用ID.")
        private String appId;

        @FieldDescribe("设计列表.")
        private List<Designer> designerList;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public List<Designer> getDesignerList() {
            return designerList == null ? new ArrayList<>() : designerList;
        }

        public void setDesignerList(List<Designer> designerList) {
            this.designerList = designerList;
        }
    }

    public class Designer extends GsonPropertyObject {

        @FieldDescribe("设计类型.")
        private String designerType;

        @FieldDescribe("设计ID列表.")
        private List<String> designerIdList;

        public String getDesignerType() {
            return designerType;
        }

        public void setDesignerType(String designerType) {
            this.designerType = designerType;
        }

        public List<String> getDesignerIdList() {
            return designerIdList == null ? new ArrayList<>() : designerIdList;
        }

        public void setDesignerIdList(List<String> designerIdList) {
            this.designerIdList = designerIdList;
        }
    }
}
