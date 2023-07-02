package com.x.program.center.core.entity.wrap;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;
import com.x.general.core.entity.wrap.WrapApplicationDict;

public class WrapServiceModule extends GsonPropertyObject {

    @FieldDescribe("业务id")
    private String id;

    @FieldDescribe("名称.")
    private String name;

    public List<String> listAgentId() throws Exception {
        return ListTools.extractProperty(this.getAgentList(), JpaObject.id_FIELDNAME, String.class, true, true);
    }

    public List<String> listInvokeId() throws Exception {
        return ListTools.extractProperty(this.getInvokeList(), JpaObject.id_FIELDNAME, String.class, true, true);
    }

    public List<String> listScriptId() throws Exception {
        return ListTools.extractProperty(this.getScriptList(), JpaObject.id_FIELDNAME, String.class, true, true);
    }

    public List<String> listDictId() throws Exception {
        return ListTools.extractProperty(this.getDictList(), JpaObject.id_FIELDNAME, String.class, true, true);
    }

    @FieldDescribe("接口服务")
    private List<WrapInvoke> invokeList = new ArrayList<>();

    @FieldDescribe("代理服务")
    private List<WrapAgent> agentList = new ArrayList<>();

    @FieldDescribe("脚本")
    private List<WrapScript> scriptList = new ArrayList<>();

    @FieldDescribe("数据字典")
    private List<WrapApplicationDict> dictList = new ArrayList<>();

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

    public List<WrapInvoke> getInvokeList() {
        return invokeList;
    }

    public void setInvokeList(List<WrapInvoke> invokeList) {
        this.invokeList = invokeList;
    }

    public List<WrapAgent> getAgentList() {
        return agentList;
    }

    public void setAgentList(List<WrapAgent> agentList) {
        this.agentList = agentList;
    }

    public List<WrapApplicationDict> getDictList() {
        return dictList;
    }

    public void setDictList(List<WrapApplicationDict> dictList) {
        this.dictList = dictList;
    }

    public List<WrapScript> getScriptList() {
        return scriptList;
    }

    public void setScriptList(List<WrapScript> scriptList) {
        this.scriptList = scriptList;
    }

    public static WrapServiceModule copy(ServiceModuleEnum serviceModuleEnum){
        WrapServiceModule wrapServiceModule = new WrapServiceModule();
        wrapServiceModule.setId(serviceModuleEnum.getValue());
        wrapServiceModule.setName(serviceModuleEnum.getDescription());
        return wrapServiceModule;
    }
}
