package com.x.query.core.entity.wrap;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.Query;

public class WrapQuery extends Query {

    private static final long serialVersionUID = -1136967220697699885L;

    public static WrapCopier<Query, WrapQuery> outCopier = WrapCopierFactory.wo(Query.class, WrapQuery.class, null,
            JpaObject.FieldsInvisible);

    public static WrapCopier<WrapQuery, Query> inCopier = WrapCopierFactory.wi(WrapQuery.class, Query.class, null,
            JpaObject.FieldsUnmodifyExcludeId);

    @FieldDescribe("视图")
    private List<WrapView> viewList = new ArrayList<>();

    @FieldDescribe("统计")
    private List<WrapStat> statList = new ArrayList<>();

    @FieldDescribe("数据表")
    private List<WrapTable> tableList = new ArrayList<>();

    @FieldDescribe("查询配置")
    private List<WrapStatement> statementList = new ArrayList<>();

    @FieldDescribe("数据导入模型")
    private List<WrapImportModel> importModelList = new ArrayList<>();

    public List<String> listViewId() throws Exception {
        return ListTools.extractProperty(this.getViewList(), JpaObject.id_FIELDNAME, String.class, true, true);
    }

    public List<String> listStatId() throws Exception {
        return ListTools.extractProperty(this.getStatList(), JpaObject.id_FIELDNAME, String.class, true, true);
    }

    public List<String> listTableId() throws Exception {
        return ListTools.extractProperty(this.getTableList(), JpaObject.id_FIELDNAME, String.class, true, true);
    }

    public List<String> listStatementId() throws Exception {
        return ListTools.extractProperty(this.getStatementList(), JpaObject.id_FIELDNAME, String.class, true, true);
    }

    public List<String> listImportModelId() throws Exception {
        return ListTools.extractProperty(this.getImportModelList(), JpaObject.id_FIELDNAME, String.class, true, true);
    }

    public List<WrapView> getViewList() {
        return viewList;
    }

    public void setViewList(List<WrapView> viewList) {
        this.viewList = viewList;
    }

    public List<WrapStat> getStatList() {
        return statList;
    }

    public void setStatList(List<WrapStat> statList) {
        this.statList = statList;
    }

    public List<WrapTable> getTableList() {
        return tableList;
    }

    public void setTableList(List<WrapTable> tableList) {
        this.tableList = tableList;
    }

    public List<WrapStatement> getStatementList() {
        return statementList;
    }

    public void setStatementList(List<WrapStatement> statementList) {
        this.statementList = statementList;
    }

    public List<WrapImportModel> getImportModelList() {
        return importModelList;
    }

    public void setImportModelList(List<WrapImportModel> importModelList) {
        this.importModelList = importModelList;
    }
}
