package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WiDesigner extends GsonPropertyObject {

    @FieldDescribe("搜索关键字.")
    private String keyword;
    @FieldDescribe("是否区分大小写.")
    private Boolean caseSensitive;
    @FieldDescribe("是否全字匹配.")
    private Boolean matchWholeWord;
    @FieldDescribe("是否正则表达式匹配.")
    private Boolean matchRegExp;
    @FieldDescribe("模块的应用ID信息.")
    private Map<String, List<String>> moduleApps = new HashMap<>();

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

    public Map<String, List<String>> getModuleApps() {
        return moduleApps;
    }

    public void setModuleApps(Map<String, List<String>> moduleApps) {
        this.moduleApps = moduleApps;
    }
}
