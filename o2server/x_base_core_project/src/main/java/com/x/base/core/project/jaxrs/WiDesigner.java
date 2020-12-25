package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import java.util.ArrayList;
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
    @FieldDescribe("设计类型：script|form|page|widget|process")
    private List<String> designerTypes = new ArrayList<>();
    @FieldDescribe("应用ID列表.")
    private List<String> appIdList = new ArrayList<>();

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

    public List<String> getAppIdList() {
        return appIdList == null ? new ArrayList<>() : appIdList;
    }

    public void setAppIdList(List<String> appIdList) {
        this.appIdList = appIdList;
    }
}
